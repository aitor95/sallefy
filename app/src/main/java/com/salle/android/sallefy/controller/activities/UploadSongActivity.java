package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.GenreCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.GenreManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UploadSongActivity extends AppCompatActivity implements TrackCallback, UploadCallback, GenreCallback {

    public static final String TAG = UploadSongActivity.class.getName();

    //Layout
    private ImageView mImg;
    private ImageButton mBackBtn;
    private Button mUploadSongBtn;
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
    private Track mTrack;
    private ArrayList<Genre> mAPIGenres;
    private ArrayList<Genre> mCurrentGenres;
    private PopupMenu mGenresMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);
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

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseCoverImage(); }
        });

        mFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseAudioFile(); }
        });

        mUploadSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTrack();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        mGenresMenu = new PopupMenu(this,mGenresBtn);
        mGenresMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mCurrentGenres.add(mAPIGenres.get(item.getItemId()));
                mGenresMenu.getMenu().removeItem(item.getItemId());
                return false;
            }
        });

    }

    private void initElements() {
        mImg = findViewById(R.id.song_photo);
        mBackBtn = findViewById(R.id.upload_song_back);
        mUploadSongBtn = findViewById(R.id.upload_song_btn_action);
        mAddGenreBtn = findViewById(R.id.add_new_genre);
        mName = findViewById(R.id.upload_song_name);
        mFileBtn = findViewById(R.id.upload_song_file_btn);
        mGenresBtn = findViewById(R.id.add_new_genre);
    }

    private void chooseCoverImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a cover image"), Constants.STORAGE.IMAGE_SELECTED);
    }

    private void chooseAudioFile() {
        audioSet = false;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a cover image"), Constants.STORAGE.SONG_SELECTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STORAGE.IMAGE_SELECTED && resultCode == RESULT_OK) {
            mCoverUri = data.getData();
            mCoverFilename = mCoverUri.toString();
            Glide
                    .with(getApplicationContext())
                    .load(mCoverUri.toString())
                    .centerCrop()
                    .override(400,400)
                    .placeholder(R.drawable.song_placeholder)
                    .into(mImg);
            coverChosen = true;
        }else{
            if (requestCode == Constants.STORAGE.SONG_SELECTED && resultCode == RESULT_OK) {
                mAudioUri = data.getData();
                mAudioFilename = mAudioUri.toString();
                mFileBtn.setText(mAudioFilename);
                trackChosen = true;
            }
        }
    }

    public void checkGenres(View v){
        if(!genresFetched){
            GenreManager.getInstance(this)
                    .getAllGenres(UploadSongActivity.this);
        }else{
            mGenresMenu.show();
        }
    }

    private void uploadTrack() {
        mTrack = new Track();

        if(!trackChosen){

            Toast.makeText(getApplicationContext(), R.string.upload_song_not_complete, Toast.LENGTH_SHORT).show();

        }else{
            if(mName.getText().toString().equals("")){

                Toast.makeText(getApplicationContext(), R.string.new_playlist_not_complete, Toast.LENGTH_SHORT).show();

            }else {

                mTrack.setName(mName.getText().toString());

                if(mCurrentGenres.size() != 0){
                    mTrack.setGenres(mCurrentGenres);
                }

                CloudinaryManager.getInstance(this)
                        .uploadAudioFile(Constants.STORAGE.TRACK_AUDIO_FOLDER, mAudioUri, mAudioFilename, UploadSongActivity.this);

            }
        }
    }


    /**********************************************************************************************
     *   *   *   *   *   *   *   *   TrackCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onTracksReceived(List<Track> tracks) {

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
        Toast.makeText(getApplicationContext(), R.string.upload_song_creation_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdatedTrack() {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure, Toast.LENGTH_LONG).show();
    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   GenreCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mAPIGenres = genres;
        for (int i = 0; i < genres.size(); i++) {
            mGenresMenu.getMenu().add(0, i, i, mAPIGenres.get(i).getName());
        }
        mGenresMenu.show();
        genresFetched = true;
    }

    @Override
    public void onTracksByGenre(ArrayList<Track> tracks) {

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   UploadCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onStart(String requestId) {

    }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {

    }

    @Override
    public void onSuccess(String requestId, Map resultData) {
        if(!audioSet){
            //Uploaded the audio file
            mTrack.setUrl((String)resultData.get("url"));
            audioSet = true;
            if (coverChosen) {
                CloudinaryManager.getInstance(this)
                        .uploadCoverImage(Constants.STORAGE.TRACK_COVER_FOLDER, mCoverUri, mCoverFilename, UploadSongActivity.this);
            }else{
                TrackManager.getInstance(this)
                        .createTrack(mTrack, UploadSongActivity.this);
            }
        }else{
            if(coverChosen){
                //Uploaded the cover file
                mTrack.setThumbnail((String)resultData.get("url"));
                TrackManager.getInstance(this)
                        .createTrack(mTrack, UploadSongActivity.this);
            }
        }
    }

    @Override
    public void onError(String requestId, ErrorInfo error) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {

    }
}
