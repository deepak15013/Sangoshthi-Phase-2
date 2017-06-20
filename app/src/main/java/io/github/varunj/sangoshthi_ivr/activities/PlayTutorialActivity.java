/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.github.varunj.sangoshthi_ivr.R;

public class PlayTutorialActivity extends AppCompatActivity {

    private TextView tvTutorialName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tutorial);

        String tutorialName = getIntent().getStringExtra("TUTORIAL_NAME");

        tvTutorialName = (TextView) findViewById(R.id.tv_tutorial_name);
        tvTutorialName.setText(tutorialName);

    }
}
