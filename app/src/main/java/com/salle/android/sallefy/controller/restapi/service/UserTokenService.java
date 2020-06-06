package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.UserLogin;
import com.salle.android.sallefy.model.UserToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserTokenService {

    @POST("authenticate")
    Call<UserToken> loginUser(@Body UserLogin login);
}
