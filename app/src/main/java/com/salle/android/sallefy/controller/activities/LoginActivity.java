package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.UserLogInAndRegisterCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.controller.download.ObjectBox;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.util.List;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements UserLogInAndRegisterCallback {

    public static final String TAG = LoginActivity.class.getName();

    private EditText etLogin;
    private EditText etPassword;
    private Button btnLogin;
    private boolean inAutomaticLogIn;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;

    //Shared links
    private int sharedId = -1;
    private String sharedUserId = null;
    private int sharedType = -1;
    private static final int USER = 0;
    private static final int PLAYLIST = 1;
    private static final int TRACK = 2;


    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(!inAutomaticLogIn){
            //Estar aqui dentro significa que hemos vuelto de SignUpActivity.
            //Miramos si hay nuevas creedenciales guardadas, como en @onCreate

            if(checkExistingPreferences()){
                //Bingo, el usuario se acaba de crear una cuenta. Vamos a logearle
                showSplash();
            }else{
                //Ignora, el usuario ha vuelto sin hacer login
            }
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            List<String> data = appLinkData.getPathSegments();
            switch (data.get(0)){
                case "user":
                    sharedUserId = appLinkData.getLastPathSegment();
                    sharedType = USER;
                    break;
                case "playlist":
                    sharedId = Integer.parseInt(appLinkData.getLastPathSegment());
                    sharedType = PLAYLIST;
                    break;
                case "track":
                    sharedId = Integer.parseInt(appLinkData.getLastPathSegment());
                    sharedType = TRACK;
                    break;
            }
           /* playlistId = Integer.parseInt(appLinkData.getLastPathSegment());
            System.out.println(playlistId);

            */

            /*Uri appData = Uri.parse("http://sallefy.eu-west-3.elasticbeanstalk.com/playlist/").buildUpon()
                    .appendPath(playlistId).build();*/

        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectBox.getInstance().init(this);
        ObjectBox.getInstance().createBoxes();
        //Uncomment this line to reset autologin data saved.
        //PreferenceUtils.resetValues(this);

        if (checkExistingPreferences()) {
            showSplash();
        } else {
            //User credentials not available. Show login.
            showLogin();
        }

        handleIntent(getIntent());

    }

    private void showSplash(){
        //Show the splashScreen
        setContentView(R.layout.splash_screen);

        //User and password are available. Try them.
        doLogin(PreferenceUtils.getUser(this),PreferenceUtils.getPassword(this));
        inAutomaticLogIn = true;
    }

    private void showLogin(){
        setContentView(R.layout.activity_login);

        //Buttons.
        initViews();
        inAutomaticLogIn = false;
    }

    private void initViews() {

        etLogin = findViewById(R.id.login_user);
        etPassword = findViewById(R.id.login_password);

        btnLogin = findViewById(R.id.login_btn_action);
        btnLogin.setOnClickListener(v -> confirmInput());

        textInputUsername = findViewById(R.id.textInputUsername);
        textInputPassword = findViewById(R.id.textInputPassword);

        Button signUpNow = findViewById(R.id.login_btn_SignUpNow);
        signUpNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateUsername() {
        String usernameInput = textInputUsername.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()){
            textInputUsername.setError("Username can't be empty");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Password can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    public void confirmInput(){
        if (!validateUsername() | !validatePassword()){
            return;
        }
            doLogin(etLogin.getText().toString(),
                    etPassword.getText().toString());

    }

    private void doLogin(String username, String password) {
        UserManager.getInstance(getApplicationContext())
                .loginAttempt(username, password, LoginActivity.this);
    }

    private boolean checkExistingPreferences () {
        //return false;
        return PreferenceUtils.getUser(this) != null
                && PreferenceUtils.getPassword(this) != null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onLoginSuccess(UserToken userToken) {
        String user;
        String pass;

        if(inAutomaticLogIn){
            user = PreferenceUtils.getUser(this);
            pass = PreferenceUtils.getPassword(this);
        }else{
            user = etLogin.getText().toString();
            pass = etPassword.getText().toString();
        }

        Session.getInstance(getApplicationContext()).setUserToken(userToken);
        PreferenceUtils.saveUser(this, user);
        PreferenceUtils.savePassword(this, pass);
        UserManager.getInstance(this).getUserData(user, this);
    }

    @Override
    public void onLoginFailure(Throwable throwable) {
        if(inAutomaticLogIn){
            Toast.makeText(getApplicationContext(), "Automatic login failed", Toast.LENGTH_LONG).show();
            showLogin();
        }else{
            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {
        Session.getInstance(getApplicationContext())
                .setUser(userData);

        if (sharedId != -1 || sharedUserId != null) {
            switch (sharedType){
                case USER:
                    Intent userIntent = new Intent(this, MainActivity.class);
                    userIntent.putExtra(Constants.INTENT_EXTRAS.USER_ID, sharedUserId);
                    startActivity(userIntent);
                    break;
                case PLAYLIST:
                    Intent playlistIntent = new Intent(this, PlaylistActivity.class);
                    playlistIntent.putExtra(Constants.INTENT_EXTRAS.PLAYLIST_ID, sharedId);
                    startActivity(playlistIntent);
                    break;
                case TRACK:
                    Intent trackIntent = new Intent(this, MainActivity.class);
                    trackIntent.putExtra(Constants.INTENT_EXTRAS.TRACK_ID, sharedId);
                    startActivity(trackIntent);
                    break;
            }


        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onFailure: "  + throwable.getMessage() + Arrays.toString(throwable.getStackTrace()));
    }
}
