package com.example.activitymusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;

import java.util.ArrayList;

public class SongTop10Adapter extends RecyclerView.Adapter<SongTop10Adapter.SongTop10ViewHolder> {
    Context mContext;
    ArrayList<SongOnline> mListSongOnline=new ArrayList<>();

    public SongTop10Adapter(Context mContext, ArrayList<SongOnline> mListSongOnline) {
        this.mContext = mContext;
        this.mListSongOnline = mListSongOnline;
    }

    @NonNull
    @Override
    public SongTop10ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongTop10ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_play_list,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongTop10ViewHolder holder, int position) {
        SongOnline songOnline=mListSongOnline.get(position);
        holder.mNameSinger.setText(songOnline.getSINGER());
        holder.mNameSong.setText(songOnline.getNAMESONG());
        Glide.with(mContext).load(songOnline.getIMAGE()).into(holder.imageSong);


    }

    @Override
    public int getItemCount() {
        return mListSongOnline.size();
    }

    public class SongTop10ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSong;
        TextView mNameSong, mNameSinger;

        public SongTop10ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSong= itemView.findViewById(R.id.imPlayListSong);
            mNameSong=itemView.findViewById(R.id.namePlayList);
            mNameSinger=itemView.findViewById(R.id.artists);



        }
    }
}
