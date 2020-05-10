package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;

import java.util.List;

public interface UserCallback extends FailureCallback {

    void onUsersReceived(List<User> users);
    void onUsersFailure(Throwable throwable);
    void onMeFollowingsReceived(List<UserPublicInfo> users);
    void onIsFollowingResponseReceived(String login, Boolean isFollowed);
    void onUpdateUser();

    void onMeFollowersReceived(List<UserPublicInfo> body);
}
