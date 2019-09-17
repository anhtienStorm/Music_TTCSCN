package com.example.activitymusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BaseSongListFragment extends Fragment implements ListSongAdapter.IListSongAdapter {

    private MediaPlaybackService mMediaPlaybackService;
    private RecyclerView mRecyclerView;
    protected ListSongAdapter mAdapter;
    private ArrayList<Song> mListSong;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);
        connectService();

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new ListSongAdapter(mListSong, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnClickListenner(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
    }

    @Override
    public void onItemClick(int position) {
        mMediaPlaybackService.playSong(mListSong, position);
    }

    public void connectService() {
        Intent it = new Intent(getActivity(), MediaPlaybackService.class);
        getActivity().bindService(it, mServiceConnection, 0);
    }

    public void setListSong(ArrayList<Song> listSong){
        mListSong = listSong;
    }
}
