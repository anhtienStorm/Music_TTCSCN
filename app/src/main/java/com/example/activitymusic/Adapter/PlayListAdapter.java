package com.example.activitymusic.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.R;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {
    final Context mContext;
    List<PlayList> mPlayLists = new ArrayList<>();
    private connectHomeOnlineAndAdapter connectHomeOnlineAndAdapter;

    public PlayListAdapter(Context mContext, ArrayList<PlayList> mPlayLists) {
        this.mContext = mContext;
        this.mPlayLists = mPlayLists;
    }

    public void setConnectHomeOnlineAndAdapter(PlayListAdapter.connectHomeOnlineAndAdapter connectHomeOnlineAndAdapter) {
        this.connectHomeOnlineAndAdapter = connectHomeOnlineAndAdapter;
    }

    @NonNull
    @Override
    public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_play_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListViewHolder holder, final int position) {
        final PlayList playList = mPlayLists.get(position);
        holder.mName.setText(playList.getNAMEPLAYLIST());
        holder.mArtists.setText("Various Artists");
        Glide.with(mContext).load(playList.getIMAGE()).into(holder.mIcon);
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectHomeOnlineAndAdapter.connectAdapter(playList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }

    class PlayListViewHolder extends RecyclerView.ViewHolder {
        ImageView mIcon;
        TextView mName, mArtists;
        ConstraintLayout mConstraintLayout;

        public PlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.imPlayListSong);
            mName = itemView.findViewById(R.id.namePlayList);
            mArtists = itemView.findViewById(R.id.artists);
            mConstraintLayout = itemView.findViewById(R.id.ConstraintLayoutItemsPlayList);

        }

    }

    public interface connectHomeOnlineAndAdapter {
        void connectAdapter( PlayList playList);
    }
}
