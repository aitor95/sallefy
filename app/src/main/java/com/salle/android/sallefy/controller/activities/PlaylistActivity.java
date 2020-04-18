package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.fragments.PlaylistSongFragment;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.PreferenceUtils;

import java.io.Serializable;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class PlaylistActivity extends AppCompatActivity implements PlaylistCallback {

    public static final String TAG = PlaylistActivity.class.getName();

    //Layout
    private ImageButton mNav;
    private ImageView mImg;
    private ImageView mCoverImg;
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mDescription;
    private TextView mNoTracks;
    private Button mFollowBtn;
    private ImageButton mShuffleBtn;
    private View mBottomSheet;
    private CardView mImgCard;

    //Logic
    private boolean followed;
    private boolean shuffle;
    private boolean owner;
    private boolean fragmentCreated;
    private int pId;
    private String pImg;
    private Playlist mPlaylist;

    //Bottom Sheet Control
    private BottomSheetBehavior mBSBehavior;

    //Fragments
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private Fragment playlistSongFragment;

    private static AdapterClickCallback adapterClickCallback;
    public static void setAdapterClickCallback(AdapterClickCallback callback){
        adapterClickCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        this.fragmentCreated = false;
        initViews();

        //Little hack to reduce code.
        onPlaylistById((Playlist) getIntent().getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getIntent().removeExtra(Constants.INTENT_EXTRAS.PLAYLIST_DATA);
        PlaylistManager.getInstance(getApplicationContext())
                .getPlaylistById(this.pId, PlaylistActivity.this);
    }

    private void initViews() {

        initElements();

        //Bottom sheet behavior
        mBSBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBSBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                switch (state) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showSongInformation(true);
                        blurTransformation(1);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        showSongInformation(false);
                        blurTransformation(25);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        showSongInformation(false);
                        blurTransformation(25);
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        blurTransformation(25);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                mCoverImg.setAlpha(v);
                blurTransformation(25);
            }
        });

        //Follow playlist button
        mFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!owner){
                    //Call API to update current user following/unfollowing playlist with id
                    PlaylistManager.getInstance(getApplicationContext()).setUserFollows(pId, followed, PlaylistActivity.this);
                }else{
                    Intent intent = new Intent(getApplicationContext(), EditPlaylistActivity.class);
                    intent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST_ID , pId);
                    startActivity(intent);
                }

            }
        });



        //Shuffle playlist button
        mShuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource((shuffle) ? R.drawable.login_btn : R.drawable.following_btn);
                shuffle = !shuffle;

            }
        });


        //Back navigation button
        mNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initElements() {
        //Get elements from layout
        mImg = findViewById(R.id.playlist_img);
        mCoverImg = findViewById(R.id.playlist_img_small);
        mImgCard = findViewById(R.id.playlist_card);
        mAuthor = findViewById(R.id.playlist_author);
        mTitle = findViewById(R.id.playlist_title);
        mDescription = findViewById(R.id.playlist_description);
        mFollowBtn = findViewById(R.id.playlist_view_follow);
        mShuffleBtn = findViewById(R.id.playlist_view_shuffle);
        mBottomSheet = findViewById(R.id.fragment_container_playlist);
        mNoTracks = findViewById(R.id.playlist_no_tracks);
        mNav = findViewById(R.id.playlist_back);
    }


    private void setInitialFragment() {
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
        PlaylistSongFragment.setAdapterClickCallback(adapterClickCallback);
        playlistSongFragment = new PlaylistSongFragment();

        mTransaction.add(R.id.fragment_container_playlist, playlistSongFragment);
        mTransaction.commit();
    }

    private void blurTransformation(int intensity) {
        if(mBSBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            if(pImg != null){
                Glide
                        .with(getApplicationContext())
                        .load(pImg)
                        .centerCrop()
                        .override(400,400)
                        .transform(new BlurTransformation(intensity))
                        .into(mImg);
            }else{
                Glide
                        .with(getApplicationContext())
                        .load(R.drawable.ic_playlist_cover)
                        .centerCrop()
                        .override(400,400)
                        .transform(new BlurTransformation(intensity))
                        .into(mImg);
            }

        }else{
            if(pImg != null){
                Glide
                        .with(getApplicationContext())
                        .load(pImg)
                        .centerCrop()
                        .override(400,400)
                        .transform(new BlurTransformation(intensity))
                        .transition(DrawableTransitionOptions.withCrossFade(2000))
                        .into(mImg);
            }else {
                Glide
                        .with(getApplicationContext())
                        .load(R.drawable.ic_playlist_cover)
                        .centerCrop()
                        .override(400,400)
                        .transform(new BlurTransformation(intensity))
                        .transition(DrawableTransitionOptions.withCrossFade(2000))
                        .into(mImg);
            }
        }
    }

    private void showSongInformation(boolean show) {
        if(show){
            mFollowBtn.setVisibility(View.VISIBLE);
            mShuffleBtn.setVisibility(View.VISIBLE);
            mDescription.setVisibility(View.VISIBLE);
            mCoverImg.setVisibility(View.GONE);
            mImgCard.setVisibility(View.GONE);
            mTitle.setTextSize(50);
        }else{
            mFollowBtn.setVisibility(View.GONE);
            mShuffleBtn.setVisibility(View.GONE);
            mDescription.setVisibility(View.GONE);
            mCoverImg.setVisibility(View.VISIBLE);
            mImgCard.setVisibility(View.VISIBLE);
            mTitle.setTextSize(30);
        }
    }



        @Override
    public void onPlaylistById(Playlist playlist) {
        this.mPlaylist = playlist;

        this.pId = mPlaylist.getId();

        if (PreferenceUtils.getUser(this).equals(mPlaylist.getUserLogin())) {
            this.owner = true;
        }


        if (!this.owner) {
            //Call API to get if current user follows playlist with id
            PlaylistManager.getInstance(getApplicationContext()).getUserFollows(this.pId, PlaylistActivity.this);
        } else {
            //Enable Edit button instead of Follow
            this.mFollowBtn.setBackgroundResource(R.drawable.login_btn);
            mFollowBtn.setText(R.string.playlist_edit);
        }
        //Load cover image from URL
        this.pImg = playlist.getThumbnail();
        if (pImg != null) {
            Glide.with(this).load(pImg).into(mImg);
            Glide.with(this).load(pImg).into(mCoverImg);
        }

        //Set playlist information
        String pTitle = playlist.getName();
        String pAuthor = playlist.getUserLogin();
        String pDescription = playlist.getDescription();

        this.followed = playlist.isFollowed();

        if (pAuthor != null) {
            mAuthor.setText(pAuthor);
        }
        if (pTitle != null) {
            mTitle.setText(pTitle);
        }
        if (pDescription != null) {
            mDescription.setText(pDescription);
        }

        if(this.mPlaylist.getTracks().size() != 0){
            this.mNoTracks.setVisibility(View.GONE);
        }
        //Send playlist tracks to PlaylistSongFragment
        getIntent().putExtra(Constants.INTENT_EXTRAS.PLAYLIST_DATA, (Serializable) this.mPlaylist);
        if (!this.fragmentCreated) {
            setInitialFragment();
            this.fragmentCreated = true;
        } else {
            updateFragment();
        }
    }

    private void updateFragment() {
        //Update playlist tracks onRestart
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.detach(playlistSongFragment);
        mTransaction.attach(playlistSongFragment);
        mTransaction.commit();
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

    }

    @Override
    public void onUserFollows(Follow response) {

        this.followed = response.getFollow();
        this.mFollowBtn.setBackgroundResource((this.followed) ? R.drawable.following_btn : R.drawable.login_btn);
        mFollowBtn.setText((followed) ? R.string.playlist_following : R.string.playlist_follow );

    }

    @Override
    public void onUpdateFollow(Follow response) {

        this.followed = response.getFollow();
        this.mFollowBtn.setBackgroundResource((this.followed) ? R.drawable.following_btn : R.drawable.login_btn);
        mFollowBtn.setText((followed) ? R.string.playlist_following : R.string.playlist_follow );

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), R.string.playlist_creation_failure, Toast.LENGTH_LONG).show();
    }


}
