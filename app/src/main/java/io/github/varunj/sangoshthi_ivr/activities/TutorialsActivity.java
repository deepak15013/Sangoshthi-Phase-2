/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.TutorialsRecyclerViewAdapter;
import io.github.varunj.sangoshthi_ivr.models.TutorialModel;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.LoadingUtil;

/**
 * Created by Varun on 12-Mar-17.
 */

public class TutorialsActivity extends AppCompatActivity {

    private static final String TAG = TutorialsActivity.class.getSimpleName();

    private RecyclerView rvTutorials;
    private TutorialsRecyclerViewAdapter mAdapter;

    private List<TutorialModel> tutorialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait), this);

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));

                    if(jsonObject.getString("objective").equals("get_show_id_for_gallery_ack")) {
                        if(!jsonObject.getString("show_id").equals("") && !jsonObject.getString("show_id").equals(" ") &&!jsonObject.getString("show_id").equals("-1")) {
                            String showId = jsonObject.getString("show_id");
                            Log.d(TAG, "show_id - " + showId);
                        }
                        LoadingUtil.getInstance().hideLoading();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "" + e);
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        RequestMessageHelper.getInstance().getShowIdForGallery();

        tutorialList = new ArrayList<>();
        tutorialList.add(new TutorialModel("Tutorial 1", "Tutorial1.mp3", false));
        tutorialList.add(new TutorialModel("Tutorial 2", "Tutorial2.mp3", false));
        tutorialList.add(new TutorialModel("Tutorial 3", "", true));

        rvTutorials = (RecyclerView) findViewById(R.id.rv_tutorials);
        mAdapter = new TutorialsRecyclerViewAdapter(this, tutorialList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvTutorials.setLayoutManager(layoutManager);
        rvTutorials.setAdapter(mAdapter);
    }
}