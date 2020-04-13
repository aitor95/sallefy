package com.salle.android.sallefy.controller.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
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
import com.salle.android.sallefy.utils.OnSwipeListener;
import com.salle.android.sallefy.utils.Session;

import java.util.List;

public class MainActivity extends FragmentActivity implements FragmentCallback {

    public static final String TAG = MainActivity.class.getName();

    //Fragment management
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    //Android Back button control variables.
    private boolean backButtonPressed;
    private long backButtonLastPressTime;

    //XML elements
    private Button home;
    private Button social;
    private Button me;
    private ImageView search;

    //Gesture detector.
    private GestureDetectorCompat detector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backButtonLastPressTime = 0;
        backButtonPressed = false;
        initViews();

        detector = new GestureDetectorCompat(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                Log.d(TAG, "onSwipe:" + direction);
                return changeFragmentBasedOnDirection(direction);
            }
        });

        setInitialFragment();
        requestPermissions();
        //Saca las siguientes lineas si no quieres testear el reproductor
        //Intent intent = new Intent(this,MusicPlayerActivity.class);
        //startActivity(intent);
    }

    private void initViews() {
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();

        home = (Button) findViewById(R.id.action_home);
        social = (Button) findViewById(R.id.action_social);
        me = (Button) findViewById(R.id.action_me);
        search = (ImageView) findViewById(R.id.action_search);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterHomeFragment();
            }
        });


        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSocialFragment();
            }
        });


        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterMeFragment();
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSearchFragment();
            }
        });
    }

    private void enterHomeFragment() {
        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView_Active);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = HomeFragment.getInstance();
        replaceFragment(fragment);
    }

    private void enterSocialFragment() {
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView);
        social.setTextAppearance(R.style.BottomNavigationView_Active);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = SocialFragment.getInstance();
        replaceFragment(fragment);
    }

    private void enterMeFragment() {
        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView_Active);
        home.setTextAppearance(R.style.BottomNavigationView);
        search.setImageResource(R.drawable.ic_search);

        Fragment fragment = MeFragment.getInstance();
        replaceFragment(fragment);
    }

    private void enterSearchFragment() {
        search.setImageResource(R.drawable.ic_search_clicked);

        social.setTextAppearance(R.style.BottomNavigationView);
        me.setTextAppearance(R.style.BottomNavigationView);
        home.setTextAppearance(R.style.BottomNavigationView);

        Fragment fragment = SearchFragment.getInstance();
        replaceFragment(fragment);
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


    //Viable per a pocs fragments.
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
        if(fragment != null && fragment.isVisible())
            return fragment;
        }
        return null;
    }

    //Lugar oscuro de la app....
    private boolean changeFragmentBasedOnDirection(OnSwipeListener.Direction direction) {
        boolean rtn = false;
        Fragment fragment = getVisibleFragment();
        if (fragment instanceof HomeFragment) {

            if(direction == OnSwipeListener.Direction.left) {
                enterSocialFragment();
                rtn = true;
            }
        } else {
            if (fragment instanceof MeFragment) {

                if(direction == OnSwipeListener.Direction.right) {
                    enterSocialFragment();
                    rtn = true;
                } else if(direction == OnSwipeListener.Direction.left) {
                    enterSearchFragment();
                    rtn = true;
                }
            } else {
                if (fragment instanceof SocialFragment) {

                    if(direction == OnSwipeListener.Direction.right) {
                        enterHomeFragment();
                        rtn = true;
                    } else if(direction == OnSwipeListener.Direction.left) {
                        enterMeFragment();
                        rtn = true;
                    }
                } else {
                    if(direction == OnSwipeListener.Direction.right) {
                        enterMeFragment();
                        rtn = true;
                    }
                }
            }
        }
        return rtn;
    }
}


