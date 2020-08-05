package com.example.n_fitness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.n_fitness.R;
import com.example.n_fitness.adapters.FriendsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing friends list from profile fragment
 */
public class FriendsFragment extends GenericFragment {

    private static final String TAG = "FriendsFragment";
    private FriendsAdapter adapter;
    private List<ParseUser> friends;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvFriends = view.findViewById(R.id.rvFriends);

        friends = new ArrayList<>();
        adapter = new FriendsAdapter(getContext(), friends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.addItemDecoration(new DividerItemDecoration(rvFriends.getContext(), DividerItemDecoration.VERTICAL));
        queryFriends();
    }

    private void queryFriends() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting friends", Toast.LENGTH_SHORT).show();
                    return;
                }

                friends.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

    }
}