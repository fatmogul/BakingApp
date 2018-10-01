package com.example.android.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.bakingapp.database.AppDatabase;
import com.example.android.bakingapp.database.RecipeEntry;
import com.example.android.bakingapp.utilities.RecipeDbJsonUtils;
import com.example.android.bakingapp.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private AppDatabase mDb;



    /*below code found at https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out from user @gar*/
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<RecipeEntry> recipes = null;
                URL recipeRequest = null;
                try {
                    recipeRequest = new URL(NetworkUtils.RECIPES_JSON_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (isOnline()) {
                    try {
                        recipes = RecipeDbJsonUtils.getRecipes(NetworkUtils.getResponseFromHttpUrl(recipeRequest));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (recipes != null) {
                    for (RecipeEntry recipe : recipes) {
                        try {
                            mDb.taskDao().insertRecipe(recipe);
                        } catch (Exception e) {
                            mDb.taskDao().updateRecipe(recipe);
                        }
                    }
                }


                return null;
            }

        }.execute();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        if(findViewById(R.id.tablet_main) != null){
            int columns = 3;
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        }
        else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        setupViewModel();


    }

    public void setupViewModel() {

        dbViewModel viewModel = ViewModelProviders.of(this).get(dbViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<RecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<RecipeEntry> recipeEntries) {
                mAdapter.setRecipes(recipeEntries);
            }
        });

    }


    @Override
    public void onClick(int position) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        int recipeId = mAdapter.getRecipeId(position);
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(DetailActivity.RECIPE_ID, recipeId);
        startActivity(intent);

    }


}
