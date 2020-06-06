package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;
import io.objectbox.relation.ToOne;

//@Entity
public class Track implements Serializable, Comparable<Track> {

    //@Id long idOB;

    @SerializedName("color")
    private String color;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("genres")
    private List<Genre> genres = null;

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

   // @Convert(converter = UserConverter.class, dbType = String.class)
    @SerializedName("owner")
    private User user;

    @SerializedName("released")
    private String released;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("url")
    private String url;

    private boolean selected = false;

    private boolean liked;

    private int likes;

    private boolean isDeleted;

    //private LikeCallback likeCallback;

    //private TrackListVerticalAdapter.ViewHolder viewHolder;

    public int getLikes() {
        return likes;
    }

    /*public TrackListVerticalAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }*/

    /*public void setViewHolder(TrackListVerticalAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }*/

    /*public LikeCallback getLikeCallback() {
        return likeCallback;
    }*/

    /*public void setLikeCallback(LikeCallback likeCallback) {
        this.likeCallback = likeCallback;
    }*/

    private int reproductions;

    public int getReproductions() {
        return reproductions;
    }

    public void setReproductions(int reproductions) {
        this.reproductions = reproductions;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
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
        user.setLogin(userLogin);
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return id.equals(track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Track track) {
        return (reproductions < track.reproductions) ? 1 : -1;
    }

  /*  public static class UserConverter implements PropertyConverter<User, String> {

        @Override
        public User convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
           /* UserManager.getInstance(this).getUserData(databaseValue, new UserCallback() {

            });*/
         /*   User user = new User();
            user.setLogin(databaseValue);
            return user;
        }

        @Override
        public String convertToDatabaseValue(User entityProperty) {
            return entityProperty == null ? null : entityProperty.getLogin();
        }
    }*/
}

