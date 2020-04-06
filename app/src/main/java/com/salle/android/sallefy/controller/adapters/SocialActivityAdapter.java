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
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;

public class SocialActivityAdapter extends RecyclerView.Adapter<SocialActivityAdapter.ViewHolder> {

    private static final String TAG = "TrackListAdapter";
    private ArrayList<Track> mTracks;
    private Context mContext;
    private TrackListCallback mCallback;
    private int NUM_VIEWHOLDERS = 0;

    private Boolean liked;

    public SocialActivityAdapter(TrackListCallback callback, Context context, ArrayList<Track> tracks ) {
        mTracks = tracks;
        mContext = context;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called. Num viewHolders: " + NUM_VIEWHOLDERS++);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_vertical, parent, false);
        ViewHolder vh = new SocialActivityAdapter.ViewHolder(v);
        Log.d(TAG, "onCreateViewHolder: called. viewHolder hashCode: " + vh.hashCode());

        ImageView likeImg = v.findViewById(R.id.track_like);
        liked = true;

        likeImg.setOnClickListener(view -> {
            ((ImageView) view).setImageResource((liked) ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_black_24dp);
            liked = !liked;
            //TODO: Vincular el estado del like con la información real de la cuenta
        });

        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Track track = mTracks.get(position);

        Log.d(TAG, "onBindViewHolder: called. viewHolder hashcode: " + holder.hashCode());

        holder.mLayout.setOnClickListener(v -> mCallback.onTrackSelected(position));

        holder.username.setText(track.getUserLogin());
        holder.timeToGo.setText("posted a track on " + track.getReleased());    //TODO: Solusionar
        holder.trackTitle.setText(track.getName());
        holder.trackArtist.setText(track.getUserLogin());                       //TODO: Buscar alternativa
        holder.NumOfLikes.setText("54");                                        //TODO: posar valor de debó
        holder.favImg.setImageResource(
                (track.isLiked()) ?
                        R.drawable.ic_favorite_black_24dp :
                        R.drawable.ic_favorite_border_black_24dp
        );

        liked = track.isLiked();

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

        LinearLayout mLayout;;
        ImageView ivPicture;
        TextView username;
        TextView timeToGo;
        TextView trackTitle;
        TextView trackArtist;
        TextView NumOfLikes;
        RecyclerView genres;
        ImageView favImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.social_activity_layout);
            ivPicture = itemView.findViewById(R.id.track_img);
            username = itemView.findViewById(R.id.user_sa);
            timeToGo = itemView.findViewById(R.id.timePassed_sa);
            trackTitle = itemView.findViewById(R.id.track_title_sa);
            trackArtist = itemView.findViewById(R.id.track_author_sa);
            NumOfLikes = itemView.findViewById(R.id.numoflikes_sa);
            genres = itemView.findViewById(R.id.listofgenres_sa);
            favImg = itemView.findViewById(R.id.track_like_sa);
        }
    }
}
