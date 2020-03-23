package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;

public interface PlaylistCallback extends FailureCallback {

    void onPlaylistById(Playlist playlist);

    void onPlaylistsByUser(ArrayList<Playlist> playlists);

    void onAllList(ArrayList<Playlist> playlists);

    void onFollowingList(ArrayList<Playlist> playlists);

}
