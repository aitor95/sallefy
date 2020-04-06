package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
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
import com.salle.android.sallefy.model.User;

import java.util.ArrayList;

public class UserVerticalAdapter extends RecyclerView.Adapter<UserVerticalAdapter.ViewHolder> {

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        isFollowing = false;

        Button followingButton = itemView.findViewById(R.id.user_following_button);
        followingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFollowing = !isFollowing;
                if (isFollowing) {
                    ((Button) view).setTextAppearance(R.style.FollowingButton);
                    view.setBackgroundResource(R.drawable.round_corner_light);
                    ((Button) view).setText(R.string.FollowingText);
                } else {
                    ((Button) view).setTextAppearance(R.style.ToFollowButton);
                    view.setBackgroundResource(R.drawable.round_corner);
                    ((Button) view).setText(R.string.ToFollowText);
                }
                //TODO: fer la interacció amb la API del following
            }
        });

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
        }
    }

    @Override
    public int getItemCount() {
        return (mUsers != null ? mUsers.size() : 0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        ImageView mPhoto;
        TextView mTitle;
        TextView mFollowers;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.item_user_vertical);
            mPhoto = (ImageView) itemView.findViewById(R.id.item_user_photo);
            mTitle = (TextView) itemView.findViewById(R.id.item_user_name);
            mFollowers = (TextView) itemView.findViewById(R.id.item_user_nfollowers);
        }
    }
}
