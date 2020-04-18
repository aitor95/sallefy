package com.salle.android.sallefy.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.activities.SettingsActivity;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.utils.PreferenceUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends Fragment {

	public static final String TAG = MeFragment.class.getName();

    public static MeFragment getInstance() {
        return new MeFragment();
    }

	private static AdapterClickCallback adapterClickCallback;
	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
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
		MeUserFragment.setAdapterClickCallback(adapterClickCallback);
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
				MeUserFragment.setAdapterClickCallback(adapterClickCallback);
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
				MeSongFragment.setAdapterClickCallback(adapterClickCallback);
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
				MePlaylistFragment.setAdapterClickCallback(adapterClickCallback);
				FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				transaction.add(R.id.me_fragment_container, fragmentMePlaylists).commit();

			}
		});

		ImageView configButton = v.findViewById(R.id.config_button);
		configButton.setOnClickListener(view -> {
			Intent intent = new Intent(getContext(), SettingsActivity.class);
			startActivity(intent);
		});

		CircleImageView user_img = v.findViewById(R.id.user_img);


		//Sustituir por informacion real en la UI
		TextView user_name = v.findViewById(R.id.user_name);
		user_name.setText(PreferenceUtils.getUser(v.getContext()));
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
