package com.salle.android.sallefy.controller.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.model.Genre;

import java.util.ArrayList;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    public static final String TAG = GenresAdapter.class.getName();

    private ArrayList<Genre> mGenres;
    private int viewId;

    private AdapterClickCallback mCallback;

    public GenresAdapter(ArrayList<Genre> genres, AdapterClickCallback callback, int viewId) {
        mGenres = genres;
        this.viewId = viewId;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        return new GenresAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = mGenres.get(position);
        holder.tvName.setText(genre.getName());
        holder.tvName.setOnClickListener(view -> mCallback.onGenreClick(genre));
    }

    @Override
    public int getItemCount() {
        return (mGenres != null ? mGenres.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_genre);
        }
    }
}
