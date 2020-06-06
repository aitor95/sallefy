package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<SearchResult> search(@Query("keyword") String keyword);
}
