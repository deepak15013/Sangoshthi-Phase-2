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

/**
 * Created by Varun on 12-Mar-17.
 */

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ShowActivity.class.getSimpleName();

    private TextView tvNumOfListeners;
    private Chronometer chronometerShow;

    private Button showEndShow;
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

        showEndShow = (Button) findViewById(R.id.show_end_show);
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

                        case "mute_unmute_response":
                            handleMuteUnmuteResponse(jsonObject);
                            break;

                        case "press_1_event":
                            handlePress1Event(jsonObject);
                            break;

                        case "end_show_call_ack":
                            LoadingUtil.getInstance().hideLoading();
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

        SharedPreferenceManager.getInstance().setShowRunning(true);
    }

    private void handleConfMemberStatus(JSONObject jsonObject) throws JSONException {
        CallerStateModel callerStateModel = new CallerStateModel(
                jsonObject.getString("phoneno"),
                true,
                false);

        int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("phoneno"));
        if(callerId == -1) {
            callerStateModelList.add(callerStateModel);
        } else {
            if(jsonObject.getString("task").equals("online")) {
                // online, change the state
                callerStateModelList.set(callerId, callerStateModel);
            } else {
                // offline, remove the caller
                callerStateModelList.remove(callerId);
            }
        }

        tvNumOfListeners.setText(getString(R.string.tv_num_of_listeners, callerStateModelList.size(), SharedPreferenceManager.getInstance().getCohortSize()));
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

    private void handleMuteUnmuteResponse(JSONObject jsonObject) throws JSONException {
        if(jsonObject.getString("info").equals("OK")) {
            int callerId = matchPhoneExists(callerStateModelList, jsonObject.getString("listener_phoneno"));
            if(callerId != -1) {
                Toast.makeText(this, "State changed", Toast.LENGTH_SHORT).show();
                callerStateModelList.get(callerId).setMuteUnmuteDisabled(false);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "State not changed", Toast.LENGTH_SHORT).show();
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
        builder.setMessage(R.string.dialog_box_end_show_message)
                .setCancelable(false)
                .setTitle(R.string.dialog_box_end_show_title);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "End show ok");
                LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait), ShowActivity.this);
                RequestMessageHelper.getInstance().showEndShow();
                SharedPreferenceManager.getInstance().setShowRunning(false);
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
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_feedback_play));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_play))) {
            RequestMessageHelper.getInstance().playShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_pause));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_pause), null, null);
        } else if(showPlayPause.getText().equals(getResources().getString(R.string.btn_show_play_pause_content_pause))) {
            RequestMessageHelper.getInstance().pauseShowContent();
            showPlayPause.setText(getResources().getString(R.string.btn_show_play_pause_content_play));
            showPlayPause.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.btn_play), null, null);
        }
    }

    @Override
    public void onBackPressed() {
        handleEndShow();
    }
}