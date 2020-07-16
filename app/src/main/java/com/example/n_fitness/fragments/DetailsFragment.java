package com.example.n_fitness.fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;

import static com.example.n_fitness.adapters.ChallengesAdapter.getTimeLeft;

public class DetailsFragment extends Fragment {

    private static final String TAG ="DetailsFragment";

    private Bundle bundle;
    private Challenge challenge;
    private Post post;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private ImageButton btnLike;
    private TextView tvLikes;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvTimestamp;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLike  = view.findViewById(R.id.btnLike);
        tvLikes = view.findViewById(R.id.tvLikes);
        ivImage  = view.findViewById(R.id.ivImage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);

        challenge = bundle.getParcelable("challenge");
        post = bundle.getParcelable("post");
        Log.d(TAG, challenge.getFrom().getUsername());
        Glide.with(getContext()).load(challenge.getFrom().getParseFile("image").getUrl()).into(ivProfileImage);
        tvUsername.setText(challenge.getFrom().getUsername());
        if (post.getImage() != null) {
            Glide.with(getContext()).load(post.getImage().getUrl()).into(ivImage);
        }
        tvDescription.setText(post.getDescription());
        tvTimestamp.setText(getTimeLeft(challenge.getDeadline().toString()));
    }
}