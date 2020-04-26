package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.AddToPlayListAdapter;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

public class  AddToPlaylistActivity extends AppCompatActivity implements PlaylistCallback {

    public static final String TAG = AddToPlaylistActivity.class.getName();

    //Layout
    private Button addToPlaylist;
    private ImageButton backBtn;
    private RecyclerView mAddToPlaylistRecyclerView;
    private AddToPlayListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    //Logic
    private Track mTrack;
    private ArrayList<Playlist> mPlaylists;
    private ArrayList<Playlist> mSelectedPlaylists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);
        mAddToPlaylistRecyclerView = (RecyclerView) findViewById(R.id.add_to_playlist_recycler_view);
        intiLogic();
        intiViews();
    }

    private void intiLogic() {
        mTrack = ((Track) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK));
        mSelectedPlaylists = new ArrayList<>();
        mPlaylists = new ArrayList<>();
        Playlist newPlaylist = new Playlist();
        newPlaylist.setName(getResources().getString(R.string.add_to_playlist_lastitem_title));
        this.mPlaylists.add(0, newPlaylist);
    }

    private void intiViews() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mAddToPlaylistRecyclerView.setLayoutManager(mLinearLayoutManager);

        PlaylistManager.getInstance(getApplicationContext()).getOwnPlaylists(this);

        backBtn = findViewById(R.id.add_to_playlist_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });


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

    private void checkExistingPlaylists() {
        for (int i = 1; i < mPlaylists.size(); i++) {
            for (int j = 0; j < mPlaylists.get(i).getTracks().size(); j++) {
                if(this.mPlaylists.get(i).getTracks().get(j).getId().intValue() == this.mTrack.getId().intValue()){
                    this.mPlaylists.remove(mPlaylists.get(i));
                }

            }
        }
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
    public void onOwnList(ArrayList<Playlist> playlists) {
        this.mPlaylists.addAll(playlists);
        //Add last track for New Track row
        checkExistingPlaylists();
        //Create RV tracks adapter
        mAdapter = new AddToPlayListAdapter(this, (ArrayList) this.mPlaylists, this.mSelectedPlaylists);
        mAddToPlaylistRecyclerView.setAdapter(mAdapter);
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
