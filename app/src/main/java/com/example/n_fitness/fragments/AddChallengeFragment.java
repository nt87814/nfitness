package com.example.n_fitness.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.n_fitness.ContactChip;
import com.example.n_fitness.R;

import java.util.ArrayList;
import java.util.List;

public class AddChallengeFragment extends DialogFragment {

    public AddChallengeFragment() {
        // Required empty public constructor
    }

    public static AddChallengeFragment newInstance(String title) {
        AddChallengeFragment frag = new AddChallengeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_challenge, container, false);
    }
}