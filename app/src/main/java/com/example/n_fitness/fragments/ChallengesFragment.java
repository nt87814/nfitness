package com.example.n_fitness.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.n_fitness.R;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static final String TAG = "PostsFragment";
    protected ChallengesAdapter adapter;
    protected List<Challenge> allChallenges;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    public static ChallengesFragment newInstance(String param1, String param2) {
        ChallengesFragment fragment = new ChallengesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvChallenges = view.findViewById(R.id.rvChallenges);

        allChallenges = new ArrayList<>();
        adapter = new ChallengesAdapter(getContext(), allChallenges);
        rvChallenges.setAdapter(adapter);
        rvChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_DEADLINE);
        query.whereEqualTo(Challenge.KEY_REC, ParseUser.getCurrentUser());
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Challenge>() {
            @Override
            public void done(List<Challenge> challenges, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting challenges", e);
                    return;
                }

                for (Challenge challenge : challenges) {
                    Post post = challenge.getPost();
                    Log.i(TAG, "Challenges: " + post.getDescription());
                }
                adapter.clear();
                adapter.addAll(challenges);
            }
        });
    }
}