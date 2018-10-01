package com.example.android.bakingapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM recipe ORDER BY id ASC")
    LiveData<List<RecipeEntry>> loadAllRecipes();

    @Insert
    void insertRecipe(RecipeEntry recipeEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(RecipeEntry recipeEntry);

    @Delete
    void deleteRecipe(RecipeEntry recipeEntry);

    @Query("SELECT * FROM recipe WHERE id = :id")
    RecipeEntry loadRecipeById(int id);

    @Query("SELECT * FROM recipe ORDER BY id ASC")
    List<RecipeEntry> loadAllRecipesForWidget();
}
