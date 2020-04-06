package com.salle.android.sallefy.controller.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.AddToPlayListAdapter;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;
import java.util.HashMap;

public class AddToPlaylistActivity extends AppCompatActivity {

    public static final String TAG = AddToPlaylistActivity.class.getName();

    private RecyclerView mAddToPlaylistRecyclerView;
    private AddToPlayListAdapter mAdapter;

    private HashMap<Integer, Integer> mSelectedPlaylists;

    private Button addToPlaylist;
    private BottomNavigationView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);
        mAddToPlaylistRecyclerView = (RecyclerView) findViewById(R.id.add_to_playlist_recycler_view);
        mAddToPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSelectedPlaylists = new HashMap<>();

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

        goBack = findViewById(R.id.add_to_playlist_back);
        goBack.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.add_to_playlist_back:
                        finish();
                        break;
                }
                return true;
            }
        });
    }

}
