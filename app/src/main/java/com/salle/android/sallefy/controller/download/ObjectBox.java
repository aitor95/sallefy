package com.salle.android.sallefy.controller.download;

import android.content.Context;


import com.salle.android.sallefy.model.DownloadFile;
import com.salle.android.sallefy.model.DownloadFile_;
import com.salle.android.sallefy.model.MyObjectBox;
import com.salle.android.sallefy.model.Track;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class ObjectBox {

    private static ObjectBox sObjectBox;
    private static BoxStore boxStore;

    Box<DownloadFile> fileBox;
    Box<Track> trackBox;


    public static ObjectBox getInstance() {
        if (sObjectBox == null) {
            sObjectBox = new ObjectBox();
        }
        return sObjectBox;
    }

    public static void init(Context context){
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    public static BoxStore get() {
        return boxStore;
    }

    public void createBoxes(){
        trackBox = ObjectBox.getInstance().get().boxFor(Track.class);
        fileBox = ObjectBox.getInstance().get().boxFor(DownloadFile.class);
    }

    public byte[] getFile(Integer trackId){
        List<DownloadFile> dFile = fileBox.query().equal(DownloadFile_.id, trackId).build().find();
        return dFile.get(0).getTrackFile();
    }

    public void addTrack(Track track, DownloadFile downloadFile){
        trackBox.put(track);
        fileBox.put(downloadFile);
    }

    public void removeTrack(Track track){
        if(checkIfFileExists(track)){
            trackBox.remove(track);
            List<DownloadFile> dFile = fileBox.query().equal(DownloadFile_.id, track.getId()).build().find();
            fileBox.remove(dFile.get(0));
        }
        System.out.println("hola");

    }

    public boolean checkIfFileExists(Track track){
        List<DownloadFile> dFile = fileBox.query().equal(DownloadFile_.id, track.getId()).build().find();
        if(dFile.size() != 0){
            return true;
        }else{
            return false;
        }
    }
}