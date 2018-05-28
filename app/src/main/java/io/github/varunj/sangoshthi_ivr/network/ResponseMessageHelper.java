/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.varunj.sangoshthi_ivr.models.ShowPlaybackModel;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

public class ResponseMessageHelper {

    private static final String TAG = ResponseMessageHelper.class.getSimpleName();

    private static final String OBJECTIVE = "objective";

    private static ResponseMessageHelper instance;
    private Handler handler;

    private ResponseMessageHelper() {
    }

    public static synchronized ResponseMessageHelper getInstance() {
        if (instance == null)
            instance = new ResponseMessageHelper();
        return instance;
    }

    public void subscribeToResponse(Handler handler) {
        this.handler = handler;
    }


    public void handle(JSONObject message) {
        try {
            switch (message.getString(OBJECTIVE)) {
                case "configuration_data":
                    handleConfigurationData(message);
                    break;

                case "upcoming_show_data":
                    handleUpcomingShowData(message);
                    break;

                case "start_show_response":
                    handleStartShowResponse(message);
                    break;

                case "conf_member_status":
                    handleConfMemberStatus(message);
                    break;

                // {"objective":"show_playback_metadata_response",
                // "media":[{"duration":7338,"type":"content","order":"1","name":"sample_content"},
                //          {"duration":4899,"type":"question","order":"2","name":"sample_question1"},
                //          {"duration":4899,"type":"answer","order":"3","name":"sample_answer1"},
                //          {"duration":3669,"type":"question","order":"4","name":"sample_question2"},
                //          {"duration":4899,"type":"answer","order":"5","name":"sample_answer2"},
                //          {"duration":4899,"type":"question","order":"6","name":"sample_question3"},
                //          {"duration":4899,"type":"answer","order":"7","name":"sample_answer3"}]}
                /*
                {"objective":"show_playback_metadata_response",
                    "media":[{"duration":135433,"type":"content","order":"1","name":"स्तनपान (सैंपल शो)"},
                             {"duration":4899,"type":"QA","order":"2","name":"QA1"},
                             {"duration":3669,"type":"QA","order":"3","name":"QA2"}]}
                 */
                case "show_playback_metadata_response":
                    handleShowPlaybackMetadataResponse(message);
                    break;

                case "media_stopped":
                    sendCallbackToActivity(message);
                    break;

                case "mute_unmute_ack":
                    sendCallbackToActivity(message);
                    break;

                // {"objective":"mute_unmute_response","info":"OK","listener_phoneno":"8527839396"}
                case "mute_unmute_response":
                    sendCallbackToActivity(message);
                    break;

                case "press_1_event":
                    sendCallbackToActivity(message);
                    break;

                case "notify":
                    sendCallbackToActivity(message);
                    break;

                case "end_show_call_ack":
                    sendCallbackToActivity(message);
                    break;

                case "get_show_id_for_gallery_ack":
                    sendCallbackToActivity(message);
                    break;

                case "broadcaster_content_listen_event_ack":
                    sendCallbackToActivity(message);
                    break;

                case "dial_listeners_response":
                    handleDialListenersResponse(message);
                    break;

                default:
                    Log.e(TAG, "Objective not matched " + message.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Response - {"objective":"configuration_data","cohort_id":"2"}
     *
     * @param message
     * @throws JSONException
     */
    private void handleConfigurationData(JSONObject message) {
        sendCallbackToActivity(message);
    }

    /**
     * Response - {"objective":"upcoming_show_data","topic":"play","show_id":"show_3","time_of_airing":"2017-06-20 14:20:59"}
     *
     * @param message
     * @throws JSONException
     */
    private void handleUpcomingShowData(JSONObject message) throws JSONException {
        SharedPreferenceManager.getInstance().setShowId(message.getString("show_id"));
        sendCallbackToActivity(message);
    }

    private void handleStartShowResponse(JSONObject message) throws JSONException {
        if (!message.getString("info").equals("FAIL")) {
            SharedPreferenceManager.getInstance().setConferenceName(message.getString("info"));
        }
    }

    private void handleConfMemberStatus(JSONObject message) throws JSONException {
        if (message.getString("task").equals("offline") || message.getString("task").equals("online")) {
            sendCallbackToActivity(message);
        }
    }

    private void handleShowPlaybackMetadataResponse(JSONObject message) throws JSONException {

        JSONArray jsonArray = message.getJSONArray("media");

        ArrayList<ShowPlaybackModel> showPlaybackModels = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            ShowPlaybackModel showPlaybackModel = new ShowPlaybackModel(ShowPlaybackModel.Type.valueOf(obj.getString("type")), obj.getInt("order"), obj.getString("duration"), obj.getString("name"));
            showPlaybackModels.add(showPlaybackModel);
            Log.d(TAG, showPlaybackModel.toString());
        }

        SharedPreferenceManager.getInstance().setShowPlaybackModels(showPlaybackModels);

        sendCallbackToActivity(message);
    }

    private void handleDialListenersResponse(JSONObject message) throws JSONException {
        String response = message.getString("cohort_members_phone_name_mapping");
        if (!response.equals("")) {
            SharedPreferenceManager.getInstance().setListenersData(response);
        }
    }

    private void sendCallbackToActivity(JSONObject message) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("msg", message.toString());
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

}
