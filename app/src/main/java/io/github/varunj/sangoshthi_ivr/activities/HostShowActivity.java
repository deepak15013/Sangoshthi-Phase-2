package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.ConstantUtil;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HostShowActivity extends AppCompatActivity {

    private static final String TAG = HostShowActivity.class.getSimpleName();

    private TextView tvShowTopic;
    private TextView tvShowDateOfAiring;
    private TextView tvShowTimeOfAiring;
    private LinearLayout llStartShow;
    private TextView tvChronometerStartShow;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_show);

        this.context = this;

        tvShowTopic = (TextView) findViewById(R.id.tv_show_topic);
        tvShowDateOfAiring = (TextView) findViewById(R.id.tv_show_date_of_airing);
        tvShowTimeOfAiring = (TextView) findViewById(R.id.tv_show_time_of_airing);
        llStartShow = (LinearLayout) findViewById(R.id.ll_start_show);
        tvChronometerStartShow = (TextView) findViewById(R.id.tv_chronometer_start_show);

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
                        String dateTime = jsonObject.getString("time_of_airing");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        Calendar showDateTime = Calendar.getInstance();
                        showDateTime.setTime(simpleDateFormat.parse(dateTime));

                        tvShowDateOfAiring.setText(String.format(Locale.ENGLISH, "%1$te %1$tb, %1$tY", showDateTime));
                        tvShowTimeOfAiring.setText(String.format(Locale.ENGLISH, "%1$tH : %1$tM", showDateTime));

                        /* compare time, if time passed then show start show button, if not then show chronometer countdown */
                        Calendar currentDateTime = Calendar.getInstance();
                        long diff = showDateTime.getTimeInMillis() - currentDateTime.getTimeInMillis();
                        if(diff <= ConstantUtil.ONE_HOUR_CLOCK) {
                            // 1 hour left to start show, move to next screen
                            llStartShow.setVisibility(View.VISIBLE);
                            tvChronometerStartShow.setVisibility(View.INVISIBLE);
                        } else {
                            // start show time not passed
                            llStartShow.setVisibility(View.INVISIBLE);
                            tvChronometerStartShow.setVisibility(View.VISIBLE);
                            // minimize 1 hour from the diff, because 1 hour before the startShow button gets enabled
                            diff = diff - ConstantUtil.ONE_HOUR_CLOCK;

                            Log.d(TAG, "diff - " + diff);
                            new CountDownTimer(diff, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    int hours = (int) (millisUntilFinished / ConstantUtil.ONE_HOUR_CLOCK);
                                    int mins = (int) (millisUntilFinished % ConstantUtil.ONE_HOUR_CLOCK);
                                    Log.d(TAG, "Show start in " + hours + " : " + mins);

                                    tvChronometerStartShow.setText(getString(R.string.tv_chronometer_start_show, hours, mins));
                                }

                                @Override
                                public void onFinish() {
                                    llStartShow.setVisibility(View.VISIBLE);
                                    tvChronometerStartShow.setVisibility(View.INVISIBLE);
                                }
                            }.start();


                        }
                    }

                    llStartShow.setVisibility(View.VISIBLE);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        llStartShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CallActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
