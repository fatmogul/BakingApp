package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakingapp.database.AppDatabase;
import com.example.android.bakingapp.database.RecipeEntry;
import com.example.android.bakingapp.utilities.RecipeDbJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakingWidgetProviderConfigureActivity BakingWidgetProviderConfigureActivity}
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        Intent intent = new Intent(context, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        final int recipeId = Integer.parseInt(BakingWidgetProviderConfigureActivity.loadTitlePref(context, appWidgetId));
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);

        final AppDatabase mDb = AppDatabase.getInstance(context);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final RecipeEntry recipe = mDb.taskDao().loadRecipeById(recipeId);
                try {
                    ArrayList<ContentValues> steps = RecipeDbJsonUtils.getStepsDetail(recipe.getIngredientsJson().toString(),null);
                    String ingredients = steps.get(0).get("description").toString();
                    views.setTextViewText(R.id.appwidget_text, ingredients);
                    views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } );
                // Instruct the widget manager to update the widget

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BakingWidgetProviderConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

