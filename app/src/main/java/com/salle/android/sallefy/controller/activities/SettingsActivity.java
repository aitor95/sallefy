package com.salle.android.sallefy.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.dialogs.DeleteDialogFragment;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener, UserCallback, AdapterClickCallback {

    public static final String TAG = SettingsActivity.class.getName();

    private MusicService mBoundService;
    private Intent intent;

    private RelativeLayout optionStats;
    private ImageButton backoption;
    private CircleImageView user_img;
    private RelativeLayout optionModify;
    private RelativeLayout optionDownloaded;
    private RelativeLayout optionDelete;
    private Button optionLogOut;
    private String updated_user;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mBoundService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: Service desconnected.");
        }
    };
    private User mUser;

    private void startStreamingService () {
        intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startStreamingService();
        setContentView(R.layout.activity_settings);
        mUser = Session.getInstance(getApplicationContext()).getUser();
        initViews();

    }

    private void initViews(){
        initElements();

        optionStats.setOnClickListener(view -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });

        Glide.with(
                getApplicationContext()
                        .getApplicationContext())
                .load(mUser.getImageUrl())
                .centerCrop()
                .override(400,400)
                .placeholder(R.drawable.user_default_image)
                .into(user_img);

        optionModify.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditAccountActivity.class);
            startActivityForResult(intent, Constants.EDIT_CONTENT.USER_EDIT_FINISHED);
        });

        optionDelete.setOnClickListener(view -> {
            openDialog();
        });

        optionLogOut.setOnClickListener(view -> {
            PreferenceUtils.resetValues(this);
            Session.getInstance(this).resetValues();
            mBoundService.stopMusic();
            unbindService(mServiceConnection);

            stopService(intent);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        backoption.setOnClickListener(view -> {
            goBack();
        });

        optionDownloaded.setOnClickListener(view -> {
            Intent intent = new Intent(this, DownloadSongsActivity.class);
            intent.putExtra("FULL_OFFLINE", false);
            startActivity(intent);
        });
    }

    private void openDialog() {
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "Delete Account Dialog");
    }

    private void initElements(){
        backoption = findViewById(R.id.back_btn_settings);
        optionStats = findViewById(R.id.settings_option_stats);
        user_img = findViewById(R.id.user_img);
        optionModify = findViewById(R.id.settings_modify_user);
        optionDelete = findViewById(R.id.settings_option_deleteAccount);
        optionLogOut = findViewById(R.id.btn_settings_logout);
        optionDownloaded = findViewById(R.id.settings_option_downloaded_songs);
    }

    @Override
    public void onYesClicked() {
        //TODO: Delete User
        UserManager.getInstance(getApplicationContext()).deleteAccount(SettingsActivity.this);
    }

    @Override
    public void onUsersReceived(List<User> users) {

    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onMeFollowingsReceived(List<User> users) {

    }

    @Override
    public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

    }

    @Override
    public void onUpdateUser() {

    }


    @Override
    public void onUpdatePassword() {

    }

    private void goBack(){
        // We return the edited User to the MainActivity
        Intent intent = getIntent();
        intent.putExtra(Constants.INTENT_EXTRAS.USER_LOGIN, updated_user);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_CONTENT.USER_EDIT_FINISHED && resultCode == RESULT_OK){

            String user = (String)data.getSerializableExtra(Constants.INTENT_EXTRAS.USER_LOGIN);
            updated_user = user;

        }
    }

    private void updateUIDataBefore(String user) {

    }


    @Override
    public void onMeFollowersReceived(List<UserPublicInfo> body) {

    }

    @Override
    public void onDeleteAccount() {
        PreferenceUtils.resetValues(this);
        Session.getInstance(this).resetValues();
        mBoundService.stopMusic();
        unbindService(mServiceConnection);

        stopService(intent);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onAllFollowingsFromUserReceived(List<User> users) {

    }

    @Override
    public void onAllFollowersFromUserReceived(List<User> users) {

    }


    @Override
    public void onFailure(Throwable throwable) {

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

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}