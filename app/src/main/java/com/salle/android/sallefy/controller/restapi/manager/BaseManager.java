package com.salle.android.sallefy.controller.restapi.manager;


//Interceptors de OKHTTP

import android.content.Context;

import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseManager {

    public static final String TAG = BaseManager.class.getName();
    protected static Retrofit mRetrofit;

    public BaseManager(Context c){
        if(mRetrofit == null)
            changeToken(c);
    }

    public static void changeToken(Context c){

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();

        okhttpBuilder.addInterceptor(
                new Interceptor() {
             @Override
             public Response intercept(Chain chain) throws IOException {
                 Request request = chain.request();
                 UserToken userToken = Session.getInstance(c).getUserToken();
                 if (userToken != null) {
                     Request.Builder newRequest = request.newBuilder().header("Authorization", "Bearer " + userToken.getIdToken());
                     return chain.proceed(newRequest.build());
                 } else return chain.proceed(request);
             }
        });

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttpBuilder.build())
                .build();
    }
}
