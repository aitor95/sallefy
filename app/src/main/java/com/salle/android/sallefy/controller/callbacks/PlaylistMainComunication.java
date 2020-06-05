package com.salle.android.sallefy.controller.callbacks;

import com.salle.android.sallefy.model.Track;

public interface PlaylistMainComunication extends AdapterClickCallback{
    void deleteSong(Track track);
    //TODO DELTE THIS
    void updateTrackDataFromEverywhere(Track track);
}
