package com.example.activitymusic;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AllSongsFragmentService extends BaseSongListFragmentService implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Song> listSong = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            int i = 0;
            int indexTitleColumn = data.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexDataColumn = data.getColumnIndex(MediaStore.Audio.Media.DATA);
            int indexArtistColumn = data.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexAlbumIDColumn = data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int indexDurationColumn = data.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                String title = data.getString(indexTitleColumn);
                String path = data.getString(indexDataColumn);
                String artist = data.getString(indexArtistColumn);
                String albumID = data.getString(indexAlbumIDColumn);
                int duration = Integer.parseInt(data.getString(indexDurationColumn));
                SimpleDateFormat formatTimeSong = new SimpleDateFormat("mm:ss");
                String timeSong = formatTimeSong.format(duration);
                Song song = new Song(i, title, path, artist, albumID, timeSong);
                listSong.add(song);
                i++;
            } while (data.moveToNext());
        }
        mAdapter.updateList(listSong);
        setListSong(listSong);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.updateList(new ArrayList<Song>());
        }
    }
}
