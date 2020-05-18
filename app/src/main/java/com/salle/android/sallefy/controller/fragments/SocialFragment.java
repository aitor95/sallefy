package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.os.Parcelable;
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
import com.salle.android.sallefy.controller.adapters.SocialActivityAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.utils.PaginatedRecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

public class SocialFragment extends Fragment implements TrackCallback, UserCallback {

	public static final String TAG = SocialFragment.class.getName();

	private PaginatedRecyclerView mRecyclerView;
	private ArrayList<Track> mTracks;
	private ArrayList<User> mFollowing;

	//Pagination
	private int currentPage = 0;

	private LinearLayoutManager mLinearLayoutManager;

	private SocialActivityAdapter adapter;

	private static AdapterClickCallback adapterClickCallback;
	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}

	public static Fragment getInstance() {
		return new SocialFragment();
	}


	private TextView nitv;

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
		mRecyclerView = (PaginatedRecyclerView) v.findViewById(R.id.dynamic_recyclerView);
		//mLinearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		mTracks = new ArrayList<>();
		adapter = new SocialActivityAdapter(adapterClickCallback, getContext(), mTracks);
		//mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mRecyclerView.setAdapter(adapter);

		nitv = v.findViewById(R.id.no_info_aviable_on_social);

		mRecyclerView.setListener(new PaginatedRecyclerView.PaginatedRecyclerViewListener() {
			@Override
			public void onPageLoaded() {
				loadMoreItems();
			}
		});

	}

	private void loadMoreItems() {
		mRecyclerView.setLoading(true);

		currentPage += 1;

		TrackManager.getInstance(getActivity()).getAllTracksPagination(this, currentPage, 10, true, false);

	}


	private void getData() {
		UserManager.getInstance(this.getContext()).getMeFollowing(this);
	}

	@Override
	public void onMeFollowingsReceived(List<User> users) {
		mFollowing = new ArrayList<User>();
		mFollowing.addAll(users);
		TrackManager.getInstance(getActivity()).getAllTracksPagination(this, currentPage, 10, true, false);
	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onUpdateUser() {

	}

	@Override
	public void onMeFollowersReceived(List<UserPublicInfo> body) {

	}

	@Override
	public void onTracksReceived(List<Track> tracks) {

		int newSongs = 0;

		if(tracks.size() < PAGE_SIZE){
			mRecyclerView.setLast(true);
		}

		for (int i = 0; i < tracks.size(); i++){
			if (!("" + tracks.get(i).getReleased()).equals("null")) {
				for (int j = 0; j < this.mFollowing.size(); j++) {
					if (tracks.get(i).getUser().getLogin().equals(this.mFollowing.get(j).getLogin())){
						mTracks.add(tracks.get(i));
						newSongs++;
					}
				}
			}
		}

		mTracks.sort(Comparator.comparing(Track::getReleased).reversed());


		//Es el ultimo y mtracks sigue siendo 0 --> texto
		//Es el ultimo y mtracks no es 0 --> simplemente actualizar
		//No es el ultimo y mtracks no llega a PAGE_SIZE --> carga mas
		//No es el ultimo y mtracks llega a PAGE_SIZE--> actualiza
		if(!mRecyclerView.isLast() && (this.mTracks.size() < PAGE_SIZE || newSongs == 0)){
			loadMoreItems();
		}else{
			if(mRecyclerView.isLast() && mTracks.size() == 0){
				if (mFollowing.size() != 0) nitv.setText(R.string.no_tracks_on_social);
				else nitv.setText(R.string.no_users_on_social);
			}else{
				if (mRecyclerView.isLoading()) mRecyclerView.setLoading(false);
				Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
				this.adapter.notifyDataSetChanged();
				mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
			}
		}
	}

	@Override
	public void onNoTracks(Throwable throwable) {

		mRecyclerView.setLast(true);
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
	public void onTrackDeleted() {

	}

	@Override
	public void onFailure(Throwable throwable) {

	}

	@Override
	public void onUsersReceived(List<User> users) {

	}

	@Override
	public void onUsersFailure(Throwable throwable) {

	}
}
