package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.SearchCallback;
import com.salle.android.sallefy.controller.restapi.service.SearchService;
import com.salle.android.sallefy.model.SearchResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchManager extends BaseManager {

    private static final String TAG = SearchManager.class.getName();
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
        super(context);

        mSearchService = mRetrofit.create(SearchService.class);
    }

    public synchronized void search(String keyword, final SearchCallback searchCallback) {


        Call<SearchResult> call = mSearchService.search(keyword);

        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {

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
