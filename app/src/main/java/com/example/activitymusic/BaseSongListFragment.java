package com.example.activitymusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class BaseSongListFragment extends Fragment implements ListSongAdapter.IListSongAdapter/*, ActivityMusic.ICallbackFragmentServiceConnection */ {

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
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
            mAdapter.setService(mMediaPlaybackService);
            update();
            mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
                @Override
                public void onSelect() {
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

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setService(mMediaPlaybackService);
        if (checkService){
            update();
            mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
                @Override
                public void onSelect() {
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        Log.d(TAG, String.valueOf(mMediaPlaybackService));
//        if (mMediaPlaybackService != null){
//            mAdapter.setService(mMediaPlaybackService);
//            update();
//            mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
//                @Override
//                public void onSelect() {
//                    update();
//                }
//            });
//        }
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
        mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
            @Override
            public void onSelect() {
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
//        mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
//            @Override
//            public void onSelect() {
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
}
