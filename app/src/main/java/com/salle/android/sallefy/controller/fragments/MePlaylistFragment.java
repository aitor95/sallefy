package com.salle.android.sallefy.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.salle.android.sallefy.controller.activities.NewPlaylistActivity;
import com.salle.android.sallefy.controller.adapters.PlaylistListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.utils.Session;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MePlaylistFragment extends Fragment implements PlaylistCallback {

	public static final String TAG = MePlaylistFragment.class.getName();
	public static final int EXTRA_NEW_PLAYLIST_CODE = 99;
	private final boolean isOwner;
	private final User mUser;

	private RecyclerView mRecyclerView;
	private ArrayList<Playlist> mPlaylists;

	private PlaylistListVerticalAdapter mAdapter;
	private View v;

	private boolean firstTime;


	private static AdapterClickCallback adapterClickCallback;

	public MePlaylistFragment(User mUser, boolean isOwner) {
		this.mUser = mUser;
		this.isOwner = isOwner;
	}

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
		firstTime = true;
		v = inflater.inflate(R.layout.fragment_me_lists_playlists, container, false);
		TextView text = v.findViewById(R.id.me_text_error);
		text.setText(R.string.LoadingMe);
		initViews(v);
		getData(v);
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		mAdapter = new PlaylistListVerticalAdapter(null, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setAdapter(mAdapter);

		Button addNew = v.findViewById(R.id.me_add_new_playlist);
		if(isOwner) {
			addNew.setVisibility(View.VISIBLE);
			addNew.setOnClickListener(view -> {
				Intent intent = new Intent(getContext(), NewPlaylistActivity.class);
				startActivityForResult(intent, EXTRA_NEW_PLAYLIST_CODE);
			});
		}else{
			addNew.setVisibility(View.GONE);
		}
	}

	private void getData(View v) {
		if(isOwner){
			PlaylistManager.getInstance(getActivity()).getPlaylistsByUser(Session.getInstance(getContext()).getUser().getLogin(),this);
		}else{
			PlaylistManager.getInstance(getActivity()).getPlaylistsByUser(mUser.getLogin(),this);
		}
		mPlaylists = new ArrayList<>();
	}

	public void updateInfo(ArrayList<Playlist> playlists){

		for (int i = 0; i < playlists.size(); i++) {
			for (int j = 0; j < mPlaylists.size(); j++) {
				if (mPlaylists.get(j).getId().intValue() == playlists.get(i).getId().intValue()) {
						mPlaylists.set(j, playlists.get(i));
				}
			}
		}

		mPlaylists.removeIf(Playlist::isDeleted);
		showInformativeMessageIfNecessary(mPlaylists);
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(mPlaylists, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		mRecyclerView.setAdapter(adapter);

	}

	@Override
	public void onPlaylistById(Playlist playlist) {

	}

	@Override
	public void onPlaylistsByUser(ArrayList<Playlist> playlists) {
		PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(playlists, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
		showInformativeMessageIfNecessary(playlists);
		mPlaylists = playlists;
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onOwnList(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onAllList(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onFollowingList(ArrayList<Playlist> playlists) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EXTRA_NEW_PLAYLIST_CODE && resultCode == RESULT_OK) {
			mPlaylists.add((Playlist) data.getSerializableExtra(NewPlaylistActivity.EXTRA_NEW_PLAYLIST));
			showInformativeMessageIfNecessary(mPlaylists);
			PlaylistListVerticalAdapter adapter = new PlaylistListVerticalAdapter(mPlaylists, getContext(), adapterClickCallback, R.layout.item_playlist_vertical);
			mRecyclerView.setAdapter(adapter);
		}else{
			Log.d(TAG, "onActivityResult: NOT DEFINED!");
		}
	}

	private void showInformativeMessageIfNecessary(ArrayList<Playlist> playlists) {
		TextView text = v.findViewById(R.id.me_text_error);
		if(playlists.isEmpty()) {
			text.setText(R.string.NoContentAvailableMePlayists);
		}else{
			text.setText(null);
		}

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

}
