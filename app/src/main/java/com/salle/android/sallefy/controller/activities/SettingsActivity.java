package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.salle.android.sallefy.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        RelativeLayout optionStats = findViewById(R.id.settings_option_stats);
        optionStats.setOnClickListener(view -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });

        RelativeLayout optionDelete = findViewById(R.id.settings_option_deleteAccount);
        optionDelete.setOnClickListener(view -> {
            //TODO: Delete user
        });

        RelativeLayout optionLogOut = findViewById(R.id.settings_option_logOut);
        optionLogOut.setOnClickListener(view -> {
            //TODO: LogOut from the account
        });

        RelativeLayout backoption = findViewById(R.id.relativeLayoutSettingsTitle);
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