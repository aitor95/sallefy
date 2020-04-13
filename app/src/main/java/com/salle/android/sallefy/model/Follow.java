package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Follow implements Serializable {

    @SerializedName("followed")
    private Boolean follow;

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public Follow(Boolean follow) {
        this.follow = follow;
    }
}