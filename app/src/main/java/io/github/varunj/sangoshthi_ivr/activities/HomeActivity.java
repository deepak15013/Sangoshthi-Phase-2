package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_host_show:
                Intent intentHostShow = new Intent(this, HostShowActivity.class);
                startActivity(intentHostShow);
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
