/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
 * This class displays the upcoming show details, and start show button is enabled only before 15 minutes of the show
 */
public class HostShowActivity extends AppCompatActivity {

    private static final String TAG = HostShowActivity.class.getSimpleName();

    private TextView tvShowTopic;
    private TextView tvShowDateOfAiring;
    private TextView tvShowTimeOfAiring;
    private LinearLayout llStartShow;
    private TextView tvChronometerStartShow;

    private ProgressDialog progressDialog;
    private Thread dismissThreadHostShow;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_show);

        showProgressBar();

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
                        if(jsonObject.getString("local_name").equals("none")) {
                            // show topic in english if local_name is none
                            tvShowTopic.setText(jsonObject.getString("topic"));
                        } else {
                            tvShowTopic.setText(jsonObject.getString("local_name"));
                        }

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
                            hideProgressBar();
                        }
                    } else {
                        Log.d(TAG, "no show present");
                        tvShowTopic.setText(getString(R.string.placeholder_tv_show_topic));
                        hideProgressBar();
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

    private void showProgressBar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progress_dialog_please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        dismissThreadHostShow = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ConstantUtil.FIVE_SECOND_CLOCK);

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlertDialog();
                        }
                    });
                    Log.d(TAG, "dismiss progress bar from thread HostShowActivity");
                } catch (InterruptedException e) {
                    Log.d(TAG, "thread interrupted " + e);
                }
            }
        });
        dismissThreadHostShow.start();
    }

    private void hideProgressBar() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if(dismissThreadHostShow != null) {
            dismissThreadHostShow.interrupt();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.toast_no_internet))
                .setCancelable(false);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
