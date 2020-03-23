package com.salle.android.sallefy.controller.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements UserCallback {

    private EditText etLogin;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        checkSavedData();
    }

    private void initViews() {

        etLogin = (EditText) findViewById(R.id.login_user);
        etPassword = (EditText) findViewById(R.id.login_password);

        btnLogin = (Button) findViewById(R.id.login_btn_action);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(etLogin.getText().toString(),
                        etPassword.getText().toString());
            }
        });
    }

    private void doLogin(String username, String password) {
        UserManager.getInstance(getApplicationContext())
                .loginAttempt(username, password, LoginActivity.this);
    }

    private void checkSavedData() {
        if (checkExistingPreferences()) {
            etLogin.setText(PreferenceUtils.getUser(this));
            etPassword.setText(PreferenceUtils.getPassword(this));
        }
    }

    private boolean checkExistingPreferences () {
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
        Session.getInstance(getApplicationContext())
                .setUserToken(userToken);
        PreferenceUtils.saveUser(this, etLogin.getText().toString());
        PreferenceUtils.savePassword(this, etPassword.getText().toString());
        UserManager.getInstance(this).getUserData(etLogin.getText().toString(), this);
    }

    @Override
    public void onLoginFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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
    public void onFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();

    }
}
