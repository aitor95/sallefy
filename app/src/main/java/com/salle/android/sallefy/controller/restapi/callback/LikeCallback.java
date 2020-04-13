package com.salle.android.sallefy.controller.restapi.callback;

public interface LikeCallback {
    void onLikeSuccess();
    void onFailure(Throwable throwable);
}
