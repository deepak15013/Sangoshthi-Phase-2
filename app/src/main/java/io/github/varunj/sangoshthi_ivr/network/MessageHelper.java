package io.github.varunj.sangoshthi_ivr.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by deepak on 03-06-2017.
 */

public class MessageHelper {

    private static MessageHelper instance;

    private MessageHelper() {

    }

    public static synchronized MessageHelper getInstance() {
        if(instance == null) {
            instance = new MessageHelper();
        }
        return instance;
    }

    /* All the JSON Packets */

    /**
     * App install
     *
     * Request - {"objective" : "app_install_notify", "broadcaster" : “9716517818”, “timestamp” : “2017-06-15 18:00:00” }
     * Response - {"objective" : "ack", "info" : “2” } // cohort id
     * Response - {"objective" : "ack", "info" : “-1” } // in case fetching of cohort id failed
     *
     * @param broadcaster
     */
    public void appInstallNotify(String broadcaster) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objective", "app_install_notify");
            jsonObject.put("broadcaster", broadcaster);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.getInstance().publishMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Upcoming Show Data (Host Show)
     *
     * Request - {"objective" : "get_upcoming_show", "broadcaster" : "9716517818", "cohort_id" : "3", "timestamp" : "erfs" }
     * Response - {"objective": "upcoming_show_data", "show_id": "show_3", "time_of_airing": "2017-06-15 18:00:00", "topic": "play and communication"}'
     *
     * @param broadcaster
     * @param cohort_id
     */
    public void getUpcomingShow(String broadcaster, String cohort_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objective", "get_upcoming_show");
            jsonObject.put("broadcaster", broadcaster);
            jsonObject.put("cohort_id", cohort_id);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.getInstance().publishMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start Show
     *
     * @param broadcaster
     * @param cohort_id
     * @param show_id
     */
    public void startShow(String broadcaster, String cohort_id, String show_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objective", "start_show");
            jsonObject.put("broadcaster", broadcaster);
            jsonObject.put("cohort_id", cohort_id);
            jsonObject.put("show_id", show_id);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.getInstance().publishMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dial Listeners
     *
     * @param broadcaster
     * @param cohort_id
     * @param show_id
     * @param conference_name
     */
    public void dialListeners(String broadcaster, String cohort_id, String show_id, String conference_name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objective", "dial_listeners");
            jsonObject.put("broadcaster", broadcaster);
            jsonObject.put("cohort_id", cohort_id);
            jsonObject.put("show_id", show_id);
            jsonObject.put("conference_name", conference_name);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.getInstance().publishMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mute
     *
     * @param broadcaster
     * @param cohort_id
     * @param show_id
     * @param conference_name
     * @param listener_phoneno
     */
    public void mute(String broadcaster, String cohort_id, String show_id, String conference_name, String listener_phoneno) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objective", "mute");
            jsonObject.put("broadcaster", broadcaster);
            jsonObject.put("cohort_id", cohort_id);
            jsonObject.put("show_id", show_id);
            jsonObject.put("conference_name", conference_name);
            jsonObject.put("listener_phoneno", listener_phoneno);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.getInstance().publishMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unmute
     *
     * @param broadcaster
     * @param cohort_id
     * @param show_id
     * @param conference_name
     * @param listener_phoneno
     */
    public void unmute(String broadcaster, String cohort_id, String show_id, String conference_name, String listener_phoneno) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objective", "mute");
            jsonObject.put("broadcaster", broadcaster);
            jsonObject.put("cohort_id", cohort_id);
            jsonObject.put("show_id", show_id);
            jsonObject.put("conference_name", conference_name);
            jsonObject.put("listener_phoneno", listener_phoneno);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.getInstance().publishMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
