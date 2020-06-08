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

    public DownloadFile(Integer id, String url, byte[] tf) {
        this.id = id;
        this.url = url;
        this.trackFile = tf;
    }


    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public byte[] getTrackFile() { return trackFile; }

    public void setTrackFile(byte[] trackFile) { this.trackFile = trackFile; }

    public long getIdOB() { return idOB; }

}
