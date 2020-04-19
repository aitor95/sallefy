package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.AddSongsToPlayListAdapter;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class AddSongsToPlaylistActivity extends AppCompatActivity implements PlaylistCallback, TrackCallback {

    public static final String TAG = AddSongsToPlaylistActivity.class.getName();

    private RecyclerView mAddSongToPlaylistRecyclerView;

    private AddSongsToPlayListAdapter mAdapter;

    private ArrayList<Track> mSelectedSongs;

    private Button doneBtn;
    private ImageButton backBtn;

    private Playlist mPlaylist;
    private List<Track> mPlaylistTracks;
    private ArrayList<Track> mTracks;
    private Integer pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs_to_playlist);
        mAddSongToPlaylistRecyclerView = (RecyclerView) findViewById(R.id.add_songs_to_playlist_recycler_view);

        Intent intent = getIntent();
        this.pId = (Integer) intent.getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST_ID);
        initViews();
    }

    private void initViews() {
        mAddSongToPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSelectedSongs = new ArrayList<>();

        mTracks = new ArrayList<>();

        TrackManager.getInstance(getApplicationContext()).getAllTracks(this);


        doneBtn = findViewById(R.id.add_songs_to_playlist_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "AddToPlaylistOnClick: called. Size of selected is " + mSelectedSongs.size());
                //TODO: Añadir la cancion a cada una de las playlists.
                updatePlaylist();

            }
        });

        //Back button
        backBtn = findViewById(R.id.add_songs_to_playlist_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updatePlaylist() {
        Log.d(TAG, "AddToPlaylistOnClick: called. Size of selected is " + mSelectedSongs.size());
        for (int i = 0; i < mSelectedSongs.size(); i++) {
            this.mPlaylist.getTracks().add(mSelectedSongs.get(i));
        }
        PlaylistManager.getInstance(getApplicationContext())
                .updatePlaylist(this.mPlaylist, AddSongsToPlaylistActivity.this);
    }

    @Override
    public void onPlaylistById(Playlist playlist) {
        this.mPlaylist = playlist;
        if(this.mPlaylist.getTracks()!=null){
            for (int i = 0; i < this.mTracks.size(); i++) {
                for (int j = 0; j < this.mPlaylist.getTracks().size(); j++) {
                    if(this.mTracks.get(i).getId().intValue() == this.mPlaylist.getTracks().get(j).getId().intValue()){
                        this.mTracks.remove(mTracks.get(i));
                    }
                }
            }
        }
        //Add last track for New Track row
        Track newTrack = new Track();
        newTrack.setName(getResources().getString(R.string.add_songs_to_playlist_lastitem_title));
        this.mTracks.add(0, newTrack);

        //Create RV tracks adapter
        mAdapter = new AddSongsToPlayListAdapter(this, (ArrayList) this.mTracks, mSelectedSongs);
        mAddSongToPlaylistRecyclerView.setAdapter(mAdapter);
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
        Toast.makeText(getApplicationContext(), R.string.edit_playlist_creation_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaylistCreated() {

    }

    @Override
    public void onUserFollows(Follow follows) {

    }

    @Override
    public void onUpdateFollow(Follow result) {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.edit_playlist_creation_failure, Toast.LENGTH_LONG).show();
        Log.e(TAG, "onFailure: "+throwable.getMessage());
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        mTracks = (ArrayList)tracks;
        PlaylistManager.getInstance(getApplicationContext())
                .getPlaylistById(this.pId, AddSongsToPlaylistActivity.this);
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
}
