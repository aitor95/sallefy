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
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

public class TrackListVerticalAdapter extends RecyclerView.Adapter<TrackListVerticalAdapter.ViewHolder> implements LikeCallback{

    private static final String TAG = "TrackListAdapter";
    private ArrayList<Track> mTracks;
    private Context mContext;
    private TrackListCallback mCallback;
    private int NUM_VIEWHOLDERS = 0;

    //Guardamos la referencia del holder que le han dado like
    private ViewHolder likedHolder;

    public TrackListVerticalAdapter(TrackListCallback callback, Context context, ArrayList<Track> tracks ) {
        mTracks = tracks;
        mContext = context;
        mCallback = callback;
        likedHolder = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called. Num viewHolders: " + NUM_VIEWHOLDERS++);

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_vertical, parent, false);
        ViewHolder vh = new TrackListVerticalAdapter.ViewHolder(itemView);
        Log.d(TAG, "onCreateViewHolder: called. viewHolder hashCode: " + vh.hashCode());

        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called. viewHolder hashcode: " + holder.hashCode());

        Track t = mTracks.get(position);
        holder.mLayout.setOnClickListener(v -> mCallback.onTrackSelected(position));

        holder.tvTitle.setText(t.getName());
        adjustTextView(holder.tvTitle);

        holder.tvAuthor.setText(t.getUserLogin());
        adjustTextView(holder.tvAuthor);

        if (t.getThumbnail() != null) {
            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(t.getThumbnail())
                    .into(holder.ivPicture);
        }

        holder.likeImg.setImageResource((!t.isLiked()) ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_black_24dp);

        holder.likeImg.setOnClickListener(view -> {
            if(likedHolder == null) {
                likedHolder = holder;
                TrackManager.getInstance(mContext).likeTrack(mTracks.get(position).getId(), !mTracks.get(position).isLiked(), this);
            }else{
                //El sistema esta ocupado dando like a otro post.
                Toast.makeText(mContext, R.string.error_like, Toast.LENGTH_SHORT).show();
            }
        });

        holder.moreInfoImg.setOnClickListener(view -> {
            //TODO: More Info track
        });
    }

    private void adjustTextView(TextView tv) {
        tv.getViewTreeObserver();
        tv.measure(0, 0);

        tv.post(() -> {
            int linesCount = tv.getLineCount();

            String titleTrack = (String) tv.getText();
            if (linesCount > 1) {
                titleTrack = titleTrack.substring(0, titleTrack.length() - 4);
                tv.setText(titleTrack.concat("..."));
                adjustTextView(tv);
            }
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
    }
}
