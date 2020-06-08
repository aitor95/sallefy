package com.salle.android.sallefy.controller.download;

import android.content.Context;
import okhttp3.ResponseBody;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import android.os.AsyncTask;
import android.util.Log;



public class Downloader {
    private Context context;

    private byte[] downloadedFile;

    private boolean finished;

    public Downloader(Context context){
        this.context = context;
    }

    public void downloadCloudinaryAudio(String urlData) {

        this.finished = false;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // InputStream iStream = context.getContentResolver().openInputStream(Uri.parse(url));
                    // URLConnection conn = null;
                    URLConnection conn = null;
                    URL url = new URL(urlData);
                    conn = url.openConnection();
                    InputStream iStream = conn.getInputStream();
                    byte[] inputData = getBytes(iStream);
                    iStream.close();

                } catch (IOException e) {
                    Log.d("TAG", e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        Log.d("TAG", "Download successful");
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        this.downloadedFile = byteBuffer.toByteArray();
        byteBuffer.close();
        this.finished = true;

        Log.d("TAG", "Seems like it downloaded the file...");
        System.out.println("INPUT DATA FINISH");
        System.out.println("INPUT DATA LENGTH: " + downloadedFile.length);
        return downloadedFile;
    }

    public byte[] getDownloadedFile() { return downloadedFile; }

    public boolean isFinished() { return finished; }

}
