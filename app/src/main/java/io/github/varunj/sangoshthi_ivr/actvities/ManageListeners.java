package io.github.varunj.sangoshthi_ivr.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.github.varunj.sangoshthi_ivr.R;

public class ManageListeners extends AppCompatActivity implements View.OnClickListener {

    private Button btnAddListeners;
    private Button btnDeleteListeners;
    private Button btnShowListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_listeners);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle(getResources().getString(R.string.create_show_listeners));

        btnAddListeners = (Button) findViewById(R.id.btn_add_listeners);
        btnDeleteListeners = (Button) findViewById(R.id.btn_show_listeners);
        btnShowListeners = (Button) findViewById(R.id.btn_show_listeners);

        btnAddListeners.setOnClickListener(this);
        btnDeleteListeners.setOnClickListener(this);
        btnShowListeners.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_listeners:
                Intent addListenersIntent = new Intent(this, AddListeners.class);
                startActivity(addListenersIntent);
                break;

            case R.id.btn_delete_listeners:
                break;

            case R.id.btn_show_listeners:
                break;
        }
    }
}
