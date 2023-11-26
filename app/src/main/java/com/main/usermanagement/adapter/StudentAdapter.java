package com.main.usermanagement.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.callback.RecyclerViewActionCallback;
import com.main.usermanagement.models.entities.Student;
import com.main.usermanagement.models.enumerations.ELockStudentAction;
import com.main.usermanagement.models.enumerations.EStatus;
import com.main.usermanagement.services.StudentService;
import com.main.usermanagement.ui.activities.StudentDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private final Context context;
    private List<Student> students;
    private List<Student> checkedStudents;
    private final StudentService service;
    private RecyclerViewActionCallback<List<Student>> actionCallback;

    public StudentAdapter(Context context, RecyclerViewActionCallback<List<Student>> action) {
        this.context = context;
        this.service = new StudentService();
        this.actionCallback = action;
        this.checkedStudents = new ArrayList<>();
    }

    public List<Student> getStudents() {
        return this.students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView txtName;
        private final TextView txtPhone;
        private final TextView txtStatus;
        private final CheckBox cbChecked;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.txtName = view.findViewById(R.id.txt_name);
            this.txtStatus = view.findViewById(R.id.txt_status);
            this.txtPhone = view.findViewById(R.id.txt_phone);
            this.cbChecked = view.findViewById(R.id.cb_checked);
        }

        public TextView getTxtStatus() {
            return txtStatus;
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtPhone() {
            return txtPhone;
        }

        public View getView() {
            return this.view;
        }
        public CheckBox getCbChecked() {
            return this.cbChecked;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_student, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.getTxtName().setText(student.getName());
        holder.getTxtPhone().setText(student.getPhone());
        holder.getTxtStatus().setText(student.getStatus().toString());

        holder.getView().setOnClickListener(view -> {
            Intent intent = new Intent(context, StudentDetailActivity.class);
            intent.putExtra("student_id", student.getId());
            context.startActivity(intent);
        });

        holder.getView().setOnLongClickListener(listener -> {
            showPopupMenu(context, holder.getView(), position);
            return true;
        });

        holder.getCbChecked().setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(actionCallback != null) {
                actionCallback.onHasItemChecked(position);

                if(isChecked) {
                    getCheckedStudents().add(getStudents().get(position));
                }
                else {
                    setCheckedStudents(getCheckedStudents().stream().filter(s -> !Objects.equals(s.getId(), getStudents().get(position).getId())).collect(Collectors.toList()));
                }

                if(checkedStudents.size() != 0) {
                    actionCallback.onHasItemChecked(checkedStudents);
                }
                else {
                    actionCallback.onNoItemChecked();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    private void showPopupMenu(Context context, View anchorView, int index) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.inflate(R.menu.student_option_menu);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.menu_detail) {
                // Handle menu_detail click
            } else if (itemId == R.id.menu_lock) {
               lockStudent(students.get(index).getId(), index);
            } else if (itemId == R.id.menu_delete) {
                deleteStudent(index);
            } else {
                return false;
            }
            return true;
        });
        popupMenu.show();
    }

    public void addStudent(Student student, int position) {
        service.addStudent(student.getId(), student.getName(), student.getAge(), student.getPhone(), student.getStatus(), new ActionCallback<Object>() {
            @Override
            public void onSuccess() {
                students.add(position, student);
                notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Add student failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(context, "Add student failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteStudent(int position) {
        final Student student = students.get(position);

        new AlertDialog.Builder(context)
            .setMessage("Delete " + student.getName() + " ?")
            .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                service.deleteStudent(student.getId(), new ActionCallback() {
                    @Override
                    public void onSuccess() {
                                students.remove(position);
                                notifyDataSetChanged();

                                View rootView = ((Activity) context).findViewById(android.R.id.content);

                                Snackbar snackbar = Snackbar
                                        .make(rootView, "Student from the list.", Snackbar.LENGTH_LONG);
                                snackbar.setAction("UNDO", view -> {
                                    addStudent(student, position);
                                    Toast.makeText(context, "Undo successfully", Toast.LENGTH_LONG).show();
                                });

                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(context, "Delete student failed: " + message, Toast.LENGTH_LONG).show();
                    }
            });
        })
        .setNegativeButton(android.R.string.no, null).show();
    }

    public void lockStudent(String studentID, int position) {
        service.lockStudent(studentID, new ActionCallback<ELockStudentAction>() {
            @Override
            public void onSuccess(ELockStudentAction action) {
                Toast.makeText(context, "Successfully to " + action + " student", Toast.LENGTH_LONG).show();
                students.get(position).setStatus(action == ELockStudentAction.LOCK ? EStatus.LOCKED : EStatus.NORMAL);
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Invalid student: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(context, "Lock student failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public List<Student> getCheckedStudents() {
        return this.checkedStudents;
    }

    public void setCheckedStudents(List<Student> checkedStudents) {
        this.checkedStudents = checkedStudents;
    }
}
