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
import com.example.n_fitness.models.ContactChip;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pchmn.materialchips.ChipsInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for adding friends to a challenge from the workout detail page
 */
public class CreateChallengeFragment extends DialogFragment {

    private static final String TAG = "CreateChallengeFragment";
    private List<ContactChip> contactList;
    private ChipsInput chipsInput;
    private Button btnConfirm;
    private Post post;

    private Bundle bundle;


    public CreateChallengeFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static CreateChallengeFragment newInstance() {
        CreateChallengeFragment frag = new CreateChallengeFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        query();
        bundle = this.getArguments();
        post = bundle.getParcelable("post");
        return inflater.inflate(R.layout.fragment_create_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chipsInput = view.findViewById(R.id.chipsInput);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        contactList = new ArrayList<>();
        chipsInput.setFilterableList(contactList);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ContactChip> contactsSelected = (List<ContactChip>) chipsInput.getSelectedChipList();
                for (ContactChip chip : contactsSelected) {
                    addChallenge(chip.getUser());
                }
            }
        });

    }

    private void query() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting users", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (ParseUser user : users) {
                    ContactChip contactChip = new ContactChip(user.getObjectId(), user.getUsername(), user);
                    contactList.add(contactChip);
                }
                chipsInput.setFilterableList(contactList);
                chipsInput.requestLayout();
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
                } else {
                    Toast.makeText(getContext(), "Save was successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}