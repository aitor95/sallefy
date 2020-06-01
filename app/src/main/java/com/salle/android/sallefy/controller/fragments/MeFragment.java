package com.salle.android.sallefy.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.activities.SettingsActivity;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.CloudinaryManager;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends Fragment implements UserCallback, UploadCallback {

	public static final String TAG = MeFragment.class.getName();
	public static final String TAG_CONTENT = MeFragment.class.getName();

	private MePlaylistFragment fragmentMePlaylists;
	private MeSongFragment fragmentMeSongs;

	public static MeFragment getInstance() {
        return new MeFragment();
    }

	private static AdapterClickCallback adapterClickCallback;
    private CircleImageView user_img;
	private TextView followers;
	private TextView following;

	private boolean profileImageChoosen;

	private User mUser;
	private boolean isOwner;

    // Url file
    private Uri mUri;

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

		mUser = (User) getArguments().getSerializable(TAG_CONTENT);

		User localUser = Session.getInstance(getContext()).getUser();

		isOwner = (mUser == localUser);

		profileImageChoosen = false;
        initViews(v);

        return v;
    }

	private void initViews(View v) {

		Button users = (Button) v.findViewById(R.id.action_me_users);
		Button songs = (Button) v.findViewById(R.id.action_me_songs);
		Button playlists = (Button) v.findViewById(R.id.action_me_playlists);

		following = v.findViewById(R.id.me_number_following);
		followers = v.findViewById(R.id.me_number_followers);

		followers.setText(String.valueOf( mUser.getFollowers()));
		followers.setText(String.valueOf( mUser.getFollowing()));


		user_img = v.findViewById(R.id.user_img);

		Fragment fragmentMeUsers = new MeUserFragment(mUser,isOwner);
		MeUserFragment.setAdapterClickCallback(adapterClickCallback);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.me_fragment_container, fragmentMeUsers).commit();

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
				Fragment fragmentMeUsers = new MeUserFragment(mUser, isOwner);
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
				fragmentMeSongs = new MeSongFragment(mUser,isOwner);
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
				fragmentMePlaylists = new MePlaylistFragment(mUser,isOwner);
				MePlaylistFragment.setAdapterClickCallback(adapterClickCallback);
				FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				transaction.add(R.id.me_fragment_container, fragmentMePlaylists).commit();

			}
		});

		CircleImageView user_img = v.findViewById(R.id.user_img);


		Glide.with(
				getActivity()
				.getApplicationContext())
				.load(mUser.getImageUrl())
				.centerCrop()
				.override(400,400)
				.placeholder(R.drawable.user_default_image)
				.into(user_img);

		TextView user_name = v.findViewById(R.id.user_name);
		user_name.setText(mUser.getLogin());

		ImageView configButton = v.findViewById(R.id.config_button);
		ImageButton changePhoto = (ImageButton) v.findViewById(R.id.action_change_photo);

		if(isOwner) {

			configButton.setOnClickListener(view -> {
				Intent intent = new Intent(getContext(), SettingsActivity.class);
				startActivity(intent);
			});

			changePhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					chooseProfileImage();
				}
			});
		}else{
			configButton.setVisibility(View.GONE);
			changePhoto.setVisibility(View.GONE);
		}
	}


	public static MeFragment newInstance(User user) {
		MeFragment fragment = new MeFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TAG_CONTENT, user);
		fragment.setArguments(bundle);

		return fragment;
	}


	private void chooseProfileImage(){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Choose a cover image"), Constants.STORAGE.IMAGE_SELECTED);
	}

	public void updateSongInfo(Track track){
		fragmentMeSongs.updateSongInfo(track);
	}

	public void updatePlaylistInfo(ArrayList<Playlist> playlists){
		fragmentMePlaylists.updateInfo(playlists);
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
	public void onMeFollowersReceived(List<UserPublicInfo> users) {
	}

	@Override
	public void onDeleteAccount() {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.STORAGE.IMAGE_SELECTED && resultCode == Activity.RESULT_OK){
			mUri = data.getData();
			Glide.with(getActivity().getApplicationContext())
					.load(mUri.toString())
					.centerCrop()
					.override(100,100)
					.placeholder(R.drawable.user_default_image)
					.into(user_img);

            profileImageChoosen = true;
            uploadProfileImage();
		}
	}

	private void uploadProfileImage() {
		CloudinaryManager.getInstance(this.getContext()).uploadCoverImage(Constants.STORAGE.USER_PICTURE_FOLDER, mUri, mUser.getLogin(), MeFragment.this);
	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onUpdateUser(UserToken userToken) {
		//Update photo with the new login name
		CloudinaryManager.getInstance(this.getContext()).uploadCoverImage(Constants.STORAGE.USER_PICTURE_FOLDER, mUri, mUser.getLogin(), MeFragment.this);
	}

	@Override
	public void onUpdatePassword(ChangePassword changePassword, UserToken userToken) {

	}


	@Override
	public void onFailure(Throwable throwable) {

	}


	@Override
	public void onStart(String requestId) {

	}

	@Override
	public void onProgress(String requestId, long bytes, long totalBytes) {

	}

	@Override
	public void onSuccess(String requestId, Map resultData) {
        if (profileImageChoosen){
            mUser.setImageUrl((String)resultData.get("url"));
            UserManager.getInstance(getActivity()).updateProfile(mUser,MeFragment.this);
            profileImageChoosen = false;
        }
	}

	@Override
	public void onError(String requestId, ErrorInfo error) {

	}

	@Override
	public void onReschedule(String requestId, ErrorInfo error) {

	}
}
