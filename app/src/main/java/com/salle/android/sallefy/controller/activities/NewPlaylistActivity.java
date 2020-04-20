package com.salle.android.sallefy.controller.activities;



import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.ResponseBody;

public class NewPlaylistActivity extends AppCompatActivity implements PlaylistCallback, UploadCallback {

    public static final String TAG = NewPlaylistActivity.class.getName();
    public static final String EXTRA_NEW_PLAYLIST = "EXTRA_NEW_PLAYLIST";

    //Layout
    private ImageButton mNav;
    private ImageView mImg;
    private EditText mDescription;
    private EditText mTitle;

    //Cover file
    private Uri mUri;
    private String mFilename;

    //Logic
    private Playlist mPlaylist;
    private boolean coverChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_playlist);
        coverChosen = false;
        initViews();
    }

    private void initViews() {
        mNav = (ImageButton) findViewById(R.id.new_playlist_nav);
        mImg = (ImageView) findViewById(R.id.new_playlist_img);
        mDescription = (EditText) findViewById(R.id.new_playlist_description);
        mTitle = (EditText) findViewById(R.id.new_playlist_title);
        mNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
        Button mCreateBtn = (Button) findViewById(R.id.new_playlist_btn);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Playlist playlist = createPlaylist();
            }
        });
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCoverImage();
            }
        });
    }

    private Playlist createPlaylist() {
        mPlaylist = new Playlist();
        mPlaylist.setDescription(mDescription.getText().toString());
        mPlaylist.setPublicAccessible(new Boolean(true));
        if(mTitle.getText().toString().equals("")){

            Toast.makeText(getApplicationContext(), R.string.new_playlist_not_complete, Toast.LENGTH_SHORT).show();

        }else {

            mPlaylist.setName(mTitle.getText().toString());

            if (coverChosen) {
                CloudinaryManager.getInstance(this).uploadCoverImage(Constants.STORAGE.PLAYLIST_COVER_FOLDER, mUri, mFilename, NewPlaylistActivity.this);
            }else{

                PlaylistManager.getInstance(getApplicationContext())
                        .createPlaylist(mPlaylist, NewPlaylistActivity.this);
            }
        }
        return mPlaylist;
    }

    private void chooseCoverImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a cover image"), Constants.STORAGE.IMAGE_SELECTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STORAGE.IMAGE_SELECTED && resultCode == RESULT_OK) {
            mUri = data.getData();
            mFilename = mUri.toString();
            Glide
                    .with(getApplicationContext())
                    .load(mUri.toString())
                    .centerCrop()
                    .override(400,400)
                    .placeholder(R.drawable.ic_chooseimage)
                    .into(mImg);
            coverChosen = true;
        }
    }


    /**********************************************************************************************
     *   *   *   *   *   *   *   *   PlaylistCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/


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
    public void onPlaylistCreated(Playlist playlist) {
        coverChosen = false;

        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_PLAYLIST, playlist);
        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    public void onUserFollows(Follow follows) {

    }

    @Override
    public void onUpdateFollow(Follow result) {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure, Toast.LENGTH_LONG).show();
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
        PlaylistManager.getInstance(getApplicationContext())
                .createPlaylist(mPlaylist, NewPlaylistActivity.this);
    }

    @Override
    public void onError(String requestId, ErrorInfo error) {
        Toast.makeText(getApplicationContext(), R.string.new_playlist_creation_failure_cloudinary, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) { }

}
