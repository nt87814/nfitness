package com.example.n_fitness.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.LoginActivity;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    protected List<Challenge> completedChallenges;
    protected ChallengesAdapter adapter;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;


    protected ImageView ivProfileImage;
    protected TextView tvUsername;
    protected Button btnLogout;
    protected Button btnEdit;
    protected RecyclerView rvProfileChallenges;
    protected TextView tvTop;
    protected TextView tvTopLabel;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        tvUsername.setText(currentUser.getUsername());
        ParseFile profileImage = currentUser.getParseFile("image");
        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).into(ivProfileImage);
        }

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goLoginActivity();
            }
        });

        btnEdit = view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        tvTopLabel = view.findViewById(R.id.tvTopLabel);
        tvTop = view.findViewById(R.id.tvTop);

        rvProfileChallenges = view.findViewById(R.id.rvProfileChallenges);
        completedChallenges = new ArrayList<>();
        adapter = new ChallengesAdapter(getContext(), completedChallenges, ChallengesAdapter.FragmentScreen.CURRENTPROFILE);
        rvProfileChallenges.setAdapter(adapter);
        rvProfileChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        query();
    }

    protected void query() {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_COMPLETED);
        query.include(Post.KEY_CATEGORY);
        query.include(Category.KEY_NAME);
        query.whereEqualTo(Challenge.KEY_REC, ParseUser.getCurrentUser());
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground((challenges, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting challenges", e);
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

    private void goLoginActivity() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    protected String getTopCategory() {
        String topCategory = "";

        if (completedChallenges.isEmpty()) {
            return topCategory;
        }

        Map<String, MutableInt> freq = new HashMap<>();

        for (Challenge c : completedChallenges) {

            String key = null;
            try {
                key = c.getPost().getCategory().fetchIfNeeded().get("name").toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            MutableInt count = freq.get(key);
            if (count == null) {
                freq.put(key, new MutableInt());
            } else {
                count.increment();
            }
        }

        Map.Entry<String, MutableInt> maxEntry = null;

        for (Map.Entry<String, MutableInt> entry : freq.entrySet()) {
            if (maxEntry == null || entry.getValue().get() > maxEntry.getValue().get()) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider1", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivProfileImage.setImageBitmap(takenImage);
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveProfileImage(currentUser, photoFile);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProfileImage(ParseUser currentUser, File photoFile) {

        currentUser.put("image", new ParseFile(photoFile));
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "User save was successful!");
            }
        });
    }

    class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }

        public int get() {
            return value;
        }
    }
}