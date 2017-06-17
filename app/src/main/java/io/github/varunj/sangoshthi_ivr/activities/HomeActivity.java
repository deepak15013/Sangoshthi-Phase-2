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

    private Button btnHostShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnHostShow = (Button) findViewById(R.id.btn_host_show);
        if(SharedPreferenceManager.getInstance().isShowStarted()) {
            btnHostShow.setText(getString(R.string.home_show_running));
        } else {
            btnHostShow.setText(getString(R.string.home_host_show));
        }

        btnHostShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_host_show:
                Intent intent = new Intent(this, HostShowActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AMQPPublish.getInstance().interruptThreads();
    }
}
