package com.salle.android.sallefy.utils;

import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

//dont even try to argue my naming skills...
public interface UpdatableFragment {

    void updateSongInfo(Track track);
    void updatePlaylistInfo(ArrayList<Playlist> mUpdatedPlaylists);
}
