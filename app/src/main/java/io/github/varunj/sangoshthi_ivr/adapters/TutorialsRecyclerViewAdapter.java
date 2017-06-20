/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;

/**
 * Created by Deepak on 20-06-2017.
 */

public class TutorialsRecyclerViewAdapter extends RecyclerView.Adapter<TutorialsRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = TutorialsRecyclerViewAdapter.class.getSimpleName();

    private Context context;
    private List<String> tutorialList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTutorial;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTutorial = (TextView) itemView.findViewById(R.id.tv_tutorial);
        }
    }

    public TutorialsRecyclerViewAdapter(Context context, List<String> tutorialList) {
        this.context = context;
        this.tutorialList = tutorialList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tutorial_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvTutorial.setText(tutorialList.get(position));
    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }
}
