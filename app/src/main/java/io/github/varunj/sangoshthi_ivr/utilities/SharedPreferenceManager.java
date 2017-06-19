package io.github.varunj.sangoshthi_ivr.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Deepak on 07-06-2017.
 */

public class SharedPreferenceManager {

    private static final String TAG = SharedPreferenceManager.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private static SharedPreferenceManager instance;

    private String broadcaster = null;
    private String cohortId = null;
    private String cohortSize = null;
    private String showId = null;
    private String conferenceName = null;

    /* only in local cache not in share preferences */
    private boolean callReceived = false;
    private boolean showRunning = false;

    private final String PREF_IS_LOGGED_IN = "is_logged_in";
    private final String PREF_BROADCASTER = "broadcaster";
    private final String PREF_COHORT_ID = "cohort_id";
    private final String PREF_COHORT_SIZE = "cohort_size";
    private final String PREF_SHOW_ID = "show_id";
    private final String PREF_CONFERENCE_NAME = "conference_name";
    private final String PREF_FEEDBACK = "feedback";
    private final String PREF_SHOW_CONTENT = "show_content";

    private SharedPreferenceManager() { }

    public static synchronized SharedPreferenceManager getInstance() {
        if(instance == null) {
            instance = new SharedPreferenceManager();
        }
        return instance;
    }

    public void init(Context context) {
        if(sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public boolean setSession() {
        return sharedPreferences != null && sharedPreferences.edit().putBoolean(PREF_IS_LOGGED_IN, true).commit();
    }

    public boolean getSession() {
        return sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    public boolean clearSession() {
        return sharedPreferences != null && sharedPreferences.edit().putBoolean(PREF_IS_LOGGED_IN, false).commit();
    }

    public boolean setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
        return sharedPreferences != null && sharedPreferences.edit().putString(PREF_BROADCASTER, broadcaster).commit();
    }

    public String getBroadcaster() {
        if(this.broadcaster == null)
            this.broadcaster = sharedPreferences.getString(PREF_BROADCASTER, "0123456789");
        return this.broadcaster;
    }

    public boolean setCohortId(String cohortId) {
        this.cohortId = cohortId;
        return sharedPreferences != null && sharedPreferences.edit().putString(PREF_COHORT_ID, cohortId).commit();
    }

    public String getCohortId() {
        if(this.cohortId == null)
            this.cohortId = sharedPreferences.getString(PREF_COHORT_ID, "-1");
        return this.cohortId;
    }

    public boolean setCohortSize(String cohortSize) {
        this.cohortSize = cohortSize;
        return sharedPreferences != null && sharedPreferences.edit().putString(PREF_COHORT_SIZE, cohortSize).commit();
    }

    public String getCohortSize() {
        if(this.cohortSize == null) {
            this.cohortSize = sharedPreferences.getString(PREF_COHORT_SIZE, "-1");
        }
        return this.cohortSize;
    }

    public boolean setShowId(String showId) {
        this.showId = showId;
        return sharedPreferences != null && sharedPreferences.edit().putString(PREF_SHOW_ID, showId).commit();
    }

    public String getShowId() {
        if(this.showId == null) {
            this.showId = sharedPreferences.getString(PREF_SHOW_ID, "");
        }
        return this.showId;
    }

    public boolean setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
        return sharedPreferences != null && sharedPreferences.edit().putString(PREF_CONFERENCE_NAME, conferenceName).commit();
    }

    public String getConferenceName() {
        if(this.conferenceName == null) {
            this.conferenceName = sharedPreferences.getString(PREF_CONFERENCE_NAME, "");
        }
        return this.conferenceName;
    }

    public boolean getFeedback() {
        return sharedPreferences.getBoolean(PREF_FEEDBACK, false);
    }

    public boolean setFeedback(boolean feedback) {
        return sharedPreferences != null && sharedPreferences.edit().putBoolean(PREF_FEEDBACK, feedback).commit();
    }

    public boolean getShowContent() {
        return sharedPreferences.getBoolean(PREF_SHOW_CONTENT, false);
    }

    public boolean setShowContent(boolean showContent) {
        return sharedPreferences != null && sharedPreferences.edit().putBoolean(PREF_SHOW_CONTENT, showContent).commit();
    }

    public boolean isCallReceived() {
        return callReceived;
    }

    public void setCallReceived(boolean callReceived) {
        this.callReceived = callReceived;
    }

    public boolean isShowRunning() {
        return showRunning;
    }

    public void setShowRunning(boolean showRunning) {
        this.showRunning = showRunning;
    }
}
