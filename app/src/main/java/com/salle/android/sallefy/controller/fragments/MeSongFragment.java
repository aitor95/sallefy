package com.salle.android.sallefy.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.activities.UploadSongActivity;
import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MeSongFragment extends Fragment implements TrackCallback {

	public static final String TAG = MeSongFragment.class.getName();
	private static final int EXTRA_NEW_SONG_CODE = 98;

	private RecyclerView mRecyclerView;
	private ArrayList<Track> mTracks;

	private View v;

	private TrackListVerticalAdapter adapter;
	private static AdapterClickCallback adapterClickCallback;
	private boolean tracksAvailable;

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
		v = inflater.inflate(R.layout.fragment_me_lists_song, container, false);
		TextView text = v.findViewById(R.id.me_text_error);
		text.setText(R.string.LoadingMe);
		initViews(v);
		getData();
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), getFragmentManager(), null);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);

		Button addNew = v.findViewById(R.id.add_new_btn);
		addNew.setOnClickListener(view -> {
			Intent intent = new Intent(getContext(), UploadSongActivity.class);
			startActivityForResult(intent, EXTRA_NEW_SONG_CODE);
		});
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EXTRA_NEW_SONG_CODE && resultCode == RESULT_OK) {
			mTracks.add((Track) data.getSerializableExtra(Constants.INTENT_EXTRAS.TRACK));
			TextView text = v.findViewById(R.id.me_text_error);
			if(mTracks.isEmpty()){
				text.setText(R.string.NoContentAvailableMeSongs);
			}else{
				text.setText(null);
			}
			TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getContext(), getFragmentManager(), mTracks);
			mRecyclerView.setAdapter(adapter);
		}
	}

	private void getData() {
		TrackManager.getInstance(getActivity()).getOwnTracks(this);
		mTracks = new ArrayList<>();
	}

	public void updateSongInfo(Track track){

		for (int i = 0; i < mTracks.size(); i++) {
			if(mTracks.get(i).getId().intValue() == track.getId().intValue()){
				mTracks.set(i, track);
			}
		}
		TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), getFragmentManager(), mTracks);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onTracksReceived(List<Track> tracks) {
		//mTracks = (ArrayList<Track>) tracks;
		//this.tracksAvailable = !tracks.isEmpty();
		//TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), mTracks);
		//mRecyclerView.setAdapter(adapter);
	}


	@Override
	public void onNoTracks(Throwable throwable) {

	}

	@Override
	public void onPersonalTracksReceived(List<Track> tracks) {
		mTracks = (ArrayList<Track>) tracks;
		TextView text = v.findViewById(R.id.me_text_error);
		if(tracks.isEmpty()){
			text.setText(R.string.NoContentAvailableMeSongs);
		}else{
			text.setText(null);
		}
		TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), getFragmentManager(), mTracks);
		mRecyclerView.setAdapter(adapter);
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
	public void onTrackDeleted() {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
}
