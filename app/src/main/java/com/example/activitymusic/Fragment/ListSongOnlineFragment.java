package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Adapter.SongOnlineListAdapter;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListSongOnlineFragment extends Fragment {

    RecyclerView mRecyclerViewListSongOnlineFragment;
    SongOnlineListAdapter songOnlineListAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_song_online, container, false);
        mRecyclerViewListSongOnlineFragment=view.findViewById(R.id.recycler_view_list_song_online);




        return view;
    }
    void getData(){
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataPlayListSong("danh sách phát");
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                final ArrayList<SongOnline> lists = (ArrayList<SongOnline>) response.body();


            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
