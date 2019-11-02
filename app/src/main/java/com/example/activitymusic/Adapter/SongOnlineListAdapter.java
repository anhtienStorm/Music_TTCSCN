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
import androidx.recyclerview.widget.RecyclerView;

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

import pub.devrel.easypermissions.EasyPermissions;

public class SongOnlineListAdapter extends RecyclerView.Adapter<SongOnlineListAdapter.SongOnlineViewHolder> {
    private ArrayList<PlayList> mListSongOnline = new ArrayList<>();
    private Context mContext;
    private actionDownloadSong actionDownloadSong;

    public void setActionDownloadSong(SongOnlineListAdapter.actionDownloadSong actionDownloadSong) {
        this.actionDownloadSong = actionDownloadSong;
    }

    public SongOnlineListAdapter(ArrayList<PlayList> mListSongOnline, Context mContext) {
        this.mListSongOnline = mListSongOnline;
        this.mContext = mContext;
        Log.d("TienNVh", "SongOnlineListAdapter: " + mListSongOnline.size());
    }

    @NonNull
    @Override
    public SongOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TienNVh", "SongOnlineListAdapter:1 " + mListSongOnline.size());
        return new SongOnlineViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_song_online, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SongOnlineViewHolder holder, int position) {
        final PlayList songOnline = mListSongOnline.get(position);
        holder.mIndex.setText(position + "");
        holder.mNamesong.setText(songOnline.getNAMESONG());
        holder.mSinger.setText(songOnline.getSINGER());
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
                                actionDownloadSong.onAddPlayListSongsList(songOnline.getID());
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
        return mListSongOnline.size();
    }

    public class SongOnlineViewHolder extends RecyclerView.ViewHolder {

        TextView mIndex, mNamesong, mSinger;
        ImageView mMore;

        public SongOnlineViewHolder(@NonNull View itemView) {
            super(itemView);
            mIndex = itemView.findViewById(R.id.index_song_online);
            mNamesong = itemView.findViewById(R.id.name_song_online);
            mSinger = itemView.findViewById(R.id.singer_song_online);
            mMore = itemView.findViewById(R.id.more_song_online);

        }
    }


    public interface actionDownloadSong {
        void onDownloadSong(String Url);
        void onRemovePlayList(String id_Song);
        void onAddPlayListSongsList(String id_Song);
        void onAddPlayingListSongsList(String id_Song);// ds hien tai
    }
}
