package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BottomSongMenuFragment extends Fragment {

    public static final String TAG = BottomSongMenuFragment.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static BottomSongMenuFragment getInstance() {
        return new BottomSongMenuFragment();
    }

}
