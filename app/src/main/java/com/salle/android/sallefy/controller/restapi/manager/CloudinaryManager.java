package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.CloudinaryConfigs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager extends AppCompatActivity {

    private static CloudinaryManager sManager;
    private Context mContext;
    private String mFileName;
    private Genre mGenre;


    public static CloudinaryManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new CloudinaryManager(context);
        }
        return sManager;
    }

    private CloudinaryManager(Context context) {
        mContext = context;
        MediaManager.init(mContext, CloudinaryConfigs.getConfigurations());
    }

    public synchronized void uploadAudioFile(Uri fileUri, String fileName, Genre genre, final UploadCallback uploadCallback) {
        mGenre = genre;
        mFileName = fileName;
        Map<String, Object> options = new HashMap<>();
        options.put("public_id", fileName);
        options.put("folder", "sallefy/songs/mobile");
        options.put("resource_type", "video");

        MediaManager.get().upload(fileUri)
                .unsigned(fileName)
                .options(options)
                .callback(uploadCallback)
                .dispatch();
    }

    public synchronized void uploadCoverImage(Uri fileUri, String fileName, final UploadCallback uploadCallback) {
        mFileName = fileName;
        Map<String, Object> options = new HashMap<>();
        options.put("public_id", fileName);
        options.put("folder", "sallefy/covers/playlists");
        options.put("resource_type", "image");

        MediaManager.get().upload(fileUri)
                .unsigned(fileName)
                .options(options)
                .callback(uploadCallback)
                .dispatch();
    }

    /*private class CloudCallback implements UploadCallback {

        @Override
        public void onStart(String requestId) {
        }

        @Override
        public void onProgress(String requestId, long bytes, long totalBytes) {
            Double progress = (double) bytes/totalBytes;
        }

        @Override
        public void onSuccess(String requestId, Map resultData) {
            Track track = new Track();
            track.setId(null);
            track.setName(mFileName);
            //track.setUser(Session.getInstance(mContext).getUser());
            //track.setUserLogin(Session.getInstance(mContext).getUser().getLogin());
            track.setUrl((String) resultData.get("url"));
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(mGenre);
            track.setGenres(genres);
            TrackManager.getInstance(mContext).createTrack(track, mCallback);
        }

        @Override
        public void onError(String requestId, ErrorInfo error) {

        }

        @Override
        public void onReschedule(String requestId, ErrorInfo error) {

        }
    }*/
}