package com.example.n_fitness.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.n_fitness.R;
import com.example.n_fitness.adapters.SpinAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for composing a workout post
 */
public class ComposeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "ComposeFragment";
    public static final int PICK_IMAGE = 1;

    private EditText etDescription;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private Button btnGetImage;
    private Spinner spinner;
    private List<Category> allCategories;
    private Category selectedCategory;

    private File photoFile;
    public String photoFileName = "photo.jpg";
    private Uri imageUri;
    private SpinAdapter adapter;


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
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnGetImage = view.findViewById(R.id.btnGetImage);
        spinner = view.findViewById(R.id.sCategories);
        spinner.setOnItemSelectedListener(this);

        btnSubmit.setOnClickListener(view1 -> {
            String description = etDescription.getText().toString();
            ParseUser currentUser = ParseUser.getCurrentUser();

            savePost(description, currentUser, photoFile);
        });

        btnGetImage.setOnClickListener(view12 -> {
            openGallery();
        });

        allCategories = new ArrayList<>();
        adapter = new SpinAdapter(getActivity(), android.R.layout.simple_spinner_item, (ArrayList<Category>) allCategories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                selectedCategory = adapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        queryCategories();
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);
        if (photoFile != null) {
            post.setImage(new ParseFile(photoFile));
        }
        post.setCategory(selectedCategory);
        post.saveInBackground(e -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getContext(), "Post save was successful!", Toast.LENGTH_SHORT).show();
            etDescription.setText("");
            ivPostImage.setImageResource(0);
        });
    }

    private void queryCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.include(Category.KEY_NAME);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting categories", e);
                    return;
                }

                for (Category category : categories) {
                    Log.i(TAG, "Categories: " + category.getName());
                }
                allCategories.addAll(categories);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            imageUri = data.getData();
            ivPostImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}