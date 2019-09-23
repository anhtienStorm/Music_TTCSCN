package com.example.activitymusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BaseSongListFragment extends Fragment implements ListSongAdapter.IListSongAdapter, ActivityMusic.ICallbackFragmentServiceConnection {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_song_list_fragment, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);
        if (mMediaPlaybackService != null){
            mActivityMusic.update();
            mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
                @Override
                public void onSelect() {
                    mActivityMusic.update();
                }
            });
        }
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new ListSongAdapter(mListSong, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnClickListenner(this);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        mMediaPlaybackService.playSong(mListSong, mListSong.get(position));
        getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);
        mActivityMusic.update();
        mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
            @Override
            public void onSelect() {
                mActivityMusic.update();
            }
        });
    }

    public void setListSong(ArrayList<Song> listSong){
        mListSong = listSong;
    }

    @Override
    public void service(MediaPlaybackService service) {
        mMediaPlaybackService = service;
        mAdapter.setService(mMediaPlaybackService);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

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
