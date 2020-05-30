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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {

    public static final String TAG = SettingsActivity.class.getName();

    private MusicService mBoundService;
    private Intent intent;

    private RelativeLayout optionStats;
    private ImageButton backoption;
    private CircleImageView user_img;
    private RelativeLayout optionModify;
    private RelativeLayout optionDelete;
    private Button optionLogOut;

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
            startActivity(intent);
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
            finish();
        });

        Toast.makeText(getApplicationContext(), PreferenceUtils.getUser(this), Toast.LENGTH_LONG).show();
    }

    private void openDialog() {
        DeleteDialog dialog = new DeleteDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    private void initElements(){
        backoption = findViewById(R.id.back_btn_settings);
        optionStats = findViewById(R.id.settings_option_stats);
        user_img = findViewById(R.id.user_img);
        optionModify = findViewById(R.id.settings_modify_user);
        optionDelete = findViewById(R.id.settings_option_deleteAccount);
        optionLogOut = findViewById(R.id.btn_settings_logout);
    }

    @Override
    public void onYesClicked() {
        //TODO: Delete User

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}