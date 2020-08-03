package com.example.n_fitness.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.n_fitness.R;
import com.example.n_fitness.adapters.FriendsAdapter;
import com.example.n_fitness.models.Challenge;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing list of friends on profile page
 */
public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "FriendsActivity";
    private FriendsAdapter adapter;
    private List<ParseUser> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        RecyclerView rvFriends = findViewById(R.id.rvFriends);

        friends = new ArrayList<>();
        adapter = new FriendsAdapter(this, friends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.addItemDecoration(new DividerItemDecoration(rvFriends.getContext(), DividerItemDecoration.VERTICAL));
        queryFriends();
    }

    private void queryFriends() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Issue with getting friends", Toast.LENGTH_SHORT).show();
                    return;
                }

                friends.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

    }
}