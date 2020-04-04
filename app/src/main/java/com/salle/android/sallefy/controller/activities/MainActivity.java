package com.salle.android.sallefy.controller.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.salle.android.sallefy.R;
import com.salle.android.sallefy.controller.callbacks.FragmentCallback;
import com.salle.android.sallefy.controller.fragments.ContentFragment;
import com.salle.android.sallefy.controller.fragments.HomeFragment;
import com.salle.android.sallefy.controller.fragments.SearchFragment;
import com.salle.android.sallefy.controller.fragments.SongsFragment;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

public class MainActivity extends FragmentActivity implements FragmentCallback {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setInitialFragment();
        requestPermissions();
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

                Fragment fragment = SongsFragment.getInstance();
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

                Fragment fragment = SearchFragment.getInstance();
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

                Fragment fragment = ContentFragment.getInstance();
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
            if (fragment instanceof SongsFragment) {
                return SongsFragment.TAG;
            } else {
                if (fragment instanceof SearchFragment) {
                    return SearchFragment.TAG;
                } else {
                    return ContentFragment.TAG;
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


}
