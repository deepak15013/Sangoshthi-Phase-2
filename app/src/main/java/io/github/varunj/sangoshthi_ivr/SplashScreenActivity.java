package io.github.varunj.sangoshthi_ivr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Varun on 12-Mar-17.
 */

public class SplashScreenActivity extends AppCompatActivity {

    public static String senderPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        senderPhoneNum = pref.getString("phoneNum", "0000000000");

        // AMQP stuff
        AMQPPublish.setupConnectionFactory();
        AMQPPublish.publishToAMQP();

        try {
            final JSONObject jsonObject = new JSONObject();
            //primary key: <broadcaster, show_name>
            jsonObject.put("objective", "app_launch_notify");
            jsonObject.put("broadcaster_phoneno", senderPhoneNum);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.queue.putLast(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }, 500);
    }


    @Override
    protected void onDestroy() {
        if (AMQPPublish.publishThread != null)
            AMQPPublish.publishThread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (AMQPPublish.publishThread != null)
            AMQPPublish.publishThread.interrupt();
        super.onBackPressed();
    }
}
