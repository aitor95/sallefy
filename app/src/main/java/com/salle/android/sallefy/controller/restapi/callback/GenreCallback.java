package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

public interface GenreCallback extends FailureCallback {
    void onGenresReceive(ArrayList<Genre> genres);
    void onTracksByGenre(ArrayList<Track> tracks);
}
