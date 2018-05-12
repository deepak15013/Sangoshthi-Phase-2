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
import io.github.varunj.sangoshthi_ivr.activities.LoginActivity;
import io.github.varunj.sangoshthi_ivr.network.ConnectivityReceiver;


public class LoadingUtil {

    private static final String TAG = LoadingUtil.class.getSimpleName();
    private static LoadingUtil instance;
    private static ProgressDialog progressDialog;
    private Thread dismissThreadLoading;
    private Context context;

    private LoadingUtil() {

    }

    public static synchronized LoadingUtil getInstance() {
        if (instance == null) {
            instance = new LoadingUtil();
        }
        return instance;
    }

    public void showLoading(String message, final Activity context) {
        Log.d(TAG, "showing loading, isConnected status - " + ConnectivityReceiver.isConnected());
        this.context = context;

        // if internet is connected show loading screen
        if (ConnectivityReceiver.isConnected()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);

            progressDialog.show();

            dismissThreadLoading = new Thread(new Runnable() {
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
                                Toast.makeText(context, context.getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d(TAG, "dismiss progress bar from thread LoadingUtil");

                        checkLogin();
                    } catch (InterruptedException e) {
                        Log.d(TAG, "thread interrupted " + e);
                    }
                }
            });
            dismissThreadLoading.start();
        } else {
            // show toast that there is no internet
            Toast.makeText(context, context.getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
            checkLogin();
        }
    }

    // If user has already logged in once, than without internet connection also he can login
    private void checkLogin() {
        if (context instanceof LoginActivity) {
            if (SharedPreferenceManager.getInstance().getSession()) {
                ((LoginActivity) context).startNextActivity();
            }
        }
    }

    public void hideLoading() {
        Log.d(TAG, "hideLoading");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (dismissThreadLoading != null) {
            dismissThreadLoading.interrupt();
        }
    }

    /*private void showAlertDialog() {
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
    }*/

}
