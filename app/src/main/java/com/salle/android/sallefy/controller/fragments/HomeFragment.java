package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.PlaylistListHorizontalAdapter;
import com.salle.android.sallefy.controller.adapters.TrackListVerticalAdapter;
import com.salle.android.sallefy.controller.callbacks.PlaylistAdapterCallback;
import com.salle.android.sallefy.controller.callbacks.TrackListCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class HomeFragment extends Fragment implements PlaylistAdapterCallback, PlaylistCallback, TrackListCallback, TrackCallback {

    public static final String TAG = HomeFragment.class.getName();
    private RecyclerView rvPlaylists;
    private RecyclerView rvSongs;

    private PlaylistListHorizontalAdapter playlistsAdapter;
    private TrackListVerticalAdapter tracksAdapter;

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
        playlistsAdapter = new PlaylistListHorizontalAdapter(null, getContext(), this, R.layout.item_playlist_horizontal);
        rvPlaylists = (RecyclerView) v.findViewById(R.id.home_new_playlists);
        rvPlaylists.setLayoutManager(manager);
        rvPlaylists.setAdapter(playlistsAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        tracksAdapter = new TrackListVerticalAdapter( null, getContext(), null);
        rvSongs = (RecyclerView) v.findViewById(R.id.songs_home);
        rvSongs.setLayoutManager(manager2);
        rvSongs.setAdapter(tracksAdapter);
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

    @Override
    public void onPlaylistClick(Playlist playlist) {

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

    @Override
    public void onAllList(ArrayList<Playlist> playlists) {
        playlistsAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), this, R.layout.item_playlist_horizontal);
        rvPlaylists.setAdapter(playlistsAdapter);
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
    public void onUserFollows(ResponseBody follows) {

    }

    @Override
    public void onUpdateFollow(ResponseBody result) {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTrackSelected(Track track) {

    }

    @Override
    public void onTrackSelected(int index) {

    }

    @Override
    public void onTrackUpdated(Track track) {
        TrackManager.getInstance(getContext()).updateTrack(track, this);
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        tracksAdapter = new TrackListVerticalAdapter(this, getContext(), (ArrayList<Track>) tracks);
        rvSongs.setAdapter(tracksAdapter);
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
