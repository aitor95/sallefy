package com.salle.android.sallefy.controller.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.AddToPlayListAdapter;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class  AddToPlaylistActivity extends AppCompatActivity implements PlaylistCallback {

    public static final String TAG = AddToPlaylistActivity.class.getName();

    private RecyclerView mAddToPlaylistRecyclerView;

    private AddToPlayListAdapter mAdapter;

    private ArrayList<Playlist> mSelectedPlaylists;

    private Button addToPlaylist;
    private BottomNavigationView goBack;

    private Track mTrack;
    private ArrayList<Playlist> mPlaylists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);
        mAddToPlaylistRecyclerView = (RecyclerView) findViewById(R.id.add_to_playlist_recycler_view);
        mTrack = ((Track) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK));
        mSelectedPlaylists = new ArrayList<>();
        mPlaylists = new ArrayList<>();
        intiViews();



        ArrayList<Playlist> playlists = new ArrayList<>();
        //TODO: Coge todas las playlists del usuario.
        for(int i = 0 ; i <= 10; i++) {
            playlists.add(new Playlist(i,"Titol guay" + i, "Autor nidea", null));
        }
        mAdapter = new AddToPlayListAdapter(this, playlists, mSelectedPlaylists);

        mAddToPlaylistRecyclerView.setAdapter(mAdapter);

        addToPlaylist = findViewById(R.id.add_to_playlist_btn);
        addToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "AddToPlaylistOnClick: called. Size of selected is " + mSelectedPlaylists.size());
                //TODO: Añadir la cancion a cada una de las playlists.
                /*
                for (Map.Entry<Integer, Integer> entry : mSelectedPlaylists.entrySet()) {
                    s += entry.getKey() + " " + entry.getValue() + " ";
                }
                */
            }
        });
    }

    private void intiViews() {
        mAddToPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   TrackCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onPlaylistById(Playlist playlist) {

    }

    @Override
    public void onPlaylistsByUser(ArrayList<Playlist> playlists) {

    }

    @Override
    public void onAllList(ArrayList<Playlist> playlists) {

    }

    @Override
    public void onFollowingList(ArrayList<Playlist> playlists) {

    }

    @Override
    public void onPlaylistUpdated() {

    }

    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onUserFollows(Follow follows) {

    }

    @Override
    public void onUpdateFollow(Follow result) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
