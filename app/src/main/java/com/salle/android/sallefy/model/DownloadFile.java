package com.salle.android.sallefy.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class DownloadFile {

    @Id
    long idOB;

    private Integer id;
    private String url;

    private byte[] trackFile;
    private byte[] trackFileFullScreen;

    public DownloadFile(Integer id, String urlBase, byte[] tf, byte[] tfFullScreen) {
        this.id = id;
        this.url = urlBase;
        this.trackFile = tf;
        this.trackFileFullScreen = tfFullScreen;
    }


    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public byte[] getTrackFile(){
        return trackFile;
    }
    public byte[] getTrackFileFullScreen(){
        return trackFileFullScreen;
    }
    public byte[] getTrackFile(boolean fullScreen) {
        return fullScreen ? trackFileFullScreen : trackFile;
    }

    public void setTrackFile(byte[] trackFile) { this.trackFile = trackFile; }

    public long getIdOB() { return idOB; }

}
