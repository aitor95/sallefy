package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @SerializedName("activated")
    private Boolean activated;

    @SerializedName("authorities")
    private List<String> authorities = null;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("id")
    private Integer id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("langKey")
    private String langKey;

    @SerializedName("lastModifiedBy")
    private String lastModifiedBy;

    @SerializedName("lastModifiedDate")
    private String lastModifiedDate;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("playlists")
    private Integer playlists;

    @SerializedName("tracks")
    private Integer tracks;

    @SerializedName("followers")
    private Integer followers;

    @SerializedName("following")
    private Integer following;

    @SerializedName("login")
    private String login;

    private Boolean followedByUser;
    private long timeSinceLastFollow;

    public Boolean getFollowedByUser() {
        return followedByUser;
    }

    public void setFollowedByUser(Boolean followedByUser) {
        this.followedByUser = followedByUser;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Integer playlists) {
        this.playlists = playlists;
    }

    public Integer getTracks() {
        return tracks;
    }

    public void setTracks(Integer tracks) {
        this.tracks = tracks;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getTimeSinceLastFollow() {
        return timeSinceLastFollow;
    }

    public void setTimeSinceLastFollow(long currentTimeMillis) {
        timeSinceLastFollow = currentTimeMillis;
    }
}