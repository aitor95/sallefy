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
import com.salle.android.sallefy.controller.adapters.SocialActivityAdapter;
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment implements TrackCallback, TrackListCallback {

	private RecyclerView mRecyclerView;
	private ArrayList<Track> mTracks;

	public static final String TAG = SocialFragment.class.getName();


	public static Fragment getInstance() {
		return new SocialFragment();
	}

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
		SocialActivityAdapter adapter = new SocialActivityAdapter(null, getContext(), null);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);
	}

	private void getData() {
		//TODO: cambiar por coger las listas que tocan
		TrackManager.getInstance(getActivity()).getAllTracks(this);
		mTracks = new ArrayList<>();
	}

	@Override
	public void onTracksReceived(List<Track> tracks) {
		mTracks = new ArrayList<Track>();
		for (int i = 0; i < tracks.size(); i++){
			if (!("" + tracks.get(i).getReleased()).equals("null")) {
				//IF MTRACK IS FROM FOLLOWED USER
				mTracks.add(tracks.get(i));
			}
		}

		//ORDER MTRACKS BY RELEASE DATE

		SocialActivityAdapter adapter = new SocialActivityAdapter(this, getContext(), mTracks);
		mRecyclerView.setAdapter(adapter);
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
	public void onFailure(Throwable throwable) {

	}

	@Override
	public void onTrackSelected(Track track) {

	}

	@Override
	public void onTrackSelected(int index) {

	}
}
