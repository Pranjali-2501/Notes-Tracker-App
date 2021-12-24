package com.example.android.notestracker;


import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.android.notestracker.database.AppDatabase;
import com.example.android.notestracker.database.TaskEntry;

import java.util.List;

public class MainViewModel  extends AndroidViewModel {
    //Constant for logging
    private  static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<TaskEntry>> tasks;
    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        tasks = database.taskDao().loadAllTask();
    }
    public LiveData<List<TaskEntry>> getTasks()
    {
        return tasks;
    }
}

