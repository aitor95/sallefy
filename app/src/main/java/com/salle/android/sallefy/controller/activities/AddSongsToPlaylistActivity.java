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

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.AddSongsToPlayListAdapter;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.PaginatedRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddSongsToPlaylistActivity extends AppCompatActivity implements PlaylistCallback, TrackCallback, BottomMenuDialog.BottomMenuDialogInterf {

    public static final String TAG = AddSongsToPlaylistActivity.class.getName();

    private PaginatedRecyclerView mAddSongToPlaylistRecyclerView;

    private AddSongsToPlayListAdapter mAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<Track> mSelectedSongs;

    private Button doneBtn;
    private ImageButton backBtn;

    private Playlist mPlaylist;
    private ArrayList<Track> mTracks;

    //Pagination
    private int currentPage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs_to_playlist);
        mAddSongToPlaylistRecyclerView = (PaginatedRecyclerView) findViewById(R.id.add_songs_to_playlist_recycler_view);
        mAddSongToPlaylistRecyclerView.setListener(new PaginatedRecyclerView.PaginatedRecyclerViewListener() {
            @Override
            public void onPageLoaded() {
                loadMoreItems();
            }
        });
        initViews();
    }

    private void initViews() {

        mSelectedSongs = new ArrayList<>();

        mTracks = new ArrayList<>();
        Track newTrack = new Track();
        newTrack.setName(getResources().getString(R.string.add_songs_to_playlist_lastitem_title));
        this.mTracks.add(0, newTrack);

        TrackManager.getInstance(getApplicationContext()).getAllTracksPagination(this, currentPage, 10, true, false);


        doneBtn = findViewById(R.id.add_songs_to_playlist_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "AddToPlaylistOnClick: called. Size of selected is " + mSelectedSongs.size());
                updatePlaylist();
            }
        });

        //Back button
        backBtn = findViewById(R.id.add_songs_to_playlist_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { exitAddSongs(); }
        });
    }

    private void exitAddSongs(){
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, mPlaylist);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitAddSongs();
    }

    private void loadMoreItems() {
        mAddSongToPlaylistRecyclerView.setLoading(true);

        currentPage += 1;

        TrackManager.getInstance(getApplicationContext()).getAllTracksPagination(this, currentPage, 10, true, false);

    }


    private void updatePlaylist() {
        Log.d(TAG, "AddToPlaylistOnClick: called. Size of selected is " + mSelectedSongs.size());
        for (int i = 0; i < mSelectedSongs.size(); i++) {
            this.mPlaylist.getTracks().add(mSelectedSongs.get(i));
        }
        PlaylistManager.getInstance(getApplicationContext())
                .updatePlaylist(this.mPlaylist, AddSongsToPlaylistActivity.this);
    }

    private void checkExistingTracks(){
        if(this.mTracks.size() < PaginatedRecyclerView.PAGE_SIZE){
            mAddSongToPlaylistRecyclerView.setLast(true);
        }

        if(this.mPlaylist.getTracks()!=null){
            for (int i = 1; i < this.mTracks.size(); i++) {
                if(i!=0){
                    for (int j = 0; j < this.mPlaylist.getTracks().size(); j++) {
                        if(this.mTracks.get(i).getId().intValue() == this.mPlaylist.getTracks().get(j).getId().intValue()){
                            this.mTracks.remove(mTracks.get(i));
                            i--;
                            break;
                        }
                    }
                }
            }
        }

        if(!mAddSongToPlaylistRecyclerView.isLast() && this.mTracks.size() < PaginatedRecyclerView.PAGE_SIZE){
            loadMoreItems();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_CONTENT.TRACK_EDIT && resultCode == RESULT_OK) {
            mTracks.add(1, (Track) data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK));
            mAdapter = new AddSongsToPlayListAdapter(this, (ArrayList) this.mTracks, mSelectedSongs);
            mAddSongToPlaylistRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onPlaylistById(Playlist playlist) {
        this.mPlaylist = playlist;
        //Add last track for New Track row
        checkExistingTracks();
        //Create RV tracks adapter
        mAdapter = new AddSongsToPlayListAdapter(this, (ArrayList) this.mTracks, mSelectedSongs);
        mAddSongToPlaylistRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onPlaylistsByUser(ArrayList<Playlist> playlists) {

    }

    @Override
    public void onOwnList(ArrayList<Playlist> playlists) {

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
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, mPlaylist);
        setResult(RESULT_OK, data);
        finish();
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
    public void onPlaylistDeleted() {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.edit_playlist_creation_failure, Toast.LENGTH_LONG).show();
        Log.e(TAG, "onFailure: "+throwable.getMessage());
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        mTracks.addAll(tracks);
        if(mAddSongToPlaylistRecyclerView.isLoading()){
            mAddSongToPlaylistRecyclerView.setLoading(false);
            checkExistingTracks();
            mAdapter.notifyDataSetChanged();
        }else{
            onPlaylistById((Playlist) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST));
        }
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
                Intent intentA2P = new Intent(this, AddToPlaylistActivity.class);
                intentA2P.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, track.getTrack());
                startActivity(intentA2P);
                break;
            case "showArtist":
                Log.d(TAG, "onButtonClicked: SHOW ARTIST!");
                break;
            case "delete":
                Log.d(TAG, "onButtonClicked: DELETE");
                break;
            case "edit":
                Log.d(TAG, "onButtonClicked: EDIT");
                Intent intent = new Intent(this, EditSongActivity.class);
                intent.putExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK, track.getTrack());
                startActivity(intent);
                break;
        }
    }
}
