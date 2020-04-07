package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.Playlist;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlaylistService {

    @GET("playlists/{id}")
    Call<Playlist> getPlaylistById(@Path("id") Integer id, @Header("Authorization") String token);

    @GET("playlists")
    Call<List<Playlist>> getAllPlaylists(@Header("Authorization") String token);

    @GET("me/playlists")
    Call<List<Playlist>> getOwnPlaylists(@Header("Authorization") String token);

    @GET("playlists/{id}/follow")
    Call<ResponseBody> getUserFollowing(@Path("id") Integer id, @Header("Authorization") String token);

    @PUT("playlists/{id}/follow")
    Call<ResponseBody> updateFollow(@Path("id") Integer id, @Body Boolean follow, @Header("Authorization") String token);

    @GET("me/playlists/following")
    Call<List<Playlist>> getFollowingPlaylists(@Header("Authorization") String token);

    @GET("users/{login}/playlists")
    Call<List<Playlist>> getUserPlaylistsById(@Path("login") String login, @Header("Authorization") String token);

    @PUT("playlists")
    Call<Playlist> updatePlaylist(@Body Playlist playlist, @Header("Authorization") String token);

    @POST("playlists")
    Call<Playlist> createPlaylist(@Body Playlist playlist, @Header("Authorization") String token);


}
