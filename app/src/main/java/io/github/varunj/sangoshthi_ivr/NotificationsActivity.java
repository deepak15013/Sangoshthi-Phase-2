package io.github.varunj.sangoshthi_ivr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Varun on 12-Mar-17.
 */

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        populateNotificationsList(HomeActivity.notifications_message, HomeActivity.notifications_state);
    }

    void populateNotificationsList(final ArrayList<String> message, final ArrayList<String> state) {
        ListView list = (ListView)findViewById(R.id.notifications_list_master);
        NotificationsListAdapter adapter = new NotificationsListAdapter(this, message, state);
        adapter.setNotifyOnChange(true);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("new activity");
            }
        });
    }
}
