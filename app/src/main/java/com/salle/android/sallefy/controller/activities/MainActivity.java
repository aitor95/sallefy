package com.salle.android.sallefy.controller.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.callbacks.PlaylistMainComunication;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.fragments.HomeFragment;
import com.salle.android.sallefy.controller.fragments.MeFragment;
import com.salle.android.sallefy.controller.fragments.SearchFragment;
import com.salle.android.sallefy.controller.fragments.SocialFragment;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.OnSwipeListener;
import com.salle.android.sallefy.utils.Session;
import com.salle.android.sallefy.utils.UpdatableFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.salle.android.sallefy.utils.Constants.EDIT_CONTENT.MUSIC_PLAYER_FINISHED;
import static com.salle.android.sallefy.utils.Constants.EDIT_CONTENT.RESULT_MP_DELETE;
import static com.salle.android.sallefy.utils.Constants.EDIT_CONTENT.RESULT_PA_DELETE;
import static com.salle.android.sallefy.utils.Constants.EDIT_CONTENT.RESULT_PA_USER;

public class MainActivity extends FragmentActivity implements AdapterClickCallback, MusicCallback, BottomMenuDialog.BottomMenuDialogInterf, PlaylistMainComunication {

    public static final String TAG = MainActivity.class.getName();

    //Tiempo que ha de pasar entre acciones para que estas se interpreten como validas
    //Esto permite eliminar los dobleclicks del usuario.
    public static final long TIME_BETWEEN_CLICKS = 300;

    //Fragment management
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private Fragment mFragment;

    //Android Back button control variables.
    private boolean backButtonPressed;
    private long backButtonLastPressTime;

    //XML elements
    private Button home;
    private Button social;
    private Button me;
    private ImageView search;

    //XML elements of media player.
    private LinearLayout linearLayoutMiniplayer;
    //El boton de playStop usa los siguientes tags para saber si tiene el drawable pause o play.
    private static final String PLAY = "Play";
    private static final String STOP = "Stop";
    private ImageButton repPlayStop;
    private ImageButton repPrev;
    private ImageButton repNext;
    private TextView repSongArtist;
    private TextView repSongTitle;
    private CircleImageView repImgUsr;

    private boolean isMeUserTheOwner ;

    //Gesture detector.
    private GestureDetectorCompat detector;

    //TAG del fragment activado actualmente...
    private  static String tagFragmentActivado;


    // Service
    private MusicService mBoundService;
    private boolean mServiceBound = false;

    //Track edit
    private static TrackViewPack mTrackViewPack;

    private long lastClick;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mBoundService = binder.getService();
            mBoundService.setCallbackMini(MainActivity.this);
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            Log.d(TAG, "onServiceDisconnected: Service desconnected.");
        }
    };

    private void startStreamingService () {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //linearLayoutMiniplayer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Volvemos del reproductor, actualizamos el mini reproductor.
        Log.d(TAG, "onResume: Volviendo a la main activity");
        updateIfBoundToService();
        lastClick = 0;
    }

    private void updateIfBoundToService() {
        if(mServiceBound){
            Track track = mBoundService.getCurrentTrack();
            if(track != null){
                updateMiniReproductorUI(track);
            }else{
                Log.d(TAG, "updateIfBoundToService: Track is null");
            }
        }
    }

    private void updateMiniReproductorUI(Track track) {
        linearLayoutMiniplayer.setVisibility(View.VISIBLE);
        repSongArtist.setText(track.getUserLogin());
        repSongTitle.setText(track.getName());

        if(mBoundService.isPlaying()){
            repPlayStop.setImageResource(R.drawable.ic_pause_circle_40dp);
            repPlayStop.setTag(STOP);
        }else{
            repPlayStop.setImageResource(R.drawable.ic_play_circle_filled_40dp);
            repPlayStop.setTag(PLAY);
        }

        //Modificar el thumbnail
        Glide.with(this)
                .asBitmap()
                .placeholder(new ColorDrawable(getResources().getColor(R.color.colorWhite,null)))
                .load(track.getThumbnail())
                .into(repImgUsr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startStreamingService();
        lastClick = 0;
        isMeUserTheOwner = true;
        backButtonLastPressTime = 0;
        backButtonPressed = false;
        initViews();
        initViewsMiniMultimedia();

        detector = new GestureDetectorCompat(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                Log.d(TAG, "onSwipe:" + direction);
                return changeFragmentBasedOnDirection(direction);
            }
        });

        enterHomeFragment();
        requestPermissions();
    }

    private void initViewsMiniMultimedia() {
        repPlayStop = findViewById(R.id.mini_rep_playStop);
        repPrev = findViewById(R.id.mini_rep_prev);
        repNext = findViewById(R.id.mini_rep_next);

        repPlayStop.setTag(STOP);
        repPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repPlayStop.getTag().equals(PLAY)){
                    playSong();
                }else{
                    pauseSong();
                }
            }
        });

        repPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevTrack();
            }
        });

        repNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack();
            }
        });


        repSongArtist = findViewById(R.id.mini_rep_song_artist);
        repSongTitle = findViewById(R.id.mini_rep_song_title);
        repImgUsr = findViewById(R.id.mini_rep_user_img);
        linearLayoutMiniplayer = findViewById(R.id.linearLayoutMiniplayer);

        linearLayoutMiniplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - lastClick <= TIME_BETWEEN_CLICKS)
                    return;

                lastClick = System.currentTimeMillis();
                Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                startActivity(intent);
            }
        });
        
        
    }

    private void initViews() {
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();

        home = (Button) findViewById(R.id.action_home);
        social = (Button) findViewById(R.id.action_social);
        me = (Button) findViewById(R.id.action_me);
        search = (ImageView) findViewById(R.id.action_search);
        
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterHomeFragment();
            }
        });

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSocialFragment();
            }
        });

        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterMeFragmentOfOwner();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSearchFragment();
            }
        });
    }

    private void enterHomeFragment() {
        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView_Active);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = HomeFragment.getInstance();
        HomeFragment.setAdapterClickCallback(this);


        replaceFragment(fragment);
    }

    private void enterSocialFragment() {
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView);
        social.setTextAppearance(R.style.BottomNavigationView_Active);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = SocialFragment.getInstance();
        SocialFragment.setAdapterClickCallback(this);
        replaceFragment(fragment);
    }

    private void enterMeFragmentOfOwner() {
        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView_Active);
        home.setTextAppearance(R.style.BottomNavigationView);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = MeFragment.newInstance(Session.getInstance(MainActivity.this).getUser());
        MeFragment.setAdapterClickCallback(this);
        replaceFragment(fragment);
    }

    private void enterMeFragmentOfOtherUser(User user){
        isMeUserTheOwner = false;
        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = MeFragment.newInstance(user);
        MeFragment.setAdapterClickCallback(this);

        tagFragmentActivado = MeFragment.TAG;
        mFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment,  MeFragment.TAG)
            //.addToBackStack(null)
            .commit();
    }

    private void enterSearchFragment() {
        search.setImageResource(R.drawable.ic_search_clicked);

        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView);

        Fragment fragment = SearchFragment.getInstance();
        SearchFragment.setAdapterClickCallback(this);
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {

        String fragmentTag = getFragmentTag(fragment);
        tagFragmentActivado = fragmentTag;

        Fragment currentFragment = mFragmentManager.findFragmentByTag(fragmentTag);

        if (currentFragment != null) {
            if (!currentFragment.isVisible() || (currentFragment.getTag() != null && currentFragment.getTag().equalsIgnoreCase(MeFragment.TAG))) {

                if (fragment.getArguments() != null) {
                    currentFragment.setArguments(fragment.getArguments());
                }

                if(!isMeUserTheOwner) {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment, fragmentTag)
                            //.addToBackStack(null)
                            .commit();
                    isMeUserTheOwner = true;
                }
            }
        } else {
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragmentTag)
                    //.addToBackStack(null)
                    .commit();
        }
    }

    private String getFragmentTag(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            return HomeFragment.TAG;
        } else {
            if (fragment instanceof MeFragment) {
                return MeFragment.TAG;
            } else {
                if (fragment instanceof SocialFragment) {
                    return SocialFragment.TAG;
                } else {
                    return SearchFragment.TAG;
                }
            }
        }
    }



    private void requestPermissions() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS}, Constants.PERMISSIONS.MICROPHONE);


        } else {
            Session.getInstance(this).setAudioEnabled(true);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS.LOCATION
                );
        }else{
            Session.getInstance(this).setLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSIONS.MICROPHONE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Session.getInstance(this).setAudioEnabled(true);
            } else {

            }
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    /***
     * Al apretar el boton de atras de android, no queremos salir de la main activity.
     * Si el usuario aprieta back 2 veces en menos de 2 segundos se sale de la app.
     */
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if(System.currentTimeMillis() - backButtonLastPressTime > 2000){
                //Han pasado 2 segundos.
                backButtonPressed = false;
                backButtonLastPressTime = System.currentTimeMillis();
            }

            if(backButtonPressed) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                if(!isMeUserTheOwner) {
                    //Miramos si el usuario esta en me.
                    if(tagFragmentActivado.equals(MeFragment.TAG)){
                        enterMeFragmentOfOwner();
                    }
                }else {
                    Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                    backButtonPressed = true;
                }
            }
        } else {
            if(tagFragmentActivado.equals(SearchFragment.TAG)){
                SearchFragment sf = (SearchFragment)mFragmentManager.findFragmentByTag(SearchFragment.TAG);
                if(sf != null)sf.onSeeAllClosed();
            }

            getSupportFragmentManager().popBackStack();
        }
    }

    //Lugar oscuro de la app....
    private boolean changeFragmentBasedOnDirection(OnSwipeListener.Direction direction) {
        boolean rtn = false;
        Fragment fragment = mFragmentManager.findFragmentByTag(tagFragmentActivado);
        if (fragment instanceof HomeFragment) {

            if(direction == OnSwipeListener.Direction.left) {
                enterSocialFragment();
                rtn = true;
            }
        } else {
            if (fragment instanceof MeFragment) {

                if(direction == OnSwipeListener.Direction.right) {
                    enterSocialFragment();
                    rtn = true;
                } else if(direction == OnSwipeListener.Direction.left) {
                    enterSearchFragment();
                    rtn = true;
                }
            } else {
                if (fragment instanceof SocialFragment) {

                    if(direction == OnSwipeListener.Direction.right) {
                        enterHomeFragment();
                        rtn = true;
                    } else if(direction == OnSwipeListener.Direction.left) {
                        enterMeFragmentOfOwner();
                        rtn = true;
                    }
                } else {
                    if(direction == OnSwipeListener.Direction.right) {
                        enterMeFragmentOfOwner();
                        rtn = true;
                    }
                }
            }
        }
        return rtn;
    }

    @Override
    public void onTrackClicked(Track track, Playlist playlist) {
        if(System.currentTimeMillis() - lastClick <= TIME_BETWEEN_CLICKS)
            return;

        lastClick = System.currentTimeMillis();

        Intent intent = new Intent(this, MusicPlayerActivity.class);
        Log.d(TAG, "onTrackSelected: Track is " + track.getName());

        if(playlist == null){

            intent.putExtra(Constants.INTENT_EXTRAS.PLAYER_SONG,track);
        }else{
            intent.putExtra(Constants.INTENT_EXTRAS.PLAYER_SONG,track);
            intent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST,playlist);
        }

        startActivityForResult(intent, MUSIC_PLAYER_FINISHED);
    }

        @Override
    public void onPlaylistClick(Playlist playlist) {
        if(System.currentTimeMillis() - lastClick <= TIME_BETWEEN_CLICKS)
            return;

        lastClick = System.currentTimeMillis();

        Log.d(TAG, "onPlaylistClick: Playlist is " + playlist.getName());

        Intent intent = new Intent(this, PlaylistActivity.class);
        PlaylistActivity.setPlaylistMainComunication(this);
        intent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, playlist);
        startActivityForResult(intent, Constants.EDIT_CONTENT.ACTIVITY_PLAYLIST_FINISHED);
    }

    @Override
    public void onUserClick(User user) {
        if(System.currentTimeMillis() - lastClick <= TIME_BETWEEN_CLICKS)
            return;

        lastClick = System.currentTimeMillis();

        Log.d(TAG, "onUserClick: User name is " + user.getLogin());
        if(user.getId()==Session.getInstance(this).getUser().getId().intValue())
            enterMeFragmentOfOwner();
        else
            enterMeFragmentOfOtherUser(user);
    }

    @Override
    public void onGenreClick(Genre genre) {
        if(System.currentTimeMillis() - lastClick <= TIME_BETWEEN_CLICKS)
            return;

        lastClick = System.currentTimeMillis();

        Log.d(TAG, "onGenreClick: Genre name is " + genre.getName());
    }

    @Override
    public void onMusicPlayerPrepared() {
        Track track = mBoundService.getCurrentTrack();
        Log.d(TAG, "onMusicPlayerPrepared: track is null? "  + (track == null));


        repPlayStop.setImageResource(R.drawable.ic_pause_circle_40dp);
        repPlayStop.setTag(STOP);
        updateIfBoundToService();
    }

    @Override
    public void onUpdatePlayButton() {
        
        if(mBoundService.isPlaying()){
            repPlayStop.setImageResource(R.drawable.ic_pause_circle_40dp);
            repPlayStop.setTag(STOP);
        }else{
            repPlayStop.setImageResource(R.drawable.ic_play_circle_filled_40dp);
            repPlayStop.setTag(PLAY);
        }
    }

    @Override
    public void onSongChanged() {
        updateIfBoundToService();
    }

    /** MiniReproductor Controls.*/

    private void playSong() {
        if(!mServiceBound){
            Toast.makeText(MainActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        mBoundService.playSong();

        repPlayStop.setImageResource(R.drawable.ic_pause_circle_40dp);
        repPlayStop.setTag(STOP);
    }

    private void pauseSong() {
        if(!mServiceBound){
            Toast.makeText(MainActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        mBoundService.pauseSong();
        repPlayStop.setImageResource(R.drawable.ic_play_circle_filled_40dp);
        repPlayStop.setTag(PLAY);
    }

    private void changeTrack(int offset){
        if(!mServiceBound){
            Toast.makeText(MainActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        Track track = mBoundService.changeTrack(offset);
        if(track == null){
            //No hay una lista cargada.
            Log.d(TAG, "changeTrack: No hay una lista cargada. Offset was " + offset);
            return;
        }

        updateMiniReproductorUI(track);
    }

    private void nextTrack() {
        changeTrack(1);
    }

    private void prevTrack() {
        changeTrack(-1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.EDIT_CONTENT.TRACK_EDITING_FINISHED:
                if(resultCode == RESULT_OK) {
                    //Update track information
                    Track track = (Track) data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK);
                    Log.d(TAG, "onActivityResult: REQUESTED A EDIT");
                    updateTrackDataFromEverywhere(track);
                }
            break;
            case Constants.EDIT_CONTENT.ACTIVITY_PLAYLIST_FINISHED:
                if(resultCode == RESULT_OK){
                    ArrayList<Playlist> mUpdatedPlaylists = (ArrayList<Playlist>) data.getSerializableExtra(Constants.INTENT_EXTRAS.SELECTED_PLAYLIST_UPDATE);
                    //If Playlist gone! Stop player and mini player.
                    Log.d(TAG, "onActivityResult: ARRAYLIST OF PLAYLIST SIZE IS " + mUpdatedPlaylists.size());
                    if(mBoundService.isPlaying()) {
                        if (currentSongInPlaylistDeleted(mUpdatedPlaylists)) {
                            linearLayoutMiniplayer.setVisibility(View.GONE);
                            mBoundService.playlistOrSongDeleted();
                        }
                    }

                    Fragment fragment = mFragmentManager.findFragmentByTag(tagFragmentActivado);
                    if(fragment instanceof UpdatableFragment)
                        ((UpdatableFragment) fragment).updatePlaylistInfo(mUpdatedPlaylists);

                }else  if(resultCode == RESULT_PA_USER){
                    //User must be shown.
                    User u = (User) data.getSerializableExtra(Constants.INTENT_EXTRAS.USER);
                    onUserClick(u);
                }else if(resultCode == RESULT_PA_DELETE){
                    //TODO:
                    //Track track = (Track) data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK);
                    //deleteSong(track);
                }
                break;
            case MUSIC_PLAYER_FINISHED:

                if(resultCode == Constants.EDIT_CONTENT.RESULT_MP_USER){
                    //User stuff
                    User u = (User) data.getSerializableExtra(Constants.INTENT_EXTRAS.USER);
                    onUserClick(u);
                }else if(resultCode == Constants.EDIT_CONTENT.RESULT_MP_TRACK_EDITED){
                    //SongEditStuff
                    Track track = (Track)data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK);

                    updateTrackDataFromEverywhere(track);
                }else if(resultCode == RESULT_MP_DELETE){
                    Track track = (Track) data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK);
                    deleteSong(track);
                }
                break;

            default:
                Log.d(TAG, "onActivityResult: ACTIVITY RESULT NOT CONTROLED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
    @Override
    public void updateTrackDataFromEverywhere(Track track) {
        if(mBoundService.hasTrack()) {
            if(track.isDeleted()){
                if (mBoundService.getCurrentTrack().getId() == track.getId().intValue()) {
                    linearLayoutMiniplayer.setVisibility(View.GONE);
                    mBoundService.playlistOrSongDeleted();
                }
            }else{
                //Update la track del reproductor.
                mBoundService.updateSongAfterEditing(track);
                updateIfBoundToService();
            }
        }
        Fragment fragment = mFragmentManager.findFragmentByTag(tagFragmentActivado);
        if(fragment instanceof UpdatableFragment)
            ((UpdatableFragment) fragment).updateSongInfo(track);
    }


    private boolean currentSongInPlaylistDeleted(ArrayList<Playlist> mUpdatedPlaylists) {
        Playlist currentPlaylist = mBoundService.getPlaylist();

        if(currentPlaylist == null || currentPlaylist.isLocalPlaylist()){
            return false;
        }
        int id = currentPlaylist.getId();
        for(Playlist p : mUpdatedPlaylists){
            if(p.getId() ==  id){
                return p.isDeleted();
            }
        }
        return false;
    }

    private void tryToLike(TrackViewPack track) {
        TrackManager.getInstance(this).likeTrack(track.getTrack().getId(),
                !track.getTrack().isLiked(),
                track.getCallback());
    }

    @Override
    public void deleteSong(Track track) {
        TrackManager.getInstance(this).deleteTrack(track.getId(), new TrackCallback() {
            @Override
            public void onTracksReceived(List<Track> tracks) {

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
            public void onCreateTrack(Track track) {

            }
            @Override
            public void onUpdatedTrack() {

            }

            @Override
            public void onTrackDeleted() {

                track.setDeleted(true);

                updateTrackDataFromEverywhere(track);
                CloudinaryManager.getInstance(MainActivity.this).deleteCoverImage(track.getThumbnail(),true);
                CloudinaryManager.getInstance(MainActivity.this).deleteAudioFile(track.getUrl());
            }

            @Override
            public void onPopularTracksReceived(List<Track> tracks) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "onFailure: CANT DELETE TRACK!");
            }
        });
    }

    //Control del dialogo que aparece al pulsar los 3 puntos de una track.
    @Override
    public void onButtonClicked(TrackViewPack track, String text) {

        switch (text) {
            case "like":
                Log.d(TAG, "onButtonClicked: LIKE!");
                tryToLike(track);
                break;
            case "addToPlaylist":
                Log.d(TAG, "onButtonClicked: ADDTOPLAYLIST");
                Intent intentA2P = new Intent(this, AddToPlaylistActivity.class);
                intentA2P.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, track.getTrack());
                startActivity(intentA2P);
                break;
            case "showArtist":
                Log.d(TAG, "onButtonClicked: SHOW ARTIST!");
                onUserClick(track.getTrack().getUser());
                break;

            case "edit":
                Log.d(TAG, "onButtonClicked: EDIT");
                mTrackViewPack = track;
                Intent intent = new Intent(this, EditSongActivity.class);
                intent.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, track.getTrack());
                startActivityForResult(intent, Constants.EDIT_CONTENT.TRACK_EDITING_FINISHED);
                break;

            case "delete":
                Log.d(TAG, "onButtonClicked: DELETE");
                deleteSong(track.getTrack());
                break;
        }
    }
    /*PELIGROSO PER EMOSIONANTE.*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }
}


