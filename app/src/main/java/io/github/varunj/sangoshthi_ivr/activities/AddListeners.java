/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import io.github.varunj.sangoshthi_ivr.R;

public class AddListeners extends AppCompatActivity {

    private EditText etListenerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listeners);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle(getResources().getString(R.string.btn_add_listeners));

        etListenerNumber = (EditText) findViewById(R.id.et_listener_number);

        assert etListenerNumber != null;
        etListenerNumber.requestFocus();

    }
}
