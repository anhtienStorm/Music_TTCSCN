package com.example.activitymusic.Server;

import com.example.activitymusic.Model.Notification;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.Model.SongOnline;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataServer {

    @GET("SelectSongs.php")
    Call<List<SongOnline>> getDataSongOnline();

    @GET("SelectTop10.php")
    Call<List<SongOnline>> getDataSongTop10Online();

    @GET("SelectSongNew.php")
    Call<List<SongOnline>> getDataSongNewOnline();


    @GET("Notification.php")
    Call<List<Notification>> getDataNotification();


    @GET("PlayList.php")
    Call<List<PlayList>> getDataPlayList();

    @FormUrlEncoded
    @POST("SelectPlayListSong.php")
    Call<List<SongOnline>> getDataPlayListSong(@Field("NAME_PLAYLIST") String NAME_PLAYLIST);

    @FormUrlEncoded
    @POST("AddPlayList.php")
    Call<String> InsertPlayList(@Field("ID_SONG") int ID_SONG, @Field("NAME_PLAYLIST") String NAME_PLAYLIST);

    @FormUrlEncoded
    @POST("RemoveSongPlayList.php")
    Call<String> RemoveSongPlayList(@Field("ID_SONG") int ID_SONG, @Field("NAME_PLAYLIST") String NAME_PLAYLIST);

    @FormUrlEncoded
    @POST("RenamePlayList.php")
    Call<String> RenamePlayList(  @Field("NAME_PLAYLIST") String NAME_PLAYLIST , @Field("NAME_PLAYLIST_NEW") String NAME_PLAYLIST_NEW );

    @FormUrlEncoded
    @POST("DeletePlayList.php")
    Call<String> DeletePlayList(  @Field("NAME_PLAYLIST") String NAME_PLAYLIST);

    @FormUrlEncoded
    @POST("CountSongPlayList.php")
    Call<Integer> CountSongPlayList(  @Field("NAME_PLAYLIST") String NAME_PLAYLIST);

    @FormUrlEncoded
    @POST("UpdateViewSong.php")
    Call<String> UpdateViewSong(  @Field("ID_SONG") int ID_SONG);
}