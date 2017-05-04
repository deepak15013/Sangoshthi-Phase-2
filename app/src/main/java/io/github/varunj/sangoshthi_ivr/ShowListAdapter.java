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

public class ShowListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> ashalist;
    private final ArrayList<Integer> img1;
    private final ArrayList<Integer> img2;
    private final ArrayList<Integer> img3;
    private final ArrayList<String> ashapoll;

    public ShowListAdapter(Activity context, ArrayList<String> ashalist, ArrayList<Integer> img1, ArrayList<Integer> img2, ArrayList<Integer> img3, ArrayList<String> ashapoll) {
        super(context, R.layout.adapter_host_show_list, ashalist);
        this.context = context;
        this.ashalist = ashalist;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.ashapoll = ashapoll;
    }

    public View getView(int position, View view , ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.adapter_show, null, true);

        TextView groupvideo_ashalist_name = (TextView) rowView.findViewById(R.id.show_ashalist_ashaname);
        ImageView groupvideo_ashalist_online = (ImageView) rowView.findViewById(R.id.show_ashalist_active);
        ImageView groupvideo_ashalist_query = (ImageView) rowView.findViewById(R.id.show_ashalist_query);
        ImageView groupvideo_ashalist_mute = (ImageView) rowView.findViewById(R.id.show_ashalist_mute);
        TextView show_ashalist_poll = (TextView) rowView.findViewById(R.id.show_ashalist_poll);

        groupvideo_ashalist_name.setText(ashalist.get(position));
        groupvideo_ashalist_online.setImageResource(img1.get(position));
        groupvideo_ashalist_query.setImageResource(img2.get(position));
        groupvideo_ashalist_mute.setImageResource(img3.get(position));
        show_ashalist_poll.setText(ashapoll.get(position));
        return rowView;
    };
}