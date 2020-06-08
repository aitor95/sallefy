package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.FilenameHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.salle.android.sallefy.utils.Constants.STORAGE.IMAGE_SELECTED;
import static com.salle.android.sallefy.utils.Constants.STORAGE.SONG_SELECTED;

public class EditSongActivity extends AppCompatActivity implements TrackCallback, UploadCallback, GenreCallback, AdapterClickCallback {

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

    private GenresAdapter mGenresAdapter;
    private RecyclerView mGenresView;

    //Dialogo para indicar el processo de carga.
    StateDialog stateDialog;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        initLogic();
        initViews();

        GenreManager.getInstance(this)
                .getAllGenres(EditSongActivity.this);
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
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
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

        Button mDeleteButton = findViewById(R.id.edit_song_btn_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloudinaryManager m = CloudinaryManager.getInstance(EditSongActivity.this);

                m.deleteAudioFile(mTrack.getUrl());
                m.deleteCoverImage(mTrack.getThumbnail(),true);

                TrackManager.getInstance(EditSongActivity.this).deleteTrack(mTrack,EditSongActivity.this, getApplicationContext());
            }
        });

        LinearLayoutManager managerGenres = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mGenresAdapter = new GenresAdapter(mCurrentGenres, this, R.layout.item_genre);
        mGenresView = findViewById(R.id.edit_song_genre_list);
        mGenresView.setLayoutManager(managerGenres);
        mGenresView.setAdapter(mGenresAdapter);
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
    public void onBackPressed() { exitEditing(); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_SELECTED:
                if (resultCode == RESULT_OK) {
                    mCoverUri = data.getData();

                    if(FilenameHelper.isInvalidFile(mCoverUri,this)){
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
                    .getAllGenres(EditSongActivity.this);
        }else{
            mGenresMenu.show();
        }
    }


    private void updateTrack() {

        String newName = FilenameHelper.removeSpecialCharsAndTail(mName.getText().toString());

        mTrack.setName(newName);

        mTrack.setGenres(mCurrentGenres);
        stateDialog = StateDialog.getInstance(this);
        stateDialog.showStateDialog(false);

        if(trackChosen){
            CloudinaryManager.getInstance(this)
                    .uploadAudioFile(Constants.STORAGE.TRACK_AUDIO_FOLDER, mAudioUri, mAudioFilename+ System.currentTimeMillis(), EditSongActivity.this);
        }else{
            if(coverChosen){
                CloudinaryManager.getInstance(this)
                        .uploadCoverImage(Constants.STORAGE.TRACK_COVER_FOLDER, mCoverUri, mCoverFilename+ System.currentTimeMillis(), EditSongActivity.this);
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

    }

    @Override
    public void onUpdatedTrack() {
        if(stateDialog != null)
            stateDialog.close();

        Toast.makeText(getApplicationContext(), R.string.edit_song_update_success, Toast.LENGTH_SHORT).show();
        exitEditing();
    }

    @Override
    public void onTrackDeleted(Track track) {
        mTrack.setDeleted(true);
        exitEditing();
    }

    @Override
    public void onPopularTracksReceived(List<Track> tracks) {

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
            Boolean found = false;

            for (int j = 0; j < mCurrentGenres.size(); j++)
                if(mCurrentGenres.get(j).getName().equals(mAPIGenres.get(i).getName())) found = true;

            if (!found) mGenresMenu.getMenu().add(0, i, i, mAPIGenres.get(i).getName());
        }

        genresFetched = true;
        //    mGenresMenu.show();
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
                        .uploadCoverImage(Constants.STORAGE.TRACK_COVER_FOLDER, mCoverUri, mCoverFilename+ System.currentTimeMillis(), EditSongActivity.this);
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
        int index = -1;
        for (int i = 0; i < mAPIGenres.size(); i++) if (genre.getName().equals(mAPIGenres.get(i).getName())) index = i;
        mGenresMenu.getMenu().add(Menu.NONE, index, Menu.NONE, genre.getName());

        mGenresAdapter = new GenresAdapter(mCurrentGenres, this, R.layout.item_genre);
        mGenresView.setAdapter(mGenresAdapter);
    }
}