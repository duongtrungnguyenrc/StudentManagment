package com.main.usermanagement.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.AddStudentCallback;
import com.main.usermanagement.callback.DeleteStudentCallback;
import com.main.usermanagement.models.Student;
import com.main.usermanagement.services.StudentService;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context context;
    private List<Student> students;
    private StudentService service;
    public StudentAdapter(Context context) {
        this.context = context;
    }

    public List<Student> getStudents() {
        return this.students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtName;
        private TextView txtPhone;
        private TextView txtAge;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.txtName = (TextView) view.findViewById(R.id.txt_name);
            this.txtAge = (TextView) view.findViewById(R.id.txt_age);
            this.txtPhone = (TextView) view.findViewById(R.id.txt_phone);
        }

        public TextView getTxtAge() {
            return txtAge;
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTxtName().setText(students.get(position).getName());
        holder.getTxtPhone().setText(students.get(position).getPhone());
        holder.getTxtAge().setText(students.get(position).getStatus().toString());

        holder.getView().setOnLongClickListener(listener -> {
            showPopupMenu(context, holder.getView(), position);
            return true;
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
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.menu_detail) {
                    // Handle menu_detail click
                } else if (itemId == R.id.menu_update) {
                    // Handle menu_update click
                }else if (itemId == R.id.menu_delete) {
                    deleteStudent(index);
                } else {
                    return false;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void addStudent(Student student, int position) {
        service.addStudent(student.getId(), student.getName(), student.getAge(), student.getPhone(), student.getStatus(), new AddStudentCallback() {
            @Override
            public void onSuccess() {
                students.add(position, student);
                notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Add student failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteStudent(int position) {
        service = new StudentService();
        final Student student = students.get(position);
        new AlertDialog.Builder(context)
            .setMessage("Delete " + student.getName() + " ?")
            .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                service.deleteStudent(student.getId(), new DeleteStudentCallback() {
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
            });
        })
        .setNegativeButton(android.R.string.no, (dialog, whichButton) -> {

        }).show();
    }
}
