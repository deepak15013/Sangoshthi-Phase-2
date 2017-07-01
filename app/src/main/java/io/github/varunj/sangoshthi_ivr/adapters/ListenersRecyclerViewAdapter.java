/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.activities.ShowActivity;
import io.github.varunj.sangoshthi_ivr.models.CallerStateModel;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;

/**
 * Created by Deepak on 08-06-2017.
 */

public class ListenersRecyclerViewAdapter extends RecyclerView.Adapter<ListenersRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = ListenersRecyclerViewAdapter.class.getSimpleName();

    private List<CallerStateModel> callerStateModelList;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvListenerItemRow;
        TextView tvListenerNumber;
        ImageButton ivMuteUnmute;
        ImageButton ivQuestion;
        ImageView ivReconnection;

        MyViewHolder(View itemView) {
            super(itemView);
            cvListenerItemRow = (CardView) itemView.findViewById(R.id.cv_listener_item_row);
            tvListenerNumber = (TextView) itemView.findViewById(R.id.tv_listener_number);
            ivMuteUnmute = (ImageButton) itemView.findViewById(R.id.iv_mute_unmute);
            ivQuestion = (ImageButton) itemView.findViewById(R.id.iv_question);
            ivReconnection = (ImageView) itemView.findViewById(R.id.iv_reconnection);
        }
    }

    public ListenersRecyclerViewAdapter(Context context, List<CallerStateModel> moviesList) {
        this.context = context;
        this.callerStateModelList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listener_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvListenerNumber.setText(callerStateModelList.get(position).getPhoneNum());

        if(callerStateModelList.get(position).getTask().equals("online")) {
            // user is online show its state
            holder.cvListenerItemRow.setVisibility(View.VISIBLE);
        } else {
            // user is offline remove it
            holder.cvListenerItemRow.setVisibility(View.GONE);
        }

        Log.d(TAG, "Reconnection - " + callerStateModelList.get(position).isReconnection());
        if(callerStateModelList.get(position).isReconnection()) {
            // show reconnection
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ShowActivity showActivity = (ShowActivity) context;
                        int blink = 0;
                        while(blink <= 10) {
                            if(blink % 2 == 0) {
                                showActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.ivReconnection.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                showActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.ivReconnection.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                            blink++;
                            Thread.sleep(1000);
                        }
                        showActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.ivReconnection.setVisibility(View.GONE);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            // don't show reconnection
            holder.ivReconnection.setVisibility(View.GONE);
        }

        if(callerStateModelList.get(position).isMuteUnmuteState()) {
            // mute
            holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mute));
        } else {
            // unmute
            holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unmute));
        }

        if(callerStateModelList.get(position).isMuteUnmuteDisabled()) {
            // disable the mute unmute button
            holder.ivMuteUnmute.setEnabled(true);
            callerStateModelList.get(position).setMuteUnmuteState(true);
            holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mute));
        } else {
            // enable the mute unmute button
            holder.ivMuteUnmute.setEnabled(true);
        }
        
        if(callerStateModelList.get(position).isQuestionState()) {
            holder.ivQuestion.setVisibility(View.VISIBLE);

        } else {
            holder.ivQuestion.setVisibility(View.INVISIBLE);
        }

        holder.ivMuteUnmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "position clicked: " + position);
                holder.ivMuteUnmute.setEnabled(false);
                if(callerStateModelList.get(position).isMuteUnmuteState()) {
                    // mute - set unmute
                    RequestMessageHelper.getInstance().unmute(callerStateModelList.get(position).getPhoneNum(), callerStateModelList.get(position).getTurn());
                    callerStateModelList.get(position).setMuteUnmuteState(false);
                    callerStateModelList.get(position).setTurn(callerStateModelList.get(position).getTurn()+1);
                    holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unmute));
                    holder.ivMuteUnmute.setEnabled(false);
                    callerStateModelList.get(position).setQuestionState(false);
                    holder.ivQuestion.setVisibility(View.INVISIBLE);
                } else {
                    // unmute - set mute
                    callerStateModelList.get(position).setMuteUnmuteState(true);
                    holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mute));
                    holder.ivMuteUnmute.setEnabled(false);
                    RequestMessageHelper.getInstance().mute(callerStateModelList.get(position).getPhoneNum(), callerStateModelList.get(position).getTurn());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return callerStateModelList.size();
    }

}
