package com.example.activitymusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BaseSongListFragmentService extends Fragment implements ListSongAdapter.IListSongAdapter, ActivityMusic.ICallbackFragmentServiceConnection {

    private MediaPlaybackService mMediaPlaybackService;
    private RecyclerView mRecyclerView;
    protected ListSongAdapter mAdapter;
    private ArrayList<Song> mListSong = new ArrayList<>();
    private ActivityMusic mActivityMusic;

    @Override
    public void onStart() {
        super.onStart();
        mActivityMusic = (ActivityMusic) getContext();
        mActivityMusic.registerClientFragment(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new ListSongAdapter(mListSong, getActivity(), mMediaPlaybackService);
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
