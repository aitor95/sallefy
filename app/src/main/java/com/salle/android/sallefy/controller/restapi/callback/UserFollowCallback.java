package com.salle.android.sallefy.controller.restapi.callback;


public interface UserFollowCallback extends FailureCallback{
    void onFollowUnfollowSuccess(String userLogin);
}
