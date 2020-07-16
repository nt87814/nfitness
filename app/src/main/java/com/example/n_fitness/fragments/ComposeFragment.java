package com.example.n_fitness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.n_fitness.R;

/**
 * Fragment for composing a workout post
 * <p>
 * This fragment utilizes access to the camera
 */
public class ComposeFragment extends Fragment {

    private static final String TAG = "ComposeFragment";

    private EditText etDescription;
    private ImageView ivPostImage;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etDescription = view.findViewById(R.id.etDescription);
        ivPostImage = view.findViewById(R.id.ivPostImage);
    }

//    private void savePost(String description, ParseUser currentUser, File photoFile) {
//        Post post = new Post();
//        post.setDescription(description);
//        post.setUser(currentUser);
//        post.setImage(new ParseFile(photoFile));
//        post.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(getContext(), "Post save was successful!", Toast.LENGTH_SHORT).show();
//                etDescription.setText("");
//                ivPostImage.setImageResource(0);
//            }
//        });
//    }
}