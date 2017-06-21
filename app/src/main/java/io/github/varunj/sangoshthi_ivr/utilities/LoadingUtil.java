/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by Deepak on 17-06-2017.
 */

public class LoadingUtil {

    private static final String TAG = LoadingUtil.class.getSimpleName();

    private LoadingUtil() {

    }

    private static LoadingUtil instance;

    private static ProgressDialog progressDialog;
    private Context context;

    public static synchronized LoadingUtil getInstance() {
        if(instance == null) {
            instance = new LoadingUtil();
        }
        return instance;
    }

    public void showLoading(String message, Context context) {
        Log.d(TAG, "showing loading");
        this.context = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    public void hideLoading() {
        Log.d(TAG, "hideLoading");
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
