package io.github.varunj.sangoshthi_ivr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Varun Jain on 16-Apr-17.
 */

public class HostShowListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> show_id;
    private final ArrayList<String> time_of_air;
    private final ArrayList<String> audio_name;
    private final ArrayList<String> ashalist;


    public HostShowListAdapter(Activity context, ArrayList<String> ashalist, ArrayList<String> show_id, ArrayList<String> time_of_air, ArrayList<String> audio_name) {
        super(context, R.layout.adapter_host_show_list, ashalist);
        this.context = context;
        this.ashalist = ashalist;
        this.show_id = show_id;
        this.time_of_air = time_of_air;
        this.audio_name = audio_name;
    }

    public View getView(int position, View view , ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.adapter_host_show_list, null, true);

        TextView host_show_list_asha_list = (TextView) rowView.findViewById(R.id.host_show_list_asha_list);
        TextView host_show_list_show_id = (TextView) rowView.findViewById(R.id.host_show_list_show_id);
        TextView host_show_list_time_of_air = (TextView) rowView.findViewById(R.id.host_show_list_time_of_air);
        TextView host_show_list_audio_name = (TextView) rowView.findViewById(R.id.host_show_list_audio_name);

        host_show_list_asha_list.setText(ashalist.get(position));
        host_show_list_show_id.setText(show_id.get(position));
        host_show_list_time_of_air.setText(time_of_air.get(position));
        host_show_list_audio_name.setText(audio_name.get(position));

        return rowView;
    };
}