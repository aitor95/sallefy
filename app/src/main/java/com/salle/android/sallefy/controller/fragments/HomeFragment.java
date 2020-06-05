package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.os.Parcelable;
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
import com.salle.android.sallefy.utils.UpdatableFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements  TrackCallback, PlaylistCallback, UpdatableFragment {

    public static final String TAG = HomeFragment.class.getName();

    private static final boolean popularSongs = true;
    private static final boolean recentSongs = true;

    private static final boolean popularPlaylists = true;
    private static final int NUMBER_OF_PLAYLISTS = 10;
    private static final int NUMBER_OF_SONGS = 10;


    private RecyclerView rvPlaylists;
    private RecyclerView rvSongs;

    private PlaylistListHorizontalAdapter playlistsAdapter;
    private TrackListVerticalAdapter tracksAdapter;

    private ArrayList<Playlist> mPlaylists;
    private ArrayList<Track> mTracks;


    private static AdapterClickCallback adapterClickCallback;
    private SeeAllSongFragment mSeeAllSongFragment;
    private SeeAllPlaylistFragment mSeeAllPlaylistFragment;

    public static void setAdapterClickCallback(AdapterClickCallback callback){
        adapterClickCallback = callback;
    }

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(v);
        return v;
    }

    private void initViews(View v) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        playlistsAdapter = new PlaylistListHorizontalAdapter(null, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        rvPlaylists = (RecyclerView) v.findViewById(R.id.home_new_playlists);
        rvPlaylists.setLayoutManager(manager);
        rvPlaylists.setAdapter(playlistsAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        tracksAdapter = new TrackListVerticalAdapter( adapterClickCallback, getContext(), getFragmentManager(), null);

        rvSongs = (RecyclerView) v.findViewById(R.id.songs_home);
        rvSongs.setLayoutManager(manager2);
        rvSongs.setAdapter(tracksAdapter);

        TextView seeAllPlaylists = v.findViewById(R.id.SeeAllSearchedPlaylists);
        seeAllPlaylists.setOnClickListener(view -> {

            mSeeAllPlaylistFragment = SeeAllPlaylistFragment.newInstance(mPlaylists, popularPlaylists);
            SeeAllPlaylistFragment.setAdapterClickCallback(adapterClickCallback);

            FragmentManager manager_seeAll = getFragmentManager();
            FragmentTransaction transaction = manager_seeAll.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container, mSeeAllPlaylistFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        TextView seeAllSongs = v.findViewById(R.id.SeeAllSearchedSongs);
        seeAllSongs.setOnClickListener(view -> {

            mSeeAllSongFragment = SeeAllSongFragment.newInstance(mTracks, popularSongs);
            SeeAllSongFragment.setAdapterClickCallback(adapterClickCallback);
            SeeAllSongFragment.setPlaylist(new Playlist((ArrayList<Track>) mTracks));
            FragmentManager manager_seeAll = getFragmentManager();
            FragmentTransaction transaction = manager_seeAll.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,mSeeAllSongFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

//        v.findViewById(R.id.edit_playlist_nav).setOnClickListener(view -> {
//            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//        });
    }

    private void getData() {
        PlaylistManager.getInstance(getContext())
                .getListOfPlaylistPagination(this, 0, NUMBER_OF_PLAYLISTS, popularPlaylists);
        TrackManager.getInstance(getContext())
                .getAllTracksPagination(this, 0, NUMBER_OF_SONGS, recentSongs, popularSongs);
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

    @Override
    public void onOwnList(ArrayList<Playlist> playlists) {

    }

    /**
     * Función que obtiene todas las playlist y las ordena por orden descendente
     * el numero de likes (followers) que tiene
     * @param playlists Array que contiene todas las playlists
     */
    @Override
    public void onAllList(ArrayList<Playlist> playlists) {
        this.mPlaylists = playlists;
        playlistsAdapter = new PlaylistListHorizontalAdapter(mPlaylists, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        rvPlaylists.setAdapter(playlistsAdapter);
    }

    @Override
    public void onFollowingList(ArrayList<Playlist> playlists) {

    }

    @Override
    public void onPlaylistUpdated() {

    }

    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onUserFollows(Follow follows) {

    }

    @Override
    public void onUpdateFollow(Follow result) {

    }

    @Override
    public void onPlaylistDeleted() {

    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        tracksAdapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity().getApplicationContext(), getFragmentManager(), (ArrayList<Track>) tracks);
        tracksAdapter.setPlaylist(new Playlist((ArrayList<Track>) tracks));
        rvSongs.setAdapter(tracksAdapter);

        this.mTracks = (ArrayList<Track>) tracks;
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
    public void onCreateTrack(Track track) {

    }

    @Override
    public void onUpdatedTrack() {

    }

    @Override
    public void onTrackDeleted() {

    }

    @Override
    public void updateSongInfo(Track track) {
        //Update with new info.
        for (int i = 0; i < mTracks.size(); i++) {
            if(mTracks.get(i).getId().intValue() == track.getId().intValue()){
                mTracks.set(i, track);
            }
        }

        //Remove if necessary.
        mTracks.removeIf(Track::isDeleted);

        tracksAdapter = new TrackListVerticalAdapter(adapterClickCallback, getActivity().getApplicationContext(), getFragmentManager(), (ArrayList<Track>) mTracks);
        tracksAdapter.setPlaylist(new Playlist((ArrayList<Track>) mTracks));
        rvSongs.setAdapter(tracksAdapter);


        if(mSeeAllSongFragment != null)
            mSeeAllSongFragment.reloadItems(mTracks);
    }

    @Override
    public void updatePlaylistInfo(ArrayList<Playlist> playlists) {
        boolean found;

        for (int i = 0; i < playlists.size(); i++) {
            found = false;
            for (int j = 0; j < mPlaylists.size(); j++) {
                if (mPlaylists.get(j).getId().intValue() == playlists.get(i).getId().intValue()) {
                    mPlaylists.set(j, playlists.get(i));
                    found = true;
                }
            }
            if(!found) mPlaylists.add(playlists.get(i));
        }

        mPlaylists.removeIf(Playlist::isDeleted);

        ArrayList<Playlist> HoritzontalPlaylist = new ArrayList<>();
        int a = 0;
        for(int i = mPlaylists.size() - 1; i >= 0; i--,a++){
            if(a == NUMBER_OF_PLAYLISTS) break;
            HoritzontalPlaylist.add(mPlaylists.get(a));
        }

        Parcelable parcelable = rvPlaylists.getLayoutManager().onSaveInstanceState();

        playlistsAdapter = new PlaylistListHorizontalAdapter(HoritzontalPlaylist, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        rvPlaylists.setAdapter(playlistsAdapter);

        rvPlaylists.getLayoutManager().onRestoreInstanceState(parcelable);

        if(mSeeAllPlaylistFragment != null)
            mSeeAllPlaylistFragment.reloadItems(mPlaylists);
    }

}
