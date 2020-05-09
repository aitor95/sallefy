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
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.fragments.HomeFragment;
import com.salle.android.sallefy.controller.fragments.MeFragment;
import com.salle.android.sallefy.controller.fragments.SearchFragment;
import com.salle.android.sallefy.controller.fragments.SocialFragment;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.OnSwipeListener;
import com.salle.android.sallefy.utils.Session;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends FragmentActivity implements AdapterClickCallback, MusicCallback, BottomMenuDialog.BottomMenuDialogInterf {

    public static final String TAG = MainActivity.class.getName();

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


    //Gesture detector.
    private GestureDetectorCompat detector;

    //TAG del fragment activado actualmente...
    private  static String tagFragmentActivado;


    // Service
    private MusicService mBoundService;
    private boolean mServiceBound = false;

    //Track edit
    private static TrackViewPack mTrackViewPack;


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
                enterMeFragment();
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

    private void enterMeFragment() {
        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView_Active);
        home.setTextAppearance(R.style.BottomNavigationView);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = MeFragment.getInstance();
        MeFragment.setAdapterClickCallback(this);
        replaceFragment(fragment);
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


    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS}, Constants.PERMISSIONS.MICROPHONE);

        } else {
            Session.getInstance(this).setAudioEnabled(true);
        }
    }

    private void replaceFragment(Fragment fragment) {

        String fragmentTag = getFragmentTag(fragment);
        tagFragmentActivado = fragmentTag;
        Fragment currentFragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (currentFragment != null) {
            if (!currentFragment.isVisible()) {

                if (fragment.getArguments() != null) {
                    currentFragment.setArguments(fragment.getArguments());
                }
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, currentFragment, fragmentTag)
                        //.addToBackStack(null)
                        .commit();

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(requestCode);
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
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonPressed = true;
            }
        } else {
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
                        enterMeFragment();
                        rtn = true;
                    }
                } else {
                    if(direction == OnSwipeListener.Direction.right) {
                        enterMeFragment();
                        rtn = true;
                    }
                }
            }
        }
        return rtn;
    }

    @Override
    public void onTrackClicked(Track track, Playlist playlist) {
        if(playlist == null){

            Log.d(TAG, "onTrackSelected: Track is " + track.getName());
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra(Constants.INTENT_EXTRAS.PLAYER_SONG,track);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra(Constants.INTENT_EXTRAS.PLAYER_SONG,track);
            intent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST,playlist);
            startActivity(intent);
        }
        Log.d(TAG, "onTrackClicked");

    }

    @Override
    public void onPlaylistClick(Playlist playlist) {
        Log.d(TAG, "onPlaylistClick: Playlist is " + playlist.getName());

        Intent intent = new Intent(this, PlaylistActivity.class);
        PlaylistActivity.setAdapterClickCallback(this);
        intent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, playlist);
        startActivityForResult(intent, Constants.EDIT_CONTENT.SELECTED_PLAYLIST_UPDATE);
    }

    @Override
    public void onUserClick(User user) {
        Log.d(TAG, "onUserClick: User name is " + user.getLogin());
    }

    @Override
    public void onGenreClick(Genre genre) {
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

        Toast.makeText(this, "Playing Audio", Toast.LENGTH_SHORT).show();
    }

    private void pauseSong() {
        if(!mServiceBound){
            Toast.makeText(MainActivity.this, R.string.ServiceNotLoadedError, Toast.LENGTH_SHORT).show();
            return;
        }
        mBoundService.pauseSong();
        repPlayStop.setImageResource(R.drawable.ic_play_circle_filled_40dp);
        repPlayStop.setTag(PLAY);
        Toast.makeText(this, "Pausing Audio", Toast.LENGTH_SHORT).show();
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

        if (requestCode == Constants.EDIT_CONTENT.TRACK_EDIT && resultCode == RESULT_OK) {
            //Update track information
            Track track = (Track)data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK);

            if(track.isDeleted()){

            }else{

                TrackViewPack tvp = mTrackViewPack;
                tvp.setTrack(track);
                tvp.getViewHolder().updateViewHolder(track);

                mTrackViewPack = tvp;

                /*Fragment fragment = mFragmentManager.findFragmentByTag(tagFragmentActivado);
                if(fragment instanceof MeFragment){
                    ((MeFragment) fragment).updateSongInfo(track);
                }*/
            }
        }else{
            if(requestCode == Constants.EDIT_CONTENT.SELECTED_PLAYLIST_UPDATE && resultCode == RESULT_OK){
                ArrayList<Playlist> mUpdatedPlaylists = (ArrayList<Playlist>) data.getSerializableExtra(Constants.INTENT_EXTRAS.SELECTED_PLAYLIST_UPDATE);
                //If Playlist gone! Stop player and mini player.

                if(currentSongDeleted(mUpdatedPlaylists)){
                    linearLayoutMiniplayer.setVisibility(View.GONE);
                    mBoundService.playlistOrSongDeleted();
                }

                Fragment fragment = mFragmentManager.findFragmentByTag(tagFragmentActivado);
                if (fragment instanceof MeFragment) {
                    ((MeFragment) fragment).updatePlaylistInfo(mUpdatedPlaylists);
                }
            }
        }
    }

    private boolean currentSongDeleted(ArrayList<Playlist> mUpdatedPlaylists) {
        Playlist currentPlaylist = mBoundService.getPlaylist();

        if(currentPlaylist == null || currentPlaylist.isLocalPlaylist()){
            return false;
        }
        int id = currentPlaylist.getId();
        for(Playlist p : mUpdatedPlaylists){
            if(p.getId() ==  id && p.isDeleted()){
                return true;
            }else{
                return false;
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
    public void onButtonClicked(TrackViewPack track, String text) {
        switch (text) {
            case "like":
                Log.d(TAG, "onButtonClicked: LIKE!");
                tryToLike(track);
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
                mTrackViewPack = track;
                Intent intent = new Intent(this, EditSongActivity.class);
                intent.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, track.getTrack());
                startActivityForResult(intent, Constants.EDIT_CONTENT.TRACK_EDIT);
                break;
        }
    }
}


