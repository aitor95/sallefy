package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.GenresAdapter;
import com.salle.android.sallefy.controller.adapters.PlaylistListHorizontalAdapter;
import com.salle.android.sallefy.controller.adapters.TrackListHorizontalAdapter;
import com.salle.android.sallefy.controller.adapters.UserHorizontalAdapter;
import com.salle.android.sallefy.controller.restapi.callback.GenreCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.SearchCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.GenreManager;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.SearchManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Search;
import com.salle.android.sallefy.model.SearchResult;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.ResponseBody;

public class SearchFragment extends Fragment implements PlaylistCallback, UserCallback, GenreCallback, TrackCallback, SearchCallback {

    public static final String TAG = SearchFragment.class.getName();

    private RecyclerView mUsersView;
    private UserHorizontalAdapter mUserHorizontalAdapter;

    private RecyclerView mPlaylistsView;
    private PlaylistListHorizontalAdapter mPlaylistAdapter;

    private RecyclerView mGenresView;
    private GenresAdapter mGenresAdapter;

    private RecyclerView mTracksView;
    private TrackListHorizontalAdapter mTracksAdapter;

    private TextView SeeAllTracks;
    private TextView SeeAllPlaylists;
    private TextView SeeAllUsers;
    private TextView SeeAllGenres;

    private EditText searchText;

    public static SearchFragment getInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(v);
        getData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initViews(View v) {
        LinearLayoutManager managerUsers = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mUserHorizontalAdapter = new UserHorizontalAdapter(null, getContext());
        mUsersView = (RecyclerView) v.findViewById(R.id.search_users_recyclerview);
        mUsersView.setLayoutManager(managerUsers);
        mUsersView.setAdapter(mUserHorizontalAdapter);

        SeeAllUsers = v.findViewById(R.id.SeeAllSearchedUsers);
        SeeAllUsers.setOnClickListener(view -> {
            //TODO: [USERS] Crear llistat de users
            Fragment fragment = SeeAllUserFragment.getInstance();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        LinearLayoutManager managerPlaylists = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mPlaylistAdapter = new PlaylistListHorizontalAdapter(null, getContext(), null, R.layout.item_playlist_horizontal);
        mPlaylistsView = (RecyclerView) v.findViewById(R.id.search_playlists_recyclerview);
        mPlaylistsView.setLayoutManager(managerPlaylists);
        mPlaylistsView.setAdapter(mPlaylistAdapter);

        SeeAllPlaylists = v.findViewById(R.id.SeeAllSearchedPlaylists);
        SeeAllPlaylists.setOnClickListener(view -> {
            //TODO: [PLAYLISTS] Crear llistat de playlists
            Fragment fragment = SeeAllPlaylistFragment.getInstance();
	        FragmentManager manager = getFragmentManager();
	        FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
	        transaction.add(R.id.fragment_container,fragment);
	        transaction.addToBackStack(null);
	        transaction.commit();
        });

        LinearLayoutManager managerGenres = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mGenresAdapter = new GenresAdapter(null, R.layout.item_genre);
        mGenresView = (RecyclerView) v.findViewById(R.id.search_genres_recyclerview);
        mGenresView.setLayoutManager(managerGenres);
        mGenresView.setAdapter(mGenresAdapter);

        SeeAllGenres = v.findViewById(R.id.SeeAllSearchedGenres);
        SeeAllGenres.setOnClickListener(view -> {
            //TODO: [GENRES] Afegir llistat de generes
        });


        LinearLayoutManager managerTracks = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mTracksAdapter = new TrackListHorizontalAdapter(null, null, null);
        mTracksView = (RecyclerView) v.findViewById(R.id.search_songs_recyclerview);
        mTracksView.setLayoutManager(managerTracks);
        mTracksView.setAdapter(mTracksAdapter);

        SeeAllTracks = v.findViewById(R.id.SeeAllSearchedSongs);
        SeeAllTracks.setOnClickListener(view -> {
            //TODO: [TRACKS] Afegir llistat de cançons
            Fragment fragment = SeeAllSongFragment.getInstance();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        searchText = v.findViewById(R.id.searchText);

        SearchCallback scallback = this;
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                ((EditText) view).setTextAppearance(R.style.SearchWritingText);
                String text = ((EditText) view).getText().toString();

                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    ((EditText) view).setText(text.replace("\n", ""));
                    if (text.equals("")) getData();
                    else SearchManager.getInstance(getContext()).search(((EditText) view).getText().toString(), scallback);
                }

                if (text.equals("")) getData();
                else SearchManager.getInstance(getContext()).search(((EditText) view).getText().toString(), scallback);

                //SearchManager.getInstance(getContext()).search(((EditText) view).getText().toString(), scallback);
                return false;
            }
        });

    }

    private void getData() {
        PlaylistManager.getInstance(getContext())
                .getListOfPlaylist(this);
        UserManager.getInstance(getContext())
                .getUsers(this);
        GenreManager.getInstance(getContext())
                .getAllGenres(this);
        TrackManager.getInstance(getContext())
                .getAllTracks(this);
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
        mPlaylistAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), null, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);
        //Toast.makeText(getContext(), "Playlists received", Toast.LENGTH_LONG).show();
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

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   UserCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onLoginSuccess(UserToken userToken) {

    }

    @Override
    public void onLoginFailure(Throwable throwable) {

    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onUsersReceived(List<User> users) {
        mUserHorizontalAdapter = new UserHorizontalAdapter((ArrayList<User>) users, getContext());
        mUsersView.setAdapter(mUserHorizontalAdapter);
    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onUserClicked(User user) {

    }

    @Override
    public void onMeFollowingsReceived(List<UserPublicInfo> users) {

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   GenreCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mGenresAdapter = new GenresAdapter(genres, R.layout.item_genre);
        mGenresView.setAdapter(mGenresAdapter);
    }

    @Override
    public void onTracksByGenre(ArrayList<Track> tracks) {

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   TrackListCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onTracksReceived(List<Track> tracks) {
        ArrayList<Track> mTracks = (ArrayList) tracks;
        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(null, getActivity(), mTracks);
        mTracksView.setAdapter(adapter);
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

    @Override
    public void onSearchResultReceived(SearchResult body) {
        ArrayList<Track> tracks = (ArrayList<Track>) body.getTracks();
        ArrayList<Playlist> playlists = (ArrayList<Playlist>) body.getPlaylists();
        ArrayList<User> users = (ArrayList<User>) body.getUsers();
        ArrayList<Genre> genres = (ArrayList<Genre>) body.getGenres();

        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(null, getActivity(), tracks);
        mTracksView.setAdapter(adapter);

        mPlaylistAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), null, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);

        mGenresAdapter = new GenresAdapter(genres, R.layout.item_genre);
        mGenresView.setAdapter(mGenresAdapter);

        mUserHorizontalAdapter = new UserHorizontalAdapter(users, getContext());
        mUsersView.setAdapter(mUserHorizontalAdapter);
    }
}
