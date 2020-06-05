package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.os.Handler;
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
import com.salle.android.sallefy.controller.adapters.UserVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MeUserFragment extends Fragment implements UserCallback {

	public static final String TAG = MeUserFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<User> mUsers;

	private View v;

	private User mUser;
	private boolean isOwner;

	private static AdapterClickCallback adapterClickCallback;
	private static MeFragmentUpdateFollowingCount followingCount;

	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}

	public static void setFollowCountCallback(MeFragmentUpdateFollowingCount meFragment) {
		followingCount = meFragment;
	}


	public interface MeFragmentUpdateFollowingCount{
		void updateFollowingCount(int followingCount);
	}

	public MeUserFragment(User user, boolean isOwner, ArrayList<User> mFollowing){
		mUsers = mFollowing;
		mUser = user;
		this.isOwner = isOwner;
	}

	private final int REFRESH_TIME = 250;
	private final int MAX_TIME = 2000;

	//Thread management para la seekbar
	private Handler mHandler;
	Runnable mSeekBarUpdater = new Runnable() {
		@Override
		public void run() {
			try{
				LinkedList<String> loginToDelete = new LinkedList<>();
				if(mUsers != null && isOwner) {
					for (User u : mUsers) {
						if (!u.getFollowedByUser() ) {
							long timeSinceLastFollow = u.getTimeSinceLastFollow();
							if (timeSinceLastFollow == 0) {
								// Value never defined. El usuari ha deixat de seguir la account fa REFRESH_TIME
								u.setTimeSinceLastFollow(System.currentTimeMillis());
							} else {
								if (System.currentTimeMillis() - timeSinceLastFollow >= MAX_TIME) {
									//User needs to be removed.
									loginToDelete.push(u.getLogin());
								}
							}

						} else if (u.getTimeSinceLastFollow() != 0) {
							u.setTimeSinceLastFollow(0);
						}
					}

					int size = loginToDelete.size();
					if (size != 0) {
						for (String s : loginToDelete) {
							mUsers.removeIf((u) -> u.getLogin().equals(s));
						}
						followingCount.updateFollowingCount(mUsers.size());
						UserVerticalAdapter adapter = new UserVerticalAdapter(mUsers, adapterClickCallback, getContext(), R.layout.item_user_vertical);
						mRecyclerView.setAdapter(adapter);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}finally{
				mHandler.postDelayed(mSeekBarUpdater, REFRESH_TIME);
			}
		}
	};


	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mSeekBarUpdater);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		mSeekBarUpdater.run();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_me_lists_users, container, false);
		TextView text = v.findViewById(R.id.me_text_error);
		text.setText(R.string.LoadingMe);
		initViews(v);
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);

		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		mRecyclerView.setLayoutManager(manager);

		if(mUsers != null){
			onDataReceived(mUsers);
		}else{
			mUsers = new ArrayList<>();
		}

		UserVerticalAdapter madapter = new UserVerticalAdapter(mUsers, adapterClickCallback, getContext(), R.layout.item_user_vertical);
		mRecyclerView.setAdapter(madapter);

	}

	@Override
	public void onUsersReceived(List<User> users) {

	}

	@Override
	public void onUsersFailure(Throwable throwable) {

	}


	private void onDataReceived(List<User> users){
		mUsers = (ArrayList<User>) users;

		TextView text = v.findViewById(R.id.me_text_error);

		if(mUsers.isEmpty()){
			text.setVisibility(View.VISIBLE);
			if(isOwner)
				text.setText(R.string.NoContentAvailableMeUsers);
			else
				text.setText(R.string.UserDontFollowAnyone);
		}else{
			text.setVisibility(View.GONE);
		}

		UserVerticalAdapter adapter = new UserVerticalAdapter(mUsers, adapterClickCallback, getContext(),  R.layout.item_user_vertical);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onMeFollowingsReceived(List<User> users) {

	}


	@Override
	public void onAllFollowingsFromUserReceived(List<User> users) {
	}

	@Override
	public void onAllFollowersFromUserReceived(List<User> users) {

	}

    @Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onUpdateUser() {

	}

    @Override
    public void onUpdatePassword() {

    }

    @Override
	public void onMeFollowersReceived(List<UserPublicInfo> body) {

	}

	@Override
	public void onDeleteAccount() {

	}


	@Override
	public void onFailure(Throwable throwable) {

	}
}
