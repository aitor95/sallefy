package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Like implements Serializable {

    @SerializedName("liked")
    private Boolean liked;

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Like(Boolean liked) {
        this.liked = liked;
    }
}