package com.example.android.bakingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.android.bakingapp.database.AppDatabase;
import com.example.android.bakingapp.database.RecipeEntry;
import com.example.android.bakingapp.utilities.RecipeDbJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class DetailFragment extends Fragment {
    private ArrayList<ContentValues> mStepsList;
    private String mName;
    private boolean mTwoPane;
    private StepFragment stepFragment;

    public DetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
            final LinearLayout stepsLayout = rootView.findViewById(R.id.steps_layout);
if(savedInstanceState != null){
    mStepsList = (ArrayList<ContentValues>) savedInstanceState.getSerializable("steps");
}
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.WHITE);
            gd.setCornerRadius(5);
            gd.setStroke(1, Color.BLACK);


            for (ContentValues cv : mStepsList) {
                String shortDescription = cv.get("shortDescription").toString();
                final int stepId = Integer.parseInt(cv.get("stepId").toString());

                Button stepButton = new Button(getContext());
                stepButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                stepButton.setText(shortDescription);
                stepButton.setTypeface(Typeface.DEFAULT_BOLD);
                stepButton.setTextSize(15);
                stepButton.setBackground(gd);
                final ArrayList<ContentValues> finalStepsList = mStepsList;
                stepButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTwoPane) {
                            stepFragment = new StepFragment();
                            stepFragment.setStepData(stepId,finalStepsList,true);
                            if(stepFragment != null){
                                DetailActivity.mFragmentManager.beginTransaction().replace(R.id.step_detail,stepFragment,"stepFragment").commit();
                            }
                            else {
                                DetailActivity.mFragmentManager.beginTransaction().add(R.id.step_detail, stepFragment,"stepFragment").commit();
                             }} else {
                            Intent intent = new Intent(getContext(), StepActivity.class);
                            intent.putExtra("stepId", stepId);
                            intent.putExtra("stepArray", finalStepsList);
                            intent.putExtra("name", mName);
                            intent.putExtra("twoPane", mTwoPane);
                            startActivity(intent);
                        }
                    }
                });
                stepsLayout.addView(stepButton);
            }
            return rootView;

    }

    public void setRecipe(ArrayList<ContentValues> mStepsList,String mName, boolean mTwoPane){
        this.mStepsList = mStepsList;
        this.mName = mName;
        this.mTwoPane = mTwoPane;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("steps",mStepsList);
        super.onSaveInstanceState(outState);
    }
}
