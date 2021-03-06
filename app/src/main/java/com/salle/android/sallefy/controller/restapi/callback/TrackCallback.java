package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Track;

import java.util.List;



public interface TrackCallback extends FailureCallback {
    void onTracksReceived(List<Track> tracks);
    void onTrackById(Track track);
    void onNoTracks(Throwable throwable);
    void onPersonalTracksReceived(List<Track> tracks);
    void onUserTracksReceived(List<Track> tracks);
    void onCreateTrack(Track track);
    void onUpdatedTrack();
    void onTrackDeleted(Track track);
    void onPopularTracksReceived(List<Track> tracks);
}
