package com.salle.android.sallefy.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;

public class FilenameHelper {

    public static String extractFromUri(Uri uri, Context c) {
        if(uri != null) {
            // Get the Uri of the selected file
            String uriString = uri.toString();
            File myFile = new File(uriString);

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = c.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        return removeTail(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                return removeTail(myFile.getName());
            }
            return uri.toString();
        }
        return "error";
    }

    private static String removeTail(String in){
        if (in.indexOf(".") > 0)
            return in.substring(0, in.lastIndexOf("."));
        return in;
    }

    public static String extractPublicIdFromUri(String fileUri) {
        String decoded = Uri.decode(fileUri);
        int lastBar = decoded.lastIndexOf("/");
        String result = removeTail(decoded.substring(lastBar + 1));
        Log.d("TEST", "extractPublicIdFromUri: OBTAINED " + result);
        return result;
    }
}

