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
import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.controller.music.MusicCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.List;

public class SeeAllSongFragment extends Fragment implements MusicCallback, TrackListCallback, TrackCallback {

	public static final String TAG = SeeAllSongFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<Track> mTracks;

	public static Fragment getInstance() {
		return new SeeAllSongFragment();
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
		getData();
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(this, getActivity(), null);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);

		v.findViewById(R.id.seeAllTitleSongs).setOnClickListener(view -> {
			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
		});
	}

	private void getData() {
		//TODO: cambiar por coger lascanciones que tocan
		TrackManager.getInstance(getActivity()).getAllTracks(this);
		mTracks = new ArrayList<>();
	}

	@Override
	public void onTracksReceived(List<Track> tracks) {
		mTracks = (ArrayList) tracks;
		TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(this, getActivity(), mTracks);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onTrackSelected(Track track) {

	}

	@Override
	public void onTrackSelected(int index) {

	}

	@Override
	public void onTrackUpdated(Track track) {
		TrackManager.getInstance(getContext()).updateTrack(track, this);
	}

	@Override
	public void onMusicPlayerPrepared() {

	}

	@Override
	public void onSongFinishedPlaying() {

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
}
