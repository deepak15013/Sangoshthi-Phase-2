/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Button btnHostShow;
    private Button btnHomeNotifications;
    private Button btnTutorials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
    protected void onDestroy() {
        super.onDestroy();
        AMQPPublish.getInstance().interruptThreads();
    }
}
