package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.utils.PreferenceUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout optionStats = findViewById(R.id.settings_option_stats);
        optionStats.setOnClickListener(view -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });

        RelativeLayout optionDelete = findViewById(R.id.settings_option_deleteAccount);
        optionDelete.setOnClickListener(view -> {
            //TODO: Delete user
        });

        Button optionLogOut = findViewById(R.id.btn_settings_logout);
        optionLogOut.setOnClickListener(view -> {
            PreferenceUtils.resetValues(this);

            Intent intent = new Intent(this, LoginActivity.class);
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