package com.example.android.bakingapp.utilities;

import android.content.ContentValues;
import android.util.Log;

import com.example.android.bakingapp.database.RecipeEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RecipeDbJsonUtils {
    public static final String RESULTS = "";

    public static final String MESSAGE_CODE = "status_code";

    public static final String RECIPE_ID = "id";

    public static final String NAME = "name";

    public static final String SERVINGS = "servings";
    public static final String IMAGE = "image";

    public static final String INGREDIENT_KEY = "ingredients";
    public static final String QUANTITY = "quantity";
    public static final String MEASURE = "measure";
    public static final String INGREDIENT = "ingredient";

    public static final String STEP_KEY = "steps";
    public static final String STEP_ID = "id";
    public static final String SHORT_DESCRIPTION = "shortDescription";
    public static final String DESCRIPTION = "description";
    public static final String VIDEO_URL = "videoURL";

    public static final String THUMBNAIL_URL = "thumbnailURL";

    public static List<RecipeEntry> getRecipes(String recipesJsonStr)
            throws JSONException {

        List<RecipeEntry> recipes = new ArrayList<RecipeEntry>();

        JSONArray recipeArray = new JSONArray(recipesJsonStr);


        for (int i = 0; i < recipeArray.length(); i++) {
            /* These are the values that will be collected */
            JSONObject recipe = recipeArray.getJSONObject(i);

            int id = Integer.parseInt(recipe.getString(RECIPE_ID));
            String name = recipe.getString(NAME);
            int servings = Integer.parseInt(recipe.getString(SERVINGS));
            String image = recipe.getString(IMAGE);

            String ingredientJson = recipe.getJSONArray(INGREDIENT_KEY).toString();
            String stepsJson = recipe.getJSONArray(STEP_KEY).toString();
            recipes.add(new RecipeEntry(id, name, servings, image, ingredientJson, stepsJson));
        }
        return recipes;


    }

    public static ArrayList<ContentValues> getStepsDetail(String ingredientJson,String stepsJson) throws JSONException {
        ArrayList<ContentValues> stepsList = new ArrayList<>();

        JSONArray ingredientArray = new JSONArray(ingredientJson);
        String finalIngredients = "";
        for (int i = 0; i < ingredientArray.length(); i++) {
            ContentValues cv = new ContentValues();
            JSONObject thisIngredient = ingredientArray.getJSONObject(i);
            String quantity = thisIngredient.getString(QUANTITY);
            String measure = thisIngredient.getString(MEASURE);
            String ingredient = thisIngredient.getString(INGREDIENT);
            finalIngredients = finalIngredients + quantity + " " + measure + " " + ingredient + "\n";
        }
        ContentValues ingredientcv = new ContentValues();
        ingredientcv.put("stepId",0);
        ingredientcv.put("shortDescription","Ingredients");
        ingredientcv.put("description",finalIngredients);
        ingredientcv.put("videoUrl","");
        ingredientcv.put("thumbnailUrl","");
        stepsList.add(ingredientcv);
        if(stepsJson != null){
        JSONArray stepArray = new JSONArray(stepsJson);
        for (int i = 0; i < stepArray.length(); i++) {
            ContentValues cv = new ContentValues();
            JSONObject thisStep = stepArray.getJSONObject(i);
            cv.put("stepId", thisStep.getInt(STEP_ID) + 1);
            cv.put("shortDescription", thisStep.getString(SHORT_DESCRIPTION));
            cv.put("description", thisStep.getString(DESCRIPTION));
            cv.put("videoUrl", thisStep.getString(VIDEO_URL));
            cv.put("thumbnailUrl", thisStep.getString(THUMBNAIL_URL));
            stepsList.add(cv);

        }}
        return stepsList;

    }
}

