/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.models.CallerStateModel;
import io.github.varunj.sangoshthi_ivr.models.ShowPlaybackModel;
import io.github.varunj.sangoshthi_ivr.models.TutorialListenModel;

public class SharedPreferenceManager {

    private static final String TAG = SharedPreferenceManager.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private static SharedPreferenceManager instance;

    private String broadcaster = null;
    private String cohortId = null;
    private String cohortSize = null;
    private String showId = null;
    private String conferenceName = null;

    private String tutorialsActivityData = null;
    private String tutorialListenData = null;
    private List<TutorialListenModel> tutorialListenModelList;
    private JSONObject listenersData;
    private final String PREF_SHOW_PLAYBACK_MODEL = "show_playback_model";

    /* only in local cache not in share preferences */
    private boolean callReceived = false;
    private boolean showRunning = false;
    private boolean showUpdateStatus = false;

    private final String PREF_IS_LOGGED_IN = "is_logged_in";
    private final String PREF_BROADCASTER = "broadcaster";
    private final String PREF_COHORT_ID = "cohort_id";
    private final String PREF_COHORT_SIZE = "cohort_size";
    private final String PREF_SHOW_ID = "show_id";
    private final String PREF_CONFERENCE_NAME = "conference_name";
    private final String PREF_TUTORIALS_ACTIVITY_DATA = "tutorials_activity_data";
    private final String PREF_TUTORIAL_LISTEN_DATA = "tutorial_listen_data";
    private final String PREF_SHOW_RUNNING = "show_running";
    private final String PREF_SHOW_SESSION_DATA = "show_session_data";
    private final String PREF_SHOW_CHRONOMETER_TIME = "show_chronometer_time";
    private final String PREF_LISTENERS_DATA = "listeners_data";
    private ArrayList<ShowPlaybackModel> showPlaybackModels;

    private SharedPreferenceManager() {
    }

    public static synchronized SharedPreferenceManager getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public void setSession() {
        sharedPreferences.edit().putBoolean(PREF_IS_LOGGED_IN, true).apply();
    }

    public boolean getSession() {
        return sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        sharedPreferences.edit().putBoolean(PREF_IS_LOGGED_IN, false).apply();
    }

    public String getBroadcaster() {
        if (this.broadcaster == null)
            this.broadcaster = sharedPreferences.getString(PREF_BROADCASTER, "0123456789");
        return this.broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
        sharedPreferences.edit().putString(PREF_BROADCASTER, broadcaster).apply();
    }

    public String getCohortId() {
        if (this.cohortId == null)
            this.cohortId = sharedPreferences.getString(PREF_COHORT_ID, "-1");
        return this.cohortId;
    }

    public void setCohortId(String cohortId) {
        this.cohortId = cohortId;
        sharedPreferences.edit().putString(PREF_COHORT_ID, cohortId).apply();
    }

    public String getCohortSize() {
        if (this.cohortSize == null) {
            this.cohortSize = sharedPreferences.getString(PREF_COHORT_SIZE, "-1");
        }
        return this.cohortSize;
    }

    public void setCohortSize(String cohortSize) {
        this.cohortSize = cohortSize;
        sharedPreferences.edit().putString(PREF_COHORT_SIZE, cohortSize).apply();
    }

    public String getShowId() {
        if (this.showId == null) {
            this.showId = sharedPreferences.getString(PREF_SHOW_ID, "");
        }
        return this.showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
        sharedPreferences.edit().putString(PREF_SHOW_ID, showId).apply();
    }

    public String getConferenceName() {
        if (this.conferenceName == null) {
            this.conferenceName = sharedPreferences.getString(PREF_CONFERENCE_NAME, "");
        }
        return this.conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
        sharedPreferences.edit().putString(PREF_CONFERENCE_NAME, conferenceName).apply();
    }

    public ArrayList<ShowPlaybackModel> getShowPlaybackModels() {
        if (this.showPlaybackModels == null) {

        }

        return this.showPlaybackModels;
    }

    public void setShowPlaybackModels(ArrayList<ShowPlaybackModel> showPlaybackModels) {
        final Gson gson = new Gson();

        this.showPlaybackModels = showPlaybackModels;
        sharedPreferences.edit().putString(PREF_SHOW_PLAYBACK_MODEL, gson.toJson(showPlaybackModels)).apply();
    }

    public boolean isCallReceived() {
        return callReceived;
    }

    public void setCallReceived(boolean callReceived) {
        this.callReceived = callReceived;
    }

    public boolean isShowRunning() {
        if (sharedPreferences != null)
            this.showRunning = sharedPreferences.getBoolean(PREF_SHOW_RUNNING, showRunning);
        return showRunning;
    }

    public void setShowRunning(boolean showRunning) {
        this.showRunning = showRunning;
        sharedPreferences.edit().putBoolean(PREF_SHOW_RUNNING, showRunning).apply();
    }

    public boolean isShowUpdateStatus() {
        return showUpdateStatus;
    }

    public void setShowUpdateStatus(boolean showUpdateStatus) {
        this.showUpdateStatus = showUpdateStatus;
    }

    public String getTutorialsActivityData() {
        if (this.tutorialsActivityData == null)
            this.tutorialsActivityData = sharedPreferences.getString(PREF_TUTORIALS_ACTIVITY_DATA, "NONE");
        return this.tutorialsActivityData;
    }

    public void setTutorialsActivityData(String tutorialsActivityData) {
        this.tutorialsActivityData = tutorialsActivityData;
        sharedPreferences.edit().putString(PREF_TUTORIALS_ACTIVITY_DATA, tutorialsActivityData).apply();
    }

    public String getTutorialListenData() {
        if (this.tutorialListenData == null)
            this.tutorialListenData = sharedPreferences.getString(PREF_TUTORIAL_LISTEN_DATA, "NONE");
        return this.tutorialListenData;
    }

    public void setTutorialListenData(String tutorialListenData) {
        this.tutorialListenData = tutorialListenData;
        sharedPreferences.edit().putString(PREF_TUTORIAL_LISTEN_DATA, tutorialListenData).apply();
    }

    public void addTutorialListenData(TutorialListenModel tutorialListenModel) {
        final Gson gson = new Gson();
        if (tutorialListenModelList == null) {
            tutorialListenModelList = new ArrayList<>();
            String json = SharedPreferenceManager.getInstance().getTutorialListenData();
            if (!json.equals("NONE")) {
                Type type = new TypeToken<List<TutorialListenModel>>() {
                }.getType();
                tutorialListenModelList = gson.fromJson(json, type);
            }
        }
        tutorialListenModel.setPacket_id(tutorialListenModelList.size());
        tutorialListenModelList.add(tutorialListenModel);

        setTutorialListenData(gson.toJson(tutorialListenModelList));
    }

    public void setTutorialListenModelList(List<TutorialListenModel> tutorialListenModelList) {
        this.tutorialListenModelList = tutorialListenModelList;
        final Gson gson = new Gson();
        setTutorialListenData(gson.toJson(tutorialListenModelList));
    }

    public void setListenersData(String listenersData) {
        try {
            Log.d(TAG, "listeners data - " + listenersData);
            this.listenersData = new JSONObject(listenersData);
            sharedPreferences.edit().putString(PREF_LISTENERS_DATA, listenersData).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Set Listeners data expection - " + e);
        }
    }

    public String getListenersData(String phoneNum) {
        try {
            if (this.listenersData == null)
                listenersData = new JSONObject(sharedPreferences.getString(PREF_LISTENERS_DATA, ""));

            return listenersData.getString(phoneNum);
        } catch (JSONException e) {
            Log.e(TAG, "listeners data exception - " + e);
        }
        return phoneNum;
    }

    public void setShowSessionData(List<CallerStateModel> callerStateModelList, long chronomterTime) {

        Log.d(TAG, "saving data - " + callerStateModelList.toString());
        Log.d(TAG, "saving time - " + chronomterTime);

        final Gson gson = new Gson();

        String json = gson.toJson(callerStateModelList);

        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(PREF_SHOW_SESSION_DATA, json).apply();
            sharedPreferences.edit().putLong(PREF_SHOW_CHRONOMETER_TIME, chronomterTime).apply();
        }
    }

    public List<CallerStateModel> getShowSessionData() {
        final Gson gson = new Gson();

        if (sharedPreferences != null) {
            String json = sharedPreferences.getString(PREF_SHOW_SESSION_DATA, "NONE");
            Log.d(TAG, "show_session_data - " + json);
            if (!json.equals("NONE")) {
                Type type = new TypeToken<List<CallerStateModel>>() {
                }.getType();
                return gson.fromJson(json, type);
            }
        }
        return null;
    }

    public long getShowChronometerTime() {
        if (sharedPreferences != null)
            return sharedPreferences.getLong(PREF_SHOW_CHRONOMETER_TIME, 0);
        return 0;
    }
}