package com.salle.android.sallefy.controller.music;

public interface MusicCallback {

    void onMusicPlayerPrepared();
    void onUpdatePlayButton();
    void onSongFinishedPlaying();
    void onSongChanged();
}
