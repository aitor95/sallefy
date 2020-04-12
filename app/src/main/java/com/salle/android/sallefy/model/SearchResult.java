package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SearchResult implements Serializable {

    @SerializedName("playlists")
    private List<Playlist> playlists;

    @SerializedName("users")
    private List<User> users;

    @SerializedName("tracks")
    private List<Track> tracks;

    @SerializedName("genres")
    private List<Genre> genres;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}