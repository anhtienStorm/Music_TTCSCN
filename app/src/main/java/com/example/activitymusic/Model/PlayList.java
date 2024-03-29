package com.example.activitymusic.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayList {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("NAME_SONG")
    @Expose
    private String nAMESONG;
    @SerializedName("SINGER")
    @Expose
    private String sINGER;
    @SerializedName("IMAGE")
    @Expose
    private String iMAGE;
    @SerializedName("LINK_SONG")
    @Expose
    private String lINKSONG;
    @SerializedName("VIEW")
    @Expose
    private String vIEW;
    @SerializedName("ID_TL")
    @Expose
    private String iDTL;
    @SerializedName("NAME_PLAYLIST")
    @Expose
    private String nAMEPLAYLIST;
    @SerializedName("ICON")
    @Expose
    private String iCON;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getNAMESONG() {
        return nAMESONG;
    }

    public void setNAMESONG(String nAMESONG) {
        this.nAMESONG = nAMESONG;
    }

    public String getSINGER() {
        return sINGER;
    }

    public void setSINGER(String sINGER) {
        this.sINGER = sINGER;
    }

    public String getIMAGE() {
        return iMAGE;
    }

    public void setIMAGE(String iMAGE) {
        this.iMAGE = iMAGE;
    }

    public String getLINKSONG() {
        return lINKSONG;
    }

    public void setLINKSONG(String lINKSONG) {
        this.lINKSONG = lINKSONG;
    }

    public String getVIEW() {
        return vIEW;
    }

    public void setVIEW(String vIEW) {
        this.vIEW = vIEW;
    }

    public String getIDTL() {
        return iDTL;
    }

    public void setIDTL(String iDTL) {
        this.iDTL = iDTL;
    }

    public String getNAMEPLAYLIST() {
        return nAMEPLAYLIST;
    }

    public void setNAMEPLAYLIST(String nAMEPLAYLIST) {
        this.nAMEPLAYLIST = nAMEPLAYLIST;
    }

    public String getICON() {
        return iCON;
    }

    public void setICON(String iCON) {
        this.iCON = iCON;
    }
}
