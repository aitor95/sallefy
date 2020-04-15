package com.salle.android.sallefy.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.model.Track;


public class CreateNotification {
    private static final String CHANNEL_ID = "music_notifications";
    private static final int NOTIFICATION_ID = 1;

    public static Notification notification;

    private static void createNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Notificatons";
            String description = "Display a notification to control the audio outside the app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    public static void createNotification(Context context, Track track){
        Log.d("ss", "createNotification: ");
        Glide.with(context)
                .asBitmap()
                .load(track.getThumbnail())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            createNotificationChannel(context);

                            // Get the layouts to use in the custom notification
                            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.mini_reproductor);

                            notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_sallefy)
                                    /*.setContentTitle("Ramon")
                                    .setContentText("ramon")
                                    .setLargeIcon(resource)
                                    .setOnlyAlertOnce(true)
                                    .setShowWhen(false)
                                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                    */
                                    //.setCustomContentView(notificationLayout)
                                    //.setCustomBigContentView(notificationLayoutExpanded)
                                    .setContent(notificationLayout)
                                    .setOnlyAlertOnce(true)
                                    .setShowWhen(false)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .build();

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("ss", "onResourceReady: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}
