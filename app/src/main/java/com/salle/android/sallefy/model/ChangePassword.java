package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChangePassword implements Serializable {

    @SerializedName("currentPassword")
    private String currentPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
