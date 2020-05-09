package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Playlist;

public interface PlaylistFollowCallback extends FailureCallback{
    void onFollowSuccess(Playlist playlist);
}
