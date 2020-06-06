package com.salle.android.sallefy.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

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
                        return removeSpecialCharsAndTail(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                return removeSpecialCharsAndTail(myFile.getName());
            }
            return uri.toString();
        }
        return "error";
    }

    public static String removeSpecialCharsAndTail(String in){
        if (in.contains("."))
            in = in.substring(0, in.lastIndexOf("."));

        in = in.replace("ñ","n")
                .replace("Ñ","N")
                .replace("ç","c")
                .replace("Ç","C");

        String regex = in.replaceAll("[^a-zA-Z0-9_ ]","");

        return regex;
    }

    public static String extractPublicIdFromUri(String fileUri) {
        Log.d("TEST", "extractPublicIdFromUri: EXTRACTING PUBLIC ID FROM " + fileUri);
        String decoded = Uri.decode(fileUri);
        int lastBar = decoded.lastIndexOf("/");
        String result = removeSpecialCharsAndTail(decoded.substring(lastBar + 1));
        Log.d("TEST", "extractPublicIdFromUri: OBTAINED " + result);
        return result;
    }

    public static String getMimeType(Uri uri,Context context) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

}

