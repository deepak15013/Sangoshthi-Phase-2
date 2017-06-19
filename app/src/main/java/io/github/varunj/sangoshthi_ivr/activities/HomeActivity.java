package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Button btnHostShow;
    private Button btnHomeNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnHostShow = (Button) findViewById(R.id.btn_host_show);
        btnHostShow.setOnClickListener(this);

        btnHomeNotifications = (Button) findViewById(R.id.btn_home_notifications);
        btnHomeNotifications.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SharedPreferenceManager.getInstance().isShowStarted()) {
            btnHostShow.setText(getString(R.string.home_show_running));
        } else {
            btnHostShow.setText(getString(R.string.home_host_show));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_host_show:
                if(btnHostShow.getText().equals(getString(R.string.home_show_running))) {
                    Intent intentShow = new Intent(this, ShowActivity.class);
                    startActivity(intentShow);
                } else {
                    Intent intentHostShow = new Intent(this, HostShowActivity.class);
                    startActivity(intentHostShow);
                }
                break;

            case R.id.btn_home_notifications:
                Intent intentNotifications = new Intent(this, NotificationsActivity.class);
                startActivity(intentNotifications);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AMQPPublish.getInstance().interruptThreads();
    }
}
