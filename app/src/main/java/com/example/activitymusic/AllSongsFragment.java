package com.example.activitymusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AllSongsFragment extends BaseSongListFragment {

    AllSongsProvider mAllSongsProvider;
    ArrayList<Song> mListSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAllSongsProvider = new AllSongsProvider(getActivity());
        mListSong = mAllSongsProvider.getListSong();
        setListSong(mListSong);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
