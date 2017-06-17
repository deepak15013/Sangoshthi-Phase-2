package io.github.varunj.sangoshthi_ivr.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.LoadingUtil;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * Created by Varun on 04-03-2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // constant for storing the runtime permission access for external storage media
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 44;
    private static final int MY_PERMISSION_READ_PHONE_STATE = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AMQPPublish.getInstance().setupConnectionFactory();
        AMQPPublish.getInstance().publishToAMQP();



        SharedPreferenceManager.getInstance().init(getApplicationContext());

        Log.d(TAG, "session - " + SharedPreferenceManager.getInstance().getSession());

        final EditText etPhone = (EditText) findViewById(R.id.et_phone);
        Button btnSignIn = (Button) findViewById(R.id.btn_sign_in);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPhone != null && !etPhone.getText().toString().equals("")) {
                    Log.d(TAG, "Sign in");
                    SharedPreferenceManager.getInstance().setBroadcaster(etPhone.getText().toString());

                    LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait), getApplicationContext());

                    AMQPPublish.getInstance().subscribe(etPhone.getText().toString());
                    RequestMessageHelper.getInstance().appInstallNotify(etPhone.getText().toString());
                }
            }
        });


            // Login Procedure done, redirect to next activity
            Log.d(TAG, "broadcaster - " + SharedPreferenceManager.getInstance().getBroadcaster());
            if(!SharedPreferenceManager.getInstance().getBroadcaster().equals("0123456789")) {
                Log.d(TAG, "start subscriber");
                LoadingUtil.getInstance().showLoading(getString(R.string.progress_dialog_please_wait), getApplicationContext());
                AMQPPublish.getInstance().subscribe(SharedPreferenceManager.getInstance().getBroadcaster());
                RequestMessageHelper.getInstance().appInstallNotify(SharedPreferenceManager.getInstance().getBroadcaster());

            } else {
                Log.d(TAG, "number equals 0123456789");
            }


        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));

                    if(jsonObject.getString("objective").equals("configuration_data")) {
                        if(jsonObject.getString("cohort_id").equals("-1") || jsonObject.getString("cohort_size").equals("-1")) {
                            // show error message
                            Toast.makeText(getApplicationContext(), getString(R.string.error_phone_not_registered), Toast.LENGTH_SHORT).show();
                        } else {
                            // everything ok
                            SharedPreferenceManager.getInstance().setSession();

                            SharedPreferenceManager.getInstance().setCohortId(jsonObject.getString("cohort_id"));
                            SharedPreferenceManager.getInstance().setCohortSize(jsonObject.getString("cohort_size"));
                            startNextActivity();
                        }
                        LoadingUtil.getInstance().hideLoading();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "" + e);
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        Log.d(TAG, "subscribe to response");

    }

    @Override
    protected void onResume() {
        super.onResume();
        /* First check for permission for external storage, Uses runtime permission */
        checkAndGetRuntimePermissions();
    }

    /**
     * Check if permission for external storage available or not,
     * if not available, then get the permission from user.
     */
    private void checkAndGetRuntimePermissions() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSION_READ_PHONE_STATE);
        }
    }

    /**
     * This is the callback method after the user permission has been asked for.
     *
     * @param requestCode
     *                  the constant for permission request
     * @param permissions
     * @param grantResults
     *                  the result for the permissions asked
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted for write_external_storage");
                }
                else {
                    Toast.makeText(this, "Storage Access Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            case MY_PERMISSION_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted for record_audio");
                }
                else {
                    Toast.makeText(this, "Record Audio Access Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
