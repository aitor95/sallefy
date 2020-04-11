package com.salle.android.sallefy.controller.activities;

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

import java.util.List;

public class SignUpActivity extends AppCompatActivity implements UserCallback {

    public static final String TAG = SignUpActivity.class.getName();

    private EditText userName;
    private EditText password;
    private EditText email;
    private EditText repeatPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        attachButtons();
    }

    void attachButtons(){
        Button signUp   = findViewById(R.id.signUp_btn_action);
        Button logInNow = findViewById(R.id.login_btn_SignUpNow);

        userName = (EditText)findViewById(R.id.signup_user);
        password = (EditText)findViewById(R.id.signup_password);

        email = (EditText)findViewById(R.id.signup_email);
        repeatPassword = (EditText)findViewById(R.id.signup_repeat_password);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the password matches.
                if(password.getText().toString().equals(repeatPassword.getText().toString())){

                    UserManager.getInstance(getApplicationContext())
                            .registerAttempt(email.getText().toString(),
                                    userName.getText().toString(),
                                    password.getText().toString(),
                                    SignUpActivity.this);
                }else{
                    //TODO: Add here some fancy error.
                    Toast.makeText(SignUpActivity.this, "Passwords don't match",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        logInNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onLoginSuccess(UserToken userToken) {}
    @Override
    public void onLoginFailure(Throwable throwable) {}

    @Override
    public void onRegisterSuccess() {
        //Se guardan las creedenciales y se vuelve al login.
        PreferenceUtils.saveUser(this, userName.getText().toString());
        PreferenceUtils.savePassword(this, password.getText().toString());
        //Finish para inicial el metodo onResume
        finish();
    }

    @Override
    public void onRegisterFailure(Throwable throwable) {
        Log.d(TAG, "onRegisterFailure:  "+throwable.getMessage());
        Toast.makeText(getApplicationContext(), "Username is not available.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onUsersReceived(List<User> users) {

    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onUserClicked(User user) {

    }

    @Override
    public void onMeFollowingsReceived(List<UserPublicInfo> users) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
