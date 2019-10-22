package com.example.activitymusic.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("ID_SONG")
    @Expose
    private String iDSONG;
    @SerializedName("TITLE")
    @Expose
    private String tITLE;
    @SerializedName("CONTENT")
    @Expose
    private String cONTENT;
    @SerializedName("DATE")
    @Expose
    private String dATE;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getIDSONG() {
        return iDSONG;
    }

    public void setIDSONG(String iDSONG) {
        this.iDSONG = iDSONG;
    }

    public String getTITLE() {
        return tITLE;
    }

    public void setTITLE(String tITLE) {
        this.tITLE = tITLE;
    }

    public String getCONTENT() {
        return cONTENT;
    }

    public void setCONTENT(String cONTENT) {
        this.cONTENT = cONTENT;
    }

    public String getDATE() {
        return dATE;
    }

    public void setDATE(String dATE) {
        this.dATE = dATE;
    }
}
