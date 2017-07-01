/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

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
import io.github.varunj.sangoshthi_ivr.utilities.LoadingUtil;


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

        LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait),HostShowActivity.this);

        this.context = this;

        tvShowTopic = (TextView) findViewById(R.id.tv_show_topic);
        tvShowDateOfAiring = (TextView) findViewById(R.id.tv_show_date_of_airing);
        tvShowTimeOfAiring = (TextView) findViewById(R.id.tv_show_time_of_airing);
        llStartShow = (LinearLayout) findViewById(R.id.ll_start_show);
        tvChronometerStartShow = (TextView) findViewById(R.id.tv_chronometer_start_show);

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));
                    if(!jsonObject.getString("show_id").equals("none")) {
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
                            if(diff <= ConstantUtil.FIFTEEN_MINUTES_CLOCK) {
                                // 1 hour left to start show, move to next screen
                                llStartShow.setVisibility(View.VISIBLE);
                                tvChronometerStartShow.setVisibility(View.GONE);
                            } else {
                                // start show time not passed
                                llStartShow.setVisibility(View.GONE);
                                tvChronometerStartShow.setVisibility(View.VISIBLE);
                                // minimize 1 hour from the diff, because 1 hour before the startShow button gets enabled
                                diff = diff - ConstantUtil.FIFTEEN_MINUTES_CLOCK;

                                Log.d(TAG, "diff - " + diff);
                                new CountDownTimer(diff, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        int hours = (int) (millisUntilFinished / ConstantUtil.ONE_HOUR_CLOCK);
                                        millisUntilFinished = millisUntilFinished % ConstantUtil.ONE_HOUR_CLOCK;
                                        int mins = (int) (millisUntilFinished / (1000*60));
                                        millisUntilFinished = millisUntilFinished % (1000 * 60);
                                        int secs = (int) (millisUntilFinished / 1000);
                                        //Log.d(TAG, "Show start in " + hours + " : " + mins + " : " + secs);

                                        tvChronometerStartShow.setText(getString(R.string.tv_chronometer_start_show, hours, mins, secs));
                                    }

                                    @Override
                                    public void onFinish() {
                                        llStartShow.setVisibility(View.VISIBLE);
                                        tvChronometerStartShow.setVisibility(View.GONE);
                                    }
                                }.start();
                            }
                            LoadingUtil.getInstance().hideLoading();
                        }
                    } else {
                        Log.d(TAG, "no show present");
                        tvShowTopic.setText(getString(R.string.placeholder_tv_show_topic));
                        LoadingUtil.getInstance().hideLoading();
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        RequestMessageHelper.getInstance().getUpcomingShow();

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
