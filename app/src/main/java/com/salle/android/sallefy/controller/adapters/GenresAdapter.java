package com.salle.android.sallefy.controller.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;

import java.util.ArrayList;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    public static final String TAG = GenresAdapter.class.getName();

    private ArrayList<String> mGenres;

    public GenresAdapter(ArrayList<String> genres) {
        mGenres = genres;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new GenresAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(mGenres.get(position));
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
