package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.Like;
import com.salle.android.sallefy.model.Track;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrackService {

    @GET("tracks")
    Call<List<Track>> getAllTracksPagination(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size, @Query("recent") boolean recent, @Query("popular") boolean popular);

    @GET("tracks")
    Call<List<Track>> getAllTracksSocial(@Header("Authorization") String token, @Query("size") int size, @Query("recent") boolean recent);

    @GET("me/tracks")
    Call<List<Track>> getOwnTracks(@Header("Authorization") String token);

    @GET("users/{login}/tracks")
    Call<List<Track>> getUserTracks(@Path("login") String login, @Header("Authorization") String token);

    @POST("tracks")
    Call<Track> createTrack(@Body Track track, @Header("Authorization") String token);

    @PUT("tracks")
    Call<ResponseBody> updateTrack(@Body Track track, @Header("Authorization") String token);

    @PUT("tracks/{id}/like")
    Call<ResponseBody> likeTrack(@Path("id") int id, @Body Like like, @Header("Authorization") String token);

    @GET("tracks/{id}/like")
    Call<Like> isTrackLiked(@Path("id") int id, @Header("Authorization") String token);

    @DELETE("tracks/{id}")
    Call<ResponseBody> deleteTrack(@Path("id") int id, @Header("Authorization") String token);
}
