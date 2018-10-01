package com.example.android.bakingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity {

    String mVideoUrl;
    FragmentManager mFragmentManager;
    StepFragment mStepFragment;
    private int mStepId;
    private ArrayList<ContentValues> mStepsList;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("name"));
        if(savedInstanceState != null){
            mStepId = savedInstanceState.getInt("stepId");
        }
        else{
        mStepId = intent.getIntExtra("stepId", 1);}
        mStepsList = intent.getParcelableArrayListExtra("stepArray");
        mTwoPane = intent.getBooleanExtra("twoPane",false);
        ContentValues cv = mStepsList.get(mStepId);
        mVideoUrl = cv.get("videoUrl").toString();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mVideoUrl.equals("")) {
            goFullScreen();
        }

            mFragmentManager = getSupportFragmentManager();
        mStepFragment = (StepFragment) mFragmentManager.findFragmentByTag("step");

        if(mStepFragment == null) {
            mStepFragment = new StepFragment();
            mStepFragment.setStepData(mStepId, mStepsList, mTwoPane);
            mFragmentManager.beginTransaction().add(R.id.step_detail, mStepFragment,"step").commit();
        }



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("stepId",mStepId);
        super.onSaveInstanceState(outState);
    }

    public void nextStep(View view) {
        mStepId = mStepId + 1;

        mStepFragment = new StepFragment();
        mStepFragment.setStepData(mStepId, mStepsList,mTwoPane);
        mFragmentManager.beginTransaction().replace(R.id.step_detail, mStepFragment,"step").commit();


    }

    public void previousStep(View view) {
        mStepId = mStepId - 1;

        mStepFragment = new StepFragment();
        mStepFragment.setStepData(mStepId, mStepsList,mTwoPane);
        mFragmentManager.beginTransaction().replace(R.id.step_detail, mStepFragment,"step").commit();
}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !mVideoUrl.equals("") && !mTwoPane) {
            goFullScreen();
        } else {
            goNormal();
        }
    }

    public void goFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();


    }

    public void goNormal() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        this.getSupportActionBar().show();

    }


}