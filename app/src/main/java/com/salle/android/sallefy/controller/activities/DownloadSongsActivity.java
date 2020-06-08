package com.salle.android.sallefy.controller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.callbacks.SeeAllCallback;
import com.salle.android.sallefy.controller.download.ObjectBox;
import com.salle.android.sallefy.controller.fragments.SeeAllSongFragment;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.PaginatedRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.salle.android.sallefy.utils.Constants.EDIT_CONTENT.MUSIC_PLAYER_FINISHED;

public class DownloadSongsActivity extends AppCompatActivity implements TrackCallback, AdapterClickCallback {

    public static final long TIME_BETWEEN_CLICKS = 300;

    public static final String TAG = SeeAllSongFragment.class.getName();
    public static final String TAG_CONTENT_POPULAR = SeeAllSongFragment.class.getName() + "_popular";
    public static final String TAG_CONTENT_DATA = SeeAllSongFragment.class.getName() + "_data";
    //private int NUMBER_OF_TRACKS;
    private long lastClick = 0;

    private PaginatedRecyclerView mRecyclerView;
    private MusicService mBoundService;
    private ArrayList mTracks;

    private static Playlist mPlaylist;
    private boolean popular;

    private TrackListVerticalAdapter mAdapter;
    private Intent intent;

    private boolean fullOffline;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mBoundService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: Service desconnected.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_songs);

        initViews();
        startStreamingService();

        popular = true;
        mTracks = ObjectBox.getInstance(this).getAllDlownloadedTracks();
        mAdapter = new TrackListVerticalAdapter(this, this, getSupportFragmentManager(), mTracks);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setPageSize(mTracks.size());

        mPlaylist = new Playlist();
        mPlaylist.setTracks(mTracks);
        mAdapter.setPlaylist(mPlaylist);

        fullOffline = getIntent().getBooleanExtra("FULL_OFFLINE", false);
        if (fullOffline) {
            ImageButton btn = findViewById(R.id.back_from_download_btn);
            btn.setVisibility(View.GONE);
        }
    }

    private void startStreamingService () {
        intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    private void initViews() {
        mRecyclerView = (PaginatedRecyclerView) findViewById(R.id.dynamic_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(new TrackListVerticalAdapter(this, this, getSupportFragmentManager(), mTracks));

        findViewById(R.id.back_from_download_btn).setOnClickListener(view -> {
            if (!fullOffline) finish();
        });
    }

    public static SeeAllSongFragment newInstance(ArrayList<Track> tracks, boolean popular) {
        SeeAllSongFragment fragment = new SeeAllSongFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_CONTENT_DATA, tracks);
        bundle.putSerializable(TAG_CONTENT_POPULAR, popular);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (!fullOffline) finish();
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onTrackById(Track track) {

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
    public void onTrackDeleted(Track track) {

    }

    @Override
    public void onPopularTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onFailure(Throwable throwable) {

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

    }

    @Override
    public void onUserClick(User user) {

    }

    @Override
    public void onGenreClick(Genre genre) {

    }
}