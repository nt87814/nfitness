package com.example.n_fitness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.n_fitness.R;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddChallengeFragment extends DialogFragment {

    private Button btnConfirm;
    private Bundle bundle;
    private Post post;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        post = bundle.getParcelable("post");
        getDialog().setTitle("My Dialog Title");
        return inflater.inflate(R.layout.fragment_add_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChallenge(ParseUser.getCurrentUser());
            }
        });
    }

    private void addChallenge(ParseUser user) {
        Challenge challenge = new Challenge();
        challenge.setPost(post);
        challenge.setFrom(ParseUser.getCurrentUser());
        challenge.setRecipient(user);
        challenge.setDeadline();
        challenge.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getActivity(), "Save was successful!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}