package com.salle.android.sallefy.utils;

import android.content.Context;

import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserToken;
import java.util.ArrayList;

public class Session {

    public static Session sSession;
    private static Object mutex = new Object();

    private Context mContext;

    private User mUser;
    private UserToken mUserToken;

    private boolean audioEnabled;

    private Track mTrack;
    private Playlist mPlaylist;
    private ArrayList<Track> mTracks;
    private int mIndex;
    private boolean isPlaying;

    public static Session getInstance(Context context) {
        Session result = sSession;
        if (result == null) {
            synchronized (mutex) {
                result = sSession;
                if (result == null)
                    sSession = result = new Session();
            }
        }
        return result;
    }

    private Session() {}

    public Session (Context context) {
        this.mContext = context;
        this.mUser = null;
        this.mUserToken = null;
        this.isPlaying = false;
        this.audioEnabled = false;
    }

    public void resetValues() {
        mUser = null;
        mUserToken = null;
        mTrack = null;
        mPlaylist = null;
        mIndex = -1;
        isPlaying = false;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public UserToken getUserToken() {
        return mUserToken;
    }

    public void setUserToken(UserToken userToken) {
        mUserToken = userToken;
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        this.audioEnabled = audioEnabled;
    }

    public Track getTrack() {
        return mTrack;
    }

    public void setTrack(Track track) {
        mTrack = track;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setTrack(ArrayList<Track> tracks, int index) {
        mTracks = tracks;
        mIndex = index;
        mTrack = tracks.get(index);
    }

    public void setTrack(Playlist playlist, int index) {
        mTracks = (ArrayList<Track>) playlist.getTracks();
        mIndex = index;
        mTrack = mTracks.get(index);
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        mTracks = tracks;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
