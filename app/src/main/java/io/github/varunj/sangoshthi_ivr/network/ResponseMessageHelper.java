package io.github.varunj.sangoshthi_ivr.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * Created by Deepak on 07-06-2017.
 */

public class ResponseMessageHelper {

    private static final String TAG = ResponseMessageHelper.class.getSimpleName();

    private static final String OBJECTIVE = "objective";

    private static ResponseMessageHelper instance;

    private ResponseMessageHelper() {}

    public static synchronized ResponseMessageHelper getInstance() {
        if(instance == null)
            instance = new ResponseMessageHelper();
        return instance;
    }


    public void handle(JSONObject message) {
        try {
            switch (message.getString(OBJECTIVE)) {
                case "configuration_data":
                    handleConfigurationData(message);
                    break;

                default:
                    Log.e(TAG, "Objective not matched " + message.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void handleConfigurationData(JSONObject message) throws JSONException {
        SharedPreferenceManager.getInstance().setCohortId(message.getString("cohort_id"));
    }
}
