package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;

import java.util.List;

public interface UserCallback extends FailureCallback {
    void onLoginSuccess(UserToken userToken);
    void onLoginFailure(Throwable throwable);
    void onRegisterSuccess();
    void onRegisterFailure(Throwable throwable);
    void onUserInfoReceived(User userData);
    void onUsersReceived(List<User> users);
    void onUsersFailure(Throwable throwable);
    void onUserClicked(User user);
    void onMeFollowingsReceived(List<UserPublicInfo> users);
    void onIsFollowingResponseReceived(String login, Boolean isFollowed);
}
