package io.github.varunj.sangoshthi_ivr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Varun on 12-Mar-17.
 */

public class TutorialsActivity extends AppCompatActivity {

    private String senderPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        senderPhoneNum = pref.getString("phoneNum", "0000000000");

        // AMQP stuff
        AMQPPublish.setupConnectionFactory();
        AMQPPublish.publishToAMQP();

        final Button tutorials_call = (Button) findViewById(R.id.tutorials_call);
        tutorials_call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    //primary key: <broadcaster, show_name>
                    jsonObject.put("objective", "call_for_help");
                    jsonObject.put("broadcaster_phoneno", senderPhoneNum);
                    jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                    AMQPPublish.queue.putLast(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Expect a call back soon.");
            }
        });

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