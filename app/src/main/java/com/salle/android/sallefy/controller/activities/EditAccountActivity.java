package com.salle.android.sallefy.controller.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.restapi.callback.UserCallback;
import com.salle.android.sallefy.controller.restapi.manager.UserManager;
import com.salle.android.sallefy.model.ChangePassword;
import com.salle.android.sallefy.model.User;
import com.salle.android.sallefy.model.UserPublicInfo;
import com.salle.android.sallefy.model.UserToken;
import com.salle.android.sallefy.utils.PreferenceUtils;
import com.salle.android.sallefy.utils.Session;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity implements UserCallback {
    public static final String TAG = EditAccountActivity.class.getName();

    private User mUser;
    private CircleImageView user_img;
    private TextInputLayout inputEmail;
    private TextInputLayout inputUsername;
    private TextInputLayout inputPassword;
    private ImageButton backoption;
    private Button btnSave;

    private String login;
    private String password;
    private String oldPassword;
    private ChangePassword mChangePasswords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        mUser = Session.getInstance(getApplicationContext()).getUser();
        mChangePasswords = new ChangePassword();
        initViews();
    }

    private void initViews() {
        initElements();

        Glide.with(
                getApplicationContext()
                        .getApplicationContext())
                .load(mUser.getImageUrl())
                .centerCrop()
                .override(400,400)
                .placeholder(R.drawable.user_default_image)
                .into(user_img);

        inputEmail.getEditText().setText(mUser.getEmail());
        inputUsername.getEditText().setText(mUser.getLogin());
        inputPassword.getEditText().setText(PreferenceUtils.getPassword(this));

        backoption.setOnClickListener(view -> finish());
        btnSave.setOnClickListener(view -> confirmInputs());
    }

    private void initElements() {
        backoption = findViewById(R.id.stats_back_btn);
        user_img = findViewById(R.id.user_img);
        inputEmail = findViewById(R.id.textInputEmail);
        inputUsername = findViewById(R.id.textInputUsername);
        inputPassword = findViewById(R.id.textInputPasswordEditAccount);
        btnSave = findViewById(R.id.edit_account_btn_action);
    }

    private boolean validateEmail() {
        String emailInput = inputEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()){
            inputEmail.setError("Email can't be empty");
            return false;
        } else {
            inputEmail.setError(null);
            return true;
        }

    }

    private boolean validateLogin() {
        String loginInput = inputUsername.getEditText().getText().toString().trim();

        if (loginInput.isEmpty()){
            inputUsername.setError("Username can't be empty");
            return false;
        } else {
            inputUsername.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {
        String passwordInput = inputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            inputPassword.setError("Password can't be empty");
            return false;
        } else {
            inputPassword.setError(null);
            return true;
        }
    }

    public void confirmInputs(){
        if (!validateEmail() | !validateLogin() | !validatePassword()){
            return;
        }
        updateUser();
    }

    private void updateUser() {
        login = inputUsername.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
        oldPassword = PreferenceUtils.getPassword(this);
        mUser.setEmail(inputEmail.getEditText().getText().toString());
        mUser.setLogin(login);

        PreferenceUtils.saveUser(this, login);

        // Añadimos las contraseñas al objeto para hacer el update en la API
        mChangePasswords.setCurrentPassword(oldPassword);
        mChangePasswords.setNewPassword(password);

        UserManager.getInstance(getApplicationContext()).updateProfile(mUser,  EditAccountActivity.this);
        UserManager.getInstance(getApplicationContext()).updatePassword(mChangePasswords, EditAccountActivity.this);

    }

    @Override
    public void onUsersReceived(List<User> users) {

    }

    @Override
    public void onUsersFailure(Throwable throwable) {

    }

    @Override
    public void onMeFollowingsReceived(List<User> users) {

    }


    @Override
    public void onIsFollowingResponseReceived(String login, Boolean isFollowed) {

    }

    @Override
    public void onUpdateUser(UserToken userToken) {
        Log.d(TAG, "onUpdateUser: USER UPDATED");
    }

    @Override
    public void onUpdatePassword(ChangePassword changePassword, UserToken userToken) {
        Log.d(TAG, "onUpdatePassword: PASSWORD UPDATED");
        //TODO: actualizar el token
        Session.getInstance(this).setUserToken(userToken);
        PreferenceUtils.savePassword(this,  password);
        finish();
    }


    @Override
    public void onMeFollowersReceived(List<UserPublicInfo> body) {

    }

    @Override
    public void onDeleteAccount() {

    }

    @Override
    public void onAllFollowingsFromUserReceived(List<User> users) {

    }

    @Override
    public void onAllFollowersFromUserReceived(List<User> users) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
