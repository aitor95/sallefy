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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.LikeCallback;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Track;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SocialActivityAdapter extends RecyclerView.Adapter<SocialActivityAdapter.ViewHolder> implements LikeCallback {

    private static final String TAG = "TrackListAdapter";
    private ArrayList<Track> mTracks;
    private Context mContext;
    private int NUM_VIEWHOLDERS = 0;

    //Guardamos la referencia del holder que le han dado like
    private ViewHolder likedHolder;

    private AdapterClickCallback mCallback;

    public SocialActivityAdapter(AdapterClickCallback callback, Context context, ArrayList<Track> tracks) {
        mTracks = tracks;
        mContext = context;
        mCallback = callback;
        likedHolder = null;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called. Num viewHolders: " + NUM_VIEWHOLDERS++);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social_activity, parent, false);
        ViewHolder vh = new SocialActivityAdapter.ViewHolder(v);
        Log.d(TAG, "onCreateViewHolder: called. viewHolder hashCode: " + vh.hashCode());

        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Track track = mTracks.get(position);

        Log.d(TAG, "onBindViewHolder: called. viewHolder hashcode: " + holder.hashCode());

        holder.mLayout.setOnClickListener(v -> mCallback.onTrackClicked(track, null));

        holder.username.setText(track.getUserLogin());

        holder.username.setOnClickListener(v-> mCallback.onUserClick(track.getUser()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        holder.timeToGo.setText(new StringBuilder().append("posted a song ")
                        .append(getTimePassed(LocalDateTime.parse(track
                            .getReleased()
                                .replace("T", "_")
                                .replace("Z", ""), formatter))).toString());

        holder.trackTitle.setText(track.getName());

        holder.trackArtist.setText(track.getUserLogin());

        int likes = mTracks.get(position).getLikes();
        holder.NumOfLikes.setText(likes + "");

        holder.favImg.setImageResource(
                (track.isLiked()) ?
                        R.drawable.ic_favorite_black_24dp :
                        R.drawable.ic_favorite_border_black_24dp
        );

        holder.favImg.setOnClickListener(view -> {
            if(likedHolder == null) {
                likedHolder = holder;
                TrackManager.getInstance(mContext).likeTrack(mTracks.get(position).getId(), !mTracks.get(position).isLiked(), this);
            }else{
                //El sistema esta ocupado dando like a otro post.
                Toast.makeText(mContext, R.string.error_like, Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.ic_audiotrack)
                .load(mTracks.get(position).getThumbnail())
                .into(holder.ivPicture);

        setGenresView(holder, (ArrayList<Genre>) track.getGenres());
    }

    private void setGenresView(ViewHolder holder, ArrayList<Genre> genres) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        LinearLayoutManager manager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        GenresAdapter adapter = new GenresAdapter(genres, mCallback, R.layout.item_genre_little);
        holder.genres.setLayoutManager(manager);
        holder.genres.setAdapter(adapter);

        holder.genres.measure(0, 0);
        int rvWidth = holder.genres.getMeasuredWidth();
        int nPlus = 0;

        if (screenWidth * 0.65 < rvWidth) genres.add(new Genre("+" + nPlus));
        while (screenWidth * 0.65 < rvWidth) {
            genres.remove(genres.size() - 2);
            nPlus += 1;
            genres.get(genres.size() - 1).setName("+" + nPlus);
            adapter = new GenresAdapter(genres, mCallback, R.layout.item_genre_little);
            holder.genres.setAdapter(adapter);
            holder.genres.measure(0, 0);
            rvWidth = holder.genres.getMeasuredWidth();
        }
    }

    private String getTimePassed(LocalDateTime trackDate) {
        LocalDateTime dateNow = LocalDateTime.now();

        if (trackDate.getYear() != dateNow.getYear()) {
            int diff = (dateNow.getYear() - trackDate.getYear());
            String unit = (diff == 1) ? " year ago" : " years ago";
            return diff + unit;
        } else if (dateNow.getDayOfYear() - trackDate.getDayOfYear() > 31) {
            int diff = (dateNow.getMonthValue() - trackDate.getMonthValue());
            String unit = (diff == 1) ? " month ago" : " months ago";
            return diff + unit;
        } else if (trackDate.getDayOfYear() != dateNow.getDayOfYear()) {
            int diff = (dateNow.getDayOfYear() - trackDate.getDayOfYear());
            String unit = (diff == 1) ? " day ago" : " days ago";
            return diff + unit;
        } else if (trackDate.getHour() != dateNow.getHour()) {
            return (dateNow.getHour() - trackDate.getHour()) + "h ago";
        } else if (trackDate.getMinute() != dateNow.getMinute()) {
            return (dateNow.getMinute() - trackDate.getMinute()) + "min ago";
        } else if (trackDate.getSecond() != dateNow.getSecond()) {
            return (dateNow.getSecond() - trackDate.getSecond()) + "s ago";
        } else {
            return "less than a second ago";
        }
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
    public synchronized void onLikeSuccess(int songId) {
        for (Track t : mTracks){
            if(t.getId() == songId){
                t.setLiked(!t.isLiked());
                boolean liked = t.isLiked();
                likedHolder.favImg.setImageResource((!liked) ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_black_24dp);
                t.setLikes(t.getLikes() + ((liked) ? +1 : -1));
                likedHolder.NumOfLikes.setText("" + t.getLikes());
                break;
            }
        }
        likedHolder = null;
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: Error donant like a social.");
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
            ivPicture = itemView.findViewById(R.id.track_img_sa);
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
