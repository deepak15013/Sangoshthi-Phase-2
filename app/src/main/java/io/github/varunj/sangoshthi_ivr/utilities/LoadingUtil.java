/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.github.varunj.sangoshthi_ivr.R;


public class LoadingUtil {

    private static final String TAG = LoadingUtil.class.getSimpleName();

    private LoadingUtil() {

    }

    private static LoadingUtil instance;

    private Thread dismissThread;
    private static ProgressDialog progressDialog;
    private Context context;

    public static synchronized LoadingUtil getInstance() {
        if(instance == null) {
            instance = new LoadingUtil();
        }
        return instance;
    }

    public void showLoading(String message, final Activity context) {
        Log.d(TAG, "showing loading");
        this.context = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

        dismissThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ConstantUtil.FIVE_SECOND_CLOCK);

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d(TAG, "dismiss progress bar from thread");
                } catch (InterruptedException e) {
                    Log.d(TAG, "thread interrupted " + e);
                }
            }
        });
        dismissThread.start();
    }

    public void hideLoading() {
        Log.d(TAG, "hideLoading");
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if(dismissThread != null) {
            dismissThread.interrupt();
        }
    }

}
