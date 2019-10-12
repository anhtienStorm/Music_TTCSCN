package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.banner_fragment, container, false);
        getData();
        return view;
    }

    private void getData(){
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataSongOnline();
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                ArrayList<SongOnline> listSongOnline = (ArrayList<SongOnline>) response.body();
            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {

            }
        });
    }
}
