package com.main.usermanagement.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.SwipeToDeleteCallback;
import com.main.usermanagement.models.enumerations.EStudentSortDirection;
import com.main.usermanagement.services.DebounceService;
import com.main.usermanagement.services.FileService;
import com.main.usermanagement.ui.activities.AddStudentActivity;
import com.main.usermanagement.adapter.StudentAdapter;
import com.main.usermanagement.models.Student;
import com.main.usermanagement.services.StudentService;
import com.main.usermanagement.ui.skeleton.Skeleton;
import com.main.usermanagement.ui.skeleton.SkeletonScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment {

    private FloatingActionButton fltBtnAdd;

    private ImageButton btnSearchMenu;

    private EditText edtSearch;

    private RecyclerView studentRecyclerView;

    private  SkeletonScreen skeletonScreen;

    private StudentAdapter adapter;
    private StudentService service;


    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        this.edtSearch = view.findViewById(R.id.edt_search);
        this.studentRecyclerView = view.findViewById(R.id.student_recycler_view);
        this.fltBtnAdd = view.findViewById(R.id.btn_add);
        this.btnSearchMenu = view.findViewById(R.id.btn_search_menu);

        FirebaseApp.initializeApp(getContext());

        this.fltBtnAdd.setOnClickListener(button -> {
            Intent intent = new Intent(getActivity(), AddStudentActivity.class);
            startActivityForResult(intent, 111);
        });

        this.adapter = new StudentAdapter(getContext());
        studentRecyclerView.setAdapter(adapter);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipeToDelete();

        this.service = new StudentService();
        fetchStudents(service);

        this.edtSearch.addTextChangedListener(new DebounceService(text -> studentsFilter(text.toString())));

        this.btnSearchMenu.setOnClickListener(button -> {
            showSearchPopupMenu(getContext(), this.btnSearchMenu);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 111 && resultCode == Activity.RESULT_OK) {
            fetchStudents(service);
        }
    }

    private void fetchStudents(StudentService service) {

       showSkeleton();
        service.getAllStudents(new StudentService.OnDataRetrievedListener() {
            @Override
            public void onDataRetrieved(List<Student> students) {
                hideSkeleton();
                adapter.setStudents(students);
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeleton() {
        this.skeletonScreen = Skeleton.bind(studentRecyclerView)
                .adapter(this.adapter)
                .load(R.layout.layout_skeleton_student)
                .count(10)
                .duration(500)
                .show();
    }

    private void hideSkeleton() {
        this.skeletonScreen.hide();
    }

    private void studentsFilter(String keyWord) {
        List<Student> students = adapter.getStudents();
        if(students != null) {
            if (!keyWord.isEmpty()) {
                List<Student> filteredStudents = students.stream().filter(student -> {
                    return student.getName().toLowerCase().contains(keyWord.toLowerCase())
                            || student.getId().toLowerCase().contains(keyWord.toLowerCase())
                            || student.getPhone().toLowerCase().contains(keyWord.toLowerCase());
                }).collect(Collectors.toList());

                adapter.setStudents(filteredStudents);
                adapter.notifyDataSetChanged();

            } else {
                fetchStudents(service);
            }
        }
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                adapter.deleteStudent(position);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(studentRecyclerView);
    }

    private void showSearchPopupMenu(Context context, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.inflate(R.menu.dashoard_search_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.menu_sort_ascending) {
                sortStudents(EStudentSortDirection.ASCENDING);
            } else if (itemId == R.id.menu_sort_descending) {
                sortStudents(EStudentSortDirection.DESCENDING);
            }else if (itemId == R.id.menu_export_file) {
                FileService fileService = new FileService(getContext(), adapter.getStudents());
                fileService.registerListener(0, (loader, data) -> {
                    if (data) {
                        Toast.makeText(getContext(), "CSV file exported successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "CSV file export failed", Toast.LENGTH_SHORT).show();
                    }
                });

                fileService.startLoading();
            } else {
                return false;
            }
            return true;
        });
        popupMenu.show();
    }

    private void sortStudents(@NonNull EStudentSortDirection direction) {
        List<Student> sortedList = new ArrayList<>(this.adapter.getStudents());

        switch (direction){
            case ASCENDING:
                sortedList.sort(Comparator.comparing(Student::getName).reversed());
                break;
            case DESCENDING:
                sortedList.sort(Comparator.comparing(Student::getName));
                break;
        }
        this.adapter.setStudents(sortedList);
    }

}