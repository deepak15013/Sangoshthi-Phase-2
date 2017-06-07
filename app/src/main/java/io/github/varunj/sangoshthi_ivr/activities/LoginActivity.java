package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.utilities.SharedPreferenceManager;

/**
 * Created by Varun on 04-03-2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AMQPPublish.getInstance().setupConnectionFactory();
        AMQPPublish.getInstance().publishToAMQP();

        SharedPreferenceManager.getInstance().init(getApplicationContext());
        if(!SharedPreferenceManager.getInstance().getSession()) {
            // first time login, get phone number
            final EditText etPhone = (EditText) findViewById(R.id.et_phone);
            Button btnSignIn = (Button) findViewById(R.id.btn_sign_in);

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(etPhone != null && !etPhone.getText().toString().equals("")) {
                        SharedPreferenceManager.getInstance().setMobileNumber(etPhone.getText().toString());
                        SharedPreferenceManager.getInstance().setSession();

                        AMQPPublish.getInstance().subscribe(etPhone.getText().toString());
                        RequestMessageHelper.getInstance().appInstallNotify(etPhone.getText().toString());

                        startNextActivity();
                    }
                }
            });
        } else {
            // Login Procedure done, redirect to next activity
            startNextActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Closing pub and sub threads");
        AMQPPublish.getInstance().interruptThreads();
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
