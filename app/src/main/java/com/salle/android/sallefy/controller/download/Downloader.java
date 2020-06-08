package com.salle.android.sallefy.controller.download;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.salle.android.sallefy.controller.dialogs.StateDialog;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.model.DownloadFile;
import com.salle.android.sallefy.model.Track;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class Downloader {

    private static final String TAG = Downloader.class.getName();

    private static byte[] downloadedFile;
    private static byte[] downloadedFileFullScreen;

    private static boolean finished;

    public static void downloadCloudinaryAudio(Context context,Track track,int w,int h){

        String urlData = track.getUrl();
        String urlDataFull = null;
        if (track.getUrl().toLowerCase().contains("mp4")) {
            urlData = CloudinaryManager.getInstance(context).createVideoThumbnail(track.getUrl());
            urlDataFull = CloudinaryManager.getInstance(context).createVideoThumbnailFullScreen(track.getUrl(), w, h);
        }

        finished = false;

        new GetFileFromURL().execute(urlData,urlDataFull);
        Log.d("TAG", "Download successful");
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        byte[]aux = byteBuffer.toByteArray();
        byteBuffer.close();

        Log.d("TAG", "Seems like it downloaded the file...");
        System.out.println("INPUT DATA FINISH");
        System.out.println("INPUT DATA LENGTH: " + aux.length);
        return aux;
    }

    public static byte[] getDownloadedFile() { return downloadedFile; }
    public static byte[] getDownloadedFileFullScreen() { return downloadedFileFullScreen; }


    public static boolean isFinished() { return finished; }

    public static void download(Track track, Context context,int w,int h) {

        Downloader.downloadCloudinaryAudio(context,track,w,h);

        StateDialog stateDialog = StateDialog.getInstance(context);
        stateDialog.showStateDialog(false);

        //TODO: CHANGE THIS WITH A CALLBACK.

        while(!Downloader.isFinished()){
            Log.d(TAG, "Download in progress");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stateDialog.close();
        DownloadFile trackFile = new DownloadFile(
                track.getId(),
                track.getUrl(),
                Downloader.getDownloadedFile(),
                Downloader.getDownloadedFileFullScreen());
        ObjectBox.getInstance(context).addTrack(track, trackFile);
        System.out.println("Download finished");
    }

    static class GetFileFromURL extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                int i = 0;
                for(Object o : objects){
                    if(o == null){
                        finished = true;
                        return null;
                    }
                    Log.d(TAG, "doInBackground: Downloading file number " + i);
                    URL url = new URL((String)o);
                    URLConnection conn = url.openConnection();
                    InputStream iStream = conn.getInputStream();
                    if(i == 0)
                        downloadedFile =  getBytes(iStream);
                    else
                        downloadedFileFullScreen =  getBytes(iStream);

                    iStream.close();
                    i++;
                }
                finished = true;
            } catch (IOException e) {
                Log.d("TAG", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}
