package com.example.android.notestracker;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.notestracker.database.AppDatabase;
import com.example.android.notestracker.database.TaskEntry;


public class AddTaskViewModel extends ViewModel {
    private LiveData<TaskEntry> task;

    public AddTaskViewModel(AppDatabase database , int taskId){
        task = database.taskDao().loadTaskById(taskId);
    }
    public LiveData<TaskEntry> getTask()
    {
        return task;
    }
}

