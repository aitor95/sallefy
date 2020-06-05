package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.PlaybackCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.service.PlaybackService;
import com.salle.android.sallefy.controller.restapi.service.UserTokenService;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playback;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaybackManager {

    private static final String TAG = "PlaybackManager";

    private static PlaybackManager sPlaybackManager;
    private Retrofit mRetrofit;
    private Context mContext;

    private PlaybackService mService;
    private UserTokenService mTokenService;

    public static PlaybackManager getInstance(Context context) {
        if (sPlaybackManager == null) {
            sPlaybackManager = new PlaybackManager(context);
        }
        return sPlaybackManager;
    }

    private PlaybackManager(Context cntxt) {
        mContext = cntxt;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(PlaybackService.class);
        mTokenService = mRetrofit.create(UserTokenService.class);
    }


    public synchronized void getPlaybacksByUser(final PlaybackCallback playbackCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        //TODO: Quan no sigui testing, posar la linia comentada (ja que ara mateix no hi ha info dels nostres users)
        //Call<List<Playback>> call = mService.getPlaybacksByUser(Session.getInstance(mContext).getUser().getLogin(), "Bearer " + userToken.getIdToken());
        Call<List<Playback>> call = mService.getPlaybacksByUser("test", "Bearer " + userToken.getIdToken());

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
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<Playback>> call = mService.getPlaybacksByGenre(genre, "Bearer " + userToken.getIdToken());

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
