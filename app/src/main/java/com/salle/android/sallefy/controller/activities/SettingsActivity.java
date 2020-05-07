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

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.music.MusicService;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = SettingsActivity.class.getName();

    private MusicService mBoundService;
    private Intent intent;

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
    private void startStreamingService () {
        intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startStreamingService();
        setContentView(R.layout.activity_settings);

        RelativeLayout optionStats = findViewById(R.id.settings_option_stats);
        optionStats.setOnClickListener(view -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });

        RelativeLayout optionModify = findViewById(R.id.settings_modify_user);
        optionModify.setOnClickListener(view -> {
            //TODO: Modify user
        });

        RelativeLayout optionDelete = findViewById(R.id.settings_option_deleteAccount);
        optionDelete.setOnClickListener(view -> {
            //TODO: Delete user
        });

        Button optionLogOut = findViewById(R.id.btn_settings_logout);
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

        ImageButton backoption = findViewById(R.id.back_btn_settings);
        backoption.setOnClickListener(view -> {
            finish();
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }


}