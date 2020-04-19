package com.salle.android.sallefy.controller.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.activities.UploadSongActivity;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.Constants;

import java.util.ArrayList;


public class AddSongsToPlayListAdapter extends RecyclerView.Adapter<AddSongsToPlayListAdapter.ViewHolder> {


    private static final String TAG = "AddSongsToPlaylistAdapter";
    private ArrayList<Track> mTracks;
    private ArrayList<Track> mSelectedTracks;



    private Context mContext;

    public AddSongsToPlayListAdapter(Context context, ArrayList<Track> tracks, ArrayList<Track> selectedTracks) {
        mContext = context;
        mTracks = tracks;
        mSelectedTracks = selectedTracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_to_playlist, parent, false);
        Log.d(TAG, "onCreateViewHolder: Created!");
        return new ViewHolder(itemView);
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Track track = mTracks.get(position);

        if(position != 0){
            holder.mLayout.setOnClickListener(v -> {
                holder.checkBox.toggle();
                if (holder.checkBox.isChecked()) {
                    mSelectedTracks.add(track);
                    holder.checkBox.setChecked(true);

                } else {
                    mSelectedTracks.remove(track);
                    holder.checkBox.setChecked(false);

                }
            });
            holder.title.setText(track.getName());
            holder.author.setText(track.getUserLogin());
            holder.id = track.getId();

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
                        mSelectedTracks.add(track);
                    } else {
                        mSelectedTracks.remove(track);
                    }
                }
            });

            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(track.getThumbnail())
                    .into(holder.photo);

            holder.checkBox.setButtonDrawable(R.drawable.checkbox_selector);


        }else{
            holder.title.setText(track.getName());
            holder.author.setText(R.string.add_songs_to_playlist_lastitem_author);
            Glide
                    .with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(R.drawable.ic_audiotrack)
                    .into(holder.photo);

            holder.checkBox.setButtonDrawable(R.drawable.ic_checkbox_add_16dp);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createTrack();
                }
            });

            holder.mLayout.setOnClickListener(v -> {
                createTrack();
            });
        }

    }

    void createTrack( ){
        Intent intent = new Intent(mContext, UploadSongActivity.class);
        ((Activity)mContext).startActivityForResult(intent, Constants.EDIT_CONTENT.TRACK_EDIT);
    }




    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout mLayout;
        CheckBox checkBox;
        TextView title;
        TextView author;
        ImageView photo;
        int id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.item_add_to_playlist_layout);
            checkBox = itemView.findViewById(R.id.item_add_to_playlist_checkbox);
            title = itemView.findViewById(R.id.item_add_to_playlist_title);
            author = itemView.findViewById(R.id.item_add_to_playlist_author);
            photo = itemView.findViewById(R.id.item_add_to_playlist_photo);
        }
    }
}
