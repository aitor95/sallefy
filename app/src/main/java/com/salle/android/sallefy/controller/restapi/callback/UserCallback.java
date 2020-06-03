package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.List;

public interface UserCallback extends FailureCallback {

    void onUsersReceived(List<User> users);
    void onUsersFailure(Throwable throwable);
    void onMeFollowingsReceived(List<User> users);
    void onIsFollowingResponseReceived(String login, Boolean isFollowed);
    void onUpdateUser(UserToken userToken);
    void onUpdatePassword(ChangePassword changePassword, UserToken userToken);
    void onMeFollowersReceived(List<UserPublicInfo> body);
    void onDeleteAccount();

    void onAllFollowingsFromUserReceived(List<User> users);

    void onAllFollowersFromUserReceived(List<User> users);
}
