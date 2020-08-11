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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anton46.stepsview.StepsView;
import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.LoginActivity;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.adapters.ChallengesAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.library.NavigationBar;
import com.library.NvTab;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.repsly.library.timelineview.TimelineView;

import java.io.File;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for viewing current user's profile or other user's profile
 * with the presence of "user" key in bundle
 */
public class ProfileFragment extends GenericFragment {

    private static final String TAG = "ProfileFragment";
    private ChallengesAdapter adapter;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;
    private RelativeLayout profile;
    private ParseUser otherUser;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private Button btnLogout;
    private RecyclerView rvProfileChallenges;
    private TextView tvTop;
    private TextView tvTopLabel;
    private TextView tvWeek;
    private TextView tvWon;

    private File photoFile;
    private static final String PHOTO_FILE_NAME = "photo.jpg";
    Bundle bundle;
    TextView tvStreak;
    List<Challenge> completedChallenges;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        if (bundle != null) {
            otherUser = (ParseUser) bundle.get("user");
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        profile = view.findViewById(R.id.profile);
        TextView tvFriends = view.findViewById(R.id.tvFriends);
        ParseFile profileImage;
        btnLogout = view.findViewById(R.id.btnLogout);
        tvStreak = view.findViewById(R.id.tvStreak);
        tvWeek = view.findViewById(R.id.tvWeek);
        tvWon = view.findViewById(R.id.tvWon);
        completedChallenges = new ArrayList<>();
        // This is another user profile
        if (bundle != null) {
            MainActivity.btnCreate.setVisibility(View.GONE);

            tvUsername.setText(otherUser.getUsername());
            profileImage = otherUser.getParseFile("image");
            btnLogout.setVisibility(View.GONE);
            adapter = new ChallengesAdapter(getContext(), completedChallenges, null, ChallengesAdapter.FragmentScreen.OTHER_PROFILE);
            queryList(otherUser);
            queryPastWeek(otherUser);
            queryWon(otherUser);
            ImageView icCamera = view.findViewById(R.id.icCamera);
            icCamera.setVisibility(View.GONE);
        }

        //This is the logged in user's profile
        else {
            MainActivity.btnCreate.setVisibility(View.VISIBLE);
            ParseUser currentUser = ParseUser.getCurrentUser();

            tvUsername.setText(currentUser.getUsername());
            profileImage = currentUser.getParseFile("image");
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser.logOut();
                    goLoginActivity();
                }
            });
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchCamera();
                }
            });
            adapter = new ChallengesAdapter(getContext(), completedChallenges, null, ChallengesAdapter.FragmentScreen.PROFILE);
            queryList(ParseUser.getCurrentUser());
            queryPastWeek(ParseUser.getCurrentUser());
            queryWon(ParseUser.getCurrentUser());
        }

        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).into(ivProfileImage);
        }

        tvTopLabel = view.findViewById(R.id.tvTopLabel);
        tvTop = view.findViewById(R.id.tvTop);
        tvFriends.setText("friends");
        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendsFragment friendsFragment = new FriendsFragment();
                switchFragment(friendsFragment);
            }
        });

        rvProfileChallenges = view.findViewById(R.id.rvProfileChallenges);
        rvProfileChallenges.setAdapter(adapter);
        rvProfileChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProfileChallenges.addItemDecoration(new DividerItemDecoration(rvProfileChallenges.getContext(), DividerItemDecoration.VERTICAL));
    }

    protected void queryList(ParseUser user) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_FROM);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_POST);
        query.include(Challenge.KEY_COMPLETED);
        query.include(Post.KEY_CATEGORY);
        query.include(Category.KEY_NAME);
        query.whereEqualTo(Challenge.KEY_REC, user);
        query.whereNotEqualTo(Challenge.KEY_DELETED, true);
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);
        query.setLimit(15);
        query.addDescendingOrder(Challenge.KEY_COMPLETED);

        query.findInBackground((challenges, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Issue with getting challenges", Toast.LENGTH_SHORT).show();
                return;
            }

            adapter.clear();
            adapter.addAll(challenges);
            tvTop.setText(getTopCategory(completedChallenges));
            tvStreak.setText("" + calculateStreak());
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    public static String getTopCategory(List<Challenge> completedChallenges) {
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
        photoFile = getPhotoFileUri(PHOTO_FILE_NAME);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider1", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
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
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "User save was successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class MutableInt {
        private int value = 1; // start at 1 since it's counting

        public void increment() {
            ++value;
        }

        public int get() {
            return value;
        }
    }

    private Integer calculateStreak() {
        if (completedChallenges.isEmpty()) {
            return 0;
        }

        ArrayList<Integer> dayDiffs = new ArrayList<>();

        Calendar currentDay = Calendar.getInstance();
        for (Challenge c: completedChallenges) {
            Calendar cal = Calendar. getInstance();
            cal.setTime(c.getCompleted());
            dayDiffs.add((int) Math.abs(ChronoUnit.DAYS.between(currentDay.toInstant(), cal.toInstant())));
        }

        int streak = 0;
        if (dayDiffs.get(0) == 1) {
            streak++;
        }

        for (int i = 0; i < dayDiffs.size(); i++) {

            if (i > 0 && dayDiffs.get(i) != dayDiffs.get(i - 1) && streak + 1 == dayDiffs.get(i)) {
                streak++;
            }
        }

        if (dayDiffs.get(0) == 0) {     // User has completed a challenge today, if not they still can
            streak++;
        }
        return streak;
    }

    private void queryPastWeek(ParseUser user) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_COMPLETED);
        query.whereEqualTo(Challenge.KEY_REC, user);
        query.whereNotEqualTo(Challenge.KEY_DELETED, true);
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7); // Subtracting 7 days
        query.whereGreaterThan(Challenge.KEY_COMPLETED, c.getTime());

        query.findInBackground((challenges, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Issue with getting challenges", Toast.LENGTH_SHORT).show();
                return;
            }

            tvWeek.setText("" + challenges.size());
        });
    }

    private void queryWon(ParseUser user) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.include(Challenge.KEY_REC);
        query.include(Challenge.KEY_WON);
        query.whereEqualTo(Challenge.KEY_WON, true);
        query.whereEqualTo(Challenge.KEY_REC, user);
        query.whereNotEqualTo(Challenge.KEY_DELETED, true);
        query.whereNotEqualTo(Challenge.KEY_COMPLETED, null);

        query.findInBackground((challenges, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Issue with getting challenges", Toast.LENGTH_SHORT).show();
                return;
            }

            tvWon.setText("" + challenges.size());
        });
    }
}