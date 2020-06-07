package com.salle.android.sallefy.controller.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.adapters.GenresAdapter;
import com.salle.android.sallefy.controller.restapi.callback.PlaybackCallback;
import com.salle.android.sallefy.controller.restapi.callback.PlaylistCallback;
import com.salle.android.sallefy.controller.restapi.callback.TrackCallback;
import com.salle.android.sallefy.controller.restapi.manager.PlaybackManager;
import com.salle.android.sallefy.controller.restapi.manager.PlaylistManager;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.Playback;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;
import com.salle.android.sallefy.utils.GraphView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity implements PlaybackCallback, TrackCallback, PlaylistCallback {

    private PlaybackManager playbackManager;
    private TrackManager trackManager;
    private PlaylistManager playlistManager;
    private GraphView graphView;

    private PopupMenu graphsMenu;

    private boolean barmode;

    public final int TOP_SIZE = 10;

    private Map<String, List<Playback>> playbacksByGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        playbackManager = PlaybackManager.getInstance(getApplicationContext());
        trackManager = TrackManager.getInstance(getApplicationContext());
        playlistManager = PlaylistManager.getInstance(getApplicationContext());

        barmode = false;

        ImageButton backoption = findViewById(R.id.stats_back_btn);
        backoption.setOnClickListener(view -> {
            finish();
        });

        Button chooseGraphButton = findViewById(R.id.choose_graph_button);
        chooseGraphButton.setOnClickListener(view -> {
            graphsMenu.show();
        });

        graphsMenu = new PopupMenu(this, chooseGraphButton);
        graphsMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Your most reproduced tracks");
        graphsMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Top liked tracks");
        graphsMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Top popular playlists");
        graphsMenu.getMenu().add(Menu.NONE, 4, Menu.NONE, "Your activity");
        ((TextView) findViewById(R.id.graph_title)).setText("");

        graphsMenu.setOnMenuItemClickListener(item -> {
            ((TextView) findViewById(R.id.graph_title)).setText(item.getTitle());
            graphView.loaded();
            switch (item.getItemId()) {
                case 1:
                    barmode = true;
                    playbackManager.getPlaybacksByUser(this);
                    break;
                case 2:
                    trackManager.getTopTracks(10, this);
                    break;
                case 3:
                    playlistManager.getTopPlaylists(10, this);
                    break;
                case 4:
                    barmode = false;
                    playbackManager.getPlaybacksByUser(this);
                    break;
            }
            return false;
        });

        graphView = findViewById(R.id.stats_graph);
    }

    @Override
    public void onPlaybacksByUserReceived(List<Playback> playbacks) {
        if (!barmode) {
            Collections.sort(playbacks);

            String[] labels = generateLabelList(playbacks);
            int[][] values = calculatePlaybacksPerWeek(playbacks, labels);

            graphView.setType(1);
            graphView.setLabels(labels);
            graphView.setTimeGraphValues(values);
            graphView.setLabels("Day", "Reproductions/week");
            graphView.resetView();
        } else {
            LinkedList<Track> tracks = getMostPlayedTracks(playbacks);
            int size = Math.min(5, tracks.size());

            String[] labels = new String[size];
            int[] values = new int[size];

            for (int i = 0; i < size; i++) {
                values[i] = tracks.get(i).getReproductions();
                labels[i] = tracks.get(i).getName();
            }

            graphView.setType(2);
            graphView.setLabels(labels);
            graphView.setBarGraphValues(values);
            graphView.setLabels("Reproductions", "Most played songs");
            graphView.resetView();
        }
    }

    private LinkedList<Track> getMostPlayedTracks(List<Playback> playbacks) {
        LinkedList<Track> tracks = new LinkedList<>();
        for (Playback p: playbacks) {
            boolean exist = false;
            for (Track t: tracks) if (t.getId().equals(p.getTrack().getId())) {
                exist = true;
                t.setReproductions(t.getReproductions() +1);
            }

            if (!exist) {
                p.getTrack().setReproductions(1);
                tracks.add(p.getTrack());
            }
        }

        Collections.sort(tracks);
        return tracks;
    }

    @Override
    public void onPlaybacksByGenreReceived(List<Playback> playbacks, String genre) {
        Collections.sort(playbacks);

        String[] labels = generateLabelList(playbacks);
        int[][] values = calculatePlaybacksPerWeek(playbacks, labels);

        graphView.setType(1);
        graphView.setLabels(labels);
        graphView.setTimeGraphValues(values);
        graphView.setLabels("Day", "Reproductions/week");
        graphView.resetView();
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.d("TAGG", "ERROOR");
        Log.d("TAGG", throwable.getMessage());
    }

    private int getDateIndex(String time, String ref) {
        String date = time.split("T")[0];
        String[] values = date.split("-");

        String dateRef = ref.split("T")[0];
        String[] valuesRef = dateRef.split("-");

        int month = Integer.parseInt(values[1]);
        int day = Integer.parseInt(values[2]);

        int monthRef = Integer.parseInt(valuesRef[1]);
        int dayRef = Integer.parseInt(valuesRef[2]);

        int daysPassed = 31 * (month - monthRef) + ((month == monthRef) ? day - dayRef : (31 - dayRef) + day);

        return Math.floorDiv(daysPassed, 7);
    }

    private String getDateLabel(String time) {
        String date = time.split("T")[0];
        String[] values = date.split("-");

        int month = Integer.parseInt(values[1]);
        int day = Integer.parseInt(values[2]);

        return month + "-" + ((int) (day / 7f))*7;
    }

    private int getMaxValue(LinkedList<Integer> values){
        int v = values.get(0);
        for (int i: values) if (v < i) v = i;
        return v;
    }

    private String[] generateLabelList(List<Playback> playbacks) {
        int size = getDateIndex(playbacks.get(playbacks.size()-1).getTime(), playbacks.get(0).getTime()) + 1;

        String[] labels = new String[size];

        String date = playbacks.get(0).getTime().split("T")[0];
        String[] values = date.split("-");

        int month = Integer.parseInt(values[1]);
        int day = Integer.parseInt(values[2]);

        for (int i = 0; i < size; i++) {
            labels[i] = month + "-" + day;
            day += 7;
            if (day >= 31) {
                day -= 31;
                month += 1;
            }
        }

        return labels;
    }

    private int[][] calculatePlaybacksPerWeek(List<Playback> playbacks, String[] labels) {
        int[][] data;
        data = new int[1][labels.length];
        for (Playback p: playbacks) data[0][getDateIndex(p.getTime(), playbacks.get(0).getTime())] += 1;
        return data;
    }

    private void groupPlaybacksByWeeks(List<Playback> playbacks, LinkedList<Integer> values, LinkedList<String> labels) {
        for (Playback p: playbacks) {
            if (values.size() == 0) {
                labels.add(getDateLabel(p.getTime()));
                values.add(1);
            } else {
                for (int i = getMaxValue(values); i < getDateIndex(p.getTime(), playbacks.get(0).getTime()); i++) {
                    labels.add(getDateLabel(p.getTime()));
                    values.add(0);
                }

                values.set(getDateIndex(p.getTime(), playbacks.get(0).getTime()), values.get(getDateIndex(p.getTime(), playbacks.get(0).getTime())) +1);
            }
        }
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onTrackById(Track track) {

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
    public void onTrackDeleted(Track track) {

    }

    @Override
    public void onPopularTracksReceived(List<Track> tracks) {
        String[] labels = new String[TOP_SIZE];
        int[] values = new int[TOP_SIZE];

        for (int i = 0; i < TOP_SIZE; i++) {
            Track t = tracks.get(i);
            labels[i] = (i + 1) + ". " + t.getName();
            values[i] = t.getLikes();
        }

        graphView.setType(2);
        graphView.setLabels(labels);
        graphView.setBarGraphValues(values);
        graphView.setLabels("Num of likes", "Most liked tracks");
        graphView.resetView();
    }

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
        String[] labels = new String[TOP_SIZE];
        int[] values = new int[TOP_SIZE];

        for (int i = 0; i < TOP_SIZE; i++) {
            Playlist t = playlists.get(i);
            labels[i] = (i + 1) + ". " + t.getName();
            values[i] = t.getFollowers();
        }

        graphView.setType(2);
        graphView.setLabels(labels);
        graphView.setBarGraphValues(values);
        graphView.setLabels("Num of followers", "Most popular playlists");
        graphView.resetView();
    }
}
