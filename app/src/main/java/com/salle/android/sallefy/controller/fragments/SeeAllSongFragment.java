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
import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.callbacks.SeeAllCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.PaginatedRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SeeAllSongFragment extends Fragment implements TrackCallback {

	public static final String TAG = SeeAllSongFragment.class.getName();
	public static final String TAG_CONTENT_POPULAR = SeeAllSongFragment.class.getName() + "_popular";
	public static final String TAG_CONTENT_DATA = SeeAllSongFragment.class.getName() + "_data";
	private int NUMBER_OF_TRACKS;


	private PaginatedRecyclerView mRecyclerView;
	private ArrayList mTracks;

	private static Playlist mPlaylist;

	//Pagination
	private int currentPage = 0;

	private boolean popular;

	private TrackListVerticalAdapter mAdapter;

	private static SeeAllCallback seeAllCallback;
	public static void setSeeAllCallback(SeeAllCallback seeAllC){
		seeAllCallback = seeAllC;
	}

	private static AdapterClickCallback adapterClickCallback;
	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}

	public static void setPlaylist(Playlist playlist) {
		mPlaylist = playlist;
	}

    @Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_see_all_songs, container, false);
		initViews(v);

		popular = (boolean) getArguments().getSerializable(TAG_CONTENT_POPULAR);
		mTracks = (ArrayList<Track>) getArguments().getSerializable(TAG_CONTENT_DATA);
		mAdapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), getFragmentManager(), mTracks);
		mAdapter.setPlaylist(mPlaylist);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setPageSize(NUMBER_OF_TRACKS);
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (PaginatedRecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		//TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), getFragmentManager(), null);
		mRecyclerView.setLayoutManager(manager);
		//mRecyclerView.setAdapter(adapter);
		mRecyclerView.setListener(new PaginatedRecyclerView.PaginatedRecyclerViewListener() {
			@Override
			public void onPageLoaded() { loadMoreItems(); }
		});


		v.findViewById(R.id.edit_playlist_nav).setOnClickListener(view -> {
			if (seeAllCallback != null) seeAllCallback.onSeeAllClosed();

			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
					.remove(this)
					.commit();
		});
	}

	public static SeeAllSongFragment newInstance(ArrayList<Track> tracks, boolean popular) {
		SeeAllSongFragment fragment = new SeeAllSongFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TAG_CONTENT_DATA, tracks);
		bundle.putSerializable(TAG_CONTENT_POPULAR, popular);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void loadMoreItems() {
		mRecyclerView.setLoading(true);

		currentPage += 1;
		TrackManager.getInstance(getActivity()).getAllTracksPagination(this, currentPage, NUMBER_OF_TRACKS, false, popular);
	}

	@Override
	public void onTracksReceived(List<Track> tracks) {
		if(tracks.size() < NUMBER_OF_TRACKS){
			mRecyclerView.setLast(true);
		}
		if(mRecyclerView.isLoading()) mRecyclerView.setLoading(false);
		mTracks.addAll(tracks);
		Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
		this.mAdapter.notifyDataSetChanged();
		mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
	}

	@Override
	public void onTrackById(Track track) {

	}

	public void reloadItems(ArrayList<Track> mTracks){
		this.mTracks = mTracks;

		Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
		this.mAdapter.notifyDataSetChanged();
		mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
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
	public void onCreateTrack(Track track) {

	}

	@Override
	public void onUpdatedTrack() {

	}

	@Override
	public void onTrackDeleted(Track track) {

	}

	@Override
	public void onPopularTracksReceived(List<Track> tracks) {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
	public void setNumber(int number) {
		this.NUMBER_OF_TRACKS = number;
	}
}
