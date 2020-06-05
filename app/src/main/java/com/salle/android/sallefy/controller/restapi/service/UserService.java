package com.salle.android.sallefy.controller.restapi.service;

import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserRegister;
import com.salle.android.sallefy.model.UserToken;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @GET("users/{login}")
    Call<User> getUserById(@Path("login") String login, @Header("Authorization") String token);

    @GET("users")
    Call<List<User>> getAllUsersPagination(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    @GET("users")
    Call<List<User>> getAllUsersMeFragment(@Header("Authorization") String token, @Query("size") int size);

    @POST("register")
    Call<ResponseBody> registerUser(@Body UserRegister user);

    @PUT("users/{login}/follow")
    Call<Follow> followUser(@Path("login") String userLogin, @Header("Authorization") String token);

    @GET("users/{login}/follow")
    Call<Follow> isFollowingUser(@Path("login") String userLogin, @Header("Authorization") String token);

    @POST("account")
    Call<ResponseBody> updateProfile(@Body User user, @Header("Authorization") String token);

    @POST("account/change-password")
    Call<ResponseBody> updatePassword(@Body ChangePassword changePassword, @Header("Authorization") String token);

    @GET("me/followings")
    Call<List<User>> getMeFollowings(@Header("Authorization") String token);

    @GET("me/followers")
    Call<List<UserPublicInfo>> getMeFollowers(@Header("Authorization") String token);

    @DELETE("account")
    Call<ResponseBody> deleteAccount(@Header("Authorization") String token);

    @GET("users/{login}/following")
    Call<List<User>> getAllFollowingsFromUser(@Header("Authorization") String s, @Path("login") String login);

    @GET("users/{login}/followers")
    Call<List<User>> getAllFollowersFromUser(@Header("Authorization") String s, @Path("login") String login);
}
