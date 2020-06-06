package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserRegister;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @GET("users/{login}")
    Call<User> getUserById(@Path("login") String login);

    @GET("users")
    Call<List<User>> getAllUsersPagination(@Query("page") int page, @Query("size") int size);

    @POST("register")
    Call<ResponseBody> registerUser(@Body UserRegister user);

    @PUT("users/{login}/follow")
    Call<Follow> followUser(@Path("login") String userLogin);

    @POST("account")
    Call<ResponseBody> updateProfile(@Body User user);

    @POST("account/change-password")
    Call<ResponseBody> updatePassword(@Body ChangePassword changePassword);

    @GET("me/followings")
    Call<List<User>> getMeFollowings();

    @GET("me/followers")
    Call<List<UserPublicInfo>> getMeFollowers();

    @DELETE("account")
    Call<ResponseBody> deleteAccount();

    @GET("users/{login}/following")
    Call<List<User>> getAllFollowingsFromUser(@Path("login") String login);

    @GET("users/{login}/followers")
    Call<List<User>> getAllFollowersFromUser(@Path("login") String login);
}
