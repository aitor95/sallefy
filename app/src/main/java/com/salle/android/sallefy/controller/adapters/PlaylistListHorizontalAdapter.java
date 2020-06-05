package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
import android.util.Log;
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
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;

public class PlaylistListHorizontalAdapter extends RecyclerView.Adapter<PlaylistListHorizontalAdapter.ViewHolder> {

    public static final String TAG = PlaylistListHorizontalAdapter.class.getName();
    private ArrayList<Playlist> mPlaylists;
    private Context mContext;
    private AdapterClickCallback mCallback;
    private int layoutId;

    public PlaylistListHorizontalAdapter(ArrayList<Playlist> playlists, Context context, AdapterClickCallback callback, int layoutId) {
        mPlaylists = playlists;
        mContext = context;
        mCallback = callback;

        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new PlaylistListHorizontalAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (mPlaylists != null && mPlaylists.size() > 0) {

            holder.mLayout.setOnClickListener(v -> {
                if (mCallback != null)
                    mCallback.onPlaylistClick(mPlaylists.get(position));
            });

            holder.mTitle.setText(mPlaylists.get(position).getName());

            holder.mAuthor.setText(mPlaylists.get(position).getUser().getLogin());

            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(mPlaylists.get(position).getThumbnail())
                    .into(holder.mPhoto);

        }else{
            Log.d(TAG, "onBindViewHolder: IM NULL!");
        }
    }


    @Override
    public int getItemCount() {
        return (mPlaylists != null ? mPlaylists.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        ImageView mPhoto;
        TextView mTitle;
        TextView mAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.item_playlist_layout);
            mPhoto = (ImageView) itemView.findViewById(R.id.item_playlist_photo);
            mTitle = (TextView) itemView.findViewById(R.id.item_playlist_title);
            mAuthor = (TextView) itemView.findViewById(R.id.item_playlist_author);
        }
    }
}
