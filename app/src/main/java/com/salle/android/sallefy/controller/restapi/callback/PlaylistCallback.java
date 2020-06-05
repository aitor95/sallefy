package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public interface PlaylistCallback extends FailureCallback {

    void onPlaylistById(Playlist playlist);

    void onPlaylistsByUser(ArrayList<Playlist> playlists);

    void onOwnList(ArrayList<Playlist> playlists);

    void onAllList(ArrayList<Playlist> playlists);

    void onFollowingList(ArrayList<Playlist> playlists);

    void onPlaylistUpdated();

    void onPlaylistCreated(Playlist playlist);

    void onUserFollows(Follow follows);

    void onUpdateFollow(Follow result);

    void onPlaylistDeleted();

    void onPopularPlaylistsReceived(List<Playlist> playlists);
}
