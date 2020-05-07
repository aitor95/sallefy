package com.salle.android.sallefy.controller.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.salle.android.sallefy.controller.activities.MusicPlayerActivity;
import com.salle.android.sallefy.controller.music.MusicService;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String TAG = NotificationReceiver.class.getName();

    public static final String PLAY_STOP_ACTION = "PLAY_STOP_ACTION";
    public static final String NEXT_ACTION = "NEXT_ACTION";
    public static final String PREV_ACTION = "PREV_ACTION";
    public static final String ENTER_SONG_ACTION = "ENTER_SONG_ACTION";

    private MusicService mBoundService;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent musicService = new Intent(context, MusicService.class);

        IBinder service = peekService(context,musicService);
        MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
        mBoundService = binder.getService();

        if(intent.getAction() == null)return;

        boolean isPlaying = mBoundService.isPlaying();
        switch (intent.getAction()) {
            case PLAY_STOP_ACTION:

                if(isPlaying){
                    mBoundService.pauseSong();
                }else{
                    mBoundService.playSong();
                }
                CustomNotification.updateAndShow(context, isPlaying);
                mBoundService.updatePlayButton();
                break;
            case NEXT_ACTION:
                mBoundService.changeTrack(1);
                break;
            case PREV_ACTION:
                mBoundService.changeTrack(-1);
                break;
            case ENTER_SONG_ACTION:

                //Close notification bar
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(it);

                //Go to the player.
                Intent goToPlayer = new Intent(context, MusicPlayerActivity.class);
                goToPlayer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                goToPlayer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(goToPlayer);
                break;
        }
    }
}
