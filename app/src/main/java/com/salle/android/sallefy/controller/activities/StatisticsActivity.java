package com.salle.android.sallefy.controller.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.salle.android.sallefy.R;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        ImageButton backoption = findViewById(R.id.stats_back_btn);
        backoption.setOnClickListener(view -> {
            finish();
        });
    }
}
