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
import com.salle.android.sallefy.controller.adapters.PlaylistListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.callbacks.SeeAllCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.utils.PaginatedRecyclerView;

import java.util.ArrayList;
import java.util.Objects;


public class SeeAllPlaylistFragment extends Fragment implements PlaylistCallback {

	public static final String TAG = SeeAllPlaylistFragment.class.getName();
	public static final String TAG_CONTENT_POPULAR = SeeAllPlaylistFragment.class.getName() + "_popular";
	public static final String TAG_CONTENT_DATA = SeeAllPlaylistFragment.class.getName() + "_data";


	private PaginatedRecyclerView mRecyclerView;
	private ArrayList<Playlist> mPlaylists;

	//Pagination
	private int currentPage = 0;

	private boolean popular;

	private PlaylistListVerticalAdapter mAdapter;

	private static SeeAllCallback seeAllCallback;
	public static void setSeeAllCallback(SeeAllCallback seeAllC){
		seeAllCallback = seeAllC;
	}

	private static AdapterClickCallback adapterClickCallback;
	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}


	public static Fragment getInstance() {
		return new SeeAllPlaylistFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_see_all_playlists, container, false);
		initViews(v);

		mPlaylists = (ArrayList<Playlist>) getArguments().getSerializable(TAG_CONTENT_DATA);
		popular = (boolean) getArguments().getSerializable(TAG_CONTENT_POPULAR);

		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(mPlaylists, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		mRecyclerView.setAdapter(adapter);
		return v;
	}

	public static SeeAllPlaylistFragment newInstance(ArrayList<Playlist> playlists, boolean popular) {
		SeeAllPlaylistFragment fragment = new SeeAllPlaylistFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TAG_CONTENT_DATA, playlists);
		bundle.putSerializable(TAG_CONTENT_POPULAR, popular);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void initViews(View v) {
		mRecyclerView = (PaginatedRecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		mAdapter = new PlaylistListVerticalAdapter(null, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setListener(new PaginatedRecyclerView.PaginatedRecyclerViewListener() {
			@Override
			public void onPageLoaded() { loadMoreItems(); }
		});

		v.findViewById(R.id.edit_playlist_nav).setOnClickListener(view -> {
			closeFragment();
		});

	}

	private void closeFragment() {

		if (seeAllCallback != null) seeAllCallback.onSeeAllClosed();

		Objects.requireNonNull(getActivity()).
				getSupportFragmentManager().
				beginTransaction().
				setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right).
				remove(this).commit();
	}

	private void loadMoreItems() {
		mRecyclerView.setLoading(true);

		currentPage += 1;

		PlaylistManager.getInstance(getActivity()).getListOfPlaylistPagination(this, currentPage, 20, popular);

	}

	@Override
	public void onPlaylistById(Playlist playlist) {

	}

	@Override
	public void onPlaylistsByUser(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onOwnList(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onAllList(ArrayList<Playlist> playlists) {
		if(playlists.size() < PaginatedRecyclerView.PAGE_SIZE){
			mRecyclerView.setLast(true);
		}
		if(mRecyclerView.isLoading()) mRecyclerView.setLoading(false);
		mPlaylists.addAll(playlists);
		Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
		this.mAdapter.notifyDataSetChanged();
		mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
	}

	@Override
	public void onFollowingList(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onPlaylistUpdated() {

	}

	@Override
	public void onPlaylistCreated(Playlist playlist) {

	}

	@Override
	public void onUserFollows(Follow follows) {

	}

	@Override
	public void onUpdateFollow(Follow result) {

	}

	@Override
	public void onPlaylistDeleted() {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}

	public void reloadItems(ArrayList<Playlist> playlists) {
		this.mPlaylists = playlists;

		Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
		mAdapter = new PlaylistListVerticalAdapter(mPlaylists, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
	}
}
