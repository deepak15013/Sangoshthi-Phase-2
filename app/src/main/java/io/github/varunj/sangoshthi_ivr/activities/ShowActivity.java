/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.ListenersRecyclerViewAdapter;
import io.github.varunj.sangoshthi_ivr.models.CallerStateModel;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.LoadingUtil;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ShowActivity.class.getSimpleName();

    private TextView tvNumOfListeners;
    private Chronometer chronometerShow;

    private ImageButton showEndShow;
    private Button showPlayPause;

    private RecyclerView rvListenersContent;
    private ListenersRecyclerViewAdapter mAdapter;

    private List<CallerStateModel> callerStateModelList;

    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        tvNumOfListeners = (TextView) findViewById(R.id.tv_num_of_listeners);
        tvNumOfListeners.setText(getString(R.string.tv_num_of_listeners, 0, SharedPreferenceManager.getInstance().getCohortSize()));

        chronometerShow = (Chronometer) findViewById(R.id.chronometer_show);
        chronometerShow.start();

        showEndShow = (ImageButton) findViewById(R.id.show_end_show);
        showEndShow.setOnClickListener(this);

        showPlayPause = (Button) findViewById(R.id.show_play_pause);
        showPlayPause.setOnClickListener(this);

        rvListenersContent = (RecyclerView) findViewById(R.id.rv_listeners_content);

        callerStateModelList = new ArrayList<>();

        mAdapter = new ListenersRecyclerViewAdapter(this, callerStateModelList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvListenersContent.setLayoutManager(layoutManager);
        rvListenersContent.setItemAnimator(new DefaultItemAnimator());
        rvListenersContent.setAdapter(mAdapter);

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

                        case "mute_unmute_ack":
                            handleMuteUnmuteAck(jsonObject);
                            break;

                        case "mute_unmute_response":
                            handleMuteUnmuteResponse(jsonObject);
                            break;

                        case "press_1_event":
                            handlePress1Event(jsonObject);
                            break;

                        case "end_show_call_ack":
                            LoadingUtil.getInstance().hideLoading();
                            if(chronometerShow != null)
                                chronometerShow.stop();
                            finish();
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

        RequestMessageHelper.getInstance().showPlaybackMetadata();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_reconnecting_call));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        SharedPreferenceManager.getInstance().setShowRunning(true);
    }

    private void handleConfMemberStatus(JSONObject jsonObject) throws JSONException {
        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("phoneno"));
        if(callerId == -1) {
            // new caller is added
            CallerStateModel callerStateModel = new CallerStateModel(
                    jsonObject.getString("phoneno"),
                    true,
                    false,
                    jsonObject.getString("task"));
            callerStateModelList.add(callerStateModel);
        } else {
            // caller exists
            callerStateModelList.get(callerId).setTask(jsonObject.getString("task"));
            if(jsonObject.getString("task").equals("online") && !callerStateModelList.get(callerId).isMuteUnmuteState()) {
                // old state was - unmuted, show reconnection
                callerStateModelList.get(callerId).setReconnection(true);
                callerStateModelList.get(callerId).setMuteUnmuteState(true);
            }
        }

        tvNumOfListeners.setText(getString(R.string.tv_num_of_listeners, getOnlineListeners(), SharedPreferenceManager.getInstance().getCohortSize()));
        Log.d(TAG, "notify data set changed");
        mAdapter.notifyDataSetChanged();
    }

    private int getOnlineListeners() {
        int count = 0;
        for(CallerStateModel callerStateModel : callerStateModelList) {
            if(callerStateModel.getTask().equals("online")) {
                count++;
            }
        }
        return count;
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
                showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                showPlayPause.setVisibility(View.VISIBLE);
            }
        } else if(jsonObject.getString("case").equals("show_content")) {
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
        }
    }

    private int matchPhoneExists(List<CallerStateModel> callerStateModelList, String phoneno) {
        for(int i = 0; i < callerStateModelList.size(); i++) {
            if(callerStateModelList.get(i).getPhoneNum().equals(phoneno)) {
                Log.d(TAG, "update " + i + " position in caller list");
                return i;
            }
        }
        Log.d(TAG, "new caller");
        return -1;
    }

    private void handleMuteUnmuteAck(JSONObject jsonObject) throws JSONException {
        if(jsonObject.getString("info").equals("FAIL")) {
            Toast.makeText(this, getString(R.string.toast_state_not_changed), Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void handleMuteUnmuteResponse(JSONObject jsonObject) throws JSONException {
        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("listener_phoneno"));
        if(callerId != -1) {
            if(jsonObject.getString("info").equals("OK")) {
                Toast.makeText(this, getString(R.string.toast_state_changed), Toast.LENGTH_SHORT).show();
                callerStateModelList.get(callerId).setMuteUnmuteState(!callerStateModelList.get(callerId).isMuteUnmuteState());
            } else {
                Toast.makeText(this, getString(R.string.toast_state_not_changed), Toast.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void handlePress1Event(JSONObject jsonObject) throws JSONException {
        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("phoneno"));
        if(callerId != -1) {
            callerStateModelList.get(callerId).setQuestionState(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_end_show:
                handleEndShow();
                break;

            case R.id.show_play_pause:
                handleToggleShowPlayPause();
                break;

            default:
                break;
        }
    }

    private void handleEndShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_box_end_show_title)
                .setMessage(R.string.dialog_box_end_show_message)
                .setCancelable(false);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "End show ok");
                LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait), ShowActivity.this);
                RequestMessageHelper.getInstance().showEndShow();
                SharedPreferenceManager.getInstance().setShowRunning(false);
                SharedPreferenceManager.getInstance().setShowUpdateStatus(true);
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void handleToggleShowPlayPause() {
        if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_feedback_play))) {
            RequestMessageHelper.getInstance().playFeedback();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_pause));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_feedback_pause))) {
            RequestMessageHelper.getInstance().pauseShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_resume));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_feedback_resume))) {
            RequestMessageHelper.getInstance().pauseShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_pause));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_play))) {
            RequestMessageHelper.getInstance().playShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_pause));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_pause))) {
            RequestMessageHelper.getInstance().pauseShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_resume));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_resume))) {
            RequestMessageHelper.getInstance().pauseShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_pause));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
        }
    }

    @Override
    public void onBackPressed() {
        handleEndShow();
    }
}