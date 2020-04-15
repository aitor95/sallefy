package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.model.User;

import java.util.ArrayList;

public class UserHorizontalAdapter extends RecyclerView.Adapter<UserHorizontalAdapter.ViewHolder> {

    public static final String TAG = UserHorizontalAdapter.class.getName();

    private ArrayList<User> mUsers;
    private Context mContext;

    private static AdapterClickCallback mCallback;

    public UserHorizontalAdapter(ArrayList<User> users,AdapterClickCallback callback, Context context) {
        mUsers = users;
        mContext = context;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_horizontal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.tvUsername.setText(user.getLogin());

        holder.item_playlist_layout.setOnClickListener(v->mCallback.onUserClick(user));

        if (user.getImageUrl() != null) {
            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_user_thumbnail)
                    .load(user.getImageUrl())
                    .into(holder.ivPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return (mUsers != null ? mUsers.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        ImageView ivPhoto;
        LinearLayout item_playlist_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername           = itemView.findViewById(R.id.item_user_name_horizontal);
            ivPhoto              = itemView.findViewById(R.id.item_user_photo_horizontal);
            item_playlist_layout = itemView.findViewById(R.id.item_playlist_layout);
        }
    }
}
