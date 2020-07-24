package com.example.n_fitness.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.adapters.SpinAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
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

    private ParseFile photoFile;
    public String photoFileName = "photo.jpg";
    private Uri imageUri;
    private SpinAdapter adapter;


    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ivPostImage.getDrawable() == null) {
                Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseUser currentUser = ParseUser.getCurrentUser();
            savePost(description, currentUser);
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
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        queryCategories();
    }

    private void savePost(String description, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);
        Bitmap imageToBeSent = ((BitmapDrawable) ivPostImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageToBeSent.compress(Bitmap.CompressFormat.PNG, 5, stream);
        byte[] imageRec = stream.toByteArray();
        photoFile = new ParseFile(photoFileName, imageRec);

        if (selectedCategory == null) {
            Toast.makeText(getContext(), "Please select a category!", Toast.LENGTH_SHORT).show();
            return;
        }
        post.setCategory(selectedCategory);
        ParseFile finalPhotoFile = photoFile;
        post.setImage(finalPhotoFile);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), "Post save was successful!", Toast.LENGTH_SHORT).show();
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }

    private void queryCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.include(Category.KEY_NAME);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting categories", Toast.LENGTH_SHORT).show();
                    return;
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
                Toast.makeText(getContext(), "Error getting image!", Toast.LENGTH_SHORT).show();
                return;
            }
            imageUri = data.getData();
            Glide.with(getContext()).load(imageUri).into(ivPostImage);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}