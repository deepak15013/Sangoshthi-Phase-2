package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import io.github.varunj.sangoshthi_ivr.R;

/**
 * Created by Varun on 12-Mar-17.
 */

public class SplashScreenActivity extends AppCompatActivity {

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }, 500);
    }



}
