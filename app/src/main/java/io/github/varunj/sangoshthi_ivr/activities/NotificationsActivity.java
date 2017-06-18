package io.github.varunj.sangoshthi_ivr.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.adapters.NotificationsRecyclerViewAdapter;
import io.github.varunj.sangoshthi_ivr.models.NotificationModel;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;
import io.github.varunj.sangoshthi_ivr.network.ResponseMessageHelper;

/**
 * Created by Varun on 12-Mar-17.
 */

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = NotificationsActivity.class.getSimpleName();

    private RecyclerView rvNotifications;
    private NotificationsRecyclerViewAdapter mAdapter;

    private List<NotificationModel> notificationModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.d(TAG, "Message received: " + msg.getData().getString("msg"));
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("msg"));

                    switch (jsonObject.getString("objective")) {
                        case "notify":
                            handleNotify(jsonObject);
                            break;

                        default:
                            Log.d(TAG, "objective not matched " + jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ResponseMessageHelper.getInstance().subscribeToResponse(incomingMessageHandler);

        RequestMessageHelper.getInstance().getNotifications();

        notificationModelList = new ArrayList<>();

        rvNotifications = (RecyclerView) findViewById(R.id.rv_notifications);
        mAdapter = new NotificationsRecyclerViewAdapter(this, notificationModelList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setItemAnimator(new DefaultItemAnimator());
        rvNotifications.setAdapter(mAdapter);

    }

    private void handleNotify(JSONObject jsonObject) throws JSONException {
        String notifications = jsonObject.getString("info");
        Log.d(TAG, "notifications - " + notifications);
        if(notifications != null && !notifications.equals("") && !notifications.equals(" ")) {
            Gson gson = new Gson();
            NotificationModel notificationModel = gson.fromJson(notifications, NotificationModel.class);
            Log.d(TAG, "notificationModel - " + notificationModel);
            if(notificationModel != null) {
                notificationModelList.add(notificationModel);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
