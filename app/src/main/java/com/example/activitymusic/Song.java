package com.example.activitymusic;

import android.graphics.Bitmap;

public class Song {
    private int id;
    private String nameSong;
    private String pathSong;
    private String singer;
    private Bitmap bmImageSong;

    public Song(int id, String nameSong, String dataSong, String singer, Bitmap bmImageSong) {
        this.id = id;
        this.nameSong = nameSong;
        this.pathSong = dataSong;
        this.singer = singer;
        this.bmImageSong = bmImageSong;
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

    public Bitmap getBmImageSong() {
        return bmImageSong;
    }

    public void setId(int id) {
        this.id = id;
    }
}
