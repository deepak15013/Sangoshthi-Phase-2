package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;
import io.github.varunj.sangoshthi_ivr.network.MessageHelper;

/**
 * Created by Varun on 04-03-2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText etPhone;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AMQPPublish.getInstance().setupConnectionFactory();
        AMQPPublish.getInstance().publishToAMQP();

        etPhone = (EditText) findViewById(R.id.et_phone);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AMQPPublish.getInstance().subscribe(etPhone.getText().toString());
                MessageHelper.getInstance().appInstallNotify(etPhone.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Closing pub and sub threads");
        AMQPPublish.getInstance().interruptThreads();
    }
}
