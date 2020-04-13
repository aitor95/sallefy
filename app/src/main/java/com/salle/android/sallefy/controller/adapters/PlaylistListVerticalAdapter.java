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
import com.salle.android.sallefy.controller.callbacks.PlaylistAdapterCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistFollowCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;

public class PlaylistListVerticalAdapter extends RecyclerView.Adapter<PlaylistListVerticalAdapter.ViewHolder> implements PlaylistFollowCallback {

    public static final String TAG = PlaylistListVerticalAdapter.class.getName();
    private ArrayList<Playlist> mPlaylists;
    private Context mContext;
    private PlaylistAdapterCallback mCallback;
    private int layoutId;

    //Guardamos la referencia del holder que le han dado follow
    private ViewHolder followHolder;

    public PlaylistListVerticalAdapter(ArrayList<Playlist> playlists, Context context, PlaylistAdapterCallback callback, int layoutId) {
        mPlaylists = playlists;
        mContext = context;
        mCallback = callback;
        this.layoutId = layoutId;
        followHolder = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);


        return new PlaylistListVerticalAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (mPlaylists != null && mPlaylists.size() > 0) {

            Playlist currPlaylist = mPlaylists.get(position);

            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onPlaylistClick(currPlaylist);
                }
            });

            holder.mTitle.setText(currPlaylist.getName());
            holder.mAuthor.setText(currPlaylist.getUser().getLogin());

            if (currPlaylist.getThumbnail() != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .placeholder(R.drawable.ic_audiotrack)
                        .load(currPlaylist.getThumbnail())
                        .into(holder.mPhoto);
            }

            if (currPlaylist.isFollowed()) {
                holder.followingButton.setTextAppearance(R.style.FollowingButton);
                holder.followingButton.setBackgroundResource(R.drawable.round_corner_light);
                holder.followingButton.setText(R.string.FollowingText);
            } else {
                holder.followingButton.setTextAppearance(R.style.ToFollowButton);
                holder.followingButton.setBackgroundResource(R.drawable.round_corner);
                holder.followingButton.setText(R.string.ToFollowText);
            }

            holder.followingButton.setOnClickListener(view -> {
                if(followHolder == null) {
                    followHolder = holder;
                    PlaylistManager.getInstance(mContext).followPlaylist(currPlaylist, !currPlaylist.isFollowed(),  PlaylistListVerticalAdapter.this);
                }else{
                    //El sistema esta ocupado dando like a otro post.
                    Toast.makeText(mContext, R.string.error_follow, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mPlaylists != null ? mPlaylists.size() : 0);
    }

    @Override
    public void onFollowSuccess(Playlist playlist) {
        for(Playlist p : mPlaylists){
            if(p.getId().intValue() == playlist.getId()){
                p.setFollowed(!p.isFollowed());
                break;
            }
        }

        Log.d(TAG, "onFollowSuccess: Playlist is " + playlist.isFollowed() + " data: " + playlist.getName());

        if (playlist.isFollowed()) {
            followHolder.followingButton.setTextAppearance(R.style.FollowingButton);
            followHolder.followingButton.setBackgroundResource(R.drawable.round_corner_light);
            followHolder.followingButton.setText(R.string.FollowingText);
        } else {
            followHolder.followingButton.setTextAppearance(R.style.ToFollowButton);
            followHolder.followingButton.setBackgroundResource(R.drawable.round_corner);
            followHolder.followingButton.setText(R.string.ToFollowText);
        }
        followHolder = null;
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: Erros following in playlistVerticalAdapter " + throwable.getMessage());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        ImageView mPhoto;
        TextView mTitle;
        TextView mAuthor;
        TextView followingButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.item_playlist_layout);
            mPhoto = (ImageView) itemView.findViewById(R.id.item_playlist_photo);
            mTitle = (TextView) itemView.findViewById(R.id.item_playlist_title);
            mAuthor = (TextView) itemView.findViewById(R.id.item_playlist_author);
            followingButton = (TextView) itemView.findViewById(R.id.playlist_following_button);
        }
    }
}
