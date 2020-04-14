package com.salle.android.sallefy.controller.restapi.callback;


public interface UserFollowCallback {

    void onFollowSuccess(String userLogin);
    void onFailure(Throwable throwable);
}
