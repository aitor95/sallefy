package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.salle.android.sallefy.utils.CloudinaryConfigs;
import com.salle.android.sallefy.utils.FilenameHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager extends AppCompatActivity {

    public static final String TAG = CloudinaryManager.class.getName();

    private static CloudinaryManager sManager;
    private Context mContext;


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

    public synchronized void uploadAudioFile(String folder, Uri fileUri, String fileName, final UploadCallback uploadCallback) {
        Map<String, Object> options = new HashMap<>();

        options.put("public_id", fileName);
        options.put("folder", folder);
        options.put("resource_type", "video");

        MediaManager.get().upload(fileUri)
                .unsigned(fileName)
                .options(options)
                .callback(uploadCallback)
                .dispatch();
    }

    public synchronized void uploadCoverImage(String folder, Uri fileUri, String fileName, final UploadCallback uploadCallback) {

        Map<String, Object> options = new HashMap<>();
        options.put("public_id", fileName);
        options.put("folder", folder);
        options.put("resource_type", "image");


        MediaManager.get().upload(fileUri)
                .unsigned(fileName)
                .options(options)
                .callback(uploadCallback)
                .dispatch();
    }

    public synchronized void deleteAudioFile(String fileUri){
        Log.d(TAG, "DELETING AUDIO FILE WITH URI: \'" + fileUri + "\'");

        Map<String, Object> options = new HashMap<>();
        options.put("invalidate","true");
        options.put("resource_type", "video");

        new DeleteConnection().execute("sallefy/tracks/"+FilenameHelper.extractPublicIdFromUri(fileUri),options);
    }

    public synchronized void deleteCoverImage(String fileUri ,boolean isTrack){
        if(fileUri == null) return;

        Log.d(TAG, "DELETING CoverImage FILE WITH URI: \'" + fileUri + "\'");

        Map<String, Object> options = new HashMap<>();
        options.put("invalidate","true");
        options.put("resource_type", "image");

        String path =  isTrack ? "sallefy/covers/songs/" : "sallefy/covers/playlists/";

        new DeleteConnection().execute(path + FilenameHelper.extractPublicIdFromUri(fileUri), options);
    }

    public synchronized void deleteUserImage(){

    }

}

class DeleteConnection extends AsyncTask{
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d(CloudinaryManager.TAG, "doInBackground: DELETING NAME " + (String)objects[0]);
            Map a = MediaManager.get().getCloudinary().uploader().destroy((String)objects[0],(Map)objects[1]);

            Object res = a.get("result");

            if( res instanceof String){
                Log.d(CloudinaryManager.TAG, "Cloudinary delete response is " + res);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}