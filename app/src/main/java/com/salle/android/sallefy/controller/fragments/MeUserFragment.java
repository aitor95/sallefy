package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.List;

import static com.salle.android.sallefy.controller.fragments.MeFragment.localFollowingUsers;

public class MeUserFragment extends Fragment implements UserCallback {

	public static final String TAG = MeUserFragment.class.getName();

	private RecyclerView mRecyclerView;
	private ArrayList<User> mUsers;

	private View v;

	private User mUser;
	private boolean isOwner;

	private static AdapterClickCallback adapterClickCallback;

	public static void setAdapterClickCallback(AdapterClickCallback callback){
		adapterClickCallback = callback;
	}

	public MeUserFragment(User user, boolean isOwner){
		mUser = user;
		this.isOwner = isOwner;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_me_lists_users, container, false);
		TextView text = v.findViewById(R.id.me_text_error);
		text.setText(R.string.LoadingMe);
		initViews(v);
		getData();
		return v;
	}

	private void initViews(View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.dynamic_recyclerView);

		LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		mRecyclerView.setLayoutManager(manager);

		mUsers = new ArrayList<>();

		UserVerticalAdapter madapter = new UserVerticalAdapter(mUsers, adapterClickCallback, getContext(), R.layout.item_user_vertical);
		mRecyclerView.setAdapter(madapter);

	}

	private void getData() {
		mUsers = new ArrayList<>();
		if(isOwner)
			UserManager.getInstance(getContext()).getMeFollowing(this);
		else
			UserManager.getInstance(getContext()).getAllFollowingsFromUser(mUser,this);
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
		localFollowingUsers = (ArrayList<User>) users;
		for (int i = 0; i < users.size(); i++) {
			users.get(i).setFollowedByUser(true);
		}
		onDataReceived(users);
	}


	@Override
	public void onAllFollowingsFromUserReceived(List<User> users) {
		for(User u : users){
			boolean aux = doLocalUserFollows(u);
			Log.d(TAG, "onAllFollowingsFromUserReceived: mUsers.contains(u) is " + aux + " " + u.getLogin()+  " " + localFollowingUsers.size());
			u.setFollowedByUser(aux);
		}
		onDataReceived(users);
	}

	private boolean doLocalUserFollows(User u) {
		for(User lu : localFollowingUsers){
			Log.d(TAG, "doLocalUserFollows: \t\t" + lu.getLogin());
			if(u.getLogin().equals(lu.getLogin())) return true;
		}
		return false;
	}

	@Override
	public void onAllFollowersFromUserReceived(List<User> users) {

	}

	@Override
	public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

	}

	@Override
	public void onUpdateUser(UserToken userToken) {

	}

    @Override
    public void onUpdatePassword(ChangePassword changePassword, UserToken us) {

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
