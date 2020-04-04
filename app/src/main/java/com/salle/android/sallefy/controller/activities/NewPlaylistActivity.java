package com.salle.android.sallefy.controller.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;

public class NewPlaylistActivity extends AppCompatActivity implements PlaylistCallback {

    private BottomNavigationView mNav;
    private ImageView mImg;
    private EditText mDescription;
    private EditText mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_playlist);
        initViews();
    }

    private void initViews() {
        mNav = (BottomNavigationView) findViewById(R.id.new_playlist_nav);
        mImg = (ImageView) findViewById(R.id.new_playlist_img);
        mDescription = (EditText) findViewById(R.id.new_playlist_description);
        mTitle = (EditText) findViewById(R.id.new_playlist_title);
        mNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.back_btn:
                        finish();
                        break;
                }
                return true;
            }
        });
        Button mCreateBtn = (Button) findViewById(R.id.new_playlist_btn);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPlaylist();
            }
        });
    }

    private void createPlaylist() {
        Playlist mPlaylist = new Playlist();
        mPlaylist.setName(mTitle.getText().toString());
        mPlaylist.setDescription(mDescription.getText().toString());
        mPlaylist.setPublicAccessible(true);
        PlaylistManager.getInstance(getApplicationContext())
                .createPlaylist(mPlaylist, NewPlaylistActivity.this);
    }

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
    public void onPlaylistCreated() {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure, Toast.LENGTH_LONG).show();
    }
}
