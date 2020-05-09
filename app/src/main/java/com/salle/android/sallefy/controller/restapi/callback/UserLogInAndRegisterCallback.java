package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserToken;

public interface UserLogInAndRegisterCallback extends FailureCallback{
    void onUserInfoReceived(User userData);
    void onLoginSuccess(UserToken userToken);
    void onLoginFailure(Throwable throwable);
    void onRegisterSuccess();
    void onRegisterFailure(Throwable throwable);
}
