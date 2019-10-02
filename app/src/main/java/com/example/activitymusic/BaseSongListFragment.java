package com.example.activitymusic;

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

import java.util.ArrayList;

public class BaseSongListFragment extends Fragment implements ListSongAdapter.ISongListAdapterClickListener {

    protected MediaPlaybackService mMediaPlaybackService;
    private RecyclerView mRecyclerView;
    protected ListSongAdapter mAdapter;
    ImageView imgPlay;
    TextView tvNameSong, tvArtist;
    ImageView imgMainSong;
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
            mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
            mCheckService = true;
            mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
        }
        getMusicActivity().setServiceConnectListenner1(new ActivityMusic.IServiceConnectListenner1() {
            @Override
            public void onConnect() {
                mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
                mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
                mAdapter.setService(mMediaPlaybackService);
                mCheckService = true;
                if (!mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")) {
                        mMediaPlaybackService.loadData();
                        updateSaveSong();
                    }
                }
            }
        });

        if (mCheckService) {
            mAdapter.setService(mMediaPlaybackService);
            mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
            update();
            if (!mMediaPlaybackService.isMusicPlay()) {
                if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")) {
                    mMediaPlaybackService.loadData();
                    updateSaveSong();
                }
            }
        }

    }

    protected ActivityMusic getMusicActivity() {
        if (getActivity() instanceof ActivityMusic) {
            return (ActivityMusic) getActivity();
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);

        initView(view);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view.findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } else {
            view.findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new ListSongAdapter(mListSong, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnClickListenner(this);

        imgPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.pause();
                    } else {
                        mMediaPlaybackService.play();
                    }
                } else {
                    mMediaPlaybackService.preparePlay();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        mMediaPlaybackService.playSong(mListSong, mListSong.get(position));
        mAdapter.setService(mMediaPlaybackService);
        update();
        int favorite = loadFavoriteStatus(mMediaPlaybackService.getId());
        int count = loadCountOfPlayStatus(mMediaPlaybackService.getId());

        if (favorite == 0) {
            if (count == 2) {
                addToFavoriteSongsList(mMediaPlaybackService.getId());
                Toast.makeText(getActivity(), "\tPlay Three times\nAdd to Favorite Songs List", Toast.LENGTH_SHORT).show();
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

    public void initView(View view) {
        imgPlay = view.findViewById(R.id.btMainPlay);
        tvNameSong = view.findViewById(R.id.tvMainNameSong);
        tvNameSong.setSelected(true);
        tvArtist = view.findViewById(R.id.tvMainArtist);
        imgMainSong = view.findViewById(R.id.imgMainSong);
    }

    public void update() {
        if (mMediaPlaybackService.isMusicPlay()) {
            mAdapter.notifyDataSetChanged();
            //mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());

            if (loadImageFromPath(mMediaPlaybackService.getPathSong()) == null) {
                imgMainSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgMainSong.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
            }

            tvNameSong.setText(mMediaPlaybackService.getNameSong());
            tvArtist.setText(mMediaPlaybackService.getArtist());
            if (mMediaPlaybackService.isPlaying()) {
                imgPlay.setImageResource(R.drawable.ic_pause_black_24dp);
            } else {
                imgPlay.setImageResource(R.drawable.ic_play_black_24dp);
            }
        }
    }

    public void updateSaveSong() {
        mAdapter.notifyDataSetChanged();

        if (loadImageFromPath(mMediaPlaybackService.getPathSong()) == null) {
            imgMainSong.setImageResource(R.drawable.icon_default_song);
        } else {
            imgMainSong.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
        }

        tvNameSong.setText(mMediaPlaybackService.getNameSong());
        tvArtist.setText(mMediaPlaybackService.getArtist());
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
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    public Bitmap loadImageFromPath(String path){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
        } catch (Exception e){
            e.printStackTrace();
        }
        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
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
