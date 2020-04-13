package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Playlist;

public interface PlaylistFollowCallback {

    void onFollowSuccess(Playlist playlist);
    void onFailure(Throwable throwable);
}
