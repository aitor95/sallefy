package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.SearchCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.service.SearchService;
import com.salle.android.sallefy.controller.restapi.service.TrackService;
import com.salle.android.sallefy.model.Like;
import com.salle.android.sallefy.model.Search;
import com.salle.android.sallefy.model.SearchResult;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchManager {

    private static final String TAG = "TrackManager";
    private Context mContext;
    private static SearchManager sTrackManager;
    private Retrofit mRetrofit;
    private SearchService mSearchService;


    public static SearchManager getInstance (Context context) {
        if (sTrackManager == null) {
            sTrackManager = new SearchManager(context);
        }

        return sTrackManager;
    }

    public SearchManager(Context context) {
        mContext = context;

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mSearchService = mRetrofit.create(SearchService.class);
    }

    public synchronized void search(String keyword, final SearchCallback searchCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<SearchResult> call = mSearchService.search(keyword, "Bearer " + userToken.getIdToken());
        Log.d("TAGG", "S'ha executat");

        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                Log.d("TAGG", "Ha arribat resposta");
                Log.d("TAGG", response.toString());

                int code = response.code();

                if (response.isSuccessful()) {
                    searchCallback.onSearchResultReceived(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    //searchCallback.onNoTracks(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                searchCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }
}
