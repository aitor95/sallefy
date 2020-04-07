package com.salle.android.sallefy.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.controller.fragments.BottomSongMenuFragment;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity implements MusicCallback, TrackListCallback, TrackCallback {

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
    private ImageButton fav;
    private ImageButton more;
    private TextView songTitle;
    private TextView songAuthor;
    private TextView currTrackTime;
    private TextView totalTrackTime;

    private ImageView thumbnail;
    private SeekBar seekBar;

    //Thread management para la seekbar
    private Runnable mSeekBarPositionUpdateTask;
    private ScheduledExecutorService mExecutor;

    private Handler mHandler;

    //Variable para abrir el detalle cancion
    private View bottomSheet;
    private BottomSheetBehavior mBottomSheetMenuBehavior;

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


    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    try {
                       updateSeekBar();
                       // Log.d(TAG, "run: Inside el thread secundari");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekBarPositionUpdateTask,
                0,
                500,
                TimeUnit.MILLISECONDS
        );
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mBoundService = binder.getService();
            mBoundService.setCallback(MusicPlayerActivity.this);
            mServiceBound = true;
            //startUpdatingCallbackWithPosition();
            updateTrack(1);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startStreamingService();
        setContentView(R.layout.activity_music_player);
        atachButtons();

        //Get all the tracks!
        TrackManager.getInstance(this).getAllTracks(this);
        mTracks = new ArrayList<>();

        mHandler = new Handler();
        //Important to run this line here, we need the UI thread!
        mSeekBarUpdater.run();

//        Fragment fragmentBottomMenu = new BottomSongMenuFragment();
//        fragmentBottomMenu = findViewById(R.layout.)
//        bottomSheet = findViewById(R.id.bottom_sheet_menu);
//        mBottomSheetMenuBehavior = BottomSheetBehavior.from(bottomSheet);

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


        fav = findViewById(R.id.music_player_fav);
        fav.setTag("Fav");
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fav.getTag().equals("Fav")){
                    fav.setImageResource(R.drawable.ic_favourite_grey_24dp);
                    fav.setTag("NoFav");
                }else{
                    fav.setTag("Fav");
                    fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            }
        });

        /*more = findViewById(R.id.music_player_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetMenuBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });*/

        songTitle = findViewById(R.id.music_player_title);
        songAuthor = findViewById(R.id.music_player_author);
        currTrackTime = findViewById(R.id.music_player_curr_time);
        totalTrackTime = findViewById(R.id.music_player_total_time);

        thumbnail = findViewById(R.id.music_player_thumbnail);
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
    public void onFailure(Throwable throwable) {

    }
}
