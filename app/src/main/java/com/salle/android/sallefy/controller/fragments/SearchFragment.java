package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
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
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
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
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Genre;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.SearchResult;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchFragment extends Fragment implements PlaylistCallback, UserCallback, GenreCallback, TrackCallback, SearchCallback {

    public static final String TAG = SearchFragment.class.getName();
    public static final int ACTIVE_WRITING_TIMEOUT = 500;

    private RecyclerView mUsersView;
    private UserHorizontalAdapter mUserHorizontalAdapter;

    private RecyclerView mPlaylistsView;
    private PlaylistListHorizontalAdapter mPlaylistAdapter;

    private RecyclerView mGenresView;
    private GenresAdapter mGenresAdapter;

    private RecyclerView mTracksView;

    private ArrayList<Playlist> playlists;
    private ArrayList<Track> tracks;
    private ArrayList<User> users;

    private Timer searchTimer;

    private static AdapterClickCallback adapterClickCallback;
    public static void setAdapterClickCallback(AdapterClickCallback callback){
        adapterClickCallback = callback;
    }

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

        playlists = new ArrayList<>();
        tracks = new ArrayList<>();
        users = new ArrayList<>();

        searchTimer = new Timer();

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
        mUserHorizontalAdapter = new UserHorizontalAdapter(null,adapterClickCallback, getContext());
        mUsersView = (RecyclerView) v.findViewById(R.id.search_users_recyclerview);
        mUsersView.setLayoutManager(managerUsers);
        mUsersView.setAdapter(mUserHorizontalAdapter);

        TextView seeAllUsers = v.findViewById(R.id.SeeAllSearchedUsers);
        seeAllUsers.setOnClickListener(view -> {
            Fragment fragment = SeeAllUserFragment.newInstance(users);
            FragmentManager manager = getFragmentManager();
            SeeAllUserFragment.setAdapterClickCallback(adapterClickCallback);
            assert manager != null;
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        LinearLayoutManager managerPlaylists = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mPlaylistAdapter = new PlaylistListHorizontalAdapter(null, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        mPlaylistsView = (RecyclerView) v.findViewById(R.id.search_playlists_recyclerview);
        mPlaylistsView.setLayoutManager(managerPlaylists);
        mPlaylistsView.setAdapter(mPlaylistAdapter);

        TextView seeAllPlaylists = v.findViewById(R.id.SeeAllSearchedPlaylists);
        seeAllPlaylists.setOnClickListener(view -> {
            Fragment fragment = SeeAllPlaylistFragment.newInstance(playlists);
	        FragmentManager manager = getFragmentManager();

            SeeAllPlaylistFragment.setAdapterClickCallback(adapterClickCallback);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
	        transaction.add(R.id.fragment_container,fragment);
	        transaction.addToBackStack(null);
	        transaction.commit();

        });


        LinearLayoutManager managerGenres = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mGenresAdapter = new GenresAdapter(null, adapterClickCallback, R.layout.item_genre);
        mGenresView = (RecyclerView) v.findViewById(R.id.search_genres_recyclerview);
        mGenresView.setLayoutManager(managerGenres);
        mGenresView.setAdapter(mGenresAdapter);


        LinearLayoutManager managerTracks = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        TrackListHorizontalAdapter mTracksAdapter = new TrackListHorizontalAdapter(adapterClickCallback, getContext(), null);
        mTracksView = (RecyclerView) v.findViewById(R.id.search_songs_recyclerview);
        mTracksView.setLayoutManager(managerTracks);
        mTracksView.setAdapter(mTracksAdapter);

        TextView seeAllTracks = v.findViewById(R.id.SeeAllSearchedSongs);
        seeAllTracks.setOnClickListener(view -> {
            Fragment fragment = SeeAllSongFragment.newInstance(tracks);
            FragmentManager manager = getFragmentManager();

            SeeAllSongFragment.setAdapterClickCallback(adapterClickCallback);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        EditText searchText = v.findViewById(R.id.searchText);

        SearchCallback scallback = this;
        searchText.setOnKeyListener((view, i, keyEvent) -> {
            ((EditText) view).setTextAppearance(R.style.SearchWritingText);
            String text = ((EditText) view).getText().toString();

            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                ((EditText) view).setText(text.replace("\n", ""));
                if (text.equals("")) getData();
                else SearchManager.getInstance(getContext()).search(((EditText) view).getText().toString(), scallback);
            }

            searchTimer.cancel();
            searchTimer.purge();

            searchTimer = new Timer();
            searchTimer.schedule(new SearchPerformer(((EditText) view).getText().toString(), scallback), ACTIVE_WRITING_TIMEOUT);

            //if (text.equals("")) getData();
            //else SearchManager.getInstance(getContext()).search(((EditText) view).getText().toString(), scallback);

            //SearchManager.getInstance(getContext()).search(((EditText) view).getText().toString(), scallback);
            return false;
        });

    }

    class SearchPerformer extends TimerTask {

        String searchText;
        SearchCallback searchCallback;

        public SearchPerformer(String searchText, SearchCallback searchCallback) {
            this.searchText = searchText;
            this.searchCallback = searchCallback;
        }

        @Override
        public void run() {
            if (searchText.equals("")) getData();
            else SearchManager.getInstance(getContext()).search(searchText, searchCallback);
        }
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
        mPlaylistAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);
        //Toast.makeText(getContext(), "Playlists received", Toast.LENGTH_LONG).show();

        this.playlists = (ArrayList<Playlist>) playlists;
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
        mUserHorizontalAdapter = new UserHorizontalAdapter((ArrayList<User>) users,adapterClickCallback, getContext());
        mUsersView.setAdapter(mUserHorizontalAdapter);

        this.users = (ArrayList<User>) users;
    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onMeFollowingsReceived(List<UserPublicInfo> users) {

    }

    @Override
    public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   GenreCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mGenresAdapter = new GenresAdapter(genres,adapterClickCallback, R.layout.item_genre);
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
        ArrayList<Track> mTracks = (ArrayList<Track>) tracks;
        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(adapterClickCallback, getActivity(), mTracks);
        mTracksView.setAdapter(adapter);

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
    public void onCreateTrack(Track track) {

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

        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(adapterClickCallback, getActivity(), tracks);
        mTracksView.setAdapter(adapter);
        this.tracks = tracks;

        mPlaylistAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);
        this.playlists = playlists;

        mGenresAdapter = new GenresAdapter(genres,adapterClickCallback, R.layout.item_genre);
        mGenresView.setAdapter(mGenresAdapter);

        mUserHorizontalAdapter = new UserHorizontalAdapter(users,adapterClickCallback, getContext());
        mUsersView.setAdapter(mUserHorizontalAdapter);
        this.users = users;
    }
}
