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
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog.BottomMenuDialogInterf;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.OnSwipeListener;

import static com.salle.android.sallefy.utils.OnSwipeListener.Direction.up;

//Mirar https://developer.android.com/guide/components/bound-services?hl=es-419

public class MusicPlayerActivity extends AppCompatActivity implements MusicCallback, LikeCallback, BottomMenuDialogInterf {

    public static final String TAG = MusicPlayerActivity.class.getName();

    // Service
    private MusicService mBoundService;
    private boolean mServiceBound = false;

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

    //Indica si se esta en el modo fiesta.
    private boolean partyMode;

    private ImageView thumbnail;
    private SeekBar seekBar;

    //Thread management para la seekbar
    private Handler mHandler;
    Runnable mSeekBarUpdater = new Runnable() {
        @Override
        public void run() {
            try{

                updateSeekBar();
                if(partyMode){
                    int color= ((int)(Math.random()*16777215)) | (0xFF << 24);
                    getWindow().setNavigationBarColor(color);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally{
                mHandler.postDelayed(mSeekBarUpdater, 750);
            }
        }
    };

    //Gesture detector.
    private GestureDetectorCompat detector;


    /** De la documentacion de Android:
     *
     * Un cliente se enlaza a un servicio llamando a bindService().
     * Cuando lo hace, debe implementar ServiceConnection, que supervisa la conexión con el servicio.
     * El valor que se muestra de bindService() indica si el servicio solicitado existe y si se
     * le permite al cliente acceder a él.
     * Cuando el sistema Android crea la conexión entre el cliente y el servicio,
     * llama a onServiceConnected() en ServiceConnection. El método onServiceConnected() incluye
     * un argumento IBinder que el cliente utiliza para comunicarse con el servicio enlazado.

     */
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

        partyMode = false;

        startStreamingService();
        setContentView(R.layout.activity_music_player);
        atachButtons();

        detector = new GestureDetectorCompat(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                if(direction == up){
                    showMoreMenu();
                    return true;
                }
                return false;
            }
        });

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
                    playSong();
                }else{
                    pauseSong();
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
                mBoundService.setCurrentPosition(userSelectedPosition);
                updateSeekBar();
            }
        });


        like = findViewById(R.id.music_player_fav);
        like.setTag("Fav");

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Track track= mBoundService.getCurrentTrack();

                //Se solicita un like. Si la respuesta es nais, se llama el callback onLikeSuccess()
                //Es alli donde de actualiza la UI.
                TrackManager.getInstance(getApplicationContext()).likeTrack(track.getId(),
                        !like.getTag().equals("Fav"),
                        MusicPlayerActivity.this);
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

    private void playSong() {
        mBoundService.playSong();
        updateSeekBar();

        playPause.setImageResource(R.drawable.ic_pause_circle_64dp);
        playPause.setTag(STOP);

        Toast.makeText(this, "Playing Audio", Toast.LENGTH_SHORT).show();
    }

    private void pauseSong() {
        mBoundService.pauseSong();
        playPause.setImageResource(R.drawable.ic_play_circle_filled_64dp);
        playPause.setTag(PLAY);
        Toast.makeText(this, "Pausing Audio", Toast.LENGTH_SHORT).show();
    }

    private void changeTrack(int offset){
        Track track = mBoundService.changeTrack(offset);
        if(track == null){
            //No hay una lista cargada.
            Log.d(TAG, "changeTrack: No hay una lista cargada. Offset was " + offset);
            return;
        }

        updateUIDataBefore(track);
    }

    private void nextTrack() {
        changeTrack(1);
    }

    private void prevTrack() {
        changeTrack(-1);
    }

    private void updateSeekBar(){
        if(!mServiceBound) return;
        currTrackTime.setText(getTimeFromSeconds(mBoundService.getCurrentPosition()));
        seekBar.setProgress(mBoundService.getCurrentPosition());
    }

    private String getTimeFromSeconds(int time) {
        return time / 60 + ":" + time % 60;
    }

    private void playSong(Track track) {
        updateUIDataBefore(track);
        mBoundService.loadSong(track.getUrl());
    }

    private void updateUIDataBefore(Track track){
        songAuthor.setText(track.getUserLogin());
        songTitle.setText(track.getName());

        //Modificar el thumbnail
        Glide.with(this)
                .asBitmap()
                .placeholder(R.drawable.ic_audiotrack)
                .load(track.getThumbnail())
                .into(thumbnail);
    }

    private void updateSongInfoAfterLoad(){
        //Update seekbar data:
        totalTrackTime.setText(getTimeFromSeconds(mBoundService.getMaxDuration()));
        seekBar.setMax(mBoundService.getMaxDuration());
        updateSeekBar();

        //Asi que modificamos el icono de playPause acorde.
        playPause.setImageResource(R.drawable.ic_pause_circle_64dp);
        playPause.setTag(STOP);
    }

    //UpdateTrack pide una nueva track. Cuando este lista, se llama a este callback.
    @Override
    public void onMusicPlayerPrepared() {
        updateSongInfoAfterLoad();
    }

    //Al acabar de reproducirse una track, se llama este callback.
    @Override
    public void onSongFinishedPlaying() {
        nextTrack();
    }
    /*
    @Override
    public void onTracksReceived(List<Track> tracks) {
        mBoundService.setSongList((ArrayList<Track>) tracks);
        playSong(tracks.get(0));
        Log.d(TAG, "onTracksReceived: Got " + tracks.size() + " songs.");
    }*/


    @Override
    public void onLikeSuccess(int songId) {
        if(like.getTag().equals("Fav")){
            like.setImageResource(R.drawable.ic_favourite_grey_24dp);
            like.setTag("NoFav");
        }else{
            like.setTag("Fav");
            like.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: ERROR AL DAR LIKE." + throwable.getMessage());
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
