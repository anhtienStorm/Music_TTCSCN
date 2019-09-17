package com.example.activitymusic;

import android.content.ComponentName;
import android.content.Context;
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

public class BaseSongListFragment extends Fragment implements ListSongAdapter.IListSongAdapter,ActivityMusic.ICallbackFragmentServiceConnection {

    private MediaPlaybackService mMediaPlaybackService;
    private RecyclerView mRecyclerView;
    protected ListSongAdapter mAdapter;
    private ArrayList<Song> mListSong;
    private ActivityMusic mActivityMusic;
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
//            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//        }
//    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityMusic = (ActivityMusic) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);
        mActivityMusic.registerClientFragment(this);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new ListSongAdapter(mListSong, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnClickListenner(this);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        mMediaPlaybackService.playSong(mListSong, position);
        getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);
    }

    public void setListSong(ArrayList<Song> listSong){
        mListSong = listSong;
    }

    @Override
    public void service(MediaPlaybackService service) {
        mMediaPlaybackService = service;
    }
}
