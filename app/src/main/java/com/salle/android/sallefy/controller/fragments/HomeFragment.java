package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.PlaylistListHorizontalAdapter;
import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment implements  TrackCallback, PlaylistCallback {

    public static final String TAG = HomeFragment.class.getName();
    private RecyclerView rvPlaylists;
    private RecyclerView rvSongs;

    private PlaylistListHorizontalAdapter playlistsAdapter;
    private TrackListVerticalAdapter tracksAdapter;

    private ArrayList<Playlist> playlists;
    private ArrayList<Track> tracks;


    private static AdapterClickCallback adapterClickCallback;
    public static void setAdapterClickCallback(AdapterClickCallback callback){
        adapterClickCallback = callback;
    }


    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void initViews(View v) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        playlistsAdapter = new PlaylistListHorizontalAdapter(null, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        rvPlaylists = (RecyclerView) v.findViewById(R.id.home_new_playlists);
        rvPlaylists.setLayoutManager(manager);
        rvPlaylists.setAdapter(playlistsAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        tracksAdapter = new TrackListVerticalAdapter( adapterClickCallback, getContext(), null);

        rvSongs = (RecyclerView) v.findViewById(R.id.songs_home);
        rvSongs.setLayoutManager(manager2);
        rvSongs.setAdapter(tracksAdapter);

        TextView seeAllPlaylists = v.findViewById(R.id.SeeAllSearchedPlaylists);
        seeAllPlaylists.setOnClickListener(view -> {

            Fragment fragment = SeeAllPlaylistFragment.newInstance(playlists);
            SeeAllPlaylistFragment.setAdapterClickCallback(adapterClickCallback);

            FragmentManager manager_seeAll = getFragmentManager();
            FragmentTransaction transaction = manager_seeAll.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });

        TextView seeAllSongs = v.findViewById(R.id.SeeAllSearchedSongs);
        seeAllSongs.setOnClickListener(view -> {

            Fragment fragment = SeeAllSongFragment.newInstance(tracks);
            SeeAllSongFragment.setAdapterClickCallback(adapterClickCallback);
            SeeAllSongFragment.setPlaylist(new Playlist((ArrayList<Track>) tracks));
            FragmentManager manager_seeAll = getFragmentManager();
            FragmentTransaction transaction = manager_seeAll.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

//        v.findViewById(R.id.edit_playlist_nav).setOnClickListener(view -> {
//            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//        });
    }

    private void getData() {
        PlaylistManager.getInstance(getContext())
                .getListOfPlaylist(this);
        TrackManager.getInstance(getContext())
                .getAllTracks(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /**********************************************************************************************
     *   *   *   *   *   *   *   *   PlaylistCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/
    @Override
    public void onPlaylistById(Playlist playlist) {

    }

    @Override
    public void onPlaylistsByUser(ArrayList<Playlist> playlists) {

    }

    /**
     * Función que obtiene todas las playlist y las ordena por orden descendente
     * el numero de likes (followers) que tiene
     * @param playlists Array que contiene todas las playlists
     */
    @Override
    public void onAllList(ArrayList<Playlist> playlists) {
        playlists.sort(Comparator.comparing(Playlist::getFollowers).reversed());
        ArrayList<Playlist> topPlaylists = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            topPlaylists.add(playlists.get(i));
        }
        playlistsAdapter = new PlaylistListHorizontalAdapter(topPlaylists, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        rvPlaylists.setAdapter(playlistsAdapter);

        this.playlists = playlists;
    }

    @Override
    public void onFollowingList(ArrayList<Playlist> playlists) {

    }

    @Override
    public void onPlaylistUpdated() {

    }

    @Override
    public void onPlaylistCreated() {

    }

    @Override
    public void onUserFollows(Follow follows) {

    }

    @Override
    public void onUpdateFollow(Follow result) {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        tracks.sort(Comparator.comparing(Track::getLikes).reversed());
        ArrayList<Track> topTracks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            topTracks.add(tracks.get(i));
        }
        tracksAdapter = new TrackListVerticalAdapter(adapterClickCallback, getContext(), topTracks);
        tracksAdapter.setPlaylist(new Playlist((ArrayList<Track>) tracks));
        rvSongs.setAdapter(tracksAdapter);

        this.tracks = (ArrayList<Track>) tracks;
    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onCreateTrack() {

    }

    @Override
    public void onUpdatedTrack() {

    }

}
