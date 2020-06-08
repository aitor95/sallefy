package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.callback.isLikedCallback;
import com.salle.android.sallefy.controller.restapi.service.TrackService;
import com.salle.android.sallefy.model.LatLong;
import com.salle.android.sallefy.model.Like;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.controller.download.ObjectBox;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackManager extends BaseManager{

    public static final String TAG = TrackManager.class.getName();

    private static TrackManager sTrackManager;
    private TrackService mTrackService;


    public static TrackManager getInstance (Context context) {
        if (sTrackManager == null) {
            sTrackManager = new TrackManager(context);
        }

        return sTrackManager;
    }

    public TrackManager(Context context) {
        super(context);

        mTrackService = mRetrofit.create(TrackService.class);
    }

    public synchronized void createTrack(Track track, final TrackCallback trackCallback) {

        Call<Track> call = mTrackService.createTrack(track);
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    trackCallback.onCreateTrack((Track) response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void updateTrack(Track track, final TrackCallback trackCallback) {


        Call<ResponseBody> call = mTrackService.updateTrack(track);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    trackCallback.onUpdatedTrack();
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void likeTrack(int songId, Boolean like, final LikeCallback likeCallback) {


        Call<ResponseBody> call = mTrackService.likeTrack(songId, new Like(like));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    likeCallback.onLikeSuccess(songId);
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    likeCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                likeCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void isTrackLiked(int trackId, final isLikedCallback isLiked){

        Call<Like> call = mTrackService.isTrackLiked(trackId);
        call.enqueue(new Callback<Like>() {
            @Override
            public void onResponse(Call<Like> call, Response<Like> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    isLiked.onIsLiked(trackId, response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    isLiked.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Like> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getMessage());
                isLiked.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /********************   ALL TRACKS    ********************/

    public synchronized void getAllTracksPagination(final TrackCallback trackCallback, int currentPage, int size, boolean recent, boolean popular) {


        Call<List<Track>> call = mTrackService.getAllTracksPagination(currentPage, size, recent, popular);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    //  String nextPage = response.headers().get("next");
                    trackCallback.onTracksReceived(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onNoTracks(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getUserTracks(String login, int currentPage, int size, boolean popular, final TrackCallback trackCallback) {


        Call<List<Track>> call = mTrackService.getUserTracks(login, currentPage, size, popular);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    trackCallback.onUserTracksReceived(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onNoTracks(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getOwnTracks(final TrackCallback trackCallback) {

        Call<List<Track>> call = mTrackService.getOwnTracks();
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    trackCallback.onPersonalTracksReceived((ArrayList<Track>) response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onNoTracks(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getTopTracks(int limit, final TrackCallback trackCallback) {

        Call<List<Track>> call = mTrackService.getTopPopularTracks(limit, true);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    trackCallback.onPopularTracksReceived(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onNoTracks(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /**
     * DELETES THE TRACK
     */
    public void deleteTrack(Track track, final TrackCallback trackCallback) {

        Call<ResponseBody> call = mTrackService.deleteTrack(track.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    ObjectBox.getInstance().removeTrack(track);
                    trackCallback.onTrackDeleted(track);
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    /********************   GET TRACK    ********************/

    public synchronized void getTrack(final TrackCallback trackCallback, int id) {

        Call<Track> call = mTrackService.getTrack(id);
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    //  String nextPage = response.headers().get("next");
                    trackCallback.onTrackById(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    trackCallback.onNoTracks(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                trackCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public void playTrack(Track track, LatLong latLong){

        Call<ResponseBody> call = mTrackService.playTrack(track.getId(), latLong);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error, Play track was not Successful: " + code);
                }else{
                    Log.d(TAG, "Playing all good bby!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error Failure on play track: " + t.getStackTrace());
            }
        });
    }
}
