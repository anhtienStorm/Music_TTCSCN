package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.SongOnlineListAdapter;
import com.example.activitymusic.Adapter.SongTop10Adapter;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;
import com.example.activitymusic.Service.MediaPlaybackService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListSongTop10 extends Fragment {
    private SongTop10Adapter mSongTop10Adapter;
    RecyclerView mRecyclerViewListSong;
    MediaPlaybackService mediaPlaybackService;

    protected MainActivityMusic getMusicactivity() {
        if (getActivity() instanceof MainActivityMusic) {
            return (MainActivityMusic) getActivity();
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_fragment, container, false);
        mRecyclerViewListSong = view.findViewById(R.id.recyclerviewPlaylist);
        getData();

        if (getMusicactivity().mMediaPlaybackService != null) {
            mediaPlaybackService = getMusicactivity().mMediaPlaybackService;
        }
        return view;
    }

    void getData() {
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataSongTop10Online();
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                final ArrayList<SongOnline> lists = (ArrayList<SongOnline>) response.body();
                mSongTop10Adapter = new SongTop10Adapter( getActivity(),lists);
                mSongTop10Adapter.setOnClickItemListenner(new SongTop10Adapter.IClickItemListenner() {
                    @Override
                    public void onClick(int position) {
                        ((MainActivityMusic)getActivity()).mMediaPlaybackService.playSongOnline(lists.get(position), lists);
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.sub_fragment_a, ((MainActivityMusic)getActivity()).mMediaPlaybackFragment).commit();
                    }
                });
                mRecyclerViewListSong.setAdapter(mSongTop10Adapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                mRecyclerViewListSong.setLayoutManager(gridLayoutManager);
              //  mRecyclerViewListSong.setLayoutManager(new LinearLayoutManager(getContext()));


            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
