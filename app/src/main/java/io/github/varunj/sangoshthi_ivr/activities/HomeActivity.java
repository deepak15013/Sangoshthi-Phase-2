/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.callhandler.CallReceiver;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * This is main HomeActivity, Phone state listener is registered here and also de-registered on destroyed.
 * Life Cycle of the stack is mostly dependent on this class and if any thing happens then the call stack is
 * cleared and moved to HomeActivity as the main Activity.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Button btnHostShow;
    private Button btnHomeNotifications;
    private Button btnTutorials;

    private CallReceiver callReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "Register Receiver");
        callReceiver = new CallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(callReceiver, intentFilter);

        btnHostShow = (Button) findViewById(R.id.btn_host_show);
        btnHostShow.setOnClickListener(this);

        btnHomeNotifications = (Button) findViewById(R.id.btn_notifications);
        btnHomeNotifications.setOnClickListener(this);

        btnTutorials = (Button) findViewById(R.id.btn_tutorials);
        btnTutorials.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(SharedPreferenceManager.getInstance().isShowUpdateStatus()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_box_update_show_status_title)
                    .setMessage(R.string.dialog_box_update_show_status_message)
                    .setCancelable(false);

            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RequestMessageHelper.getInstance().updateShowStatus();
                    SharedPreferenceManager.getInstance().setShowUpdateStatus(false);
                }
            });

            builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    SharedPreferenceManager.getInstance().setShowUpdateStatus(false);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if(SharedPreferenceManager.getInstance().isShowRunning()) {
            // if show is running then resume the already running show and send the resume packet
            Log.d(TAG, "Show already running, redirecting to ShowActivity");
            Intent intentResumeShow = new Intent(this, ShowActivity.class);
            startActivity(intentResumeShow);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_host_show:
                Intent intentHostShow = new Intent(this, HostShowActivity.class);
                startActivity(intentHostShow);
                break;

            case R.id.btn_notifications:
                Intent intentNotifications = new Intent(this, NotificationsActivity.class);
                startActivity(intentNotifications);
                break;

            case R.id.btn_tutorials:
                Intent intentTutorials = new Intent(this, TutorialsActivity.class);
                startActivity(intentTutorials);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Unregister Receiver");
        unregisterReceiver(callReceiver);
    }

}
