package com.salle.android.sallefy.controller.callbacks;

import com.salle.android.sallefy.model.Track;

public interface TrackListCallback {
    void onTrackSelected(Track track);
    void onTrackSelected(int index);
    void onTrackUpdated(Track track);
}
