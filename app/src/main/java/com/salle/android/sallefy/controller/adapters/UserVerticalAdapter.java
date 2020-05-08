package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserFollowCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.User;

import java.util.ArrayList;

public class UserVerticalAdapter extends RecyclerView.Adapter<UserVerticalAdapter.ViewHolder> implements UserFollowCallback {

    public static final String TAG = UserVerticalAdapter.class.getName();
    private ArrayList<User> mUsers;
    private Context mContext;
    private AdapterClickCallback mCallback;
    private int layoutId;

    //Guardamos la referencia del holder que le han dado follow
    private ViewHolder followHolder;

    public UserVerticalAdapter(ArrayList<User> users, AdapterClickCallback callback, Context context, int layoutId) {
        mUsers = users;
        mContext = context;
        this.layoutId = layoutId;
        followHolder = null;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (mUsers != null && mUsers.size() > 0) {

            User currUser = mUsers.get(position);

            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onUserClick(currUser);
                }
            });

            holder.mTitle.setText(currUser.getLogin());
            holder.mFollowers.setText(currUser.getFollowers() + " followers");
            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_user_thumbnail)
                    .load(currUser.getImageUrl())
                    .into(holder.mPhoto);



            //isFollowing = currUser.getFollowedByUser();

            if (currUser.getFollowedByUser() == null){
                Log.d("TAGG", currUser.getLogin());
                currUser.setFollowedByUser(false);
            }

            if (currUser.getFollowedByUser()) {
                holder.mFollowing.setTextAppearance(R.style.FollowingButton);
                holder.mFollowing.setBackgroundResource(R.drawable.round_corner_light);
                holder.mFollowing.setText(R.string.FollowingText);
            } else {
                holder.mFollowing.setTextAppearance(R.style.ToFollowButton);
                holder.mFollowing.setBackgroundResource(R.drawable.round_corner);
                holder.mFollowing.setText(R.string.ToFollowText);
            }

            holder.mFollowing.setOnClickListener(view -> {
                if(followHolder == null) {
                    followHolder = holder;
                    UserManager.getInstance(mContext).setFollowing(currUser.getLogin(), !currUser.getFollowedByUser(), UserVerticalAdapter.this);
                }else{
                    //El sistema esta ocupado dando like a otro post.
                    Toast.makeText(mContext, R.string.error_follow, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mUsers != null ? mUsers.size() : 0);
    }


    @Override
    public void onFollowSuccess(String userLogin) {
        for(User currUser : mUsers){
            if(currUser.getLogin().equals(userLogin)){
                currUser.setFollowedByUser(!currUser.getFollowedByUser());


                if (currUser.getFollowedByUser()) {
                    followHolder.mFollowing.setTextAppearance(R.style.FollowingButton);
                    followHolder.mFollowing.setBackgroundResource(R.drawable.round_corner_light);
                    followHolder.mFollowing.setText(R.string.FollowingText);
                } else {
                    followHolder.mFollowing.setTextAppearance(R.style.ToFollowButton);
                    followHolder.mFollowing.setBackgroundResource(R.drawable.round_corner);
                    followHolder.mFollowing.setText(R.string.ToFollowText);
                }

                currUser.setFollowers(currUser.getFollowers() + ((currUser.getFollowedByUser()) ? +1 : -1));
                followHolder.mFollowers.setText(currUser.getFollowers() + " followers");

                followHolder = null;
                break;
            }
        }
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
