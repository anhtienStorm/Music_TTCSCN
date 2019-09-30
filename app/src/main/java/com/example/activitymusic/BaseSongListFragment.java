package com.example.activitymusic;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
    AllSongsProvider mAllSongsProvider;
    private ArrayList<Song> mListSong = new ArrayList<>();
    private static final String TAG = "abc";

//    IServiceConnectListennerMediaPlaybackFragment mServiceConnectListennerMediaPlaybackFragment;

//    ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
//            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
//            mAdapter.setService(mMediaPlaybackService);
//            mCheckService = true;
//            mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
//            update();
//            mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
//                @Override
//                public void onUpdate() {
//                    update();
//                }
//            });
//            if (!mMediaPlaybackService.isMusicPlay()) {
//                if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")){
//                    mMediaPlaybackService.loadData();
//                    updateSaveSong();
//                }
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mCheckService = false;
//        }
//    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (isMyServiceRunning(MediaPlaybackService.class)) {
//            connectService();
//        } else {
//            startService();
//            connectService();
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllSongsProvider = new AllSongsProvider(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getMusicActivity().mMediaPlaybackService != null) {
            mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
            mCheckService = true;
        }
        getMusicActivity().setServiceConnectListenner1(new ActivityMusic.IServiceConnectListenner1() {
            @Override
            public void onConnect() {
                Log.d(TAG, "BaseSong: ");
                mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
                mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
                mAdapter.setService(mMediaPlaybackService);
                mCheckService = true;
                update();
                mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
                    @Override
                    public void onUpdate() {
                        update();
                    }
                });
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
            mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
                @Override
                public void onUpdate() {
                    update();
                }
            });
            if (!mMediaPlaybackService.isMusicPlay()) {
                if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")) {
                    mMediaPlaybackService.loadData();
                    updateSaveSong();
                }
            }
        }

        Log.d(TAG, "BaseSong: " + mMediaPlaybackService);

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
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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
        mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
            @Override
            public void onUpdate() {
                update();
            }
        });
    }

    public void setListSong(ArrayList<Song> listSong) {
        mListSong = listSong;
    }

//    protected void setService(MediaPlaybackService service){
//        mMediaPlaybackService = service;
//        Log.d(TAG, "baseSong: "+service);
//        mAdapter.setService(mMediaPlaybackService);
//            mCheckService = true;
//            mRecyclerView.scrollToPosition(mMediaPlaybackService.getIndexofPlayingSong());
//            update();
//            mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
//                @Override
//                public void onUpdate() {
//                    update();
//                }
//            });
//            if (!mMediaPlaybackService.isMusicPlay()) {
//                if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")){
//                    mMediaPlaybackService.loadData();
//                    updateSaveSong();
//                }
//            }
//    }

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

            if (mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()) == null) {
                imgMainSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgMainSong.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
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

        if (mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()) == null) {
            imgMainSong.setImageResource(R.drawable.icon_default_song);
        } else {
            imgMainSong.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
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

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        getActivity().unbindService(mServiceConnection);
//    }

//    public void connectService() {
//        Intent it = new Intent(getContext(), MediaPlaybackService.class);
//        getActivity().bindService(it, mServiceConnection, 0);
//    }

//    public void startService() {
//        Intent it = new Intent(getContext(), MediaPlaybackService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            getActivity().startService(it);
//        }
//    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    interface IServiceConnectListennerMediaPlaybackFragment{
//        void onConnect();
//    }
//
//    public void setServiceConnectListennerMediaPlaybackFragment(IServiceConnectListennerMediaPlaybackFragment serviceConnectListennerMediaPlaybackFragment){
//        this.mServiceConnectListennerMediaPlaybackFragment = serviceConnectListennerMediaPlaybackFragment;
//    }

}
