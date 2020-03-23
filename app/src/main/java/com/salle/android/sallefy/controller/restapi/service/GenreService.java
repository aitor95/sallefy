package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GenreService {

    @GET("genres/{id}")
    Call<Genre> getGenreById(@Path("id") Integer id, @Header("Authorization") String token);

    @GET("genres")
    Call<List<Genre>> getAllGenres(@Header("Authorization") String token);

    @GET("genres/{id}/tracks")
    Call<List<Track>> getTracksByGenre(@Path("id") Integer id, @Header("Authorization") String token);
}
