package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;

import com.salle.android.sallefy.controller.restapi.callback.PlaybackCallback;
import com.salle.android.sallefy.controller.restapi.service.PlaybackService;
import com.salle.android.sallefy.model.Playback;
import com.salle.android.sallefy.utils.Session;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaybackManager extends BaseManager{

    private static final String TAG = PlaybackManager.class.getName();

    private static Context mContext;
    private static PlaybackManager sPlaybackManager;

    private PlaybackService mService;

    public static PlaybackManager getInstance(Context context) {
        if (sPlaybackManager == null) {
            sPlaybackManager = new PlaybackManager(context);
        }
        mContext = context;
        return sPlaybackManager;
    }

    private PlaybackManager(Context cntxt) {
        super(cntxt);

        mService = mRetrofit.create(PlaybackService.class);
    }


    public synchronized void getPlaybacksByUser(final PlaybackCallback playbackCallback) {

        //TODO: Quan no sigui testing, posar la linia comentada (ja que ara mateix no hi ha info dels nostres users)
        Call<List<Playback>> call = mService.getPlaybacksByUser(Session.getInstance(mContext).getUser().getLogin());
        //Call<List<Playback>> call = mService.getPlaybacksByUser("test");

        call.enqueue(new Callback<List<Playback>>() {
            @Override
            public void onResponse(Call<List<Playback>> call, Response<List<Playback>> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    playbackCallback.onPlaybacksByUserReceived(response.body());
                } else {
                    try {
                        playbackCallback.onFailure(new Throwable("ERROR " + code + ", " + response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Playback>> call, Throwable t) {
                playbackCallback.onFailure(t);
            }
        });
    }

    public synchronized void getPlaybacksByGenre(String genre, final PlaybackCallback playbackCallback) {

        Call<List<Playback>> call = mService.getPlaybacksByGenre(genre);

        call.enqueue(new Callback<List<Playback>>() {
            @Override
            public void onResponse(Call<List<Playback>> call, Response<List<Playback>> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    playbackCallback.onPlaybacksByGenreReceived(response.body(), genre);
                } else {
                    try {
                        playbackCallback.onFailure(new Throwable("ERROR " + code + ", " + response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Playback>> call, Throwable t) {
                playbackCallback.onFailure(t);
            }
        });
    }
}
