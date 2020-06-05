package com.salle.android.sallefy.controller.fragments;

import android.os.Bundle;
import android.os.Parcelable;
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
import com.salle.android.sallefy.controller.adapters.PlaylistListHorizontalAdapter;
import com.salle.android.sallefy.controller.adapters.TrackListHorizontalAdapter;
import com.salle.android.sallefy.controller.adapters.UserHorizontalAdapter;
import com.salle.android.sallefy.controller.callbacks.AdapterClickCallback;
import com.salle.android.sallefy.controller.callbacks.SeeAllCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.SearchCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.SearchManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.SearchResult;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.UpdatableFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchFragment extends Fragment implements PlaylistCallback, UserCallback, TrackCallback, SearchCallback, SeeAllCallback, UpdatableFragment {

    public static final String TAG = SearchFragment.class.getName();

    private static final int NUMBER_OF_PLAYLISTS = 10;
    private static final int NUMBER_OF_SONGS = 10;
    private static final int NUMBER_OF_USERS = 12;

    public static final int ACTIVE_WRITING_TIMEOUT = 500;

    private RecyclerView mUsersView;
    private UserHorizontalAdapter mUserHorizontalAdapter;

    private RecyclerView mPlaylistsView;
    private PlaylistListHorizontalAdapter mPlaylistAdapter;

    private RecyclerView mTracksView;

    private ArrayList<Playlist> mPlaylists;
    private ArrayList<Track> mTracks;
    private ArrayList<User> users;

    private Timer searchTimer;

    private EditText searchText;

    private SeeAllPlaylistFragment mSeeAllPlaylistFragment;
    private SeeAllSongFragment mSeeAllSongFragment;

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

        mPlaylists = new ArrayList<>();
        mTracks = new ArrayList<>();
        users = new ArrayList<>();

        searchTimer = new Timer();

        return v;
    }

    private void initViews(View v) {
        LinearLayoutManager managerUsers = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mUserHorizontalAdapter = new UserHorizontalAdapter(null,adapterClickCallback, getContext());
        mUsersView = (RecyclerView) v.findViewById(R.id.search_users_recyclerview);
        mUsersView.setLayoutManager(managerUsers);
        mUsersView.setAdapter(mUserHorizontalAdapter);

        TextView seeAllUsers = v.findViewById(R.id.SeeAllSearchedUsers);
        seeAllUsers.setOnClickListener(view -> {
            SeeAllUserFragment fragment = SeeAllUserFragment.newInstance(users);
            //TODO: API BUG: THIS IS A API BUG
            fragment.setNumber(NUMBER_OF_USERS - 2);
            FragmentManager manager = getFragmentManager();
            SeeAllUserFragment.setAdapterClickCallback(adapterClickCallback);

            //Search text enable/disable system
            SeeAllUserFragment.setSeeAllCallback(SearchFragment.this);
            searchText.setEnabled(false);

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
            mSeeAllPlaylistFragment = SeeAllPlaylistFragment.newInstance(mPlaylists, false);
            mSeeAllPlaylistFragment.setNumber(NUMBER_OF_PLAYLISTS);
	        FragmentManager manager = getFragmentManager();

            SeeAllPlaylistFragment.setAdapterClickCallback(adapterClickCallback);

            //Search text enable/disable system
            SeeAllPlaylistFragment.setSeeAllCallback(SearchFragment.this);
            searchText.setEnabled(false);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
	        transaction.add(R.id.fragment_container,mSeeAllPlaylistFragment);
	        transaction.addToBackStack(null);
	        transaction.commit();
        });


        LinearLayoutManager managerTracks = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        TrackListHorizontalAdapter mTracksAdapter = new TrackListHorizontalAdapter(adapterClickCallback, getContext(), null);
        mTracksView = (RecyclerView) v.findViewById(R.id.search_songs_recyclerview);
        mTracksView.setLayoutManager(managerTracks);
        mTracksView.setAdapter(mTracksAdapter);

        TextView seeAllTracks = v.findViewById(R.id.SeeAllSearchedSongs);
        seeAllTracks.setOnClickListener(view -> {
            mSeeAllSongFragment = SeeAllSongFragment.newInstance(mTracks, false);
            mSeeAllSongFragment.setNumber(NUMBER_OF_SONGS);
            FragmentManager manager = getFragmentManager();

            SeeAllSongFragment.setAdapterClickCallback(adapterClickCallback);

            //Search text enable/disable system
            SeeAllSongFragment.setSeeAllCallback(SearchFragment.this);
            searchText.setEnabled(false);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(R.id.fragment_container,mSeeAllSongFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        searchText = v.findViewById(R.id.searchText);

        SearchCallback scallback = this;

        searchText.setOnKeyListener((view, i, keyEvent) -> {
            ((EditText) view).setTextAppearance(R.style.SearchWritingText);
            String text = ((EditText) view).getText().toString();

            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                EditText t = ((EditText) view);
                t.setText(text.replace("\n", ""));
                if (text.equals("")) getData();
                else SearchManager.getInstance(getContext()).search(t.getText().toString(), scallback);
                t.setSelection(t.getText().length());
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

    @Override
    public void onSeeAllClosed() {
        searchText.setEnabled(true);
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


        Parcelable parcelable = mTracksView.getLayoutManager().onSaveInstanceState();

        //Update the adapter.
        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(adapterClickCallback, getActivity(), mTracks);
        mTracksView.setAdapter(adapter);

        mTracksView.getLayoutManager().onRestoreInstanceState(parcelable);

        if(mSeeAllSongFragment != null)
            ((SeeAllSongFragment)(mSeeAllSongFragment)).reloadItems(mTracks);
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
            if(a == NUMBER_OF_PLAYLISTS)break;
            HoritzontalPlaylist.add(mPlaylists.get(a));
        }

        Parcelable parcelable = mPlaylistsView.getLayoutManager().onSaveInstanceState();

        mPlaylistAdapter = new PlaylistListHorizontalAdapter(HoritzontalPlaylist, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);

        mPlaylistsView.getLayoutManager().onRestoreInstanceState(parcelable);

        if(mSeeAllPlaylistFragment != null)
            ((SeeAllPlaylistFragment)(mSeeAllPlaylistFragment)).reloadItems(mPlaylists);

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
                .getListOfPlaylistPagination(this, 0, NUMBER_OF_PLAYLISTS, false);

        UserManager.getInstance(getContext())
                .getUsersPagination(this, 0, NUMBER_OF_USERS);

        TrackManager.getInstance(getContext())
                .getAllTracksPagination(this, 0, NUMBER_OF_SONGS, false, false);
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

    @Override
    public void onAllList(ArrayList<Playlist> playlists) {
        Log.d(TAG, "onAllList: Got playlists " + playlists.size());
        mPlaylistAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);
        //Toast.makeText(getContext(), "Playlists received", Toast.LENGTH_LONG).show();

        this.mPlaylists = (ArrayList<Playlist>) playlists;
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
    public void onPopularPlaylistsReceived(List<Playlist> playlists) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    /**********************************************************************************************
     *   *   *   *   *   *   *   *   UserCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onUsersReceived(List<User> users) {
        Log.d(TAG, "onUsersReceived: Got users " + users.size());
        mUserHorizontalAdapter = new UserHorizontalAdapter((ArrayList<User>) users,adapterClickCallback, getContext());
        mUsersView.setAdapter(mUserHorizontalAdapter);

        this.users = (ArrayList<User>) users;
    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onMeFollowingsReceived(List<User> users) {

    }

    @Override
    public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

    }

    @Override
    public void onUpdateUser() {

    }

    @Override
    public void onUpdatePassword() {

    }

    @Override
    public void onMeFollowersReceived(List<UserPublicInfo> body) {

    }

    @Override
    public void onDeleteAccount() {

    }

    @Override
    public void onAllFollowingsFromUserReceived(List<User> users) {

    }

    @Override
    public void onAllFollowersFromUserReceived(List<User> users) {

    }


    /**********************************************************************************************
     *   *   *   *   *   *   *   *   TrackListCallback   *   *   *   *   *   *   *   *   *
     **********************************************************************************************/

    @Override
    public void onTracksReceived(List<Track> tracks) {
        Log.d(TAG, "onTracksReceived: Got tracks " + tracks.size());
        ArrayList<Track> mTracks = (ArrayList<Track>) tracks;
        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(adapterClickCallback, getActivity(), mTracks);
        mTracksView.setAdapter(adapter);

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
    public void onPopularTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onSearchResultReceived(SearchResult body) {
        Log.d(TAG, "onSearchResultReceived: GOT THE RESULTS");
        ArrayList<Track> tracks = (ArrayList<Track>) body.getTracks();
        ArrayList<Playlist> playlists = (ArrayList<Playlist>) body.getPlaylists();
        ArrayList<User> users = (ArrayList<User>) body.getUsers();

        TrackListHorizontalAdapter adapter = new TrackListHorizontalAdapter(adapterClickCallback, getActivity(), tracks);
        mTracksView.setAdapter(adapter);
        this.mTracks = tracks;

        ///TODO: CHANGE THIS: THIS IS A BUG, it can really be null and crash the app! !!!!!!
        TextView tvSeeAllTracks = getView().findViewById(R.id.SeeAllSearchedSongs);
        if(this.mTracks.isEmpty()){
            tvSeeAllTracks.setVisibility(View.INVISIBLE);
        }else{
            tvSeeAllTracks.setVisibility(View.VISIBLE);
        }

        mPlaylistAdapter = new PlaylistListHorizontalAdapter(playlists, getContext(), adapterClickCallback, R.layout.item_playlist_horizontal);
        mPlaylistsView.setAdapter(mPlaylistAdapter);
        this.mPlaylists = playlists;

        TextView tvSeeAllPlaylists = getView().findViewById(R.id.SeeAllSearchedPlaylists);
        if(this.mPlaylists.isEmpty()){
            tvSeeAllPlaylists.setVisibility(View.INVISIBLE);
        }else{
            tvSeeAllPlaylists.setVisibility(View.VISIBLE);
        }

        mUserHorizontalAdapter = new UserHorizontalAdapter(users,adapterClickCallback, getContext());
        mUsersView.setAdapter(mUserHorizontalAdapter);
        this.users = users;

        TextView tvSeeAllUsers = getView().findViewById(R.id.SeeAllSearchedUsers);
        if(this.users.isEmpty()){
            tvSeeAllUsers.setVisibility(View.INVISIBLE);
        }else{
            tvSeeAllUsers.setVisibility(View.VISIBLE);
        }
    }
}
