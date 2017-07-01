/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.callhandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.github.varunj.sangoshthi_ivr.activities.CallActivity;
import io.github.varunj.sangoshthi_ivr.activities.HomeActivity;
import io.github.varunj.sangoshthi_ivr.activities.ShowActivity;
import io.github.varunj.sangoshthi_ivr.utilities.ConstantUtil;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

public class CallReceiver extends PhoneCallReceiver {

    private static final String TAG = CallReceiver.class.getSimpleName();

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        super.onCallStateChanged(context, state, number);

        Log.i(TAG, "state changed: " + state + " number: " + number);

        if(state == 0 && number != null) {
            if(number.contains(ConstantUtil.SERVER_NUM)) {
                Log.d(TAG, "call disconnected");
                SharedPreferenceManager.getInstance().setCallReceived(false);
                if(CallActivity.progressDialog != null && CallActivity.progressDialog.isShowing()) {
                    CallActivity.progressDialog.dismiss();
                }
                if(ShowActivity.progressDialog != null && ShowActivity.progressDialog != null && SharedPreferenceManager.getInstance().isShowRunning()) {
                    ShowActivity.progressDialog.show();
                } else {
                    if(!SharedPreferenceManager.getInstance().isShowUpdateStatus()) {
                        Intent startHomeActivity = new Intent(context, HomeActivity.class);
                        startHomeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(startHomeActivity);
                        Log.d(TAG, "Not in show activity");
                    } else {
                        Log.d(TAG, "inside show activity after end show");
                    }
                }
            }
        }

        // incoming call status = 1
        if(state == 1 && number != null) {
            if(number.contains(ConstantUtil.SERVER_NUM)) {
                // incoming call
            }
        }

        if(state == 2 && number != null) {
            if(number.contains(ConstantUtil.SERVER_NUM)) {
                SharedPreferenceManager.getInstance().setCallReceived(true);
                if(CallActivity.progressDialog != null && CallActivity.progressDialog.isShowing()) {
                    CallActivity.progressDialog.dismiss();
                }
                if(ShowActivity.progressDialog != null && ShowActivity.progressDialog.isShowing() && SharedPreferenceManager.getInstance().isShowRunning()) {
                    ShowActivity.progressDialog.dismiss();
                }
            }
        }
    }
}
