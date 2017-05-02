package io.github.varunj.sangoshthi_ivr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Varun Jain on 16-Apr-17.
 */

public class NotificationsListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> message;
    private final ArrayList<String> state;

    public NotificationsListAdapter(Activity context, ArrayList<String> message, ArrayList<String> state) {
        super(context, R.layout.adapter_notifications, message);
        this.context = context;
        this.message = message;
        this.state = state;
    }

    public View getView(int position, View view , ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.adapter_notifications, null, true);

        TextView notifications_message = (TextView) rowView.findViewById(R.id.notifications_message);
        TextView notifications_state = (TextView) rowView.findViewById(R.id.notifications_state);

        notifications_message.setText(message.get(position));
        notifications_state.setText(state.get(position));

        return rowView;
    };
}