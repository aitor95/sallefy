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
import com.salle.android.sallefy.controller.adapters.UserVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.List;

public class MeUserFragment extends Fragment implements UserCallback {

	public static final String TAG = MeUserFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<User> mUsers;
	private ArrayList<User> mUsersFollowed;
	private ArrayList<UserPublicInfo> mUsersPublic;

	private View v;

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
		v = inflater.inflate(R.layout.fragment_me_lists_users, container, false);
		TextView text = v.findViewById(R.id.me_text_error);
		text.setText(R.string.LoadingMe);
		initViews(v);
		getData();
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		UserVerticalAdapter adapter = new UserVerticalAdapter(null, adapterClickCallback, getContext(),R.layout.item_user_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);
	}

	private void getData() {
		mUsers = new ArrayList<>();
		mUsersPublic = new ArrayList<>();
		mUsersFollowed = new ArrayList<>();
		UserManager.getInstance(getContext()).getMeFollowing(this);
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

		for(User u: mUsers){
			for(UserPublicInfo f: mUsersPublic){
				if(u.getLogin().equals(f.getLogin())){
					mUsersFollowed.add(u);
					break;
				}
			}
		}

		for (User u : mUsersFollowed) {
			u.setFollowedByUser(false);
			for (UserPublicInfo upi : mUsersPublic)
				if (u.getLogin().equals(upi.getLogin())) u.setFollowedByUser(true);
		}

		TextView text = v.findViewById(R.id.me_text_error);
		text.setText(null);

		UserVerticalAdapter adapter = new UserVerticalAdapter(mUsersFollowed,adapterClickCallback, getContext(),  R.layout.item_user_vertical);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onUsersFailure(Throwable throwable) {

	}


	@Override
	public void onMeFollowingsReceived(List<UserPublicInfo> users) {
		mUsersPublic = (ArrayList<UserPublicInfo>) users;
		if(mUsersPublic.isEmpty()){
			TextView text = v.findViewById(R.id.me_text_error);
			text.setText(R.string.NoContentAvailableMeUsers);
			UserVerticalAdapter adapter = new UserVerticalAdapter(mUsersFollowed,adapterClickCallback, getContext(),  R.layout.item_user_vertical);
			mRecyclerView.setAdapter(adapter);
		}else{
			UserManager.getInstance(getActivity()).getUsersFragmentMe(this);
		}
	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
}
