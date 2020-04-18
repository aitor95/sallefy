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
import com.salle.android.sallefy.controller.adapters.PlaylistListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.utils.PreferenceUtils;

import java.util.ArrayList;

public class MePlaylistFragment extends Fragment implements PlaylistCallback {

	public static final String TAG = MePlaylistFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<Playlist> mPlaylists;
	private boolean playlistsAvailable;



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
		View v = inflater.inflate(R.layout.fragment_me_lists, container, false);
		initViews(v);
		getData(v);
		if(!playlistsAvailable) {
			TextView text = v.findViewById(R.id.me_text_error);
			text.setText(R.string.NoContentAvailable);
		}
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(null, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);
	}

	private void getData(View v) {
		PlaylistManager.getInstance(getActivity()).getPlaylistsByUser(PreferenceUtils.getUser(v.getContext()),this);
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
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(playlists, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		this.playlistsAvailable = !playlists.isEmpty();
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
	public void onUserFollows(Follow follows) {

	}

	@Override
	public void onUpdateFollow(Follow result) {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}
}
