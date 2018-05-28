/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.ListenersRecyclerViewAdapter;
import io.github.varunj.sangoshthi_ivr.models.CallerStateModel;
import io.github.varunj.sangoshthi_ivr.models.ShowPlaybackModel;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.LoadingUtil;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * This is the main Dashboard activity that runs the show
 */
public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ShowActivity.class.getSimpleName();

    private TextView tvNumOfListeners;
    private Chronometer chronometerShow;

    private ImageButton showEndShow;
    private Button showPlayPause;
    private ImageButton btnPreviousContent;
    private ImageButton btnNextContent;
    private RelativeLayout llMediaControls;
    private TextView tvMediaName;

    private RecyclerView rvListenersContent;
    private ListenersRecyclerViewAdapter mAdapter;

    private List<CallerStateModel> callerStateModelList;
    private ArrayList<ShowPlaybackModel> showPlaybackModels;
    private int currentPlayingIndex;

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

        btnPreviousContent = (ImageButton) findViewById(R.id.btn_previous_content);
        btnPreviousContent.setOnClickListener(this);

        btnNextContent = (ImageButton) findViewById(R.id.btn_next_content);
        btnNextContent.setOnClickListener(this);

        llMediaControls = (RelativeLayout) findViewById(R.id.ll_media_controls);
        tvMediaName = (TextView) findViewById(R.id.tv_media_name);

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
                            if (chronometerShow != null)
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

        if (SharedPreferenceManager.getInstance().isShowRunning()) {
            Log.d(TAG, "Resuming show");

            List<CallerStateModel> resumeCallerStateModelList = SharedPreferenceManager.getInstance().getShowSessionData();
            if (resumeCallerStateModelList != null) {

                Log.d(TAG, "Resume callerStateModelList - " + resumeCallerStateModelList.toString());
                callerStateModelList.clear();
                callerStateModelList.addAll(resumeCallerStateModelList);

                tvNumOfListeners.setText(getString(R.string.tv_num_of_listeners, getOnlineListeners(), SharedPreferenceManager.getInstance().getCohortSize()));
            }

            long chronometerTime = SharedPreferenceManager.getInstance().getShowChronometerTime();
            Log.d(TAG, "chronometerTime - " + chronometerTime);
            chronometerShow.setBase(SystemClock.elapsedRealtime() - chronometerTime);
            chronometerShow.start();

            Log.d(TAG, "resume notifyDataSetChanged");
            mAdapter.notifyDataSetChanged();
        } else {
            SharedPreferenceManager.getInstance().setShowRunning(true);
        }
    }

    private void handleConfMemberStatus(JSONObject jsonObject) throws JSONException {
        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("phoneno"));
        if (callerId == -1) {
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
            if (jsonObject.getString("task").equals("online") && !callerStateModelList.get(callerId).isMuteUnmuteState()) {
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
        for (CallerStateModel callerStateModel : callerStateModelList) {
            if (callerStateModel.getTask().equals("online")) {
                count++;
            }
        }
        return count;
    }

    private void handleShowPlaybackMetadataResponse(JSONObject jsonObject) {
        Log.d(TAG, "handleShowPlaybackMetadataResponse function called");

        showPlaybackModels = SharedPreferenceManager.getInstance().getShowPlaybackModels();

        // sort the list by order
        Collections.sort(showPlaybackModels);

        Log.d(TAG, showPlaybackModels.toString());

        btnPreviousContent.setVisibility(View.INVISIBLE);
        btnNextContent.setVisibility(View.INVISIBLE);

        if (showPlaybackModels.size() <= 0) {
            showPlayPause.setVisibility(View.INVISIBLE);
            llMediaControls.setVisibility(View.GONE);
        } else {
            llMediaControls.setVisibility(View.VISIBLE);
            showPlayPause.setVisibility(View.VISIBLE);
            currentPlayingIndex = 0;

            handleToggleShowPlayPause();
        }
    }

    private void handleMediaStopped(JSONObject jsonObject) {

        if (showPlaybackModels != null) {
            showPlaybackModels.get(currentPlayingIndex).setOncePlayed(true);
            showPlaybackModels.get(currentPlayingIndex).setAudioState(0);

            if (currentPlayingIndex < showPlaybackModels.size() - 1) {
                btnNextContent.setVisibility(View.VISIBLE);
            } else {
                btnNextContent.setVisibility(View.INVISIBLE);
            }

            handleToggleShowPlayPause();
        } else {
            Log.e(TAG, "showPlaybackModel null");
        }
    }

    private int matchPhoneExists(List<CallerStateModel> callerStateModelList, String phoneno) {
        if (callerStateModelList != null) {
            for (int i = 0; i < callerStateModelList.size(); i++) {
                if (callerStateModelList.get(i).getPhoneNum().equals(phoneno)) {
                    Log.d(TAG, "update " + i + " position in caller list");
                    return i;
                }
            }
        }
        Log.d(TAG, "new caller");
        return -1;
    }

    private void handleMuteUnmuteAck(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString("info").equals("FAIL")) {
            Toast.makeText(this, getString(R.string.toast_state_not_changed), Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void handleMuteUnmuteResponse(JSONObject jsonObject) throws JSONException {
        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("listener_phoneno"));
        if (callerId != -1) {
            if (jsonObject.getString("info").equals("OK")) {
                Toast.makeText(this, getString(R.string.toast_state_changed), Toast.LENGTH_SHORT).show();
                callerStateModelList.get(callerId).setMuteUnmuteState(!callerStateModelList.get(callerId).isMuteUnmuteState());
                Log.d(TAG, "CallerStateModelList after muteUnmute response -  " + callerStateModelList.toString());
            } else {
                Toast.makeText(this, getString(R.string.toast_state_not_changed), Toast.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void handlePress1Event(JSONObject jsonObject) throws JSONException {
        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("phoneno"));
        if (callerId != -1) {
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
                sendPlayPauseResumePacket();
                break;

            case R.id.btn_previous_content:
                handlePrevButton();
                break;

            case R.id.btn_next_content:
                handleNextButton();
                break;

            default:
                break;
        }
    }

    private void handleNextButton() {

        // check if playback models is present
        if (showPlaybackModels != null) {

            // check if audio is played once fully and there is no current audio played
            if (showPlaybackModels.get(currentPlayingIndex).isOncePlayed()) {
                if (showPlaybackModels.get(currentPlayingIndex).getAudioState() != 1) {
                    // only next can happen when audio is fully played and stopped
                    currentPlayingIndex++;
                    tvMediaName.setText(showPlaybackModels.get(currentPlayingIndex).getName());

                    if (currentPlayingIndex == showPlaybackModels.size() - 1) {
                        btnNextContent.setVisibility(View.INVISIBLE);
                    }

                    if (currentPlayingIndex == 1) {
                        btnPreviousContent.setVisibility(View.VISIBLE);
                    }
                    handleToggleShowPlayPause();
                } else {
                    Toast.makeText(this, getString(R.string.toast_pause_fully_play_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_unlock_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "showPlaybackModel null");
        }
    }

    private void handlePrevButton() {

        if (showPlaybackModels != null) {

            // check if current audio is not playing
            if (showPlaybackModels.get(currentPlayingIndex).getAudioState() != 1) {
                currentPlayingIndex--;
                tvMediaName.setText(showPlaybackModels.get(currentPlayingIndex).getName());

                if (currentPlayingIndex == 0) {
                    btnPreviousContent.setVisibility(View.INVISIBLE);
                }

                handleToggleShowPlayPause();
            } else {
                Toast.makeText(this, "Pause or fully play the current audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendPlayPauseResumePacket() {
        switch (showPlaybackModels.get(currentPlayingIndex).getAudioState()) {
            case 0:
                // send play
                RequestMessageHelper.getInstance().playShowMedia(currentPlayingIndex + 1, showPlaybackModels.get(currentPlayingIndex).getType().name());
                showPlaybackModels.get(currentPlayingIndex).setAudioState(1);
                break;

            case 1:
                RequestMessageHelper.getInstance().pausePlayShowContent();
                showPlaybackModels.get(currentPlayingIndex).setAudioState(2);
                break;

            case 2:
                RequestMessageHelper.getInstance().pausePlayShowContent();
                showPlaybackModels.get(currentPlayingIndex).setAudioState(1);
                break;

            default:
                Log.e(TAG, "sendPlayPauseResumePacket error state - " + showPlaybackModels.get(currentPlayingIndex).getAudioState());
        }
        handleToggleShowPlayPause();
    }

    private void handleEndShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_box_end_show_title)
                .setMessage(R.string.dialog_box_end_show_message)
                .setCancelable(false);

        builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "End show ok");
                LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait), ShowActivity.this);
                RequestMessageHelper.getInstance().showEndShow();
                SharedPreferenceManager.getInstance().setShowRunning(false);
                SharedPreferenceManager.getInstance().setShowUpdateStatus(true);
            }
        });

        builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void handleToggleShowPlayPause() {
        Log.d(TAG, "handleToggleShowPlayPause function called, current index - " + currentPlayingIndex);
        Log.d(TAG, "Type - " + showPlaybackModels.get(currentPlayingIndex).getType());
        Log.d(TAG, "AudioState - " + showPlaybackModels.get(currentPlayingIndex).getAudioState());

        switch (showPlaybackModels.get(currentPlayingIndex).getType()) {
            case content:
                switch (showPlaybackModels.get(currentPlayingIndex).getAudioState()) {
                    case 0:
                        // stopped state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
                        tvMediaName.setText(showPlaybackModels.get(currentPlayingIndex).getName());
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;

                    case 1:
                        // playing state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_pause));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
                        break;

                    case 2:
                        // resume state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_resume));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;
                }
                break;

            case question:
                switch (showPlaybackModels.get(currentPlayingIndex).getAudioState()) {
                    case 0:
                        // stopped state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_question_play));
                        tvMediaName.setText(showPlaybackModels.get(currentPlayingIndex).getName());
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;

                    case 1:
                        // playing state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_question_pause));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
                        break;

                    case 2:
                        // resume state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_question_resume));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;
                }
                break;

            case answer:
                switch (showPlaybackModels.get(currentPlayingIndex).getAudioState()) {
                    case 0:
                        // stopped state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_answer_play));
                        tvMediaName.setText(showPlaybackModels.get(currentPlayingIndex).getName());
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;

                    case 1:
                        // playing state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_answer_pause));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
                        break;

                    case 2:
                        // resume state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_answer_resume));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;
                }
                break;

            case QA:
                switch (showPlaybackModels.get(currentPlayingIndex).getAudioState()) {
                    case 0:
                        // stopped state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_question_answer_play));
                        tvMediaName.setText(showPlaybackModels.get(currentPlayingIndex).getName());
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;

                    case 1:
                        // playing state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_question_answer_pause));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
                        break;

                    case 2:
                        // resume state
                        showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_question_answer_resume));
                        showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
                        break;
                }
                break;

            default:
                Log.e(TAG, "error toggle index");
        }
    }

    @Override
    public void onBackPressed() {
        handleEndShow();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "SHOW ON RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "SHOW ON PAUSE");

        if (SharedPreferenceManager.getInstance().isShowRunning()) {
            Log.d(TAG, "SHOW ACTIVITY PAUSE IN BETWEEN SHOW");

            long chronometerTime = SystemClock.elapsedRealtime() - chronometerShow.getBase();
            Log.d(TAG, "Current show time - " + chronometerTime);

            // save all the states
            SharedPreferenceManager.getInstance().setShowSessionData(callerStateModelList, chronometerTime);
        }

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "SHOW ACTIVITY KILLED");
        super.onDestroy();
    }
}