package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public static final String TAG = UserAdapter.class.getName();

    private ArrayList<User> mUsers;
    private Context mContext;

    public UserAdapter(ArrayList<User> users, Context context) {
        mUsers = users;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUsername.setText(mUsers.get(position).getLogin());
        if (mUsers.get(position).getImageUrl() != null) {
            Glide.with(mContext)
                    .asBitmap()
                    //.placeholder(R.drawable.ic_user_thumbnail)
                    .load(mUsers.get(position).getImageUrl())
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = (TextView) itemView.findViewById(R.id.item_user_name);
            ivPhoto = (ImageView) itemView.findViewById(R.id.item_user_photo);
        }
    }
}
