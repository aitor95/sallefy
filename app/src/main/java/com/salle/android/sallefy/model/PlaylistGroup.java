package com.salle.android.sallefy.model;

import java.util.ArrayList;

public class PlaylistGroup {

    private String title;
    private ArrayList<Playlist> content;

    public PlaylistGroup(String title, ArrayList<Playlist> content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Playlist> getContent() {
        return content;
    }

    public void setContent(ArrayList<Playlist> content) {
        this.content = content;
    }
}
