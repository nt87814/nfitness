package com.example.n_fitness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserFragment extends ProfileFragment {

    private static final String TAG = "UserFragment";
    private ParseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (ParseUser) bundle.get("user");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        tvUsername.setText(user.getUsername());
        ParseFile profileImage = user.getParseFile("image");
        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).into(ivProfileImage);
        }

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.GONE);

        tvTopLabel = view.findViewById(R.id.tvTopLabel);
        tvTop = view.findViewById(R.id.tvTop);

        rvProfileChallenges = view.findViewById(R.id.rvProfileChallenges);
        completedChallenges = new ArrayList<>();
        adapter = new ChallengesAdapter(getContext(), completedChallenges, ChallengesAdapter.FragmentScreen.CURRENTPROFILE);
        rvProfileChallenges.setAdapter(adapter);
        rvProfileChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        query();
    }

    @Override
    public void query() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_COMPLETED);
        query.include(Post.KEY_CATEGORY);
        query.include(Category.KEY_NAME);
        query.whereEqualTo(Challenge.KEY_REC, user);
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground((challenges, e) -> {
            if (e != null) {
//                Log.e(TAG, "Issue with getting challenges", e);
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
}
