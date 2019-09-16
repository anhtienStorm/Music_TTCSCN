package com.example.activitymusic;

import android.graphics.Bitmap;

public class Song {
    private int id;
    private String nameSong;
    private String pathSong;
    private String singer;
    private String albumID;
//    private Bitmap bmImageSong;

//    public Song(int id, String nameSong, String dataSong, String singer, Bitmap bmImageSong) {
//        this.id = id;
//        this.nameSong = nameSong;
//        this.pathSong = dataSong;
//        this.singer = singer;
//        this.bmImageSong = bmImageSong;
//    }


    public Song(int id, String nameSong, String pathSong, String singer, String albumID) {
        this.id = id;
        this.nameSong = nameSong;
        this.pathSong = pathSong;
        this.singer = singer;
        this.albumID = albumID;
    }

    public int getId() {
        return id;
    }

    public String getNameSong() {
        return nameSong;
    }

    public String getPathSong() {
        return pathSong;
    }

    public String getSinger() {
        return singer;
    }

//    public Bitmap getBmImageSong() {
//        return bmImageSong;
//    }


    public String getAlbumID() {
        return albumID;
    }

    public void setId(int id) {
        this.id = id;
    }
}
