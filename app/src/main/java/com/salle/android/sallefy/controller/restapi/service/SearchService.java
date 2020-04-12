package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.Like;
import com.salle.android.sallefy.model.Search;
import com.salle.android.sallefy.model.SearchResult;
import com.salle.android.sallefy.model.Track;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<SearchResult> search(@Query("keyword") String keyword, @Header("Authorization") String token);
}
