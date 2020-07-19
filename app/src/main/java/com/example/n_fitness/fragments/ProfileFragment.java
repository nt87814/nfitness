package com.example.n_fitness.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.LoginActivity;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    protected List<Challenge> completedChallenges;
    protected ChallengesAdapter adapter;

    protected ImageView ivProfileImage;
    protected TextView tvUsername;
    protected Button btnLogout;
    protected RecyclerView rvProfileChallenges;
    protected TextView tvTop;
    protected TextView tvTopLabel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(currentUser.getUsername());
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        ParseFile profileImage = currentUser.getParseFile("image");
        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).into(ivProfileImage);
        }

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goLoginActivity();
            }
        });

        tvTopLabel = view.findViewById(R.id.tvTopLabel);
        tvTop = view.findViewById(R.id.tvTop);

        rvProfileChallenges = view.findViewById(R.id.rvProfileChallenges);
        completedChallenges = new ArrayList<>();
        adapter = new ChallengesAdapter(getContext(), completedChallenges, ChallengesAdapter.FragmentScreen.CURRENTPROFILE);
        rvProfileChallenges.setAdapter(adapter);
        rvProfileChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        query();
    }

    private void query() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_COMPLETED);
        query.include(Post.KEY_CATEGORY);
        query.include(Category.KEY_NAME);
        query.whereEqualTo(Challenge.KEY_REC, ParseUser.getCurrentUser());
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground((challenges, e) -> {
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
            tvTop.setText(getTopCategory());
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    private String getTopCategory() {
        String topCategory = "";

        if (completedChallenges.isEmpty()) {
            return topCategory;
        }

        Map<String, MutableInt> freq = new HashMap<>();

        for (Challenge c : completedChallenges) {

            String key = null;
            try {
                key = c.getPost().getCategory().fetchIfNeeded().get("name").toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            MutableInt count = freq.get(key);
            if (count == null) {
                freq.put(key, new MutableInt());
            } else {
                count.increment();
            }
        }

        Map.Entry<String, MutableInt> maxEntry = null;

        for (Map.Entry<String, MutableInt> entry : freq.entrySet()) {
            if (maxEntry == null || entry.getValue().get() > maxEntry.getValue().get()) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }

    class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }

        public int get() {
            return value;
        }
    }
}