package com.example.n_fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.n_fitness.R;
import com.example.n_fitness.fragments.ChallengesFragment;
import com.example.n_fitness.fragments.ComposeFragment;
import com.example.n_fitness.fragments.DetailsFragment;
import com.example.n_fitness.fragments.ExploreFragment;
import com.example.n_fitness.fragments.FriendsFragment;
import com.example.n_fitness.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity for bottom navigation view
 * <p>
 * This activity is used to open different fragments from the bottom navigation view
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static FloatingActionButton btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        btnCreate = findViewById(R.id.btnCreate);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;

                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new ChallengesFragment();
                        break;
                    case R.id.action_explore:
                        fragment = new ExploreFragment();
                        break;
                    case R.id.action_map:
                        Intent i = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.flContainer, fragment);
                ft.commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ComposeFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    public void loadFragment(int id, Fragment fragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public static void setBtnCreateVisibility(Fragment fragment) {
        if (fragment instanceof DetailsFragment || fragment instanceof FriendsFragment || fragment instanceof ComposeFragment) {
            btnCreate.setVisibility(View.GONE);
        } else {
            btnCreate.setVisibility(View.VISIBLE);
        }
    }
}