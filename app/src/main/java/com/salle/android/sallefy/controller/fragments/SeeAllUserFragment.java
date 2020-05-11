package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.os.Parcelable;
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
import com.salle.android.sallefy.utils.PaginatedRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

public class SeeAllUserFragment extends Fragment implements UserCallback {

	public static final String TAG = SeeAllUserFragment.class.getName();
	public static final String TAG_CONTENT = SeeAllUserFragment.class.getName();

	private PaginatedRecyclerView mRecyclerView;
	private ArrayList<User> mUsers;

	private UserVerticalAdapter mAdapter;

	private int currentPage = 0;

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
		mRecyclerView = (PaginatedRecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		mAdapter = new UserVerticalAdapter(null,adapterClickCallback, getContext(), R.layout.item_user_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setListener(new PaginatedRecyclerView.PaginatedRecyclerViewListener() {
			@Override
			public void onPageLoaded() { loadMoreItems(); }
		});

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

	private void loadMoreItems() {
		mRecyclerView.setLoading(true);

		currentPage += 1;

		UserManager.getInstance(getActivity()).getUsersPagination(this, currentPage, 10);

	}

	private void getData() {
		UserManager.getInstance(getActivity()).getUsersPagination(this, currentPage, 10);
		mUsers = new ArrayList<>();
	}

	@Override
	public void onUsersReceived(List<User> users) {
		if(users.size() < PAGE_SIZE){
			mRecyclerView.setLast(true);
		}
		if(mRecyclerView.isLoading()) mRecyclerView.setLoading(false);
		mUsers.addAll(users);
		Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
		this.mAdapter.notifyDataSetChanged();
		mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);

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

		mAdapter = new UserVerticalAdapter(mUsers,adapterClickCallback, getContext(), R.layout.item_user_vertical);
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onUpdateUser() {

	}

	@Override
	public void onMeFollowersReceived(List<UserPublicInfo> body) {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
}
