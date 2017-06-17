package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HostShowActivity extends AppCompatActivity {

    private static final String TAG = HostShowActivity.class.getSimpleName();

    private TextView tvShowTopic;
    private TextView tvShowDateOfAiring;
    private TextView tvShowTimeOfAiring;
    private Button btnStartShow;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_show);

        this.context = this;

        tvShowTopic = (TextView) findViewById(R.id.tv_show_topic);
        tvShowDateOfAiring = (TextView) findViewById(R.id.tv_show_date_of_airing);
        tvShowTimeOfAiring = (TextView) findViewById(R.id.tv_show_time_of_airing);
        btnStartShow = (Button) findViewById(R.id.btn_start_show);

        RequestMessageHelper.getInstance().getUpcomingShow();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));
                    tvShowTopic.setText(jsonObject.getString("topic"));

                    // 2017-06-20 14:20:59
                    if(jsonObject.getString("time_of_airing") != null) {
                        String[] dateTime = jsonObject.getString("time_of_airing").split(" ");
                        tvShowDateOfAiring.setText(dateTime[0]);
                        tvShowTimeOfAiring.setText(dateTime[1]);
                    }

                    btnStartShow.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        btnStartShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CallActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
