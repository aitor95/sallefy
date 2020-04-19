package com.salle.android.sallefy.model;

import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;

public class TrackViewPack {

    private Track track;
    private TrackListVerticalAdapter.ViewHolder viewHolder;
    private LikeCallback callback;

    public TrackViewPack(Track track, TrackListVerticalAdapter.ViewHolder viewHolder, LikeCallback callback) {
        this.track = track;
        this.viewHolder = viewHolder;
        this.callback = callback;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public TrackListVerticalAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(TrackListVerticalAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public LikeCallback getCallback() {
        return callback;
    }

    public void setCallback(LikeCallback callback) {
        this.callback = callback;
    }
}
