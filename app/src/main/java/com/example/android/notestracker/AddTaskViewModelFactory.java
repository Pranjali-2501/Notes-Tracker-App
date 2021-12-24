package com.example.android.notestracker;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.notestracker.database.AppDatabase;


public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mdb;
    private final int mTaskId;

    public AddTaskViewModelFactory(AppDatabase database , int taskId) {
        this.mdb = database;
        mTaskId = taskId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        //noinspection unchecked
        return (T) new AddTaskViewModel(mdb, mTaskId);
    }
}
