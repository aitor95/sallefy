package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playback;
import com.salle.android.sallefy.model.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlaybackService {

    @GET("playbacks")
    Call<List<Playback>> getPlaybacksByUser(@Query("username") String username, @Header("Authorization") String token);

    @GET("playbacks")
    Call<List<Playback>> getPlaybacksByGenre(@Query("genre") String genre, @Header("Authorization") String token);

}
