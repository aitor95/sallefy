package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist implements Serializable {

    @SerializedName("cover")
    private String cover;

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("publicAccessible")
    private Boolean publicAccessible;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("owner")
    private User user;

    @SerializedName("tracks")
    private List<Track> tracks = null;

    @SerializedName("followers")
    private Integer followers;

    private boolean followed;
    private boolean isStartAsShuffle;
    private boolean isDeleted;
    private boolean isLocalPlaylist;

    public Playlist(){

    }

    public Playlist(int i,String title, String author, String thumbnail) {
        this.id = i;
        this.name = title;
        this.user = new User();
        user.setLogin(author);
        this.thumbnail = thumbnail;
        isDeleted = false;
        isLocalPlaylist = false;
    }

    public Playlist(ArrayList<Track> tracks) {
        this.tracks = (ArrayList<Track>) tracks.clone();
        isLocalPlaylist = true;
        isDeleted = false;
    }

    public Playlist(Playlist mPlaylist) {
        this.id = mPlaylist.id;

        this.name = mPlaylist.name;
        this.user = mPlaylist.user;
        this.description = mPlaylist.description;
        this.cover = mPlaylist.cover;
        this.publicAccessible = mPlaylist.publicAccessible;
        this.thumbnail = mPlaylist.thumbnail;
        this.followed = mPlaylist.followed;
        this.followers = mPlaylist.followers;
        this.isStartAsShuffle = mPlaylist.isStartAsShuffle;
        //Duele a la vista, pero funciona.
        this.tracks = (ArrayList<Track>) ((ArrayList<Track>) mPlaylist.tracks).clone();
        isDeleted = false;
        isLocalPlaylist = false;
    }


    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublicAccessible() {
        return publicAccessible;
    }

    public void setPublicAccessible(Boolean publicAccessible) {
        this.publicAccessible = publicAccessible;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserLogin() {
        return user.getLogin();
    }

    public void setUserLogin(String userLogin) {
        if (user == null) { user = new User(); }
        user.setLogin(userLogin);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public boolean isFollowed() {
        return followed;
    }

    public int getFollowers(){
        return followers;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public void shuffle() {
        Collections.shuffle(tracks);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();	// return shallow copy
    }

    public void startAsShuffle(boolean isStartAsShuffle) {
        this.isStartAsShuffle = isStartAsShuffle;
    }

    public boolean getIsShuffleStart() {
        return isStartAsShuffle;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isLocalPlaylist() {
        return isLocalPlaylist;
    }

    public void setLocalPlaylist(boolean localPlaylist) {
        isLocalPlaylist = localPlaylist;
    }
}
