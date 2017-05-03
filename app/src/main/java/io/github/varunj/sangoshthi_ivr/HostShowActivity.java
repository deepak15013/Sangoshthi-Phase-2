package io.github.varunj.sangoshthi_ivr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HostShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_show);

        populateHostShowList(HomeActivity.ashalist, HomeActivity.show_id, HomeActivity.time_of_air, HomeActivity.audio_name);
    }


    void populateHostShowList(final ArrayList<String> ashalist, final ArrayList<String> show_id, final ArrayList<String> time_of_air, final ArrayList<String> audio_name) {
        ListView list = (ListView)findViewById(R.id.host_show_list_master);
        HostShowListAdapter adapter = new HostShowListAdapter(this, ashalist, show_id, time_of_air, audio_name);
        adapter.setNotifyOnChange(true);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ShowActivity.class);
                i.putExtra("show_id", show_id.get(position));
                i.putExtra("time_of_air", time_of_air.get(position));
                i.putExtra("audio_name", audio_name.get(position));
                i.putExtra("ashalist", ashalist.get(position));
                startActivity(i);
            }
        });
    }
}
