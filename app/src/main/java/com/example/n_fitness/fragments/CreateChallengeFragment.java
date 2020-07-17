package com.example.n_fitness.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n_fitness.ContactChip;
import com.example.n_fitness.R;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pchmn.materialchips.ChipsInput;

import java.util.ArrayList;
import java.util.List;

public class CreateChallengeFragment extends DialogFragment {

    public static final String TAG = "CreateChallengeFragment";
    private List<ContactChip> contactList;
    Button btnValidate;
    TextView tvChipList;
    ChipsInput chipsInput;
    Button btnDone;
    Post post;

    private Bundle bundle;


    public CreateChallengeFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static CreateChallengeFragment newInstance(String title) {
        CreateChallengeFragment frag = new CreateChallengeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        query();
        bundle = this.getArguments();
        post = bundle.getParcelable("post");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chipsInput = view.findViewById(R.id.chips_input);
        btnValidate = view.findViewById(R.id.btnValidate);
        tvChipList = view.findViewById(R.id.tvChipList);
        btnDone = view.findViewById(R.id.btnDone);
        contactList = new ArrayList<>();
        chipsInput.setFilterableList(contactList);

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // get the list
                List<ContactChip> contactsSelected = (List<ContactChip>) chipsInput.getSelectedChipList();
                String listString = "";
                for (ContactChip chip: contactsSelected) {
                    listString += chip.getLabel() + ", ";
                }

                tvChipList.setText(listString);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ContactChip> contactsSelected = (List<ContactChip>) chipsInput.getSelectedChipList();
                for (ContactChip chip: contactsSelected) {
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
                    Log.e(TAG, "Issue with getting challenges", e);
                    return;
                }

                for (ParseUser user : users) {
                    Log.i(TAG, "User: " + user.getUsername());
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
                }
                Toast.makeText(getContext(), "Save was successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}