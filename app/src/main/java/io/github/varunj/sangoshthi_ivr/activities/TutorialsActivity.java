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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.TutorialsRecyclerViewAdapter;
import io.github.varunj.sangoshthi_ivr.models.TutorialListenModel;
import io.github.varunj.sangoshthi_ivr.models.TutorialModel;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * This the activity called from HomeActivity and fetches the tutorials from server
 * The tutorials can be locked or unlocked
 */
public class TutorialsActivity extends AppCompatActivity {

    private static final String TAG = TutorialsActivity.class.getSimpleName();

    private RecyclerView rvTutorials;
    private TutorialsRecyclerViewAdapter mAdapter;

    private List<TutorialModel> tutorialList;
    private List<TutorialListenModel> tutorialListenModelList;
    final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        tutorialList = new ArrayList<>();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));

                    switch (jsonObject.getString("objective")) {
                        case "get_show_id_for_gallery_ack":
                            handlegetShowIdForGalleryAck(jsonObject);
                            break;

                        case "broadcaster_content_listen_event_ack":
                            Log.d(TAG, "tutorial telemetry ack");
                            // TODO: 01-07-2017 ack can be out of sync then tutorialModelList can be smaller then given packet_id
                            if(jsonObject.getString("info").equals("OK") && !jsonObject.getString("packet_id").equals("")) {
                                int packetId = Integer.parseInt(jsonObject.getString("packet_id"));
                                Log.d(TAG, "packet_id - " + packetId);
                                Log.d(TAG, "tutorial list - " + tutorialListenModelList.toString());

                                int packet_id = Integer.parseInt(jsonObject.getString("packet_id"));
                                if(packet_id >= 0) {
                                    int tutorialListenModelId = -1;
                                    for(int i = 0; i < tutorialListenModelList.size(); i++) {
                                        if(tutorialListenModelList.get(i).getPacket_id() == packet_id) {
                                            tutorialListenModelId = i;
                                        }
                                    }
                                    Log.d(TAG, "packet found at - " + tutorialListenModelId);
                                    if(tutorialListenModelId != -1) {
                                        tutorialListenModelList.remove(tutorialListenModelId);
                                    }
                                    SharedPreferenceManager.getInstance().setTutorialListenModelList(tutorialListenModelList);
                                }
                            }
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "" + e);
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        RequestMessageHelper.getInstance().getShowIdForGallery();

        Log.d(TAG, "TutorialsActivityData - " + SharedPreferenceManager.getInstance().getTutorialsActivityData());
        if(SharedPreferenceManager.getInstance().getTutorialsActivityData().equals("NONE")) {
            Log.d(TAG, "No tutorial data present");

            // String tutorialName, String fileName, String showName/content-id, boolean locked
            // files are unlocked using the content id in the acknowledgement of get_show_id_for_gallery_ack packet
            // Locked - true (locked), false (unlocked)
            tutorialList.add(new TutorialModel("नवजात का तापमान", "नवजात का तापमान.mp3", "15", false));
            tutorialList.add(new TutorialModel("नवजात के साथ खेलना और बातें", "नवजात के साथ खेलना और बातें.mp3", "13", true));
            tutorialList.add(new TutorialModel("नवजात शिशु का रोना", "नवजात शिशु का रोना.mp3", "5", true));
            tutorialList.add(new TutorialModel("नवजात शिशु में खतरे के लक्षण", "नवजात शिशु में खतरे के लक्षण.mp3", "6", true));
            tutorialList.add(new TutorialModel("माँ की मायूसी", "माँ की मायूसी.mp3", "12", true));
            tutorialList.add(new TutorialModel("माँ बच्चे की ख़ुशी", "माँ बच्चे की ख़ुशी.mp3", "10", true));
            tutorialList.add(new TutorialModel("माँ में खतरे के लक्षण", "माँ में खतरे के लक्षण.mp3", "11", true));
            tutorialList.add(new TutorialModel("स्तनपान", "स्तनपान.mp3", "7", true));
            tutorialList.add(new TutorialModel("हैंड वाशिंग", "हैंड वाशिंग.mp3", "8", true));

            SharedPreferenceManager.getInstance().setTutorialsActivityData(gson.toJson(tutorialList));

        } else {
            Log.d(TAG, "Tutorial data present");
            String json = SharedPreferenceManager.getInstance().getTutorialsActivityData();
            Type type = new TypeToken<List<TutorialModel>>(){}.getType();
            tutorialList = gson.fromJson(json, type);
        }

        rvTutorials = (RecyclerView) findViewById(R.id.rv_tutorials);
        mAdapter = new TutorialsRecyclerViewAdapter(this, tutorialList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvTutorials.setLayoutManager(layoutManager);
        rvTutorials.setAdapter(mAdapter);

        String json = SharedPreferenceManager.getInstance().getTutorialListenData();
        if(!json.equals("NONE")) {
            Type type = new TypeToken<List<TutorialListenModel>>(){}.getType();
            tutorialListenModelList = gson.fromJson(json, type);

            for(TutorialListenModel tutorialListenModel : tutorialListenModelList) {
                RequestMessageHelper.getInstance().broadcasterContentListenEvent(tutorialListenModel, tutorialListenModelList.indexOf(tutorialListenModel));
            }
        }
    }

    private void handlegetShowIdForGalleryAck(JSONObject jsonObject) throws JSONException {
        if(!jsonObject.getString("content_id").equals("") && !jsonObject.getString("content_id").equals(" ") &&!jsonObject.getString("content_id").equals("-1")) {
            String contentId = jsonObject.getString("content_id");
            Log.d(TAG, "content_id - " + contentId);

            for(int i = 0; i < tutorialList.size(); i++) {
                if(tutorialList.get(i).getShowName().equals(contentId)) {
                    tutorialList.get(i).setLocked(false);
                }
            }

            SharedPreferenceManager.getInstance().setTutorialsActivityData(gson.toJson(tutorialList));
            mAdapter.notifyDataSetChanged();
        }
    }
}