package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.Playback;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.List;

public interface PlaybackCallback extends FailureCallback {

    void onPlaybacksByUserReceived(List<Playback> playbacks);
    void onPlaybacksByGenreReceived(List<Playback> playbacks, String genre);

    void onFailure(Throwable throwable);
}
