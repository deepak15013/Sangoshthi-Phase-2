/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.ConstantUtil;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

public class CallActivity extends AppCompatActivity {

    private static final String TAG = CallActivity.class.getSimpleName();
    private Context context;

    private ImageButton btnCall;
    private TextView tvCall;

    public static ProgressDialog progressDialog;
    public static Thread dismissThreadCall;

    private boolean callStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        this.context = this;

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));

                    if(jsonObject.getString("objective").equals("start_show_response")) {
                        switch (jsonObject.getString("info")) {
                            case "FAIL":
                                Toast.makeText(context, "calling failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                break;

                            default:
                                Log.d(TAG, "start_show_response" + jsonObject.getString("info"));
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "" + e);
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        btnCall = (ImageButton) findViewById(R.id.btn_call);
        tvCall = (TextView) findViewById(R.id.tv_call);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvCall.getText().equals(getResources().getString(R.string.btn_call_broadcaster))) {
                    RequestMessageHelper.getInstance().startShow();
                    progressDialog.show();
                    callStarted = true;
                    if(dismissThreadCall != null)
                        dismissThreadCall.start();

                } else if(tvCall.getText().equals(getResources().getString(R.string.btn_call_listeners))) {
                    RequestMessageHelper.getInstance().dialListeners();
                    Toast.makeText(CallActivity.this, "calling listeners", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ShowActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_calling_broadcaster));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        dismissThreadCall = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ConstantUtil.THIRTY_SECOND_CLOCK);

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Log.d(TAG, "dismiss progress bar from thread CallActivity");
                } catch (InterruptedException e) {
                    Log.d(TAG, "thread interrupted " + e);
                }
            }
        });

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d(TAG, "Call received: " + SharedPreferenceManager.getInstance().isCallReceived());
                dismissThreadCall.interrupt();
                if(SharedPreferenceManager.getInstance().isCallReceived()) {
                    // call received, change button to call listeners
                    tvCall.setText(getResources().getString(R.string.btn_call_listeners));
                    btnCall.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.call_listeners));
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(callStarted) {
            Toast.makeText(this, getString(R.string.toast_back_disabled), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
