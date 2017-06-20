/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.TutorialsRecyclerViewAdapter;

/**
 * Created by Varun on 12-Mar-17.
 */

public class TutorialsActivity extends AppCompatActivity {

    private RecyclerView rvTutorials;
    private TutorialsRecyclerViewAdapter mAdapter;

    private List<String> tutorialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        tutorialList = new ArrayList<>();
        tutorialList.add("Tutorial 1");
        tutorialList.add("Tutorial 2");
        tutorialList.add("Tutorial 3");

        rvTutorials = (RecyclerView) findViewById(R.id.rv_tutorials);
        mAdapter = new TutorialsRecyclerViewAdapter(this, tutorialList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvTutorials.setLayoutManager(layoutManager);
        rvTutorials.setAdapter(mAdapter);
    }
}