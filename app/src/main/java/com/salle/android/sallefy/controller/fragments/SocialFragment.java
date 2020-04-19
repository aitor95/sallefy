package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.SocialActivityAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SocialFragment extends Fragment implements TrackCallback, UserCallback{

	public static final String TAG = SocialFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<Track> mTracks;
	private ArrayList<UserPublicInfo> mFollowing;

	private static AdapterClickCallback adapterClickCallback;
	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}

	public static Fragment getInstance() {
		return new SocialFragment();
	}


	private TextView nitv;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social, container, false);
		initViews(v);
		getData();
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		SocialActivityAdapter adapter = new SocialActivityAdapter(adapterClickCallback, getContext(), null);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);

		nitv = v.findViewById(R.id.no_info_aviable_on_social);
	}

	private void getData() {
		UserManager.getInstance(this.getContext()).getMeFollowing(this);
	}

	@Override
	public void onMeFollowingsReceived(List<UserPublicInfo> users) {
		mFollowing = new ArrayList<UserPublicInfo>();
		mFollowing.addAll(users);
		TrackManager.getInstance(getActivity()).getAllTracksSocial(this);
	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onTracksReceived(List<Track> tracks) {

		mTracks = new ArrayList<Track>();
		for (int i = 0; i < tracks.size(); i++){
			if (!("" + tracks.get(i).getReleased()).equals("null")) {
				for (int j = 0; j < this.mFollowing.size(); j++) {
					if (tracks.get(i).getUser().getLogin().equals(this.mFollowing.get(j).getLogin())){
						mTracks.add(tracks.get(i));
					}
				}
			}
		}
		mTracks.sort(Comparator.comparing(Track::getReleased).reversed());

		if (mTracks.size() != 0) {
			SocialActivityAdapter adapter = new SocialActivityAdapter(adapterClickCallback, getContext(), mTracks);
			mRecyclerView.setAdapter(adapter);
		} else {
			if (mFollowing.size() != 0) nitv.setText(R.string.no_tracks_on_social);
			else nitv.setText(R.string.no_users_on_social);
		}
	}

	@Override
	public void onNoTracks(Throwable throwable) {

	}

	@Override
	public void onPersonalTracksReceived(List<Track> tracks) {

	}

	@Override
	public void onUserTracksReceived(List<Track> tracks) {

	}

	@Override
	public void onCreateTrack() {

	}

	@Override
	public void onUpdatedTrack() {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}


	@Override
	public void onLoginSuccess(UserToken userToken) {

	}

	@Override
	public void onLoginFailure(Throwable throwable) {

	}

	@Override
	public void onRegisterSuccess() {

	}

	@Override
	public void onRegisterFailure(Throwable throwable) {

	}

	@Override
	public void onUserInfoReceived(User userData) {
	}

	@Override
	public void onUsersReceived(List<User> users) {

	}

	@Override
	public void onUsersFailure(Throwable throwable) {

	}

}
