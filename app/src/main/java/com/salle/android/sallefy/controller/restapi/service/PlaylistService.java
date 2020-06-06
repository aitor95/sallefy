package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;

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

public interface PlaylistService {

    @GET("playlists/{id}")
    Call<Playlist> getPlaylistById(@Path("id") Integer id);

    @GET("playlists")
    Call<List<Playlist>> getAllPlaylistsPagination(@Query("page") int page, @Query("size") int size, @Query("popular") boolean popular);

    @DELETE("playlists/{id}")
    Call<ResponseBody> deletePlaylist(@Path("id") Integer id);

    @GET("me/playlists")
    Call<List<Playlist>> getOwnPlaylists();

    @GET("playlists/{id}/follow")
    Call<Follow> getUserFollowing(@Path("id") Integer id);

    @PUT("playlists/{id}/follow")
    Call<Follow> updateFollow(@Path("id") Integer id, @Body Boolean follow);

    @GET("me/playlists/following")
    Call<List<Playlist>> getFollowingPlaylists();

    @GET("users/{login}/playlists")
    Call<List<Playlist>> getUserPlaylistsById(@Path("login") String login);

    @PUT("playlists")
    Call<Playlist> updatePlaylist(@Body Playlist playlist);

    @POST("playlists")
    Call<Playlist> createPlaylist(@Body Playlist playlist);

    @GET("playlists")
    Call<List<Playlist>> getTopPopularPlaylists(@Query("size") int limit, @Query("popular") Boolean sortByFollowed);
}
