package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.LatLong;
import com.salle.android.sallefy.model.Like;
import com.salle.android.sallefy.model.Track;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrackService {

    @GET("tracks")
    Call<List<Track>> getAllTracksPagination(@Query("page") int page, @Query("size") int size, @Query("recent") boolean recent, @Query("popular") boolean popular);

    @GET("tracks")
    Call<List<Track>> getAllTracksSocial(@Query("size") int size, @Query("recent") boolean recent);

    @GET("me/tracks")
    Call<List<Track>> getOwnTracks();

    @GET("users/{login}/tracks")
    Call<List<Track>> getUserTracks(@Path("login") String login, @Query("page") int page, @Query("size") int size, @Query("popular") boolean popular);

    @POST("tracks")
    Call<Track> createTrack();

    @PUT("tracks")
    Call<ResponseBody> updateTrack(@Body Track track);

    @PUT("tracks/{id}/like")
    Call<ResponseBody> likeTrack(@Path("id") int id, @Body Like like);

    @GET("tracks/{id}/like")
    Call<Like> isTrackLiked(@Path("id") int id);

    @DELETE("tracks/{id}")
    Call<ResponseBody> deleteTrack(@Path("id") int id);

    @GET("tracks")
    Call<List<Track>> getTopPopularTracks(@Query("size") int limit, @Query("liked") Boolean sortByPlayed);

    @PUT("tracks/{id}/play")
    Call<ResponseBody> playTrack(@Path("id") int id, @Body LatLong latLong);
}
