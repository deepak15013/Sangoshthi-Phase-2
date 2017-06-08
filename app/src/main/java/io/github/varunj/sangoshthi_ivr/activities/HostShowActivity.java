package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HostShowActivity extends AppCompatActivity {

    private static final String TAG = HostShowActivity.class.getSimpleName();

    private TextView tvShowDetails;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_show);

        this.context = this;

        tvShowDetails = (TextView) findViewById(R.id.tv_show_details);

        RequestMessageHelper.getInstance().getUpcomingShow();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                tvShowDetails.setText(msg.getData().getString("msg"));
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        tvShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowActivity.class);
                startActivity(intent);
            }
        });

//        populateHostShowList(HomeActivity.ashalist, HomeActivity.show_id, HomeActivity.time_of_air, HomeActivity.audio_name);
    }


   /* void populateHostShowList(final ArrayList<String> ashalist, final ArrayList<String> show_id, final ArrayList<String> time_of_air, final ArrayList<String> audio_name) {
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
    }*/
}
