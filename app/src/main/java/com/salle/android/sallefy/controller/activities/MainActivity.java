package com.salle.android.sallefy.controller.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.FragmentCallback;
import com.salle.android.sallefy.controller.fragments.HomeFragment;
import com.salle.android.sallefy.controller.fragments.MeFragment;
import com.salle.android.sallefy.controller.fragments.SearchFragment;
import com.salle.android.sallefy.controller.fragments.SocialFragment;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

public class MainActivity extends FragmentActivity implements FragmentCallback {

    public static final String TAG = MainActivity.class.getName();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    private boolean backButtonPressed;
    private long backButtonLastPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backButtonLastPressTime = 0;
        backButtonPressed = false;
        initViews();
        setInitialFragment();
        requestPermissions();
        //Saca las siguientes lineas si no quieres testear el reproductor
//        Intent intent = new Intent(this,MusicPlayerActivity.class);
//        startActivity(intent);
    }

    private void initViews() {
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();

        Button home = (Button) findViewById(R.id.action_home);
        Button social = (Button) findViewById(R.id.action_social);
        Button me = (Button) findViewById(R.id.action_me);
        ImageView search = (ImageView) findViewById(R.id.action_search);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                social.setTextAppearance(R.style.BottomNavigationView);
                me.setTextAppearance(R.style.BottomNavigationView);
                home.setTextAppearance(R.style.BottomNavigationView_Active);
                search.setImageResource(R.drawable.ic_search);

                Fragment fragment = HomeFragment.getInstance();
                replaceFragment(fragment);
            }
        });


        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                me.setTextAppearance(R.style.BottomNavigationView);
                home.setTextAppearance(R.style.BottomNavigationView);
                social.setTextAppearance(R.style.BottomNavigationView_Active);
                search.setImageResource(R.drawable.ic_search);

                //Fragment fragment = SongsFragment.getInstance();
                Fragment fragment = SocialFragment.getInstance();
                replaceFragment(fragment);
            }
        });


        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                social.setTextAppearance(R.style.BottomNavigationView);
                me.setTextAppearance(R.style.BottomNavigationView_Active);
                home.setTextAppearance(R.style.BottomNavigationView);
                search.setImageResource(R.drawable.ic_search);

                Fragment fragment = MeFragment.getInstance();
                replaceFragment(fragment);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setImageResource(R.drawable.ic_search_clicked);

                social.setTextAppearance(R.style.BottomNavigationView);
                me.setTextAppearance(R.style.BottomNavigationView);
                home.setTextAppearance(R.style.BottomNavigationView);

                Fragment fragment = SearchFragment.getInstance();
                replaceFragment(fragment);
            }
        });
    }

    private void setInitialFragment() {
        mTransaction.add(R.id.fragment_container, HomeFragment.getInstance());
        mTransaction.commit();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS}, Constants.PERMISSIONS.MICROPHONE);

        } else {
            Session.getInstance(this).setAudioEnabled(true);
        }
    }

    private void replaceFragment(Fragment fragment) {
        String fragmentTag = getFragmentTag(fragment);
        Fragment currentFragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (currentFragment != null) {
            if (!currentFragment.isVisible()) {

                if (fragment.getArguments() != null) {
                    currentFragment.setArguments(fragment.getArguments());
                }
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, currentFragment, fragmentTag)
                        .addToBackStack(null)
                        .commit();

            }
        } else {
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragmentTag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private String getFragmentTag(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            return HomeFragment.TAG;
        } else {
            if (fragment instanceof MeFragment) {
                return MeFragment.TAG;
            } else {
                if (fragment instanceof SocialFragment) {
                    return SocialFragment.TAG;
                } else {
                    return SearchFragment.TAG;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(requestCode);
        if (requestCode == Constants.PERMISSIONS.MICROPHONE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Session.getInstance(this).setAudioEnabled(true);
            } else {

            }
            return;
        }
    }

    @Override
    public void onChangeFragment(Fragment fragment) {
        replaceFragment(fragment);

    }

    /***
     * Al apretar el boton de atras de android, no queremos salir de la main activity.
     * Si el usuario aprieta back 2 veces en menos de 2 segundos se sale de la app.
     */
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - backButtonLastPressTime > 2000){
            //Han pasado 2 segundos.
            backButtonPressed = false;
            backButtonLastPressTime = System.currentTimeMillis();
        }

        if(backButtonPressed) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonPressed = true;
        }
    }
}
