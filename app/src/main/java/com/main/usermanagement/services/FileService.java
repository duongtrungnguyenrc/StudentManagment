package com.main.usermanagement.services;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.main.usermanagement.models.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileService extends AsyncTaskLoader<Boolean> {

    private final List<Student> students;

    public FileService(@NonNull Context context, List<Student> students) {
        super(context);
        this.students = students;
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "students.csv");

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("ID,Name,Age,Phone");
            writer.append("\n");

            for (Student student : students) {
                writer.append(String.valueOf(student.getId()));
                writer.append(",");
                writer.append(student.getName());
                writer.append(",");
                writer.append(String.valueOf(student.getAge()));
                writer.append(",");
                writer.append(student.getPhone());
                writer.append("\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"CSV file export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}
