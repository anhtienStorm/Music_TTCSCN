package com.example.activitymusic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;

public class FavoriteSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 1;

    static final String AUTHORITY = "com.android.example.provider.FavoriteSongs";
    static final String CONTENT_PATH = "backupdata";
    static final String URL = "content://" + AUTHORITY + "/" + CONTENT_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
//    static final String TITLE = "title";
//    static final String DATA = "data";
//    static final String ALBUM_ID = "albumid";
//    static final String ARTIST = "artist";
//    static final String DURATION = "duration";
//    static final String FAVORITE = "favorite";
//    static final String COUNT = "count";

    static final String ID_PROVIDER = "id_provider";
    static final String IS_FAVORITE = "is_favorite";
    static final String COUNT_OF_PLAY = "count_of_play";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

//    public ArrayList<Song> loadFavoriteSongs(){
//        ArrayList<Song> list = new ArrayList<>();
//        Cursor c = getContext().getContentResolver().query(CONTENT_URI, null, null, null,null);
//
//        int indexIdColumn = c.getColumnIndex(_ID);
//        int indexTitleColumn = c.getColumnIndex(TITLE);
//        int indexDataColumn = c.getColumnIndex(DATA);
//        int indexArtistColumn = c.getColumnIndex(ARTIST);
//        int indexAlbumIDColumn = c.getColumnIndex(ALBUM_ID);
//        int indexDurationColumn = c.getColumnIndex(DURATION);
//
//        if (c.moveToFirst()){
//            do {
//                int id = Integer.parseInt(c.getString(indexIdColumn));
//                String title = c.getString(indexTitleColumn);
//                String data = c.getString(indexDataColumn);
//                String artist = c.getString(indexArtistColumn);
//                String album_id = c.getString(indexAlbumIDColumn);
//                String duration = c.getString(indexDurationColumn);
//                Song song = new Song(id,title,data,artist,album_id,duration);
//                list.add(song);
//            } while (c.moveToNext());
//        }
//
//        return list;
//    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(), CONTENT_URI, null, FavoriteSongsProvider.IS_FAVORITE+" = "+1, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {
        ArrayList<Song> list = new ArrayList<>();

        if (c.moveToFirst()){
            do {
                int id = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider._ID)));
                int id_provider = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER)));
                Song song = mAllSongList.get(id_provider);
                Song newSong = new Song(id,song.getNameSong(),song.getPathSong(),song.getSinger(),song.getAlbumID(),song.getDuration());
                list.add(newSong);

//                String title = c.getString(indexTitleColumn);
//                String data = c.getString(indexDataColumn);
//                String artist = c.getString(indexArtistColumn);
//                String album_id = c.getString(indexAlbumIDColumn);
//                String duration = c.getString(indexDurationColumn);
//                Song song = new Song(id,title,data,artist,album_id,duration);
//                list.add(song);
            } while (c.moveToNext());
        }
        mAdapter.updateList(list);
        setListSong(list);
        mAdapter.setTypeSongList("FavoriteSongs");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.updateList(new ArrayList<Song>());
        }
    }
}
