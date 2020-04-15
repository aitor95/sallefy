package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.PlaylistListVerticalAdapter;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class SeeAllPlaylistFragment extends Fragment {

	public static final String TAG = SeeAllPlaylistFragment.class.getName();
	public static final String TAG_CONTENT = "TAG_LIST";

	private RecyclerView mRecyclerView;
	private ArrayList<Playlist> mPlaylists;

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

		mPlaylists = (ArrayList<Playlist>) getArguments().getSerializable(TAG_CONTENT);
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(mPlaylists, getContext(), null, R.layout.item_playlist_vertical);
		mRecyclerView.setAdapter(adapter);
		return v;
	}

	public static SeeAllPlaylistFragment newInstance(ArrayList<Playlist> playlists) {
		SeeAllPlaylistFragment fragment = new SeeAllPlaylistFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TAG_CONTENT, playlists);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(null, getContext(), null, R.layout.item_playlist_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(adapter);

		v.findViewById(R.id.seeAllTitlePlaylists).setOnClickListener(view -> {
			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
		});

	}
}
