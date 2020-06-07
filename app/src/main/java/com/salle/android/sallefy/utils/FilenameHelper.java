package com.salle.android.sallefy.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

    public static boolean isInvalidFile(Uri uri, Context c) {
        if(uri == null) return true;

        //Check image size:
        AssetFileDescriptor afd = null;
        try {
            afd = c.getContentResolver().openAssetFileDescriptor(uri,"r");
            if(afd != null) {
                long fileSize = afd.getLength();
                afd.close();
                afd = null;
                Log.d("TEST", "isInvalidFile: file size is " + fileSize + "bytes");
                if (fileSize < 10000000L) {
                    Toast.makeText(c, "Image too big. Max 10MB", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if(afd != null){
                try {
                    afd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(FilenameHelper.getMimeType(uri,c).contains("tiff")) {
            Toast.makeText(c, "Tiff images are not supported.", Toast.LENGTH_SHORT).show();
            return true;
        }


        return false;
    }
}

