package com.example.n_fitness.fragments;

import androidx.fragment.app.Fragment;

import com.example.n_fitness.activities.MainActivity;

/**
 * Generic Fragment class that other fragments in this project can extend from
 */
public class GenericFragment extends Fragment {

    public GenericFragment() {
        // Required empty public constructor
    }

    public void switchFragment(int id, Fragment fragment) {
        if (getContext() == null)
            return;
        if (getContext() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getContext();
            mainActivity.loadFragment(id, fragment);
        }
    }
}