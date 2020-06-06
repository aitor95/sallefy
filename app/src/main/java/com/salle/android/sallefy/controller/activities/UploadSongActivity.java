package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.GenresAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.dialogs.StateDialog;
import com.salle.android.sallefy.controller.restapi.callback.GenreCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.GenreManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.FilenameHelper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.salle.android.sallefy.utils.Constants.STORAGE.IMAGE_SELECTED;
import static com.salle.android.sallefy.utils.Constants.STORAGE.SONG_SELECTED;


public class UploadSongActivity extends AppCompatActivity implements TrackCallback, UploadCallback, GenreCallback, AdapterClickCallback {

    public static final String TAG = UploadSongActivity.class.getName();
    public static final String EXTRA_NEW_SONG = "EXTRA_NEW_SONG";

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
    private boolean videoSet;

    private boolean uploaded;
    private Track mTrack;
    private ArrayList<Genre> mAPIGenres;
    private ArrayList<Genre> mCurrentGenres;
    private PopupMenu mGenresMenu;
    private Boolean uploading;

    private GenresAdapter mGenresAdapter;
    private RecyclerView mGenresView;

    //Dialogo para indicar el processo de carga.
    StateDialog stateDialog;


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
        uploaded = false;
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

        uploading = false;
        mUploadSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uploading) {
                    ((Button) v).setText("Uploading...");

                    uploading = true;
                    mName.setEnabled(false);
                    mFileBtn.setEnabled(false);
                    mAddGenreBtn.setEnabled(false);
                    mGenresBtn.setEnabled(false);
                    mImg.setEnabled(false);
                    uploadTrack();
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        AdapterClickCallback callback = this;
        mGenresMenu = new PopupMenu(this,mGenresBtn);
        mGenresMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mCurrentGenres.add(mAPIGenres.get(item.getItemId()));
                mGenresMenu.getMenu().removeItem(item.getItemId());

                mGenresAdapter = new GenresAdapter(mCurrentGenres, callback, R.layout.item_genre);
                mGenresView.setAdapter(mGenresAdapter);
                return false;
            }
        });

        LinearLayoutManager managerGenres = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mGenresAdapter = new GenresAdapter(null, this, R.layout.item_genre);
        mGenresView = findViewById(R.id.upload_song_genre_list);
        mGenresView.setLayoutManager(managerGenres);
        mGenresView.setAdapter(mGenresAdapter);
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    private void exitActivity() {
        //We return the edited playlist to PlaylistActivity
        if(uploaded){
            Intent data = new Intent();
            data.putExtra(Constants.INTENT_EXTRAS.TRACK, mTrack);
            setResult(RESULT_OK, data);
        }
        finish();
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
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a cover image"), IMAGE_SELECTED);
    }

    private void chooseAudioFile() {
        audioSet = false;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*, video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"audio/*", "video/*"});

        startActivityForResult(intent, Constants.STORAGE.SONG_SELECTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_SELECTED:
                if (resultCode == RESULT_OK) {
                    mCoverUri = data.getData();
                    if (mCoverUri == null) break;

                    if(FilenameHelper.getMimeType(mCoverUri,this).contains("tiff")) {
                        Toast.makeText(getApplicationContext(), "Tiff images are not supported.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    mCoverFilename = FilenameHelper.extractFromUri(mCoverUri, this);

                    Glide
                            .with(getApplicationContext())
                            .load(mCoverUri.toString())
                            .centerCrop()
                            .override(400, 400)
                            .placeholder(R.drawable.song_placeholder)
                            .into(mImg);
                    coverChosen = true;
                }

                break;
            case SONG_SELECTED:
                if (resultCode == RESULT_OK) {
                    mAudioUri = data.getData();
                    if(mAudioUri != null){
                        String mime = FilenameHelper.getMimeType(mAudioUri,this);

                        if(mime.equals("video/mp4") || mime.contains("audio")) {
                            mAudioFilename = FilenameHelper.extractFromUri(mAudioUri, this);
                            mFileBtn.setText(mAudioFilename);
                            mName.setText(mAudioFilename);
                            trackChosen = true;
                        }else{
                            Toast.makeText(getApplicationContext(),"Only MP4 videos can be uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
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

    private Track uploadTrack() {
        mTrack = new Track();

        if(!trackChosen){
            mName.setEnabled(true);
            mFileBtn.setEnabled(true);
            mAddGenreBtn.setEnabled(true);
            mGenresBtn.setEnabled(true);
            mImg.setEnabled(true);

            uploading = false;
            Toast.makeText(getApplicationContext(), R.string.upload_song_not_complete, Toast.LENGTH_SHORT).show();
            mUploadSongBtn.setText("UPLOAD");
        }else{
            if(mName.getText().toString().equals("")){
                mName.setEnabled(true);
                mFileBtn.setEnabled(true);
                mAddGenreBtn.setEnabled(true);
                mGenresBtn.setEnabled(true);
                mImg.setEnabled(true);
                Toast.makeText(getApplicationContext(), R.string.new_playlist_not_complete, Toast.LENGTH_SHORT).show();
                mUploadSongBtn.setText("UPLOAD");
                uploading = false;
            }else {

                mTrack.setName(FilenameHelper.removeSpecialCharsAndTail(mName.getText().toString()));
                ZonedDateTime currentTime = ZonedDateTime.now();
                mTrack.setReleased(currentTime.toString());
                //System.out.println(currentTime.toString());
                mTrack.setLikes(0);
                mTrack.setLiked(false);
                mTrack.setColor("#3411EE");

                if(mCurrentGenres.size() != 0){
                    mTrack.setGenres(mCurrentGenres);
                }

                stateDialog = StateDialog.getInstance(this);
                stateDialog.showStateDialog(false);

                CloudinaryManager.getInstance(this)
                        .uploadAudioFile(Constants.STORAGE.TRACK_AUDIO_FOLDER, mAudioUri, mAudioFilename+ System.currentTimeMillis(), UploadSongActivity.this);

            }
        }
        return mTrack;
    }


    /**********************************************************************************************
     *   *   *   *   *   *   *   *   TrackCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onTrackById(Track track) {

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
        Log.d("TAGG", "Track uploaded");
        stateDialog.close();
        uploaded = true;
        mTrack = track;
        mName.setEnabled(true);
        mFileBtn.setEnabled(true);
        mAddGenreBtn.setEnabled(true);
        mGenresBtn.setEnabled(true);
        mImg.setEnabled(true);
        Toast.makeText(getApplicationContext(), R.string.upload_song_creation_success, Toast.LENGTH_SHORT).show();

        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.TRACK, mTrack);
        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    public void onUpdatedTrack() {

    }

    @Override
    public void onTrackDeleted(Track track) {

    }

    @Override
    public void onPopularTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure, Toast.LENGTH_LONG).show();
        mUploadSongBtn.setText("UPLOAD");
        mName.setEnabled(true);
        mFileBtn.setEnabled(true);
        mAddGenreBtn.setEnabled(true);
        mGenresBtn.setEnabled(true);
        mImg.setEnabled(true);
        uploading = false;
        removeAllDataFromCloudinary();
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
            Log.d("TEST", "onSuccess: URL RESPONSE IS " + mTrack.getUrl());
            audioSet = true;
            if (coverChosen) {
                CloudinaryManager.getInstance(this)
                        .uploadCoverImage(Constants.STORAGE.TRACK_COVER_FOLDER, mCoverUri, mCoverFilename+ System.currentTimeMillis(), UploadSongActivity.this);
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
        mUploadSongBtn.setText("UPLOAD");
        mName.setEnabled(true);
        mFileBtn.setEnabled(true);
        mAddGenreBtn.setEnabled(true);
        mGenresBtn.setEnabled(true);
        mImg.setEnabled(true);
        uploading = false;
        removeAllDataFromCloudinary();

    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {

    }

    private void tryToLike(TrackViewPack track) {
        TrackManager.getInstance(this).likeTrack(track.getTrack().getId(),
                !track.getTrack().isLiked(),
                track.getCallback());
    }

    private void removeAllDataFromCloudinary(){
        CloudinaryManager m = CloudinaryManager.getInstance(this);

        m.deleteAudioFile(mTrack.getUrl());
        m.deleteCoverImage(mTrack.getThumbnail(),true);
    }

    @Override
    public void onTrackClicked(Track track, Playlist playlist) {

    }

    @Override
    public void onPlaylistClick(Playlist playlist) {

    }

    @Override
    public void onUserClick(User user) {

    }

    @Override
    public void onGenreClick(Genre genre) {
        mCurrentGenres.remove(genre);
        mGenresMenu.getMenu().add(Menu.NONE, mAPIGenres.indexOf(genre), Menu.NONE, genre.getName());

        mGenresAdapter = new GenresAdapter(mCurrentGenres, this, R.layout.item_genre);
        mGenresView.setAdapter(mGenresAdapter);
    }
}
