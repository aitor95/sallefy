package com.salle.android.sallefy.controller.restapi.callback;

public interface LikeCallback {
    void onLikeSuccess(int songId);
    void onFailure(Throwable throwable);
}
