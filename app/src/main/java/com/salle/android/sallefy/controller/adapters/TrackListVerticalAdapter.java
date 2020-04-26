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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.dialogs.BottomMenuDialog;
import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.TrackViewPack;

import java.util.ArrayList;
import java.util.LinkedList;

public class TrackListVerticalAdapter extends RecyclerView.Adapter<TrackListVerticalAdapter.ViewHolder> implements LikeCallback {

    private static final String TAG = TrackListVerticalAdapter.class.getName();
    private ArrayList<Track> mTracks;
    private Context mContext;
    private AdapterClickCallback mCallback;

    //Guardamos la referencia del holder que le han dado like
    private ViewHolder likedHolder;
    private Playlist mPlaylist;
    private FragmentManager fragmentManager;
    private LinkedList<TrackViewPack> tracksToLikeOnDialog;

    public TrackListVerticalAdapter(AdapterClickCallback callback, Context context, FragmentManager fragmentManager, ArrayList<Track> tracks) {
        mTracks = tracks;
        mContext = context;
        this.fragmentManager = fragmentManager;
        mCallback = callback;
        likedHolder = null;

        tracksToLikeOnDialog = new LinkedList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_vertical, parent, false);
        ViewHolder vh = new TrackListVerticalAdapter.ViewHolder(itemView);

        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called. viewHolder hashcode: " + holder.hashCode());

        Track track = mTracks.get(position);
        holder.mLayout.setOnClickListener(v -> {
            if(mPlaylist!=null){
                mPlaylist.startAsShuffle(false);
            }
            mCallback.onTrackClicked(track, mPlaylist);
        });


        holder.tvTitle.setText(track.getName());
      
        holder.tvAuthor.setText(track.getUserLogin());

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.ic_audiotrack)
                .load(track.getThumbnail())
                .into(holder.ivPicture);


        holder.likeImg.setImageResource((!track.isLiked()) ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_black_24dp);

        holder.likeImg.setOnClickListener(view -> {
            if(likedHolder == null) {
                likedHolder = holder;
                TrackManager.getInstance(mContext).likeTrack(mTracks.get(position).getId(), !mTracks.get(position).isLiked(), TrackListVerticalAdapter.this);
            }else{
                //El sistema esta ocupado dando like a otro post.
                Toast.makeText(mContext, R.string.error_like, Toast.LENGTH_SHORT).show();
            }
        });

        LikeCallback lc = this;
        holder.moreInfoImg.setOnClickListener(view -> {
            TrackViewPack tvp = new TrackViewPack(track, holder, lc);
            Boolean exists = false;
            for (TrackViewPack t: tracksToLikeOnDialog) {
                if (t.getTrack().getId().equals(track.getId())) {
                    exists = true;
                    t.setCallback(lc);
                    t.setViewHolder(holder);
                }
            }

            if (!exists) {
                tracksToLikeOnDialog.add(tvp);
            }

            BottomMenuDialog dialog = new BottomMenuDialog(tvp, mContext);
            dialog.show(fragmentManager,"options");
        });
    }


    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size():0;
    }

    public void updateTrackLikeStateIcon(int position, boolean isLiked) {
        mTracks.get(position).setLiked(isLiked);
        notifyDataSetChanged();
    }

    @Override
    public void onLikeSuccess(int songId) {
        for (Track t : mTracks){
            if(t.getId() == songId){
                t.setLiked(!t.isLiked());
                boolean liked = t.isLiked();

                for (TrackViewPack tvp: tracksToLikeOnDialog) if (tvp.getTrack().getId().equals(songId)) {
                    if (likedHolder == null) likedHolder = tvp.getViewHolder();
                }

                Log.d(TAG, "onLikeSuccess: Song now is liked?" + t.isLiked());
                likedHolder.likeImg.setImageResource((!liked) ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_black_24dp);
                t.setLikes(t.getLikes() + ((liked) ? +1 : -1));
                break;
            }
        }
        likedHolder = null;
    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    public void setPlaylist(Playlist mPlaylist) {
        this.mPlaylist = mPlaylist;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLayout;
        TextView tvTitle;
        TextView tvAuthor;
        ImageView ivPicture;
        ImageView likeImg;
        ImageView moreInfoImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.track_item_vertical_layout);
            tvTitle = (TextView) itemView.findViewById(R.id.track_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.track_author);
            ivPicture = (ImageView) itemView.findViewById(R.id.track_img);
            likeImg = (ImageView) itemView.findViewById(R.id.track_like);
            moreInfoImg = (ImageView) itemView.findViewById(R.id.track_moreInfo);
        }

        public void updateViewHolder(Track track) {
            tvTitle.setText(track.getName());
            if (track.getThumbnail() != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .placeholder(R.drawable.ic_audiotrack)
                        .load(track.getThumbnail())
                        .into(ivPicture);

            }
        }

    }
}
