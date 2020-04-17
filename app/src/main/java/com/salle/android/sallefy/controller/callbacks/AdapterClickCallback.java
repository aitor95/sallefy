package com.salle.android.sallefy.controller.callbacks;

import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;

public interface AdapterClickCallback {

    void onTrackClicked(Track track, Playlist playlist);
    void onPlaylistClick(Playlist playlist);
    void onUserClick(User user);
    void onGenreClick(Genre genre);
}

