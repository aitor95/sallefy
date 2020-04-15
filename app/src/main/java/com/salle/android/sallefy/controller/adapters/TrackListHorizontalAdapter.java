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
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

public class TrackListHorizontalAdapter extends RecyclerView.Adapter<TrackListHorizontalAdapter.ViewHolder> {

    private static final String TAG = "TrackListAdapter";
    private ArrayList<Track> mTracks;
    private Context mContext;
    private AdapterClickCallback mCallback;
    private int NUM_VIEWHOLDERS = 0;

    public TrackListHorizontalAdapter(AdapterClickCallback callback, Context context, ArrayList<Track> tracks ) {
        mTracks = tracks;
        mContext = context;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called. Num viewHolders: " + NUM_VIEWHOLDERS++);

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_horizontal, parent, false);
        ViewHolder vh = new TrackListHorizontalAdapter.ViewHolder(itemView);
        Log.d(TAG, "onCreateViewHolder: called. viewHolder hashCode: " + vh.hashCode());

        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called. viewHolder hashcode: " + holder.hashCode());

        Track track = mTracks.get(position);

        holder.mLayout.setOnClickListener(v -> mCallback.onTrackSelected(track));

        holder.tvTitle.setText(track.getName());
        adjustTextView(holder.tvTitle);

        holder.tvAuthor.setText(track.getUserLogin());
        adjustTextView(holder.tvAuthor);

        if (track.getThumbnail() != null) {
            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(track.getThumbnail())
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
            mLayout = itemView.findViewById(R.id.track_item_horizontal_layout);
            tvTitle = (TextView) itemView.findViewById(R.id.track_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.track_author);
            ivPicture = (ImageView) itemView.findViewById(R.id.track_img);
        }
    }
}
