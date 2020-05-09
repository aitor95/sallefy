package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.UserVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.callbacks.SeeAllCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeeAllUserFragment extends Fragment implements UserCallback {

	public static final String TAG = SeeAllUserFragment.class.getName();
	public static final String TAG_CONTENT = SeeAllUserFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<User> mUsers;

	private static AdapterClickCallback adapterClickCallback;
	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}
	private static SeeAllCallback seeAllCallback;
	public static void setSeeAllCallback(SeeAllCallback seeAllC){
		seeAllCallback = seeAllC;
	}

	public static Fragment getInstance() {
		return new SeeAllUserFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_see_all_users, container, false);
		initViews(v);

		mUsers = (ArrayList<User>) getArguments().getSerializable(TAG_CONTENT);
		UserManager.getInstance(getContext()).getMeFollowing(this);
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		UserVerticalAdapter adapter = new UserVerticalAdapter(null,adapterClickCallback, getContext(), R.layout.item_user_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);

		v.findViewById(R.id.edit_playlist_nav).setOnClickListener(view -> {
			if (seeAllCallback != null) seeAllCallback.onSeeAllClosed();
			Objects.requireNonNull(
					getActivity()).
					getSupportFragmentManager().
					beginTransaction().
					setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right).
					remove(this).
					commit();
		});
	}

	public static SeeAllUserFragment newInstance(ArrayList<User> users) {
		SeeAllUserFragment fragment = new SeeAllUserFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TAG_CONTENT, users);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void getData() {
		//TODO: cambiar por coger las listas que tocan
		UserManager.getInstance(getActivity()).getUsers(this);
		mUsers = new ArrayList<>();
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
		mUsers = (ArrayList<User>) users;

		//UserVerticalAdapter adapter = new UserVerticalAdapter(mUsers, getContext(), this, R.layout.item_user_vertical);
		//mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onUsersFailure(Throwable throwable) {

	}

	@Override
	public void onMeFollowingsReceived(List<UserPublicInfo> users) {
		for (User u: mUsers) {
			u.setFollowedByUser(false);
			for (UserPublicInfo upi: users)
				if (u.getLogin().equals(upi.getLogin())) u.setFollowedByUser(true);
		}

		UserVerticalAdapter adapter = new UserVerticalAdapter(mUsers,adapterClickCallback, getContext(), R.layout.item_user_vertical);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onUpdateUser() {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
}
