package com.salle.android.sallefy.controller.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.model.Genre;

import java.util.ArrayList;

public class EditSongActivity extends AppCompatActivity {

    public static final String TAG = EditSongActivity.class.getName();

    //Layout
    private ImageView mImg;
    private ImageButton mBackBtn;
    private Button mSaveBtn;
    private ImageButton mAddGenreBtn;
    private Button mFileBtn;
    private ImageButton mGenresBtn;
    private EditText mName;

    //Cover file
    private Uri mCoverUri;
    private String mCoverFilename;

    //Audio file
    private Uri mAudioUri;
    private String mAudioFilename;

    //Logic
    private boolean coverChosen;
    private boolean trackChosen;
    private boolean genresFetched;
    private boolean audioSet;
    private ArrayList<Genre> mAPIGenres;
    private ArrayList<Genre> mCurrentGenres;
    private PopupMenu mGenresMenu;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);
        initLogic();
        initViews();
    }

    private void initLogic() {
        coverChosen = false;
        trackChosen = false;
        genresFetched = false;
        audioSet = false;
        mCurrentGenres = new ArrayList<Genre>();
    }

    private void initViews() {
        initElements();

    }

    private void initElements() {
        mImg = findViewById(R.id.song_photo);
        mBackBtn = findViewById(R.id.edit_song_back_btn);
        mSaveBtn = findViewById(R.id.edit_song_btn_action);
        mAddGenreBtn = findViewById(R.id.add_new_genre);
        mName = findViewById(R.id.edit_song_name);
        mFileBtn = findViewById(R.id.edit_song_file_btn);
        mGenresBtn = findViewById(R.id.add_new_genre);
    }
}
