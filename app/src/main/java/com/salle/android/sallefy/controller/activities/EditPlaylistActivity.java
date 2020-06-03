package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.FilenameHelper;

import java.util.ArrayList;
import java.util.Map;

public class EditPlaylistActivity extends AppCompatActivity implements PlaylistCallback, UploadCallback {

    public static final String TAG = EditPlaylistActivity.class.getName();

    //Layout
    private ImageButton mNav;
    private ImageView mImg;
    private EditText mDescription;
    private EditText mTitle;
    private ImageButton mImgBtn;

    //Cover file
    private Uri mUri;
    private String mFilename;

    //Logic
    private Playlist mPlaylist;
    private boolean coverChosen;
    private boolean saved;
    private boolean completed;
    private String previousThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        coverChosen = false;
        completed = true;
        saved = true;
        initViews();
    }

    private void initViews() {
        mImg = (ImageView) findViewById(R.id.edit_playlist_img);
        mDescription = (EditText) findViewById(R.id.edit_playlist_description);
        mTitle = (EditText) findViewById(R.id.edit_playlist_title);
        mImgBtn = (ImageButton) findViewById(R.id.edit_playlist_change_photo);

        onPlaylistById((Playlist) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST));

        mNav = (ImageButton) findViewById(R.id.edit_playlist_nav);
        mNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { exitEditing(); }
        });

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseCoverImage(); }
        });

        mImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseCoverImage(); }
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
                intent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, mPlaylist);
                startActivityForResult(intent, Constants.EDIT_CONTENT.PLAYLIST_EDIT);
            }
        });

        Button mDeletePlaylist = (Button) findViewById(R.id.edit_playlist_delete);
        mDeletePlaylist.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PlaylistManager.getInstance(EditPlaylistActivity.this).deletePlaylist(mPlaylist.getId(),EditPlaylistActivity.this);
            }
        });
    }

    private void updatePlaylist() {
        this.mPlaylist.setName(mTitle.getText().toString());
        this.mPlaylist.setDescription(mDescription.getText().toString());
        this.saved = true;
        if(coverChosen){
            CloudinaryManager.getInstance(this).uploadCoverImage(Constants.STORAGE.PLAYLIST_COVER_FOLDER, mUri, mFilename, EditPlaylistActivity.this);
            completed = false;
        }else{
            PlaylistManager.getInstance(getApplicationContext())
                    .updatePlaylist(this.mPlaylist, EditPlaylistActivity.this);
        }
    }

    private void chooseCoverImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a cover image"), Constants.STORAGE.IMAGE_SELECTED);
    }

    private void exitEditing(){
        //We return the edited playlist to PlaylistActivity
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, mPlaylist);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public void onBackPressed() { exitEditing(); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STORAGE.IMAGE_SELECTED && resultCode == RESULT_OK) {
            mUri = data.getData();
            mFilename = FilenameHelper.extractFromUri(mUri,this);
            System.out.println("this is the uri " + mUri.toString());
            saved = false;
            Glide.with(getApplicationContext())
                .load(mUri.toString())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .placeholder(R.drawable.ic_playlist_cover)
                .override(400,400)
                .into(mImg);
            coverChosen = true;
        }else {
            if (requestCode == Constants.EDIT_CONTENT.PLAYLIST_EDIT && resultCode == RESULT_OK) {
                //Update playlist information
                onPlaylistById((Playlist) data.getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST));
                updatePlaylist();
            }
        }
    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   PlaylistCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onPlaylistById(Playlist playlist) {
        this.mPlaylist = playlist;
        String pImg = playlist.getThumbnail();
        if(pImg != null){
            Glide.with(getApplicationContext())
                .load(pImg)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .placeholder(R.drawable.ic_playlist_cover)
                .override(400,400)
                .into(mImg);
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
        //Toast.makeText(getApplicationContext(), R.string.edit_playlist_creation_success, Toast.LENGTH_SHORT).show();
        exitEditing();
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
        //We return with a null playlist
        CloudinaryManager.getInstance(this).deleteCoverImage(mPlaylist.getThumbnail(),false);
        mPlaylist.setDeleted(true);
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_EXTRAS.PLAYLIST, mPlaylist);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.edit_playlist_creation_failure, Toast.LENGTH_LONG).show();
        Log.e(TAG, "onFailure: "+throwable.getMessage());
        CloudinaryManager.getInstance(this).deleteCoverImage(mPlaylist.getThumbnail(),false);
    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   UploadCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onStart(String requestId) { }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {
        Double progress = (double) bytes/totalBytes;
    }

    @Override
    public void onSuccess(String requestId, Map resultData) {
        mPlaylist.setThumbnail((String)resultData.get("url"));
        mPlaylist.setCover((String)resultData.get("url"));
        coverChosen = false;
        completed = true;
        PlaylistManager.getInstance(getApplicationContext())
                .updatePlaylist(this.mPlaylist, EditPlaylistActivity.this);
    }

    @Override
    public void onError(String requestId, ErrorInfo error) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure, Toast.LENGTH_LONG).show();
        CloudinaryManager.getInstance(this).deleteCoverImage(mPlaylist.getThumbnail(),false);
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) { }

}
