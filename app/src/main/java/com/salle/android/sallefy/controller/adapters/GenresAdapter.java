package com.salle.android.sallefy.controller.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.model.Genre;

import java.util.ArrayList;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    public static final String TAG = GenresAdapter.class.getName();

    private ArrayList<Genre> mGenres;
    private int viewId;

    //public GenresAdapter(ArrayList<> genres) {
    //    mGenres = genres;
    //}

    public GenresAdapter(ArrayList<Genre> genres, int viewId) {
        mGenres = genres;
        this.viewId = viewId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        return new GenresAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(mGenres.get(position).getName());
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
            tvName.setOnClickListener(view -> {
                //TODO: [GENRE] Anar a la llista de cançons del genere
            });
        }
    }
}
