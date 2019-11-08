package com.example.activitymusic.Adapter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SongOnlineListAdapter extends RecyclerView.Adapter<SongOnlineListAdapter.SongOnlineViewHolder> {
    public ArrayList<SongOnline> mPlayListSongOnline = new ArrayList<>();
    private String mStatus;
    private Context mContext;
    private actionDownloadSong actionDownloadSong;
    private IClickItemListenner mClickItemListenner;

    public void setActionDownloadSong(SongOnlineListAdapter.actionDownloadSong actionDownloadSong) {
        this.actionDownloadSong = actionDownloadSong;
    }

    public SongOnlineListAdapter(ArrayList<SongOnline> mListSongOnline, Context mContext , String mStatus) {
        this.mPlayListSongOnline = mListSongOnline;
        this.mContext = mContext;
        this.mStatus=mStatus;
        Log.d("TienNVh", "SongOnlineListAdapter: " + mListSongOnline.size());
    }

    @NonNull
    @Override
    public SongOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TienNVh", "SongOnlineListAdapter:1 " + mPlayListSongOnline.size());
        return new SongOnlineViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_song_online, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SongOnlineViewHolder holder, final int position) {
        final SongOnline songOnline = mPlayListSongOnline.get(position);
        holder.mIndex.setText(position +1 + "");
        holder.mNamesong.setText(songOnline.getNAMESONG());
        holder.mSinger.setText(songOnline.getSINGER());
//        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                if(mStatus.equals("view")){
////                    mClickItemListenner.onClick(position);
////                }else {
////                    actionDownloadSong.onAddPlayListSongsList(songOnline.getID(),mStatus);
////                }
//            }
//        });
        if(!mStatus.equals("view"))
            holder.mMore.setVisibility(View.GONE);

        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.mMore);
                popupMenu.inflate(R.menu.more_online_song_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add_playinglist_song:
                                actionDownloadSong.onAddPlayingListSongsList(songOnline.getID());
                                return true;
                            case R.id.add_playlist_song:
                                actionDownloadSong.onAddPlayListSongsList(songOnline.getID(), "view");
                                return true;
                            case R.id.download_song:
                                actionDownloadSong.onDownloadSong(songOnline.getLINKSONG());
                                return true;
                            case R.id.remove_playlist_song:
                                actionDownloadSong.onRemovePlayList(songOnline.getID());
                               return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPlayListSongOnline.size();
    }

    public class SongOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mIndex, mNamesong, mSinger;
        ImageView mMore;
        ConstraintLayout mConstraintLayout;
        public SongOnlineViewHolder(@NonNull View itemView) {
            super(itemView);
            mIndex = itemView.findViewById(R.id.index_song_online);
            mNamesong = itemView.findViewById(R.id.name_song_online);
            mSinger = itemView.findViewById(R.id.singer_song_online);
            mMore = itemView.findViewById(R.id.more_song_online);
            mConstraintLayout=itemView.findViewById(R.id.constraintLayoutSongOnline);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mStatus.equals("view")){
                mClickItemListenner.onClick(getAdapterPosition());
            }else {
                actionDownloadSong.onAddPlayListSongsList(mPlayListSongOnline.get(getAdapterPosition()).getID(),mStatus);
            }
        }
    }


    public interface actionDownloadSong {
        void onDownloadSong(String Url);
        void onRemovePlayList(String id_Song);
        void onAddPlayListSongsList(String id_Song, String status);
        void onAddPlayingListSongsList(String id_Song);// ds hien tai
    }

    public interface IClickItemListenner {
        void onClick(int position);
    }

    public void setOnClickItemListenner(IClickItemListenner clickItemListenner) {
        mClickItemListenner = clickItemListenner;
    }
}
