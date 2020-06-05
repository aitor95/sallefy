package com.salle.android.sallefy.utils;

public class Constants {
    final String URL = "url";

    public interface INTENT_EXTRAS{
        String PLAYER_SONG = "_EXTRA_SONG_" ;
        String PLAYER_PLAYLIST = "_EXTRA_PLAYLIST_" ;
        String isSongLikedBottomMenu = "_EXTRA_SONG_LIKED_";
        String SELECTED_PLAYLIST_UPDATE = "_SELECTED_PLAYLIST_UPDATE_";
        String PLAYLIST = "_PLAYLIST_";
        String TRACK = "_TRACK_";
        String PLAYLIST_DATA = "_PLAYLIST_DATA_";
        String CURRENT_TRACK = "_CURRENT_TRACK_";
        String PLAYLIST_ID = "_PLAYLIST_ID_";
        String USER = "_USER_";
    }

    public interface CALLBACKS {
        String MUSIC_CALLBACK = "musicCallback";
    }

    public interface NETWORK {
         String BASE_URL = "http://" + "sallefy.eu-west-3.elasticbeanstalk.com/api/";
    }

    public interface PERMISSIONS {
        int MICROPHONE = 7;
    }

    public interface SENDING {
        String PLAYLIST = "sendingPlaylist";
        String INDEX = "sendingIndex";
        String TRACK = "sendingTrack";
        String TRACKS = "sendingTracks";
    }

    public interface EDIT_CONTENT {
        int PLAYLIST_EDIT = 8;
        int TRACK_EDITING_FINISHED = 9;
        int ACTIVITY_PLAYLIST_FINISHED = 10;
        int RESULT_MP_USER = 11;
        int RESULT_MP_ORDINARY = 12;
        int RESULT_MP_TRACK_EDITED = 13;
        int RESULT_MP_DELETE = 14;
        int RESULT_PA_USER = 15;
        int RESULT_PA_DELETE = 16;
        int MUSIC_PLAYER_FINISHED = 5005;
    }

    public interface ACTION {
        String STARTFOREGROUND_ACTION = "salle.android.sallefy_heroes_v10A.action.startforeground";
        String STOPFOREGROUND_ACTION = "salle.android.projects.sallefy_heroes_v10.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public interface STORAGE {
        int SONG_SELECTED = 4;
        int IMAGE_SELECTED = 5;
        String PLAYLIST_COVER_FOLDER = "sallefy/covers/playlists";
        String TRACK_COVER_FOLDER = "sallefy/covers/songs";
        String USER_PICTURE_FOLDER = "sallefy/users";
        String TRACK_AUDIO_FOLDER = "sallefy/tracks";
    }
}
