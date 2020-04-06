package com.salle.android.sallefy.controller.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
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
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    private static final String TAG = "TrackListAdapter";
    private ArrayList<Track> mTracks;
    private Context mContext;
    private TrackListCallback mCallback;
    private int NUM_VIEWHOLDERS = 0;

    private Boolean liked;

    private ImageView likeImg;
    private ImageView moreInfoImg;

    public TrackListAdapter(TrackListCallback callback, Context context, ArrayList<Track> tracks ) {
        mTracks = tracks;
        mContext = context;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called. Num viewHolders: " + NUM_VIEWHOLDERS++);

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_vertical, parent, false);
        ViewHolder vh = new TrackListAdapter.ViewHolder(itemView);
        Log.d(TAG, "onCreateViewHolder: called. viewHolder hashCode: " + vh.hashCode());

        likeImg = itemView.findViewById(R.id.track_like);
        moreInfoImg = itemView.findViewById(R.id.track_moreInfo);
        liked = true;

        likeImg.setOnClickListener(view -> {
            ((ImageView) view).setImageResource((liked) ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_black_24dp);
            liked = !liked;
            //TODO: Vincular el estado del like con la información real de la cuenta
        });

        moreInfoImg.setOnClickListener(view -> {
            //TODO: Crear el menu despregable de la canción
        });

        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called. viewHolder hashcode: " + holder.hashCode());

        holder.mLayout.setOnClickListener(v -> mCallback.onTrackSelected(position));

        holder.tvTitle.setText(mTracks.get(position).getName());
        adjustTextView(holder.tvTitle);

        holder.tvAuthor.setText(mTracks.get(position).getUserLogin());
        adjustTextView(holder.tvAuthor);

        if (mTracks.get(position).getThumbnail() != null) {
            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(mTracks.get(position).getThumbnail())
                    .into(holder.ivPicture);
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLayout;
        TextView tvTitle;
        TextView tvAuthor;
        ImageView ivPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.track_item_layout);
            tvTitle = (TextView) itemView.findViewById(R.id.track_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.track_author);
            ivPicture = (ImageView) itemView.findViewById(R.id.track_img);
        }
    }
}
