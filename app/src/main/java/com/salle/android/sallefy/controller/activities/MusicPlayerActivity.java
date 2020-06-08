package com.salle.android.sallefy.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog.BottomMenuDialogInterf;
import com.salle.android.sallefy.controller.dialogs.StateDialog;
import com.salle.android.sallefy.controller.download.Downloader;
import com.salle.android.sallefy.controller.download.ObjectBox;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;
import com.salle.android.sallefy.controller.restapi.callback.isLikedCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Like;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.OnSwipeListener;

//Mirar https://developer.android.com/guide/components/bound-services?hl=es-419

public class MusicPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback , MusicCallback, LikeCallback, BottomMenuDialogInterf, isLikedCallback {

    public static final String TAG = MusicPlayerActivity.class.getName();
    private boolean trackWasEdited;
    private SurfaceView videoThumbnail;
    private int videoPositionBeforeFullScreen;
    private StateDialog stateDialog;



    public enum LoopButtonState {
        LOOP_NOT_ACTIVATED,
        LOOP_PLAYLIST_ON,
        LOOP_SONG_ON
    }

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
    private ImageButton back;
    private ImageButton more;
    private TextView songTitle;
    private TextView songAuthor;
    private TextView currTrackTime;
    private TextView totalTrackTime;
    private ImageButton shuffleBtn;
    private ImageButton loopBtn;

    private LinearLayout UI_buttons;

    private FrameLayout frameLayoutVideo;
    private SurfaceHolder mSurfaceHolder;

    //Indica si se esta en el modo fiesta.
    private boolean partyMode;

    private ImageView thumbnail;
    private SeekBar seekBar;

    //Guardamos la referencia a la cancion/playlist que abre el reproductor.
    private Track initTrack;
    private Playlist initPlaylist;

    //Definicion de los modos del sistema.
    private Modo modo;

    private enum Modo {
        //MusicPlayerActivity abierta porque el usuario ha pulsado una track
        PLAY_SONG_FROM_ZERO,
        //MusicPlayerActivity abierta porque el usuario ha pulsado el miniReproductor
        OPEN_SONG,
        //MusicPlayerActivity abierta porque el usuario ha pulsado una playlist
        PLAY_PLAYLIST_FROM_ZERO
    }

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
            Track currTrack = mBoundService.getCurrentTrack();

            switch (modo){
                case PLAY_SONG_FROM_ZERO:

                    //Miramos si el usuario ha pulsado la track que se estava reproduciendo
                    if(currTrack != null && currTrack.getId().intValue() == initTrack.getId()){

                        videoPositionBeforeFullScreen = mBoundService.getCurrentPosition();
                        mBoundService.resetVideoFullScreen();

                        //Actualizar current track con la info de initTrack (para los likes...)
                        mBoundService.songUpdateLike(initTrack.isLiked());

                        //Actualizamos la activity para cuadrar la progressbar y las imagenes y tutulos.
                        //Pero no volvemos a cargar toda la cancion en streaming.
                        updateUIDataBefore(currTrack);
                        updateSongInfoAfterLoad();
                    }else{
                        //Track nueva, empezamos de zero.
                        mBoundService.removePlaylist();
                        mBoundService.loadSong(initTrack);
                    }
                    break;

                case OPEN_SONG:
                    Track t = mBoundService.getCurrentTrack();
                    videoPositionBeforeFullScreen = mBoundService.getCurrentPosition();
                    mBoundService.resetVideoFullScreen();

                    //Hay que mirar si le han dado like.
                    TrackManager.getInstance(MusicPlayerActivity.this).isTrackLiked(t.getId(),MusicPlayerActivity.this);
                    updateUIDataBefore(t);
                    updateSongInfoAfterLoad();
                    break;

                case PLAY_PLAYLIST_FROM_ZERO:
                    updateUIDataBefore(initTrack);

                    //Miramos si el usuario ha pulsado la track que se estava reproduciendo
                    if(currTrack != null && currTrack.getId().intValue() == initTrack.getId()){
                        //Actualizar current track con la info de initTrack (para los likes...)
                        mBoundService.songUpdateLike(initTrack.isLiked());
                        updateUIDataBefore(currTrack);
                        updateSongInfoAfterLoad();
                        videoPositionBeforeFullScreen = mBoundService.getCurrentPosition();
                        mBoundService.resetVideoFullScreen();
                        mBoundService.loadSongs(initPlaylist, initTrack,false);
                    }else{
                        mBoundService.loadSongs(initPlaylist, initTrack,true);
                    }

                    mBoundService.setShuffle(initPlaylist.getIsShuffleStart());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + modo);
            }
            updateVideoViews(mBoundService.getCurrentTrack());
            mBoundService.setVideoFullScreen(false,getScreenWidth(),getScreenHeight());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            Log.d(TAG, "onServiceDisconnected: Service desconnected.");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Destroying. ");
        mHandler.removeCallbacks(mSeekBarUpdater);
        unbindService(mServiceConnection);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        partyMode = false;
        trackWasEdited = false;

        setContentView(R.layout.activity_music_player);

        ///Deduce el modo de trabajo de la classe.
        Track track = (Track) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.PLAYER_SONG);
        Playlist playlist = (Playlist) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST);

        atachButtons();
        if(playlist != null){
            //Nos han pasado una playlist!
            modo = Modo.PLAY_PLAYLIST_FROM_ZERO;
            initPlaylist = playlist;
            initTrack = track;

            if(initPlaylist.getIsShuffleStart()){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_active);
                shuffleBtn.setTag("playRandom");
            }else{
                shuffleBtn.setTag("DontPlayRandom");
            }

        }else{

            //Nos han pasado una track?
            if(track != null) {
                updateUIDataBefore(track);
                initTrack = track;
                modo = Modo.PLAY_SONG_FROM_ZERO;
            }else{
                //Han apretado el minireproductor.
                modo = Modo.OPEN_SONG;
                Log.d(TAG, "onCreate: El reproductor sha obert sense track de referencia.");
            }
        }

        startStreamingService();

        detector = new GestureDetectorCompat(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                switch (direction) {
                    case up:
                        if(mServiceBound && mBoundService.isVideoFullScreen()){
                            closeVideoFullScreen();
                        }else {
                            showMoreMenu();
                        }
                        break;
                    case left:
                        nextTrack();
                        break;
                    case right:
                        prevTrack();
                        break;
                    case down:
                        openVideoFullScreen();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        detector.setOnDoubleTapListener(new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(mServiceBound && mBoundService.isVideoFullScreen())
                    UI_buttons.setVisibility(UI_buttons.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                closeVideoFullScreen();
                return false;
            }
        });

        mHandler = new Handler();

        //Important to run this line here, we need the UI thread!
        mSeekBarUpdater.run();
    }

    private void closeVideoFullScreen() {
        Log.d(TAG, "closeVideoFullScreen: IS visible" + videoThumbnail.getVisibility());
        if(videoThumbnail.getVisibility()==View.VISIBLE && mServiceBound && mBoundService.isVideoFullScreen()) {
            Log.d(TAG, "closeVideoFullScreen: CLOSING VIDEO FULL SCREEN");
            //UI changes.
            showMultimediaUI(false);

            mBoundService.setVideoFullScreen(false, getScreenWidth(), getScreenHeight());
            videoPositionBeforeFullScreen = 0;
            updateVideoFullScreen();
        }
    }

    private void showMultimediaUI(boolean addDecorations) {
        UI_buttons.setVisibility(View.VISIBLE);
        if(addDecorations) {
            UI_buttons.setBackgroundResource(R.drawable.ui_reproductor_round);
        }else{
            UI_buttons.setBackground(null);
        }
    }

    private void openVideoFullScreen() {

        if(videoThumbnail.getVisibility()==View.VISIBLE && mServiceBound && !mBoundService.isVideoFullScreen()) {

            //Ui changes.
            showMultimediaUI(true);

            mBoundService.setVideoFullScreen(true, getScreenWidth(), getScreenHeight());
            updateVideoFullScreen();
        }
    }

    /**START MUSIC CONTROL ZONE*/
    private void playSong() {
        if(!mServiceBound){
            Toast.makeText(MusicPlayerActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        mBoundService.playSong();
        updateSeekBar();

        playPause.setImageResource(R.drawable.ic_pause_circle_64dp);
        playPause.setTag(STOP);

    }

    private void pauseSong() {
        if(!mServiceBound){
            Toast.makeText(MusicPlayerActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        mBoundService.pauseSong();
        playPause.setImageResource(R.drawable.ic_play_circle_filled_64dp);
        playPause.setTag(PLAY);
    }

    private void changeTrack(int offset){
        if(!mServiceBound){
            Toast.makeText(MusicPlayerActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }

        Track track = mBoundService.changeTrack(offset);
        if(track == null){
            //No hay una lista cargada.
            Log.d(TAG, "changeTrack: No hay una lista cargada. Offset was " + offset);
            return;
        }

        updateUIDataBefore(track);
    }

    private void nextTrack() {
        videoPositionBeforeFullScreen = 0;
        mBoundService.setVideoFullScreen(false,0,0);
        changeTrack(1);

    }

    private void prevTrack() {
        videoPositionBeforeFullScreen = 0;
        mBoundService.setVideoFullScreen(false,0,0);
        changeTrack(-1);
    }

    /**END MUSIC CONTROL ZONE*/

    private void startStreamingService () {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void atachButtons(){


        loopBtn = findViewById(R.id.music_player_loop);
        loopBtn.setTag(LoopButtonState.LOOP_NOT_ACTIVATED);

        loopBtn.setOnClickListener(v -> {
            LoopButtonState loopState = (LoopButtonState)loopBtn.getTag();

            switch (loopState) {
                case LOOP_NOT_ACTIVATED:
                    loopBtn.setImageResource(R.drawable.ic_repeat_activated);
                    loopBtn.setTag(LoopButtonState.LOOP_PLAYLIST_ON);
                    break;

                case LOOP_PLAYLIST_ON:
                    loopBtn.setImageResource(R.drawable.ic_repeat_one);
                    loopBtn.setTag(LoopButtonState.LOOP_SONG_ON);
                    break;

                case LOOP_SONG_ON:
                    loopBtn.setTag(LoopButtonState.LOOP_NOT_ACTIVATED);
                    loopBtn.setImageResource(R.drawable.ic_repeat);
                    break;
            }
            mBoundService.setLoopMode((LoopButtonState)loopBtn.getTag());
        });



        shuffleBtn = findViewById(R.id.music_player_shuffle);

        shuffleBtn.setOnClickListener(v -> {
            boolean shuffleActivated =shuffleBtn.getTag().equals("DontPlayRandom");

            if(shuffleActivated){
                shuffleBtn.setTag("playRandom");
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_active);
            }else{
                shuffleBtn.setTag("DontPlayRandom");
                shuffleBtn.setImageResource(R.drawable.ic_shuffle);
            }

            mBoundService.setShuffle(shuffleActivated);
        });



        playPause = findViewById(R.id.music_player_playStop);
        playPause.setTag(PLAY);
        playPause.setOnClickListener(view -> {
            if(playPause.getTag().equals(PLAY)){
                playSong();
            }else{
                pauseSong();
            }
        });

        nextTrack = findViewById(R.id.music_player_next);
        nextTrack.setOnClickListener(view -> nextTrack());

        prevTrack = findViewById(R.id.music_player_prev);
        prevTrack.setOnClickListener(view -> prevTrack());

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
                if(!mServiceBound){
                    Toast.makeText(MusicPlayerActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
                    return;
                }
                mBoundService.setCurrentPosition(userSelectedPosition);

                updateSeekBar();
            }
        });


        like = findViewById(R.id.music_player_fav);

        like.setTag("noFav");

        like.setOnClickListener(view -> tryToLike());

        more = findViewById(R.id.music_player_more);
        more.setOnClickListener(view -> showMoreMenu());

        back = findViewById(R.id.new_playlist_nav);
        back.setOnClickListener(view -> {
            if(mServiceBound) {
                showMultimediaUI(false);
                videoPositionBeforeFullScreen = 0;
                //Ignoramos el setVideoFullScreen pq asi lo podemos detectar en MusicService al empezar musicPLayer con OPEN_SONG.
                //  mBoundService.setVideoFullScreen(false,getScreenWidth(),getScreenHeight());
            }
            exitPlayer();
        });

        songTitle = findViewById(R.id.music_player_title);
        songAuthor = findViewById(R.id.music_player_author);
        currTrackTime = findViewById(R.id.music_player_curr_time);
        totalTrackTime = findViewById(R.id.music_player_total_time);
        thumbnail = findViewById(R.id.music_player_thumbnail);
        videoThumbnail = findViewById(R.id.video_thumbnail);
        frameLayoutVideo = findViewById(R.id.frameThumbnail);

        videoThumbnail.getHolder().addCallback(this);

        UI_buttons = findViewById(R.id.UI_buttons);

    }

    private void exitPlayer(){
        Intent data = new Intent();
        if(trackWasEdited) {
            data.putExtra(Constants.INTENT_EXTRAS.TRACK, mBoundService.getCurrentTrack());
            setResult(Constants.EDIT_CONTENT.RESULT_MP_TRACK_EDITED, data);
        }else {
            setResult(Constants.EDIT_CONTENT.RESULT_MP_ORDINARY, data);
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        if(videoThumbnail.getVisibility() == View.VISIBLE && mServiceBound && mBoundService.isVideoFullScreen()){
            videoPositionBeforeFullScreen = mBoundService.getCurrentPosition();
            closeVideoFullScreen();
        }else {
            if(mServiceBound)
                videoPositionBeforeFullScreen = mBoundService.getCurrentPosition();
            exitPlayer();
        }
    }

    private void updateSeekBar(){
        if(!mServiceBound){
            return;
        }
        currTrackTime.setText(getTimeFromSeconds(mBoundService.getCurrentPosition()));
        seekBar.setProgress(mBoundService.getCurrentPosition());
    }

    private String getTimeFromSeconds(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String min = minutes < 10 ? "0"+minutes:""+minutes;
        String sec = seconds < 10 ? "0"+seconds:""+seconds;
        return min + ":" + sec;
    }

    private void updateUIDataBefore(Track track){
        songAuthor.setText(track.getUserLogin());
        songTitle.setText(track.getName());

        if(track.isLiked()){
            like.setTag("Fav");
            like.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            like.setImageResource(R.drawable.ic_favourite_grey_24dp);
            like.setTag("NoFav");
        }
        if(mServiceBound){
            updatePlayButton();
        }
        //Modificar el thumbnail
        Glide.with(this)
                .asBitmap()
                .placeholder(new ColorDrawable(getResources().getColor(R.color.colorWhite, null)))
                .load(track.getThumbnail())
                .into(thumbnail);
    }

    private void updateVideoViews(Track track){
        if (track.getUrl().toLowerCase().contains("mp4")) {
            Log.d(TAG, "updateUIDataBefore: MAKING VISIBLE VIDEO PLAYER.");
            videoThumbnail.setVisibility(View.VISIBLE);

            //videoThumbnail.setZOrderOnTop(true);
            videoThumbnail.getHolder().setFormat(PixelFormat.TRANSLUCENT);

            frameLayoutVideo.setVisibility(View.GONE);
            //thumbnail.setVisibility(View.GONE);


/*
            //ON TOUCCH DEL THUMBNAIL
            videoThumbnail.setOnTouchListener((v, event) -> {

                detector.onTouchEvent(event);
                return false;
            });

            videoThumbnail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (mBoundService != null) {
                        if(!mBoundService.isVideoFullScreen()) {
                            mBoundService.setVideoFullScreen(true, getScreenWidth(), getScreenHeight());
                            updateVideoFullScreen();
                        }
                    }
                }
            });
*/
        } else {
            Log.d(TAG, "updateUIDataBefore: MAKING VISIBLE THUMBNAIL.");

            videoThumbnail.setVisibility(View.GONE);
            frameLayoutVideo.setVisibility(View.VISIBLE);
            //thumbnail.setVisibility(View.VISIBLE);
        }
    }

    private void updateVideoFullScreen() {
        videoPositionBeforeFullScreen = mBoundService.getCurrentPosition();
        mBoundService.loadSong(mBoundService.getCurrentTrack());
        mBoundService.getMediaPlayer().setDisplay(mSurfaceHolder);
    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public int getScreenWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public int getScreenHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels + getNavigationBarHeight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: SURFACE CREATED.");
        //mediaPlayer.setDisplay(holder);
        //mBoundService.updateSurfaceHolder(videoThumbnail.getHolder());
        mSurfaceHolder = holder;
        if(mBoundService != null)
        try{
        //    mBoundService.getPapa().setDisplay(holder);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: CHANGED");
        mSurfaceHolder = holder;
        //mediaPlayer.setDisplay(holder);
        //if(mBoundService != null)
       // mBoundService.updateSurfaceHolder(videoThumbnail.getHolder());
        if(mBoundService != null){

            try{
                mBoundService.getMediaPlayer().setDisplay(holder);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            Log.d(TAG, "surfaceChanged: IS NULL!!!!");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: DESTROY");
        mSurfaceHolder = null;
       // mBoundService.updateSurfaceHolder(null);
        if(mBoundService != null){
            try{
                mBoundService.getMediaPlayer().setDisplay(null);

            }catch (Exception e){
                e.printStackTrace();
            }

    }else{
        Log.d(TAG, "surfaceChanged: IS NULL!!!!");
    }
    }



    private void updatePlayButton() {
        if(!mBoundService.isPlaying()){
            playPause.setImageResource(R.drawable.ic_play_circle_filled_64dp);
            playPause.setTag(PLAY);
        }else{
            playPause.setImageResource(R.drawable.ic_pause_circle_64dp);
            playPause.setTag(STOP);
        }
    }

    private void updateSongInfoAfterLoad(){
        //Update seekbar data:
        totalTrackTime.setText(getTimeFromSeconds(mBoundService.getMaxDuration()));
        seekBar.setMax(mBoundService.getMaxDuration());
        updateSeekBar();

        updatePlayButton();

        //mBoundService.updateSurfaceHolder(videoThumbnail.getHolder());
    }


    private void tryToLike() {
        if(!mServiceBound){
            Toast.makeText(MusicPlayerActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        Track track= mBoundService.getCurrentTrack();

        //Se solicita un like. Si la respuesta es nais, se llama el callback onLikeSuccess()
        //Es alli donde de actualiza la UI.
        TrackManager.getInstance(getApplicationContext()).likeTrack(track.getId(),
                like.getTag().equals("noFav"),
                MusicPlayerActivity.this);
    }

    private void showMoreMenu() {
        Log.d("TAGG", mBoundService.getCurrentTrack().toString());

        BottomMenuDialog dialog = new BottomMenuDialog(new TrackViewPack(mBoundService.getCurrentTrack(), null, null), this,null);
        dialog.show(getSupportFragmentManager(),"FRAGMENT_TAG_MAX_ONE_INSTANCE");
    }


    /* <FrameLayout

        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clipToPadding="false"

        android:background="#FAFAFA"
        android:layout_centerHorizontal="true"
        android:layout_centerVertical="false">

        ///


         if(mBoundService.isVideoFullScreen()) {

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.setMargins(0,0,0,0);
                videoThumbnail.setLayoutParams(params);
                videoThumbnail.requestLayout();
            }else{
                final float scale = getResources().getDisplayMetrics().density;
                int pixels =  (int) (232 * scale + 0.5f);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        pixels,
                        pixels
                );
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.setMargins(0,(int)(85* scale + 0.5f),0,0);
                videoThumbnail.setLayoutParams(params);
                videoThumbnail.requestLayout();
            }
*/

    @Override
    public void onMusicPlayerPrepared() {


        if(mSurfaceHolder == null) {
            videoThumbnail.requestLayout();
            Log.d(TAG, "onMusicPlayerPrepared: SURFACE IS NULL");
            updateVideoViews(mBoundService.getCurrentTrack());
        }

        if(videoThumbnail.getVisibility()==View.VISIBLE)
            updateVideoViews(mBoundService.getCurrentTrack());



        if(videoThumbnail.getVisibility()==View.VISIBLE) {
            Log.d(TAG, "onMusicPlayerPrepared: NO TE VOY A MENTIR MAMI. Visible.");
            mBoundService.setCurrentPosition(videoPositionBeforeFullScreen);

            if(mBoundService.isVideoFullScreen()) {

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
                //params. = Gravity.CENTER_HORIZONTAL;
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.setMargins(0,0,0,0);
                videoThumbnail.setLayoutParams(params);
                videoThumbnail.requestLayout();
            }else{
                final float scale = getResources().getDisplayMetrics().density;
                int pixels =  (int) (232 * scale + 0.5f);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        pixels,
                        pixels
                );
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                //params.gravity = Gravity.CENTER_HORIZONTAL;
                params.setMargins(0,(int)(85* scale + 0.5f),0,0);
                videoThumbnail.setLayoutParams(params);
                videoThumbnail.requestLayout();
            }
        }else{
            Log.d(TAG, "onMusicPlayerPrepared: THIS IS IT");
            mBoundService.setVideoFullScreen(false,getScreenWidth(),getScreenHeight());
        }

        updateSongInfoAfterLoad();

    }

    @Override
    public void onSongChanged() {
        updateUIDataBefore(mBoundService.getCurrentTrack());
    }

    @Override
    public void onUpdatePlayButton(){
        Log.d(TAG, "onUpdatePlayButton: LMO");
        updatePlayButton();
    }


    @Override
    public void onLikeSuccess(int songId) {
        boolean wasLikedBefore = like.getTag().equals("Fav");

        if(wasLikedBefore){
            like.setImageResource(R.drawable.ic_favourite_grey_24dp);
            like.setTag("NoFav");
        }else{
            like.setTag("Fav");
            like.setImageResource(R.drawable.ic_favorite_black_24dp);
        }

        mBoundService.getCurrentTrack().setLiked(!wasLikedBefore);
    }

    @Override
    public void onIsLiked(int songId, Like isLiked) {

        mBoundService.songUpdateLike(isLiked.getLiked());
        if(isLiked.getLiked()){
            like.setTag("Fav");
            like.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            like.setImageResource(R.drawable.ic_favourite_grey_24dp);
            like.setTag("NoFav");
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: ERROR AL DAR LIKE." + throwable.getMessage());
    }

    //On button clicked del dialog de opciones de cancion
    @Override
    public void onButtonClicked(TrackViewPack track, String text) {
        switch (text) {
            case "like":
                Log.d(TAG, "onButtonClicked: LIKE!");
                tryToLike();
                break;
            case "addToPlaylist":
                Log.d(TAG, "onButtonClicked: ADDTOPLAYLIST");
                Intent intentA2P = new Intent(this, AddToPlaylistActivity.class);
                intentA2P.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, track.getTrack());
                startActivity(intentA2P);
                break;
            case "showArtist":
                Log.d(TAG, "onButtonClicked: SHOW ARTIST!");
                showArtist(track.getTrack().getUser());
                break;

            case "edit":
                Log.d(TAG, "onButtonClicked: EDIT");
                Intent intent = new Intent(this, EditSongActivity.class);
                intent.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, mBoundService.getCurrentTrack());
                startActivityForResult(intent, Constants.EDIT_CONTENT.TRACK_EDITING_FINISHED);
                break;
            case "delete":
                Log.d(TAG, "onButtonClicked: DELETE");
                deleteTrack(track.getTrack());
                break;
            case "download":
                Log.d(TAG, "onButtonClicked: DOWNLOAD");
                Downloader.download(track.getTrack(),this,getScreenWidth(),getScreenHeight());
                break;
            case "removeLocally":
                ObjectBox.getInstance(this).removeTrack(track.getTrack());
                break;
        }
    }

    private void showArtist(User user) {
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.USER, user);
        setResult(Constants.EDIT_CONTENT.RESULT_MP_USER, data);
        finish();
    }

    private void deleteTrack(Track track){
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.TRACK, track);
        setResult(Constants.EDIT_CONTENT.RESULT_MP_DELETE, data);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Back from edit song!
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_CONTENT.TRACK_EDITING_FINISHED && resultCode == RESULT_OK) {
            //Update track information
            boolean audioSet = data.getBooleanExtra("audioSet", false);
            Track newTrack = (Track) data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK);

            if(audioSet){
                //Audio file was updated
                mBoundService.loadSong(newTrack);
                mBoundService.updateSongAfterEditing(newTrack);
                updateUIDataBefore(newTrack);

            }else{
                //Audio file was not updated
                mBoundService.updateSongAfterEditing(newTrack);
                updateUIDataBefore(newTrack);
                updateSongInfoAfterLoad();
            }
            trackWasEdited = true;
        }
    }
}
