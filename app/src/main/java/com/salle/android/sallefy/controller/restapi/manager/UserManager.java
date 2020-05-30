package com.salle.android.sallefy.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserFollowCallback;
import com.salle.android.sallefy.controller.restapi.callback.UserLogInAndRegisterCallback;
import com.salle.android.sallefy.controller.restapi.service.UserService;
import com.salle.android.sallefy.controller.restapi.service.UserTokenService;
import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.Follow;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserLogin;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserRegister;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserManager {

    private static final String TAG = "UserManager";

    private static UserManager sUserManager;
    private Retrofit mRetrofit;
    private Context mContext;

    private UserService mService;
    private UserTokenService mTokenService;

    public static UserManager getInstance(Context context) {
        if (sUserManager == null) {
            sUserManager = new UserManager(context);
        }
        return sUserManager;
    }

    private UserManager(Context cntxt) {
        mContext = cntxt;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(UserService.class);
        mTokenService = mRetrofit.create(UserTokenService.class);

    }


    /********************   LOGIN    ********************/
    public synchronized void loginAttempt (String username, String password, final UserLogInAndRegisterCallback userCallback) {

        Call<UserToken> call = mTokenService.loginUser(new UserLogin(username, password, true));

        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {

                int code = response.code();
                UserToken userToken = response.body();

                if (response.isSuccessful()) {
                    userCallback.onLoginSuccess(userToken);
                } else {
                    Log.d(TAG, "Error: " + code);
                    userCallback.onLoginFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(t);
            }
        });
    }


    /********************   USER INFO    ********************/
    public synchronized void getUserData (String login, final UserLogInAndRegisterCallback userCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<User> call = mService.getUserById(login, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onUserInfoReceived(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(new Throwable("ERROR " + Arrays.toString(t.getStackTrace())));
            }
        });
    }

    /********************   REGISTRATION    ********************/
    public synchronized void registerAttempt (String email, String username, String password, final UserLogInAndRegisterCallback userCallback) {

        Call<ResponseBody> call = mService.registerUser(new UserRegister(email, username, password));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onRegisterSuccess();
                } else {
                    try{
                        assert response.errorBody() != null;
                        userCallback.onRegisterFailure(new Throwable("ERROR " + code + ", " + response.errorBody().string()));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }

    /********************   UPDATE PROFILE    ********************/
    public synchronized void updateProfile(User user, final UserCallback userCallback){
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<UserToken> call = mService.updateProfile(user, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                int code = response.code();

                if (response.isSuccessful()){
                    UserToken userToken = response.body();
                    userCallback.onUpdateUser(userToken);

                } else {
                    Log.d(TAG, "Can't upload profile " + code);
                    userCallback.onFailure(new Throwable("Error " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + Arrays.toString(t.getStackTrace()));
                userCallback.onFailure(new Throwable("ERROR " + Arrays.toString(t.getStackTrace())));
            }
        });
    }

    /********************   UPDATE PASSWORD    ********************/
    public synchronized void updatePassword(ChangePassword changePassword, final UserCallback userCallback){
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<UserToken> call = mService.updatePassword(changePassword, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                int code = response.code();

                if (response.isSuccessful()){
                    UserToken userToken = response.body();

                    userCallback.onUpdatePassword(changePassword, userToken);

                } else {
                    Log.d(TAG, "Can't upload profile " + code);
                    userCallback.onFailure(new Throwable("Error " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + Arrays.toString(t.getStackTrace()));
                userCallback.onFailure(new Throwable("ERROR " + Arrays.toString(t.getStackTrace())));
            }
        });
    }

    public synchronized void setFollowing(String userLogin, Boolean following, final UserFollowCallback userFollowCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<ResponseBody> call = mService.followUser(userLogin, following, "Bearer " + userToken.getIdToken());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userFollowCallback.onFollowUnfollowSuccess(userLogin);
                } else {
                    try {
                        userFollowCallback.onFailure(new Throwable("ERROR " + code + ", " + response.errorBody().string()));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                userFollowCallback.onFailure(t);
            }
        });
    }

    public synchronized void getFollowing(String userLogin, int position, final UserCallback userCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<Follow> call = mService.isFollowingUser(userLogin, "Bearer " + userToken.getIdToken());

        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                int code = response.code();

                if (response.isSuccessful()) {

                        userCallback.onIsFollowingResponseReceived(userLogin, response.body().getFollow());

                } else {
                    try {

                        userCallback.onFailure(new Throwable("ERROR " + code + ", " + response.errorBody().string()));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }

    /********************   ALL USERS    ********************/

    public synchronized void getUsersPagination (final UserCallback userCallback, int currentPage, int size) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<User>> call = mService.getAllUsersPagination( "Bearer " + userToken.getIdToken(), currentPage, size);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userCallback.onUsersReceived(response.body());
                } else {
                    userCallback.onUsersFailure(new Throwable());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }

   /* public synchronized void getUsersFragmentMe (final UserCallback userCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<User>> call = mService.getAllUsersMeFragment( "Bearer " + userToken.getIdToken(), 1000);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userCallback.onUsersReceived(response.body());
                } else {
                    userCallback.onUsersFailure(new Throwable());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }*/


    /********************   GETTERS / SETTERS    ********************/
    public synchronized void getMeFollowing (final UserCallback userCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<User>> call = mService.getMeFollowings( "Bearer " + userToken.getIdToken());

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if (response.isSuccessful()) {
                    userCallback.onMeFollowingsReceived(response.body());
                } else {
                    userCallback.onUsersFailure(new Throwable());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }

    public synchronized void getMeFollowers (final UserCallback userCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<UserPublicInfo>> call = mService.getMeFollowers( "Bearer " + userToken.getIdToken());

        call.enqueue(new Callback<List<UserPublicInfo>>() {
            @Override
            public void onResponse(Call<List<UserPublicInfo>> call, Response<List<UserPublicInfo>> response) {

                if (response.isSuccessful()) {
                    userCallback.onMeFollowersReceived(response.body());
                } else {
                    userCallback.onUsersFailure(new Throwable());
                }
            }

            @Override
            public void onFailure(Call<List<UserPublicInfo>> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }
}
