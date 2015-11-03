package com.kinnack.dgmt2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kinnack.dgmt2.R;

public class ExerciseSettingsActivity extends AppCompatActivity {
    private static final String EXERCISE_TYPE = "com.kinnack.dgmt2.EXERCISE_TYPE";

    public static Intent exerciseSettingsIntent(Context context, String type) {
        Intent intent = new Intent(context, ExerciseSettingsActivity.class);
        intent.putExtra(EXERCISE_TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getIntent().getStringExtra(EXERCISE_TYPE));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
