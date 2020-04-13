package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserVerticalAdapter extends RecyclerView.Adapter<UserVerticalAdapter.ViewHolder> implements UserCallback {

    public static final String TAG = UserVerticalAdapter.class.getName();
    private ArrayList<User> mUsers;
    private Context mContext;
    private UserCallback mCallback;
    private int layoutId;
    private Boolean isFollowing;

    public UserVerticalAdapter(ArrayList<User> users, Context context, UserCallback callback, int layoutId) {
        mUsers = users;
        mContext = context;
        mCallback = callback;
        this.layoutId = layoutId;
    }

    private View fragmentView;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        fragmentView = itemView;
        isFollowing = false;

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (mUsers != null && mUsers.size() > 0) {

            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onUserClicked(mUsers.get(position));
                }
            });

            holder.mTitle.setText(mUsers.get(position).getFirstName());
            holder.mFollowers.setText(mUsers.get(position).getFollowers() + " followers");
            if (mUsers.get(position).getImageUrl() != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .placeholder(R.drawable.ic_audiotrack)
                        .load(mUsers.get(position).getImageUrl())
                        .into(holder.mPhoto);
            }

            User user = mUsers.get(position);
            //isFollowing = mUsers.get(position).getFollowedByUser();

            if (user.getFollowedByUser() == null){
                Log.d("TAGG", user.getLogin());
                user.setFollowedByUser(false);
            }

            if (user.getFollowedByUser()) {
                holder.mFollowing.setTextAppearance(R.style.FollowingButton);
                holder.mFollowing.setBackgroundResource(R.drawable.round_corner_light);
                holder.mFollowing.setText(R.string.FollowingText);
            } else {
                holder.mFollowing.setTextAppearance(R.style.ToFollowButton);
                holder.mFollowing.setBackgroundResource(R.drawable.round_corner);
                holder.mFollowing.setText(R.string.ToFollowText);
            }

            holder.mFollowing.setOnClickListener(view -> {
                user.setFollowedByUser(!user.getFollowedByUser());
                //isFollowing = !isFollowing;

                UserManager.getInstance(mContext).setFollowing(mUsers.get(position).getLogin(), user.getFollowedByUser(), mCallback);

                if (user.getFollowedByUser()) {
                    ((Button) view).setTextAppearance(R.style.FollowingButton);
                    view.setBackgroundResource(R.drawable.round_corner_light);
                    ((Button) view).setText(R.string.FollowingText);
                } else {
                    ((Button) view).setTextAppearance(R.style.ToFollowButton);
                    view.setBackgroundResource(R.drawable.round_corner);
                    ((Button) view).setText(R.string.ToFollowText);
                }

                mUsers.get(position).setFollowers(mUsers.get(position).getFollowers() + ((user.getFollowedByUser()) ? +1 : -1));
                holder.mFollowers.setText(mUsers.get(position).getFollowers() + " followers");
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mUsers != null ? mUsers.size() : 0);
    }

    @Override
    public void onLoginSuccess(UserToken userToken) {

    }

    @Override
    public void onLoginFailure(Throwable throwable) {

    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onUsersReceived(List<User> users) {

    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onUserClicked(User user) {

    }

    @Override
    public void onMeFollowingsReceived(List<UserPublicInfo> users) {

    }

    @Override
    public void onIsFollowingResponseReceived(String login, Boolean isFollowing) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        ImageView mPhoto;
        TextView mTitle;
        TextView mFollowers;
        TextView mFollowing;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.item_user_vertical);
            mPhoto = (ImageView) itemView.findViewById(R.id.item_user_photo);
            mTitle = (TextView) itemView.findViewById(R.id.item_user_name);
            mFollowers = (TextView) itemView.findViewById(R.id.item_user_nfollowers);
            mFollowing = (TextView) itemView.findViewById(R.id.user_following_button);
        }
    }
}
