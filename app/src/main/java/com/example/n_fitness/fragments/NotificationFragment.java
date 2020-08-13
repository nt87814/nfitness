package com.example.n_fitness.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.adapters.NotificationsAdapter;
import com.example.n_fitness.models.Challenge;
import com.google.android.material.badge.BadgeDrawable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.n_fitness.activities.MainActivity.bottomNavigationView;

/**
 * Fragment for viewing current user's notifications
 *
 * User can view challenges from friends and accept or decline them
 */
public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private NotificationsAdapter adapter;
    protected List<Challenge> notifications;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.setBtnCreateVisibility(this);
        RecyclerView rvNotifications = view.findViewById(R.id.rvNotifications);

        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(getContext(), notifications);
        rvNotifications.setAdapter(adapter);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.addItemDecoration(new DividerItemDecoration(rvNotifications.getContext(), DividerItemDecoration.VERTICAL));
        queryNotifications();
    }

    private void queryNotifications() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_DEADLINE);
        query.whereEqualTo(Challenge.KEY_REC, ParseUser.getCurrentUser());
        query.whereEqualTo(Challenge.KEY_COMPLETED, null);
        query.whereNotEqualTo(Challenge.KEY_DELETED, true);
        query.whereEqualTo(Challenge.KEY_STATUS, "pending");
        query.addAscendingOrder(Challenge.KEY_DEADLINE);

        query.findInBackground(new FindCallback<Challenge>() {
            @Override
            public void done(List<Challenge> challenges, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting challenges", Toast.LENGTH_SHORT).show();
                    return;
                }
                notifications.addAll(challenges);
                adapter.notifyDataSetChanged();

                if (challenges.size() > 0) {
                    BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.action_notification);
                    badge.setVisible(true);
                    badge.setNumber(challenges.size());
                }
            }
        });
    }
}