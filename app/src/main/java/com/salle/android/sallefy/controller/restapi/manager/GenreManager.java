package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.GenreCallback;
import com.salle.android.sallefy.controller.restapi.service.GenreService;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreManager extends BaseManager{

    private static final String TAG = GenreManager.class.getName();
    private static GenreManager sGenreManager;

    private GenreService mService;

    public static GenreManager getInstance(Context context) {
        if (sGenreManager == null) {
            sGenreManager = new GenreManager(context);
        }
        return sGenreManager;
    }

    private GenreManager(Context cntxt) {
        super(cntxt);
        mService = mRetrofit.create(GenreService.class);
    }

    public synchronized void getAllGenres(final GenreCallback genreCallback) {


        Call<List<Genre>> call = mService.getAllGenres();

        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                int code = response.code();
                ArrayList<Genre> data = (ArrayList<Genre>) response.body();

                if (response.isSuccessful()) {
                    genreCallback.onGenresReceive(data);

                } else {
                    Log.d(TAG, "Error: " + code);
                    genreCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.d(TAG, "Error: " + t);
                genreCallback.onFailure(new Throwable("ERROR " + t.getMessage() ));
            }
        });
    }

    public synchronized void getTracksByGenre(int genreId, final GenreCallback genreCallback) {


        Call<List<Track>> call = mService.getTracksByGenre(genreId);

        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                int code = response.code();
                ArrayList<Track> data = (ArrayList<Track>) response.body();

                if (response.isSuccessful()) {
                    genreCallback.onTracksByGenre(data);

                } else {
                    Log.d(TAG, "Error: " + code);
                    genreCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.d(TAG, "Error: " + t);
                genreCallback.onFailure(new Throwable("ERROR " + t.getMessage() ));
            }
        });

    }

}
