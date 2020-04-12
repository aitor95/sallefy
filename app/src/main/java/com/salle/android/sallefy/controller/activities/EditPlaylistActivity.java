package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;

import okhttp3.ResponseBody;

public class EditPlaylistActivity extends AppCompatActivity implements PlaylistCallback {

    public static final String TAG = EditPlaylistActivity.class.getName();
    private ImageButton mNav;
    private ImageView mImg;
    private EditText mDescription;
    private EditText mTitle;
    private Playlist mPlaylist;
    private Integer pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        Intent intent = getIntent();
        this.pId = (Integer) intent.getSerializableExtra("playlistId");
        initViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PlaylistManager.getInstance(getApplicationContext())
                .getPlaylistById(this.pId, EditPlaylistActivity.this);
    }

    private void initViews() {
        mImg = (ImageView) findViewById(R.id.edit_playlist_img);
        mDescription = (EditText) findViewById(R.id.edit_playlist_description);
        mTitle = (EditText) findViewById(R.id.edit_playlist_title);

        PlaylistManager.getInstance(getApplicationContext())
                .getPlaylistById(this.pId, EditPlaylistActivity.this);

        mNav = (ImageButton) findViewById(R.id.edit_playlist_nav);
        mNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button mSaveBtn = (Button) findViewById(R.id.edit_playlist_save);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlaylist();
            }
        });

        Button mAddSongsBtn = (Button) findViewById(R.id.edit_playlist_add);
        mAddSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddSongsToPlaylistActivity.class);
                intent.putExtra("playlistId", pId);
                startActivity(intent);
            }
        });
    }

    private void updatePlaylist() {
        this.mPlaylist.setName(mTitle.getText().toString());
        this.mPlaylist.setDescription(mDescription.getText().toString());
        PlaylistManager.getInstance(getApplicationContext())
                .updatePlaylist(this.mPlaylist, EditPlaylistActivity.this);
    }

    @Override
    public void onPlaylistById(Playlist playlist) {
        this.mPlaylist = playlist;
        String pImg = playlist.getThumbnail();

        if(pImg != null){
            Glide.with(this).load(pImg).into(mImg);
        }

        String pTitle = playlist.getName();
        String pDescription = playlist.getDescription();

        if(pDescription != null){
            mDescription.setText(pDescription);
            mDescription.setSelection(0);
        }

        if(pTitle != null){
            mTitle.setText(pTitle);
        }
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
    public void onUserFollows(ResponseBody follows) {

    }

    @Override
    public void onUpdateFollow(ResponseBody result) {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.edit_playlist_creation_failure, Toast.LENGTH_LONG).show();
        Log.e(TAG, "onFailure: "+throwable.getMessage());
    }
}
