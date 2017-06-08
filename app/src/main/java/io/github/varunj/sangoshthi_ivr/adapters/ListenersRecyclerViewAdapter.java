package io.github.varunj.sangoshthi_ivr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;

/**
 * Created by Deepak on 08-06-2017.
 */

public class ListenersRecyclerViewAdapter extends RecyclerView.Adapter<ListenersRecyclerViewAdapter.MyViewHolder> {

    private List<String> moviesList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListenerNumber;
        public ImageView ivMuteUnmute;
        public ImageView ivQuestion;
        public ImageView ivOnline;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvListenerNumber = (TextView) itemView.findViewById(R.id.tv_listener_number);
            ivMuteUnmute = (ImageView) itemView.findViewById(R.id.iv_mute_unmute);
            ivQuestion = (ImageView) itemView.findViewById(R.id.iv_question);
            ivQuestion = (ImageView) itemView.findViewById(R.id.iv_online);
        }
    }

    public ListenersRecyclerViewAdapter(List<String> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listener_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvListenerNumber.setText(moviesList.get(position));
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
