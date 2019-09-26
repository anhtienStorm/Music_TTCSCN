package com.example.activitymusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
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

public class BaseSongListFragment extends Fragment implements ListSongAdapter.IListSongAdapter/*, ActivityMusic.ICallbackFragmentServiceConnection*/  {

    private MediaPlaybackService mMediaPlaybackService;
    private RecyclerView mRecyclerView;
    protected ListSongAdapter mAdapter;
    ImageView imgPlay;
    TextView tvNameSong, tvArtist;
    ImageView imgMainSong;
    boolean checkService = false;
    AllSongsProvider mAllSongsProvider;
    private ArrayList<Song> mListSong = new ArrayList<>();
    private static final String TAG = "abc";
//    private ActivityMusic mActivityMusic;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
            mAdapter.setService(mMediaPlaybackService);
            Log.d("abc", "onServiceConnected: " + mMediaPlaybackService.getNameSong());
            Log.d("abc", "onServiceConnected: " + mMediaPlaybackService.getPlayingSongList().size());
            update();
            mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
                @Override
                public void onUpdate() {
                    update();
                }
            });
            checkService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            checkService = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        connectService();
        mAllSongsProvider = new AllSongsProvider(getContext());
//        mActivityMusic = new ActivityMusic();
//        mActivityMusic.registerClientFragment(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkService){
            mAdapter.setService(mMediaPlaybackService);
            update();
            mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
                @Override
                public void onUpdate() {
                    update();
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);
        initView(view);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
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
//        mActivityMusic.listennerConnectService(this);

        imgPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.pause();
                    } else {
                        mMediaPlaybackService.play();
                    }
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

//    @Override
//    public void service(MediaPlaybackService service) {
//        mMediaPlaybackService = service;
//        mAdapter.setService(mMediaPlaybackService);
//        mAdapter.notifyDataSetChanged();
//        mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
//            @Override
//            public void onUpdate() {
//                mAdapter.notifyDataSetChanged();
//            }
//        });
//    }

    public void connectService() {
        Intent it = new Intent(getContext(), MediaPlaybackService.class);
        getActivity().bindService(it, mServiceConnection, 0);
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
            Log.d("abc", "update: " + mMediaPlaybackService.getNameSong());
            Log.d("abc", "update: " + mMediaPlaybackService.getPlayingSongList().size());

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
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

//    public void setService(MediaPlaybackService service){
//        mMediaPlaybackService = service;
//        mAdapter.setService(mMediaPlaybackService);
//        update();
//        mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
//            @Override
//            public void onUpdate() {
//                update();
//            }
//        });
//        checkService = true;
//    }

//    @Override
//    public void service(MediaPlaybackService service) {
//        mMediaPlaybackService = service;
//        mAdapter.setService(mMediaPlaybackService);
//        update();
//        mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
//            @Override
//            public void onUpdate() {
//                update();
//            }
//        });
//        checkService = true;
//    }
}
