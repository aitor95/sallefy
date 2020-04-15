package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements UserCallback {

    public static final String TAG = LoginActivity.class.getName();

    private EditText etLogin;
    private EditText etPassword;
    private Button btnLogin;
    private boolean inAutomaticLogIn;


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
        }else{
            //El usuario vuelve con a login porque ha salido del home.
            //TODO: repair this
            //showLogin();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Uncomment this line to reset autologin data saved.
        //PreferenceUtils.resetValues(this);

        if(checkExistingPreferences()){
            showSplash();
        }else{
            //User credentials not available. Show login.
            showLogin();
        }
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
        btnLogin.setOnClickListener(v -> doLogin(etLogin.getText().toString(),
                etPassword.getText().toString()));

        Button signUpNow = findViewById(R.id.login_btn_SignUpNow);
        signUpNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
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

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUsersReceived(List<User> users) {

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

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onFailure: "  +throwable.getMessage());
    }
}
