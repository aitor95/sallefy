package com.salle.android.sallefy.controller.adapters;

import android.content.Context;
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
import com.salle.android.sallefy.model.Playlist;

import java.util.ArrayList;
import java.util.HashMap;


public class AddToPlayListAdapter extends RecyclerView.Adapter<AddToPlayListAdapter.ViewHolder> {


    private static final String TAG = "AddToPlaylistAdapter";
    private ArrayList<Playlist> mPlaylists;
    private HashMap<Integer, Integer> mSelectedPlaylists;

    private Context mContext;

    public AddToPlayListAdapter(Context context, ArrayList<Playlist> playlists, HashMap<Integer, Integer> selectedPlaylists) {
        mContext = context;
        mPlaylists = playlists;
        mSelectedPlaylists = selectedPlaylists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_to_playlist, parent, false);
        Log.d(TAG, "onCreateViewHolder: Created!");
        return new ViewHolder(itemView);
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Playlist playlist = mPlaylists.get(position);

        if(mSelectedPlaylists.get(playlist.getId()) != null){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        holder.mLayout.setOnClickListener(v -> {
            holder.checkBox.toggle();
            if (holder.checkBox.isChecked()) {
                mSelectedPlaylists.put(holder.id, holder.id);
            } else {
                mSelectedPlaylists.remove(holder.id);
            }
        });

        holder.title.setText(playlist.getName());
        holder.author.setText(playlist.getUserLogin());
        holder.id = playlist.getId();

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) {
                    mSelectedPlaylists.put(holder.id, holder.id);
                } else {
                    mSelectedPlaylists.remove(holder.id);
                }
            }
        });


        //TODO: Change placeholder.
        if (playlist.getThumbnail() != null) {
            Glide.with(mContext)
                    .asBitmap()
                    .placeholder(R.drawable.ic_audiotrack)
                    .load(playlist.getThumbnail())
                    .into(holder.photo);
        }

        //Es la ultima playlist? Significa que és la playlist para crear de nuevas.
        if (position == mPlaylists.size() - 1) {
            Log.d(TAG, "onBindViewHolder: WOW SOY POSITION "+position  + " DE: "+ mPlaylists.size());
            holder.title.setText(R.string.add_to_playlist_lastitem_title);
            holder.author.setText(R.string.add_to_playlist_lastitem_author);
            holder.photo.setImageResource(R.drawable.ic_chooseimage);

            holder.checkBox.setButtonDrawable(R.drawable.ic_checkbox_add_16dp);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPlaylist();
                }
            });

            holder.mLayout.setOnClickListener(v -> {
                createPlaylist();
            });
        }else{
            holder.checkBox.setButtonDrawable(R.drawable.checkbox_selector);
        }

    }

    void createPlaylist(){
        //Todo: Crear una nueva playlist.
        Log.d(TAG, "createPlaylist: Creating playlist...");
    }

    @Override
    public int getItemCount() {
        return mPlaylists != null ? mPlaylists.size() : 0;
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
