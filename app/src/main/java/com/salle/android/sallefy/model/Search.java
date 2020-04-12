package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Search implements Serializable {

    @SerializedName("keyword")
    private String keyword;

    public Search(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}