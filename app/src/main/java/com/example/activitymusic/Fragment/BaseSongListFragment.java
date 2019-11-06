package com.example.activitymusic.Fragment;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Provider.FavoriteSongsProvider;
import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Service.MediaPlaybackService;
import com.example.activitymusic.R;
import com.example.activitymusic.Model.Song;
import com.example.activitymusic.Adapter.SongListAdapter;

import java.util.ArrayList;

public class BaseSongListFragment extends Fragment implements SongListAdapter.ISongListAdapterClickListener {

    protected MediaPlaybackService mMediaPlaybackService;
    private RecyclerView mRecyclerViewBaseSongList;
    public SongListAdapter mSongListAdapter;
    boolean mCheckService = false;
    private ArrayList<Song> mListSong = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getMusicActivity().mMediaPlaybackService != null) {
            ((MainActivityMusic) getActivity()).update();
            mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
            mCheckService = true;
            mRecyclerViewBaseSongList.smoothScrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
        }
        getMusicActivity().setServiceConnectListenner1(new MainActivityMusic.IServiceConnectListenner1() {
            @Override
            public void onConnect() {
                mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
                mRecyclerViewBaseSongList.smoothScrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
                mSongListAdapter.setService(mMediaPlaybackService);
                mCheckService = true;
                ((MainActivityMusic) getActivity()).update();
            }
        });

        if (mCheckService) {
            mSongListAdapter.setService(mMediaPlaybackService);
            mRecyclerViewBaseSongList.smoothScrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
            ((MainActivityMusic) getActivity()).update();
        }

    }

    protected MainActivityMusic getMusicActivity() {
        if (getActivity() instanceof MainActivityMusic) {
            return (MainActivityMusic) getActivity();
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);

        mRecyclerViewBaseSongList = view.findViewById(R.id.recycler_view);
        mSongListAdapter = new SongListAdapter(mListSong, getActivity());
        mRecyclerViewBaseSongList.setAdapter(mSongListAdapter);
        mRecyclerViewBaseSongList.addItemDecoration(new DividerItemDecoration(mRecyclerViewBaseSongList.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewBaseSongList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSongListAdapter.setOnClickListenner(this);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        mMediaPlaybackService.playSong(mListSong, mListSong.get(position));
        mSongListAdapter.setService(mMediaPlaybackService);
        int favorite = loadFavoriteStatus(mMediaPlaybackService.getId());
        int count = loadCountOfPlayStatus(mMediaPlaybackService.getId());

        if (favorite == 0) {
            if (count == 2) {
                addToFavoriteSongsList(mMediaPlaybackService.getId());
                Toast.makeText(getActivity(), "\t\t\t\t\tPlay Three times\nAdd to Favorite Songs List", Toast.LENGTH_SHORT).show();
                setDefaultCountOfPlayStatus(mMediaPlaybackService.getId());
            } else {
                increaseCountOfPlay(mMediaPlaybackService.getId());
            }
        } else {
            if (count != 0) {
                setDefaultCountOfPlayStatus(mMediaPlaybackService.getId());
            }
        }
    }

    public void setListSong(ArrayList<Song> listSong) {
        mListSong = listSong;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSongListAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    public void increaseCountOfPlay(int id) {
        int count = loadCountOfPlayStatus(id);
        count++;
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.COUNT_OF_PLAY, count);
        getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, "ID_PROVIDER = " + id, null);
    }

    public int loadFavoriteStatus(int id) {
        int isFavorite = 0;
        Cursor c = getActivity().getContentResolver().query(FavoriteSongsProvider.CONTENT_URI, null, FavoriteSongsProvider.ID_PROVIDER + " = " + id, null, null);
        if (c.moveToFirst()) {
            do {
                isFavorite = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider.IS_FAVORITE)));
            } while (c.moveToNext());
        }
        return isFavorite;
    }

    public int loadCountOfPlayStatus(int id) {
        int countOfPlay = 0;
        Cursor c = getActivity().getContentResolver().query(FavoriteSongsProvider.CONTENT_URI, null, FavoriteSongsProvider.ID_PROVIDER + " = " + id, null, null);
        if (c.moveToFirst()) {
            do {
                countOfPlay = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider.COUNT_OF_PLAY)));
            } while (c.moveToNext());
        }
        return countOfPlay;
    }

    public void setDefaultCountOfPlayStatus(int id) {
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.COUNT_OF_PLAY, 0);
        getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, "ID_PROVIDER = " + id, null);
    }

    public void addToFavoriteSongsList(int id) {
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.IS_FAVORITE, 2);
        getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, "ID_PROVIDER = " + id, null);
    }
}
