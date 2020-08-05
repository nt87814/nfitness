package com.example.n_fitness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Challenge;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fragment for list of challenges the current user has
 */
public class ChallengesFragment extends Fragment {

    private static final String TAG = "ChallengesFragment";
    protected ChallengesAdapter adapter;
    protected List<Challenge> currentChallenges;
    private List<Challenge> pastChallenges;
    private Date current;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.setBtnCreateVisibility(this);
        RecyclerView rvChallenges = view.findViewById(R.id.rvChallenges);
        current = new Date();

        currentChallenges = new ArrayList<>();
        pastChallenges = new ArrayList<>();
        adapter = new ChallengesAdapter(getContext(), currentChallenges, pastChallenges, ChallengesAdapter.FragmentScreen.HOME);
        rvChallenges.setAdapter(adapter);
        rvChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChallenges.addItemDecoration(new DividerItemDecoration(rvChallenges.getContext(), DividerItemDecoration.VERTICAL));
        query();
        queryPastChallenges();
    }

    private void query() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_DEADLINE);
        query.whereEqualTo(Challenge.KEY_REC, ParseUser.getCurrentUser());
        query.whereEqualTo(Challenge.KEY_COMPLETED, null);
        query.whereNotEqualTo(Challenge.KEY_DELETED, true);
        query.whereGreaterThan(Challenge.KEY_DEADLINE, current);
        query.addAscendingOrder(Challenge.KEY_DEADLINE);

        query.findInBackground(new FindCallback<Challenge>() {
            @Override
            public void done(List<Challenge> challenges, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting challenges", Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.clear();
                adapter.addAll(challenges);
            }
        });
    }

    private void queryPastChallenges() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_DEADLINE);
        query.whereEqualTo(Challenge.KEY_REC, ParseUser.getCurrentUser());
        query.whereEqualTo(Challenge.KEY_COMPLETED, null);
        query.whereNotEqualTo(Challenge.KEY_DELETED, true);
        query.whereLessThan(Challenge.KEY_DEADLINE, current);
        query.addAscendingOrder(Challenge.KEY_DEADLINE);

        query.findInBackground(new FindCallback<Challenge>() {
            @Override
            public void done(List<Challenge> challenges, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting challenges", Toast.LENGTH_SHORT).show();
                    return;
                }
                pastChallenges.addAll(challenges);
                adapter.notifyDataSetChanged();
            }
        });
    }
}