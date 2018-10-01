package com.example.android.bakingapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

@Entity(tableName = "recipe")
public class RecipeEntry {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private String name;
    private int servings;
    private String image;
    private String ingredientsJson;
    private String stepsJson;

    /**
     * No args constructor for use in serialization
     */

    public RecipeEntry(int id, String name, int servings, String image,String ingredientsJson,String stepsJson) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.ingredientsJson = ingredientsJson;
        this.stepsJson = stepsJson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredientsJson(){return ingredientsJson;}

    public void setIngredientsJson(String ingredientsJson){this.ingredientsJson = ingredientsJson;}

    public String getStepsJson(){return stepsJson;}

    public void setStepsJson(String stepsJson){this.stepsJson = stepsJson;}
}



