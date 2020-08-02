package com.example.n_fitness.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.n_fitness.R;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment for adding a workout to challenge list from the explore page
 */
public class AddChallengeFragment extends DialogFragment {

    private static final String TAG = "AddChallengeFragment";
    private Post post;
    private Date deadline;

    public AddChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        post = bundle.getParcelable("post");
        return inflater.inflate(R.layout.fragment_add_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton btnDeadline = view.findViewById(R.id.btnDeadline);
        TextView tvDeadline = view.findViewById(R.id.tvDeadline);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        //TODO: redundant code
        btnDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                MaterialDatePicker picker = builder.build();
                picker.show(getActivity().getSupportFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        deadline = new Date(Long.parseLong(selection.toString()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(deadline);
                        c.add(Calendar.DATE, 1); // 1 day off
                        deadline = c.getTime();
                        tvDeadline.setText(deadline.toString());
                    }
                });
            }
        });

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
        if (deadline != null) {
            challenge.setDeadline(deadline);
        } else {
            challenge.setDeadline();
        }
        challenge.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Save was successful!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }
}