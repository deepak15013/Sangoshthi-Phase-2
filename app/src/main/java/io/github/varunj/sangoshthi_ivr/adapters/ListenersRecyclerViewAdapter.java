package io.github.varunj.sangoshthi_ivr.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.models.CallerState;
import io.github.varunj.sangoshthi_ivr.network.RequestMessageHelper;

/**
 * Created by Deepak on 08-06-2017.
 */

public class ListenersRecyclerViewAdapter extends RecyclerView.Adapter<ListenersRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = ListenersRecyclerViewAdapter.class.getSimpleName();

    private List<CallerState> callerStateList;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListenerNumber;
        public ImageButton ivMuteUnmute;
        public ImageButton ivQuestion;
        public ImageButton ivOnline;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvListenerNumber = (TextView) itemView.findViewById(R.id.tv_listener_number);
            ivMuteUnmute = (ImageButton) itemView.findViewById(R.id.iv_mute_unmute);
            ivQuestion = (ImageButton) itemView.findViewById(R.id.iv_question);
            ivOnline = (ImageButton) itemView.findViewById(R.id.iv_online);
        }
    }

    public ListenersRecyclerViewAdapter(Context context, List<CallerState> moviesList) {
        this.context = context;
        this.callerStateList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listener_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvListenerNumber.setText(callerStateList.get(position).getPhoneNum());

        if(callerStateList.get(position).isMuteUnmuteState()) {
            // mute
            // TODO: 09-06-2017
            holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mute));
        } else {
            // unmute
            // TODO: 09-06-2017
            holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unmute));
        }
        
        if(callerStateList.get(position).isQuestionState()) {
            // TODO: 09-06-2017

        } else {
            // TODO: 09-06-2017  
        }
        
        if(callerStateList.get(position).getOnlineState().equals("offline")) {
            // TODO: 09-06-2017
            // user is offline display online
            holder.ivOnline.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.online));
        } else {
            // TODO: 09-06-2017
            // user is online display offline
            holder.ivOnline.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.offline));
        }

        holder.ivMuteUnmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "position clicked: " + position);
                if(callerStateList.get(position).isMuteUnmuteState()) {
                    // mute - set unmute
                    callerStateList.get(position).setMuteUnmuteState(false);
                    callerStateList.get(position).setTurn(callerStateList.get(position).getTurn()+1);
                    holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unmute));
                    RequestMessageHelper.getInstance().unmute(callerStateList.get(position).getPhoneNum(), callerStateList.get(position).getTurn());
                } else {
                    // unmute - set mute
                    callerStateList.get(position).setMuteUnmuteState(true);
                    holder.ivMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mute));
                    RequestMessageHelper.getInstance().mute(callerStateList.get(position).getPhoneNum(), callerStateList.get(position).getTurn());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return callerStateList.size();
    }

}
