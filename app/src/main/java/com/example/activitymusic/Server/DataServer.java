package com.example.activitymusic.Server;

import com.example.activitymusic.Model.SongOnline;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DataServer {

    @GET("SelectSongs.php")
    Call<List<SongOnline>> getDataSongOnline();
}