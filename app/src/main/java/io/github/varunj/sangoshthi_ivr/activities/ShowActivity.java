package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.ListenersRecyclerViewAdapter;
import io.github.varunj.sangoshthi_ivr.models.CallerState;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * Created by Varun on 12-Mar-17.
 */

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ShowActivity.class.getSimpleName();

    private Button showCallSelf;
    private Button showCallElse;
    private Button showEndShow;
    private Button showPlayPause;

    private RecyclerView rvListenersContent;
    private ListenersRecyclerViewAdapter mAdapter;

    private List<CallerState> callerStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        showCallSelf = (Button) findViewById(R.id.show_call_self);
        showCallSelf.setOnClickListener(this);

        showCallElse = (Button) findViewById(R.id.show_call_else);
        showCallElse.setOnClickListener(this);

        showEndShow = (Button) findViewById(R.id.show_end_show);
        showEndShow.setOnClickListener(this);

        showPlayPause = (Button) findViewById(R.id.show_play_pause);
        showPlayPause.setOnClickListener(this);

        rvListenersContent = (RecyclerView) findViewById(R.id.rv_listeners_content);

        callerStateList = new ArrayList<>();

        mAdapter = new ListenersRecyclerViewAdapter(this, callerStateList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvListenersContent.setLayoutManager(layoutManager);
        rvListenersContent.setItemAnimator(new DefaultItemAnimator());
        rvListenersContent.setAdapter(mAdapter);

        RequestMessageHelper.getInstance().getUpcomingShow();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                try {
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));

                    switch (jsonObject.getString("objective")) {
                        case "conf_member_status":
                            handleConfMemberStatus(jsonObject);
                            break;

                        case "show_playback_metadata_response":
                            handleShowPlaybackMetadataResponse(jsonObject);
                            break;

                        case "media_stopped":
                            handleMediaStopped(jsonObject);
                            break;

                        default:
                            Log.d(TAG, "objective not matched: " + jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);
    }

    private void handleConfMemberStatus(JSONObject jsonObject) throws JSONException {
        CallerState callerState = new CallerState(
                jsonObject.getString("phoneno"),
                true,
                false,
                jsonObject.getString("task"));

        int callerId = matchPhoneExists(callerStateList, jsonObject.getString("phoneno"));
        if(callerId == -1) {
            callerStateList.add(callerState);
        } else {
            callerStateList.set(callerId, callerState);
        }

        Log.d(TAG, "notify data set changed");
        mAdapter.notifyDataSetChanged();
    }

    private void handleShowPlaybackMetadataResponse(JSONObject jsonObject) throws JSONException {
        if(SharedPreferenceManager.getInstance().getFeedback()) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_play));
            showPlayPause.setVisibility(View.VISIBLE);
        } else if(SharedPreferenceManager.getInstance().getShowContent()) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
            showPlayPause.setVisibility(View.VISIBLE);
        }
    }

    private void handleMediaStopped(JSONObject jsonObject) throws JSONException {
        if(jsonObject.getString("case").equals("feedback")) {
            if(SharedPreferenceManager.getInstance().getShowContent()) {
                showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
                showPlayPause.setVisibility(View.VISIBLE);
            }
        } else if(jsonObject.getString("case").equals("show_content")) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
        }
    }

    private int matchPhoneExists(List<CallerState> callerStateList, String phoneno) {
        for(int i = 0; i < callerStateList.size(); i++) {
            if(callerStateList.get(i).getPhoneNum().equals(phoneno)) {
                Log.d(TAG, "update " + i + " position in caller list");
                return i;
            }
        }
        Log.d(TAG, "new caller");
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_call_self:
                Toast.makeText(this, "Calling broadcaster", Toast.LENGTH_SHORT).show();
                RequestMessageHelper.getInstance().startShow();
                RequestMessageHelper.getInstance().showPlaybackMetadata();
                break;

            case R.id.show_call_else:
                Toast.makeText(this, "Calling listeners", Toast.LENGTH_SHORT).show();
                RequestMessageHelper.getInstance().dialListeners();
                break;

            case R.id.show_end_show:
                Toast.makeText(this, "End show", Toast.LENGTH_SHORT).show();
                RequestMessageHelper.getInstance().showEndShow();
                break;

            case R.id.show_play_pause:
                handleToggleShowPlayPause();
                break;

            default:
                break;
        }
    }

    private void handleToggleShowPlayPause() {
        if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_feedback_play))) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_pause));
            RequestMessageHelper.getInstance().playFeedback();
        }
        if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_feedback_pause))) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_play));
            RequestMessageHelper.getInstance().pauseShowContent();
        }
        if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_play))) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_pause));
            RequestMessageHelper.getInstance().playShowContent();
        }
        if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_pause))) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
            RequestMessageHelper.getInstance().pauseShowContent();
        }
    }
}