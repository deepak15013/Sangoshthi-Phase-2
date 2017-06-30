/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.fragments.PlaybackFragment;
import io.github.varunj.sangoshthi_ivr.models.RecordingItem;
import io.github.varunj.sangoshthi_ivr.models.TutorialModel;

/**
 * Created by Deepak on 20-06-2017.
 */

public class TutorialsRecyclerViewAdapter extends RecyclerView.Adapter<TutorialsRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = TutorialsRecyclerViewAdapter.class.getSimpleName();

    private Context context;
    private List<TutorialModel> tutorialList;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvTutorial;
        TextView tvTutorial;
        ImageView ivLockUnlock;

        MyViewHolder(View itemView) {
            super(itemView);
            cvTutorial = (CardView) itemView.findViewById(R.id.cv_tutorial);
            tvTutorial = (TextView) itemView.findViewById(R.id.tv_tutorial);
            ivLockUnlock = (ImageView) itemView.findViewById(R.id.iv_lock_unlock);
        }
    }

    public TutorialsRecyclerViewAdapter(Context context, List<TutorialModel> tutorialList) {
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvTutorial.setText(tutorialList.get(position).getTutorialName());
        if(tutorialList.get(position).isLocked()) {
            holder.ivLockUnlock.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.locked));
        } else {
            holder.ivLockUnlock.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unlock));
        }

        holder.cvTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (tutorialList.get(position).isLocked()) {
                        Toast.makeText(context, "content is locked", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Playing audio - " + tutorialList.get(position).getShowName());

                        PlaybackFragment playbackFragment =
                                new PlaybackFragment().newInstance(new RecordingItem(tutorialList.get(position).getTutorialName(),
                                        tutorialList.get(position).getFileName(),
                                        tutorialList.get(position).getShowName(),
                                        tutorialList.get(position).getTutorialName()));

                        FragmentTransaction transaction = ((FragmentActivity) context)
                                .getSupportFragmentManager()
                                .beginTransaction();

                        playbackFragment.show(transaction, "dialog_playback");

                    }
                } catch(Exception e) {
                    Log.e(TAG, "" + e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }
}
