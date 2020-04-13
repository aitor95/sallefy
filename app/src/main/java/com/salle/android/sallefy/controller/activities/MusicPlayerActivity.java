package com.salle.android.sallefy.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog.BottomMenuDialogInterf;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.OnSwipeListener;

import java.util.ArrayList;
import java.util.List;


public class MusicPlayerActivity extends AppCompatActivity implements MusicCallback, TrackListCallback, TrackCallback, BottomMenuDialogInterf {

    public static final String TAG = MusicPlayerActivity.class.getName();

    // Service
    private MusicService mBoundService;
    private boolean mServiceBound = false;

    //Lista de tracks a reproducir.
    private ArrayList<Track> mTracks;
    private int currentTrack = 0;

    //Buttons del reproductor
    private ImageButton playPause;
    //El boton de playStop usa los siguientes tags para saber si tiene el drawable pause o play.
    private static final String PLAY = "Play";
    private static final String STOP = "Stop";

    private ImageButton nextTrack;
    private ImageButton prevTrack;
    private ImageButton like;
    private ImageButton more;
    private TextView songTitle;
    private TextView songAuthor;
    private TextView currTrackTime;
    private TextView totalTrackTime;

    private ImageView thumbnail;
    private SeekBar seekBar;

    //Thread management para la seekbar
    private Handler mHandler;
    Runnable mSeekBarUpdater = new Runnable() {
        @Override
        public void run() {
            try{

                updateSeekBar();
            }catch (Exception e){
                e.printStackTrace();
            }finally{
                mHandler.postDelayed(mSeekBarUpdater, 1000);
            }
        }
    };

    //Gesture detector.
    private GestureDetectorCompat detector;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mBoundService = binder.getService();
            mBoundService.setCallback(MusicPlayerActivity.this);
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mSeekBarUpdater);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startStreamingService();
        setContentView(R.layout.activity_music_player);
        atachButtons();

        detector = new GestureDetectorCompat(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                boolean rtn = false;
                switch (direction) {
                    case down:
                        Log.d(TAG, "onSwipe: DOWN");
                        rtn = true;
                        break;
                    case up:
                        Log.d(TAG, "onSwipe: UP");
                        showMoreMenu();
                        rtn = true;
                        break;
                    case left:
                        Log.d(TAG, "onSwipe: LEFT");
                        rtn = true;
                        break;
                    case right:
                        Log.d(TAG, "onSwipe: RIGHT");
                        rtn = true;
                        break;
                }
                return rtn;
            }
        });

        //Get all the tracks!
        TrackManager.getInstance(this).getAllTracks(this);
        mTracks = new ArrayList<>();

        mHandler = new Handler();
        //Important to run this line here, we need the UI thread!
        mSeekBarUpdater.run();
    }

    private void startStreamingService () {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void atachButtons(){
        playPause = findViewById(R.id.music_player_playStop);
        playPause.setTag(PLAY);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playPause.getTag().equals(PLAY)){
                    playAudio();
                }else{
                    pauseAudio();
                }
            }
        });

        nextTrack = findViewById(R.id.music_player_next);
        nextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               nextTrack();
            }
        });

        prevTrack = findViewById(R.id.music_player_prev);
        prevTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevTrack();
            }
        });

        seekBar = findViewById(R.id.music_player_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectedPosition = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: Going to " + userSelectedPosition);
                mBoundService.setCurrentDuration(userSelectedPosition);
                //updateSeekBar();
            }
        });


        like = findViewById(R.id.music_player_fav);
        like.setTag("Fav");

        TrackCallback callback = this;
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(like.getTag().equals("Fav")){
                    like.setImageResource(R.drawable.ic_favourite_grey_24dp);
                    like.setTag("NoFav");
                    TrackManager.getInstance(getApplicationContext()).likeTrack(currentTrack, false, callback);
                }else{
                    like.setTag("Fav");
                    like.setImageResource(R.drawable.ic_favorite_black_24dp);
                    TrackManager.getInstance(getApplicationContext()).likeTrack(currentTrack, true, callback);
                }
            }
        });

        more = findViewById(R.id.music_player_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getWindow().setNavigationBarColor(RED);
                showMoreMenu();

            }
        });

        songTitle = findViewById(R.id.music_player_title);
        songAuthor = findViewById(R.id.music_player_author);
        currTrackTime = findViewById(R.id.music_player_curr_time);
        totalTrackTime = findViewById(R.id.music_player_total_time);

        thumbnail = findViewById(R.id.music_player_thumbnail);
    }

    private void showMoreMenu() {
        BottomMenuDialog dialog = new BottomMenuDialog();
        dialog.show(getSupportFragmentManager(),"options");
    }

    private void playAudio() {
        if (!mBoundService.isPlaying()) { mBoundService.togglePlayer(); }
        //updateSeekBar();

        playPause.setImageResource(R.drawable.ic_pause_circle_64dp);
        playPause.setTag(STOP);

        Toast.makeText(this, "Playing Audio", Toast.LENGTH_SHORT).show();
    }

    private void pauseAudio() {
        if (mBoundService.isPlaying()) { mBoundService.togglePlayer(); }
        playPause.setImageResource(R.drawable.ic_play_circle_filled_64dp);
        playPause.setTag(PLAY);
        Toast.makeText(this, "Pausing Audio", Toast.LENGTH_SHORT).show();
    }

    private void nextTrack() {
        int size = mTracks.size(); //Only query size once.
        currentTrack = (currentTrack - 1) % size;

        //Si nos pasamos por debajo, pon el indice al final.
        currentTrack = currentTrack < 0 ? (mTracks.size() - 1) : currentTrack;
        updateTrack(currentTrack);
    }

    private void prevTrack() {
        int size = mTracks.size(); //Only query size once.
        currentTrack = (currentTrack + 1) % size;

        //Si nos pasamos por encima, pon el indice al inicio.
        currentTrack = currentTrack >= size ? 0 : currentTrack;
        updateTrack(currentTrack);
    }


    private void updateTrack(int index) {
        Track track = mTracks.get(index);
        currentTrack = index;

        songAuthor.setText(track.getUserLogin());
        songTitle.setText(track.getName());

        //Modificar el thumbnail
        Glide.with(this)
                .asBitmap()
                .placeholder(R.drawable.ic_audiotrack)
                .load(track.getThumbnail())
                .into(thumbnail);

        //Esta linea para la canción
        mBoundService.playStream(mTracks.get(index).getUrl());

        //Update seekbar data:
        totalTrackTime.setText(getTimeFromSeconds(mBoundService.getMaxDuration()));
        seekBar.setMax(mBoundService.getMaxDuration());
        //updateSeekBar();


        //Asi que modificamos el icono de playPause acorde.
        playPause.setImageResource(R.drawable.ic_pause_circle_64dp);
        playPause.setTag(STOP);
    }

    private void updateSeekBar(){
        if(!mServiceBound) return;
        //Log.d(TAG, "updateSeekBar: time is " + mBoundService.getCurrrentPosition());
        currTrackTime.setText(getTimeFromSeconds(mBoundService.getCurrrentPosition()));
        seekBar.setProgress(mBoundService.getCurrrentPosition());
    }

    private String getTimeFromSeconds(int time) {
        return time / 60 + ":" + time % 60;
    }


    @Override
    public void onTrackSelected(Track track) {

    }

    @Override
    public void onTrackSelected(int index) {
        updateTrack(index);
    }

    @Override
    public void onTrackUpdated(Track track) {
        TrackManager.getInstance(this).updateTrack(track, this);
    }

    //UpdateTrack pide una nueva track. Cuando este lista, se llama a este callback.
    @Override
    public void onMusicPlayerPrepared() {
        playAudio();
    }

    //Al acabar de reproducirse una track, se llama este callback.
    @Override
    public void onSongFinishedPlaying() {
        nextTrack();
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        mTracks = (ArrayList<Track>) tracks;
        Log.d(TAG, "onTracksReceived: Got " + tracks.size());
    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onCreateTrack() {

    }

    @Override
    public void onUpdatedTrack() {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    //On button clicked del dialog de opciones de cancion
    @Override
    public void onButtonClicked(String text) {
        switch (text) {
            case "like":
                Log.d(TAG, "onButtonClicked: LIKE!");
                break;
            case "addToPlaylist":
                Log.d(TAG, "onButtonClicked: ADDTOPLAYLIST");
                break;
            case "showArtist":
                Log.d(TAG, "onButtonClicked: SHOW ARTIST!");
                break;
            case "delete":
                Log.d(TAG, "onButtonClicked: DELETE");
                break;
            case "edit":
                Log.d(TAG, "onButtonClicked: EDIT");
                break;
        }
    }
}
