package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistFollowCallback;
import com.salle.android.sallefy.controller.restapi.service.PlaylistService;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistManager extends BaseManager{

    private static final String TAG = PlaylistManager.class.getName();

    private static PlaylistManager sUserManager;

    private PlaylistService mService;

    public static PlaylistManager getInstance(Context context) {
        if (sUserManager == null) {
            sUserManager = new PlaylistManager(context);
        }
        return sUserManager;
    }

    private PlaylistManager(Context cntxt) {
        super(cntxt);
        mService = mRetrofit.create(PlaylistService.class);
    }


    /**********************
     * Get playlist by id
     **********************/
    public synchronized void getPlaylistById(Integer id, final PlaylistCallback playlistCallback) {
        Call<Playlist> call = mService.getPlaylistById(id);
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistById(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /**********************
     * Get all playlist of backend
     **********************/

    public synchronized void getListOfPlaylistPagination (final PlaylistCallback playlistCallback, int currentPage, int size, boolean popular) {

        Call<List<Playlist>> call = mService.getAllPlaylistsPagination(currentPage, size, popular);

        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {

                int code = response.code();
                ArrayList<Playlist> data = (ArrayList<Playlist>) response.body();

                if (response.isSuccessful()) {
                    playlistCallback.onAllList(data);
                    Log.d(TAG, "getList");

                } else {
                    Log.d(TAG, "Error: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                playlistCallback.onFailure(t);
            }
        });
    }

    /**********************
     * Get playlists by user
     **********************/
    public synchronized void getPlaylistsByUser(String login, final PlaylistCallback playlistCallback) {
        Call<List<Playlist>> call = mService.getUserPlaylistsById(login);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistsByUser((ArrayList<Playlist>) response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /**********************
    * Get the playlist the current user follows
     **********************/
    public synchronized void getFollowingPlaylists (final PlaylistCallback playlistCallback) {
        Call<List<Playlist>> call = mService.getFollowingPlaylists();
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    ArrayList<Playlist> playlists = (ArrayList<Playlist>) response.body();
                    playlistCallback.onFollowingList(playlists);

                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }


    /**********************
     * Update info of a playlist
     **********************/
    public synchronized void updatePlaylist(Playlist playlist, final PlaylistCallback playlistCallback){
        Call<Playlist> call = mService.updatePlaylist(playlist);
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistUpdated();
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message() + " \n"+ response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });

    }

    /**********************
     * Follows a playlist
     **********************/
    public synchronized void followPlaylist(Playlist playlist, Boolean isFollowing, final PlaylistFollowCallback playlistCallback){
        Call<Follow> call = mService.updateFollow(playlist.getId(), isFollowing);
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onFollowSuccess(playlist);
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message() + " \n"+ response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });

    }

    /**********************
     * Create a playlist
     **********************/
    public synchronized void createPlaylist(Playlist playlist, final PlaylistCallback playlistCallback){
        Call<Playlist> call = mService.createPlaylist(playlist);
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistCreated(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });

    }

    /**********************
     * Checks if current user follows a playlist
     **********************/
    public void getUserFollows(Integer pId, PlaylistCallback playlistCallback) {
        Call<Follow> call = mService.getUserFollowing(pId);
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onUserFollows(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /**********************
     * Current user follows/Unfollows playlist
     **********************/
    public void setUserFollows(Integer pId, Boolean followed, PlaylistCallback playlistCallback) {
        Call<Follow> call = mService.updateFollow(pId, followed);
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onUpdateFollow(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /**********************
     * Gets current user's own playlists
     **********************/
    public void getOwnPlaylists(PlaylistCallback playlistCallback){
        Call<List<Playlist>> call = mService.getOwnPlaylists();
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onOwnList((ArrayList) response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /***
     * DELETES THE PLAYLIST
     */
    public void deletePlaylist(int id, PlaylistCallback playlistCallback) {
        Call<ResponseBody> call = mService.deletePlaylist(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistDeleted();
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getTopPlaylists(int limit, final PlaylistCallback playlistCallback) {

        Call<List<Playlist>> call = mService.getTopPopularPlaylists(limit, true);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPopularPlaylistsReceived(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

}
