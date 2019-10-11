package com.example.activitymusic.Model;

public class Song {
    private int id;
    private String nameSong;
    private String pathSong;
    private String singer;
    private String albumID;
    private String duration;

    public Song(int id, String nameSong, String pathSong, String singer, String albumID, String duration) {
        this.id = id;
        this.nameSong = nameSong;
        this.pathSong = pathSong;
        this.singer = singer;
        this.albumID = albumID;
        this.duration = duration;
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

    public String getDuration() {
        return duration;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setId(int id) {
        this.id = id;
    }
}
