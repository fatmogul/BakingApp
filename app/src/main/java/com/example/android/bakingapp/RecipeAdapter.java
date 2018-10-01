package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.database.RecipeEntry;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private final RecipeAdapterOnClickHandler mClickHandler;
    private List<RecipeEntry> mRecipes;

    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }



    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view);


    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        RecipeEntry recipeEntry = mRecipes.get(position);
        String thisRecipeName = recipeEntry.getName();
        holder.mTextView.setText(thisRecipeName);


    }

    public int getItemCount() {
        if (null == mRecipes) return 0;
        return mRecipes.size();
    }

    public void setRecipes(List<RecipeEntry> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public int getRecipeId(int position) {
        RecipeEntry recipe = mRecipes.get(position);
        int recipeId = recipe.getId();
        return recipeId;
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(int position);
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final LinearLayout mLayout;
        TextView mTextView;

        public RecipeAdapterViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.recipe_view);
            mLayout = (LinearLayout) view.findViewById(R.id.ll_main);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}
