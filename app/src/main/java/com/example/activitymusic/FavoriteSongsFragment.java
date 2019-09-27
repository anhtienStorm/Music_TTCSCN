package com.example.activitymusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FavoriteSongsFragment extends BaseSongFragmentList {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ArrayList<Song> list = new ArrayList<>();
        Song song = new Song(2,"a","b","c",null,"aa");
        list.add(song);
        setListSong(list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
