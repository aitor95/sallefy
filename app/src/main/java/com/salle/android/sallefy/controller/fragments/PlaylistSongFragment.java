package com.salle.android.sallefy.controller.fragments;

import android.content.Intent;
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
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;

public class PlaylistSongFragment extends Fragment {

	public static final String TAG = PlaylistSongFragment.class.getName();

	private RecyclerView mRecyclerView;
	private Playlist mPlaylist;

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
		View v = inflater.inflate(R.layout.fragment_playlist_song, container, false);
		initViews(v);
		Intent i = getActivity().getIntent();
		mPlaylist = (Playlist) i.getSerializableExtra(Constants.INTENT_EXTRAS.PLAYLIST_DATA);
		TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), (ArrayList<Track>) mPlaylist.getTracks());
		adapter.setPlaylist(mPlaylist);

		mRecyclerView.setAdapter(adapter);
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		TrackListVerticalAdapter adapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity(), null);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);
	}
}
