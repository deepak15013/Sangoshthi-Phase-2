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

    private String mobileNumber = null;
    private String cohortId = null;

    private final String PREF_IS_LOGGED_IN = "isLoggedIn";
    private final String PREF_USER_CONTACT_NUM = "userContactNum";
    private final String PREF_COHORT_ID = "cohort_id";

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

    public boolean setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return sharedPreferences != null && sharedPreferences.edit().putString(PREF_USER_CONTACT_NUM, mobileNumber).commit();
    }

    public String getMobileNumber() {
        if(this.mobileNumber == null)
            this.mobileNumber = sharedPreferences.getString(PREF_USER_CONTACT_NUM, "0123456789");
        return this.mobileNumber;
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

}
