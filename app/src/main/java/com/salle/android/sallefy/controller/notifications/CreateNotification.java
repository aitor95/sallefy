package com.salle.android.sallefy.controller.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import static com.salle.android.sallefy.controller.notifications.NotificationReceiver.ENTER_SONG_ACTION;
import static com.salle.android.sallefy.controller.notifications.NotificationReceiver.NEXT_ACTION;
import static com.salle.android.sallefy.controller.notifications.NotificationReceiver.PLAY_STOP_ACTION;
import static com.salle.android.sallefy.controller.notifications.NotificationReceiver.PREV_ACTION;


public class CreateNotification {
    private static final String CHANNEL_ID = "music_notifications";
    public static final int NOTIFICATION_ID = 1;

    public static Notification notification;

    public static RemoteViews notificationLayout;

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
    public static void createNotification(Context context, Track track, boolean isPlaying){
        Log.d("ss", "createNotification: ");
        Glide.with(context)
                .asBitmap()
                .load(track.getThumbnail())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            generateNotification(context,track,resource,isPlaying);
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

    private static void generateNotification(Context context, Track track, Bitmap resource,boolean isPlaying) {
        createNotificationChannel(context);

        // Get the layouts to use in the custom notification
        notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notificacion_reproductor);

        Intent imageAndSongClick = new Intent(context, NotificationReceiver.class);
        imageAndSongClick.setAction(ENTER_SONG_ACTION);
        PendingIntent imageAndSongClickPendingIntent = PendingIntent.getBroadcast(context,
                0,imageAndSongClick,0);


        Intent nextClick = new Intent(context, NotificationReceiver.class);
        nextClick.setAction(NEXT_ACTION);
        PendingIntent nextPending = PendingIntent.getBroadcast(context,
                0,nextClick,0);


        Intent prevClick = new Intent(context, NotificationReceiver.class);
        prevClick.setAction(PREV_ACTION);
        PendingIntent prevPending = PendingIntent.getBroadcast(context,
                0,prevClick,0);


        Intent playClick = new Intent(context, NotificationReceiver.class);
        playClick.setAction(PLAY_STOP_ACTION);
        PendingIntent playPending = PendingIntent.getBroadcast(context,
                0,playClick,0);

        if(!isPlaying){
            notificationLayout.setImageViewResource(R.id.notif_play_pause,R.drawable.ic_play_not);
        }else{
            notificationLayout.setImageViewResource(R.id.notif_play_pause,R.drawable.ic_pause_not);
        }

        notificationLayout.setTextViewText(R.id.notif_song_title,track.getName());
        notificationLayout.setTextViewText(R.id.notif_song_author,track.getUserLogin());
        notificationLayout.setImageViewBitmap(R.id.notif_image,resource);

        notificationLayout.setOnClickPendingIntent(R.id.notif_play_pause,playPending);
        notificationLayout.setOnClickPendingIntent(R.id.notif_next,nextPending);
        notificationLayout.setOnClickPendingIntent(R.id.notif_prev,prevPending);

        notificationLayout.setOnClickPendingIntent(R.id.notif_image, imageAndSongClickPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notif_main_layout,imageAndSongClickPendingIntent);

        notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sallefy)
                .setContent(notificationLayout)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);

    }

    public static void updateAndShow(Context context,boolean isNotPlaying) {

        if(isNotPlaying){
            notificationLayout.setImageViewResource(R.id.notif_play_pause,R.drawable.ic_play_not);
        }else{
            notificationLayout.setImageViewResource(R.id.notif_play_pause,R.drawable.ic_pause_not);
        }

        notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sallefy)
                .setContent(notificationLayout)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }
}
