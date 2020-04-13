package com.salle.android.sallefy.utils;

public class Constants {
    public static final String URL = "url";

    public interface INTENT_EXTRAS{
        String SONG = "_EXTRA_SONG_";
    }

    public interface CALLBACKS {
        public static String MUSIC_CALLBACK = "musicCallback";
    }

    public interface NETWORK {
        public static  String BASE_URL = "http://" + "sallefy.eu-west-3.elasticbeanstalk.com/api/";
    }

    public interface PERMISSIONS {
        public static int MICROPHONE = 7;
    }

    public interface SENDING {
        public static String PLAYLIST = "sendingPlaylist";
        public static String INDEX = "sendingIndex";
        public static String TRACK = "sendingTrack";
        public static String TRACKS = "sendingTracks";
    }

    public interface ACTION {
        public static String STARTFOREGROUND_ACTION = "salle.android.sallefy_heroes_v10A.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "salle.android.projects.sallefy_heroes_v10.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
