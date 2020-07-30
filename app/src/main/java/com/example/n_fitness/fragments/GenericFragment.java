package com.example.n_fitness.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    public static void goUserFragment(ParseUser user, MainActivity activity) {
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        userFragment.setArguments(bundle);
        switchFragment(userFragment, activity);
    }

    public static void switchFragment(Fragment fragment, MainActivity activity) {
        if (activity == null)
            return;
        activity.loadFragment(R.id.flContainer, fragment);
    }
}