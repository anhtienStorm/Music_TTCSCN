package com.example.activitymusic.Server;

public class APIServer {
    private static String BASE_URL = "https://appmusicttn.000webhostapp.com/";

    public static DataServer getServer(){
        return APIRetrofitClient.getClient(BASE_URL).create(DataServer.class);
    }
}
