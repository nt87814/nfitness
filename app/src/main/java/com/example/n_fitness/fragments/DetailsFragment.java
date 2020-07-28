package com.example.n_fitness.fragments;

import android.gesture.Gesture;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import static com.example.n_fitness.adapters.ChallengesAdapter.getTimeLeft;

/**
 * Fragment for viewing details on a challenge
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    private Bundle bundle;
    private Challenge challenge;
    private Post post;
    private ChallengesAdapter.FragmentScreen fragmentScreen;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private ImageButton btnLike;
    private TextView tvLikes;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvTimestamp;
    private Button btnComplete;
    private Button btnChallenge;

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
        btnLike = view.findViewById(R.id.btnLike);
        tvLikes = view.findViewById(R.id.tvLikes);
        ivImage = view.findViewById(R.id.ivImage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        btnComplete = view.findViewById(R.id.btnComplete);
        btnChallenge = view.findViewById(R.id.btnChallenge);

        challenge = bundle.getParcelable("challenge");
        post = bundle.getParcelable("post");
        Log.d(TAG, challenge.getFrom().getUsername());
        Glide.with(getContext()).load(challenge.getFrom().getParseFile("image").getUrl()).into(ivProfileImage);
        tvUsername.setText(challenge.getFrom().getUsername());
        if (post.getImage() != null) {
            Glide.with(getContext()).load(post.getImage().getUrl()).into(ivImage);
        }
        tvDescription.setText(post.getDescription());
        setTvLikes();
        setActiveHeart();

        btnComplete = view.findViewById(R.id.btnComplete);
        fragmentScreen = ChallengesAdapter.FragmentScreen.valueOf(bundle.getString("screenFrom"));
        if (fragmentScreen == ChallengesAdapter.FragmentScreen.CURRENTPROFILE || fragmentScreen == ChallengesAdapter.FragmentScreen.USERPROFILE) {
            btnComplete.setVisibility(View.GONE);
            tvTimestamp.setVisibility(View.GONE);
        } else {
            tvTimestamp.setText(getTimeLeft(challenge.getDeadline().toString()));
            btnComplete.setOnClickListener(view12 -> {
                challenge.setCompleted();
                challenge.saveInBackground();
                switchFragment(R.id.flContainer, new ProfileFragment());
            });
        }
        btnChallenge = view.findViewById(R.id.btnChallenge);

        btnChallenge.setOnClickListener(view1 -> {
            Challenge newChallenge = new Challenge();
            newChallenge.setPost(post);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            CreateChallengeFragment createChallengeDialogFragment = CreateChallengeFragment.newInstance("Some Title");
            Bundle bundle = new Bundle();
            bundle.putParcelable("post", post);
            createChallengeDialogFragment.setArguments(bundle);
            createChallengeDialogFragment.show(fm, "fragment_create_challenge");
        });

        ivProfileImage.setOnClickListener(view14 -> goUserFragment());

        tvUsername.setOnClickListener(view13 -> goUserFragment());

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost();
            }
        });

        ivImage.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    likePost();
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    private void goUserFragment() {
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", challenge.getFrom());
        userFragment.setArguments(bundle);
        switchFragment(R.id.flContainer, userFragment);
    }

    public void switchFragment(int id, Fragment fragment) {
        if (getContext() == null)
            return;
        if (getContext() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getContext();
            mainActivity.loadFragment(id, fragment);
        }
    }

    //TODO: better name
    public static boolean listHasUserLike(ArrayList<ParseUser> list, ParseUser parseUser) {
        for (ParseUser user: list) {
            if ( user.getUsername().equals(parseUser.getUsername())) {
                return true;
            }
        }
        return false;
    }

    private void setTvLikes() {
        if (post.getLikes() != null) {
            int numLikes = post.getLikes().size();
            if (numLikes == 1) {
                tvLikes.setText(1 + " like");
            } else {
                tvLikes.setText(numLikes + " likes");
            }
        }
    }

    private void setActiveHeart() {
        if (post.getLikes() != null && listHasUserLike(post.getLikes(), ParseUser.getCurrentUser())) {
            btnLike.setImageResource(R.drawable.ufi_heart_active);
        }
    }

    private void likePost() {
        post.addLike(ParseUser.getCurrentUser());
        post.saveInBackground();
        setTvLikes();
        setActiveHeart();
    }
}