package com.example.n_fitness.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.parse.ParseUser;

/**
 * Generic Fragment class that other fragments in this project can extend from
 */
public class GenericFragment extends Fragment {

    public GenericFragment() {
        // Required empty public constructor
    }

    public void switchFragment(Fragment fragment) {
        if (getContext() == null)
            return;
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.loadFragment(R.id.flContainer, fragment);
    }

    public void goUserFragment(ParseUser user) {
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        userFragment.setArguments(bundle);
        switchFragment(userFragment);
    }

    public static void goUserFragment(ParseUser user, Activity activity) {
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        userFragment.setArguments(bundle);
        if (activity instanceof MainActivity) {
            switchFragment(userFragment, (MainActivity) activity);
        }

    }

    public static void switchFragment(Fragment fragment, Activity activity) {
        if (activity == null)
            return;
        loadFragment(R.id.flContainer, fragment, activity);
    }

    private static void loadFragment(int id, Fragment fragment, Activity activity) {
        FragmentTransaction ft;
        if (activity instanceof MainActivity) {
            ft = ((MainActivity) activity).getSupportFragmentManager().beginTransaction();
        }

        else {
            Toast.makeText(activity, "Could not load fragment", Toast.LENGTH_SHORT).show();
            return;
        }

        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }
}