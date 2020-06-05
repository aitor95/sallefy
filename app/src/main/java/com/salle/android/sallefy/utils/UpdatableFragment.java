package com.salle.android.sallefy.utils;

import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

/**
 * Tots els fragments que s'hagin de actualitzar despres de la edició de una playlist o una track
 * han d'implementar aquesta interficie.
 * */


//dont even try to argue my naming skills...
public interface UpdatableFragment {

    void updateSongInfo(Track track);
    void updatePlaylistInfo(ArrayList<Playlist> mUpdatedPlaylists);
}
