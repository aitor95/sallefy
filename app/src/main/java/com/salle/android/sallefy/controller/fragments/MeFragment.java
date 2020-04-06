package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.salle.android.sallefy.R;

public class MeFragment extends Fragment {

	public static final String TAG = MeFragment.class.getName();

    public static MeFragment getInstance() {
        return new MeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, container, false);
		initViews(v);
        return v;
    }

	private void initViews(View v) {

    	ImageButton changePhoto = (ImageButton) v.findViewById(R.id.action_change_photo);
		Button users = (Button) v.findViewById(R.id.action_me_users);
		Button songs = (Button) v.findViewById(R.id.action_me_songs);
		Button playlists = (Button) v.findViewById(R.id.action_me_playlists);

		Fragment fragmentMeUsers = new MeUserFragment();
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.me_fragment_container, fragmentMeUsers).commit();

		changePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//TODO Hacer el cambio de foto de usuario
			}
		});

		users.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				users.setTextAppearance(R.style.MeNavigationView_Active);
				users.setBackgroundResource(R.drawable.round_corner_light);
				songs.setTextAppearance(R.style.MeNavigationView);
				songs.setBackgroundResource(0);
				playlists.setTextAppearance(R.style.MeNavigationView);
				playlists.setBackgroundResource(0);

				// FRAGMENT INTO FRAGMENT
				Fragment fragmentMeUsers = new MeUserFragment();
				FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				transaction.add(R.id.me_fragment_container, fragmentMeUsers).commit();
			}
		});

		songs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				users.setTextAppearance(R.style.MeNavigationView);
				users.setBackgroundResource(0);
				songs.setTextAppearance(R.style.MeNavigationView_Active);
				songs.setBackgroundResource(R.drawable.round_corner_light);
				playlists.setTextAppearance(R.style.MeNavigationView);
				playlists.setBackgroundResource(0);

				// FRAGMENT INTO FRAGMENT
				Fragment fragmentMeSongs = new MeSongFragment();
				FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				transaction.add(R.id.me_fragment_container, fragmentMeSongs).commit();
			}
		});

		playlists.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				users.setTextAppearance(R.style.MeNavigationView);
				users.setBackgroundResource(0);
				songs.setTextAppearance(R.style.MeNavigationView);
				songs.setBackgroundResource(0);
				playlists.setTextAppearance(R.style.MeNavigationView_Active);
				playlists.setBackgroundResource(R.drawable.round_corner_light);

				// FRAGMENT INTO FRAGMENT
				Fragment fragmentMePlaylists = new MePlaylistFragment();
				FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				transaction.add(R.id.me_fragment_container, fragmentMePlaylists).commit();

			}
		});

	}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
