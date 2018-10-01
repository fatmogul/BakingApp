package com.example.android.bakingapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.bakingapp.database.AppDatabase;
import com.example.android.bakingapp.database.RecipeEntry;

import java.util.List;

public class dbViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = dbViewModel.class.getSimpleName();
    private AppDatabase database;
    private LiveData<List<RecipeEntry>> recipes;


    public dbViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        recipes = database.taskDao().loadAllRecipes();
    }


    public LiveData<List<RecipeEntry>> getRecipes() {
            return recipes;

    }
}

