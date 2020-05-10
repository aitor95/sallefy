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
import com.salle.android.sallefy.controller.dialogs.StateDialog;
import com.salle.android.sallefy.controller.restapi.callback.GenreCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.GenreManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.FilenameHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditSongActivity extends AppCompatActivity implements TrackCallback, UploadCallback, GenreCallback {

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
    private Track mTrack;
    private ArrayList<Genre> mAPIGenres;
    private ArrayList<Genre> mCurrentGenres;
    private PopupMenu mGenresMenu;

    //Dialogo para indicar el processo de carga.
    StateDialog stateDialog;

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
        Intent intent = getIntent();
        mTrack = (Track) intent.getSerializableExtra(Constants.INTENT_EXTRAS.CURRENT_TRACK);

        mCurrentGenres = (ArrayList<Genre>) mTrack.getGenres();
    }

    private void initViews() {
        initElements();
        initTrackInfo();

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseCoverImage(); }
        });

        mFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseAudioFile(); }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrack();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEditing();
            }
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

        Button mDeleteButton = findViewById(R.id.edit_song_btn_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloudinaryManager m = CloudinaryManager.getInstance(EditSongActivity.this);

                m.deleteAudioFile(mTrack.getUrl());
                m.deleteCoverImage(mTrack.getThumbnail(),true);

                TrackManager.getInstance(EditSongActivity.this).deleteTrack(mTrack.getId(),EditSongActivity.this);
            }
        });
    }

    private void initTrackInfo() {
        mName.setText(mTrack.getName().replace("\n", ""));
        Glide.with(getApplicationContext())
                .load(mTrack.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.ic_audiotrack)
                .override(400,400)
                .into(mImg);
    }

    private void initElements() {
        mImg = findViewById(R.id.song_photo);
        mBackBtn = findViewById(R.id.edit_song_back_btn);
        mSaveBtn = findViewById(R.id.edit_song_btn_save);
        mAddGenreBtn = findViewById(R.id.add_new_genre);
        mName = findViewById(R.id.edit_song_name);
        mFileBtn = findViewById(R.id.edit_song_file_btn);
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
    public void onBackPressed() { exitEditing(); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STORAGE.IMAGE_SELECTED && resultCode == RESULT_OK) {
            mCoverUri = data.getData();
            mCoverFilename = FilenameHelper.extractFromUri(mCoverUri,this);
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
                mAudioFilename = FilenameHelper.extractFromUri(mAudioUri,this);
                mFileBtn.setText(mAudioFilename);
                mName.setText(mAudioFilename);
                trackChosen = true;
            }
        }
    }


    public void checkGenres(View v){
        if(!genresFetched){
            GenreManager.getInstance(this)
                    .getAllGenres(EditSongActivity.this);
        }else{
            mGenresMenu.show();
        }
    }


    private void updateTrack() {

        mTrack.setName(mName.getText().toString());
        mTrack.setGenres(mCurrentGenres);
        stateDialog = StateDialog.getInstance(this);
        stateDialog.showStateDialog(false);

        if(trackChosen){
            CloudinaryManager.getInstance(this)
                    .uploadAudioFile(Constants.STORAGE.TRACK_AUDIO_FOLDER, mAudioUri, mAudioFilename, EditSongActivity.this);
        }else{
            if(coverChosen){
                CloudinaryManager.getInstance(this)
                        .uploadCoverImage(Constants.STORAGE.TRACK_COVER_FOLDER, mCoverUri, mCoverFilename, EditSongActivity.this);
            }else{
                TrackManager.getInstance(this)
                        .updateTrack(mTrack, EditSongActivity.this);
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
    public void onCreateTrack(Track track) {

    }

    @Override
    public void onUpdatedTrack() {
        if(stateDialog != null)
            stateDialog.close();
        Toast.makeText(getApplicationContext(), R.string.edit_song_update_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrackDeleted() {
        mTrack.setDeleted(true);
        exitEditing();
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
            for (int j = 0; j < mCurrentGenres.size(); j++) {
                if(!mCurrentGenres.get(j).getId().equals(mAPIGenres.get(i).getId())){
                    mGenresMenu.getMenu().add(0, i, i, mAPIGenres.get(i).getName());
                }
            }
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
        if(!audioSet && trackChosen){
            //Uploaded the audio file
            mTrack.setUrl((String)resultData.get("url"));
            audioSet = true;
            trackChosen = false;
            if (coverChosen) {
                CloudinaryManager.getInstance(this)
                        .uploadCoverImage(Constants.STORAGE.TRACK_COVER_FOLDER, mCoverUri, mCoverFilename, EditSongActivity.this);
            }else{
                TrackManager.getInstance(this)
                        .updateTrack(mTrack, EditSongActivity.this);
            }
        }else{
            if(coverChosen){
                //Uploaded the cover file
                mTrack.setThumbnail((String)resultData.get("url"));

                TrackManager.getInstance(this)
                        .updateTrack(mTrack, EditSongActivity.this);
                coverChosen = false;
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

    private void exitEditing() {
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.TRACK, mTrack);
        data.putExtra("audioSet", audioSet);
        setResult(RESULT_OK, data);
        finish();
    }
}