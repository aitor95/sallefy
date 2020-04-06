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
import com.salle.android.sallefy.controller.adapters.PlaylistListVerticalAdapter;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;

public class MePlaylistFragment extends Fragment implements PlaylistCallback {

	public static final String TAG = MePlaylistFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<Playlist> mPlaylists;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_me_lists, container, false);
		initViews(v);
		getData();
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(null, getContext(), null, R.layout.item_playlist_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);
	}

	private void getData() {
		//TODO: cambiar por coger las listas que tocan
		PlaylistManager.getInstance(getActivity()).getListOfPlaylist(this);
		mPlaylists = new ArrayList<>();
	}

	@Override
	public void onPlaylistById(Playlist playlist) {

	}

	@Override
	public void onPlaylistsByUser(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onAllList(ArrayList<Playlist> playlists) {
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(playlists, getContext(), null, R.layout.item_playlist_vertical);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onFollowingList(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onPlaylistUpdated() {

	}

	@Override
	public void onPlaylistCreated() {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
}
