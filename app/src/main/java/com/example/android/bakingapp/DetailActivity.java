package com.example.android.bakingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.database.AppDatabase;
import com.example.android.bakingapp.database.RecipeEntry;
import com.example.android.bakingapp.utilities.RecipeDbJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    public static final String RECIPE_ID = "recipe_id";
    public static final int DEFAULT_POSITION = -1;
    int recipeId;
    private AppDatabase mDb;
    private boolean mTwoPane;
    private RecipeEntry mRecipe;
    static FragmentManager mFragmentManager;
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        recipeId = intent.getIntExtra(RECIPE_ID, DEFAULT_POSITION);
        if (savedInstanceState == null) {
            detailFragment = new DetailFragment();
            mDb = AppDatabase.getInstance(getApplicationContext());
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mRecipe = mDb.taskDao().loadRecipeById(recipeId);
                    final RecipeEntry recipe = mRecipe;
                    String name = recipe.getName().toString();
                    setTitle(name);
                    String ingredientJson = mRecipe.getIngredientsJson();
                    String stepsJson = mRecipe.getStepsJson();
                    ArrayList<ContentValues> stepsList = new ArrayList<>();
                    try {
                        stepsList = RecipeDbJsonUtils.getStepsDetail(ingredientJson, stepsJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    detailFragment.setRecipe(stepsList, name, mTwoPane);
                    mFragmentManager = getSupportFragmentManager();

                    mFragmentManager.beginTransaction().add(R.id.step_listing, detailFragment, "detailFragment").commit();

                }
            });

        }else{detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detailFragment");}

            if(findViewById(R.id.tablet_layout) != null){
            mTwoPane = true;
        }else{
            mTwoPane = false;
        }

    }
    private void closeOnError() {
        finish();
        Toast.makeText(this, "@string/error_message", Toast.LENGTH_SHORT).show();
    }



}
