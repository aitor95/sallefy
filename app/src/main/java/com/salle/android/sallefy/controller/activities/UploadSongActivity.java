package com.salle.android.sallefy.controller.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.salle.android.sallefy.R;

public class UploadSongActivity extends AppCompatActivity {

    private Button uploadSong;
    private Button addGenre;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

    }
}
