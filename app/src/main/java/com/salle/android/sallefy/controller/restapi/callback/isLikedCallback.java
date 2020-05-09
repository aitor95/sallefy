package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.Like;

public interface isLikedCallback extends FailureCallback{
    void onIsLiked(int songId, Like isLiked);
}
