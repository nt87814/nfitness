package com.example.n_fitness.adapters;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.fragments.DetailsFragment;
import com.example.n_fitness.fragments.GenericFragment;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import permissions.dispatcher.NeedsPermission;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Adapter for timeline challenges
 */
public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder> {

    private Context context; //TODO: change to MainActivity
    private List<Challenge> challenges;
    private List<Challenge> pastChallenges;
    private FragmentScreen fragmentScreen;
    private Location mCurrentLocation;
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public enum FragmentScreen {
        HOME,
        PROFILE,
        COMPOSE
    }

    public ChallengesAdapter(Context context, List<Challenge> challenges, List<Challenge> pastChallenges, FragmentScreen fs) {
        this.context = context;
        this.challenges = challenges;
        this.pastChallenges = pastChallenges;
        fragmentScreen = fs;
        getCurrentLocation();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge challenge = getChallengeFromPosition(position);
        holder.bind(challenge);
    }

    @Override
    public int getItemCount() {
        if (pastChallenges != null) {
            return challenges.size() + pastChallenges.size();
        }
        return challenges.size();
    }

    public void clear() {
        challenges.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Challenge> list) {
        challenges.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvDeadline;
        private TextView tvDescription;
        private TextView tvFrom;
        private SwipeLayout swipeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeItem);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.bottom_wrapper));

            ivImage = itemView.findViewById(R.id.ivImage);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            TextView tvComplete = itemView.findViewById(R.id.tvComplete);
            ImageView delete = itemView.findViewById(R.id.delete);
            LinearLayout ll_surface_view = itemView.findViewById(R.id.ll_surface_view);

            tvFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser fromUser = (challenges.get(getAdapterPosition())).getFrom();
                    GenericFragment.goUserFragment(fromUser, (MainActivity) context);
                }
            });

            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewWithTag("Bottom2"));

            ll_surface_view.setOnClickListener(view -> {
                if (swipeLayout.getOpenStatus() == SwipeLayout.Status.Close) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Challenge challenge = getChallengeFromPosition(position);
                        DetailsFragment detailsFragment = new DetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(context.getResources().getString(R.string.challenge), challenge);
                        bundle.putString(context.getResources().getString(R.string.screenFrom), fragmentScreen.name());
                        detailsFragment.setArguments(bundle);
                        GenericFragment.switchFragment(detailsFragment, (MainActivity) context);
                    }
                }
            });

            tvComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Challenge challenge = getChallengeFromPosition(position);
                        challenge.setCompleted();
                        challenge.setLocation(mCurrentLocation);
                        challenge.saveInBackground();
                        removeChallengeAtPosition(position);
                        notifyItemRemoved(position);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Challenge challenge = getChallengeFromPosition(position);
                        challenge.setDeleted();
                        challenge.saveInBackground();
                        removeChallengeAtPosition(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        public void bind(Challenge challenge) {
            Post post = challenge.getPost();
            switch (fragmentScreen) {
                case HOME:
                    tvDeadline.setText(getTimeLeft(challenge.getDeadline().toString()));
                    break;
                case PROFILE:
                    swipeLayout.setSwipeEnabled(false);
                    tvDeadline.setText(getDisplayDate(challenge.getCompleted().toString()));
                    break;
            }
            tvDescription.setText(post.getDescription());
            tvFrom.setText("Challenged by " + challenge.getFrom().getUsername());
            if (post.getImage() != null) {
                    Glide.with(context).load(post.getImage().getUrl()).centerInside().into(ivImage);
            }
        }
    }

    public static String getTimeLeft(String rawJsonDate) {
        if (rawJsonDate == null) {
            return "NULL";
        }

        SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);

        long diff = 0;
        try {
            Date date1 = sf.parse(rawJsonDate);
            Date date2 = new Date(System.currentTimeMillis());
            diff = date1.getTime() - date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int daysLeft = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (daysLeft == 1) {
            return 1 + " day left";
        }

        else if (daysLeft < 1) {
            return "Deadline passed";
        }

        return daysLeft + " days left";
    }

    public static String getDisplayDate(String rawJsonDate) {
        String rString = "";
        if (rawJsonDate == null) {
            return "NULL";
        }
        SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);
        Date date = new Date();
        try {
            date = sf.parse(rawJsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d YYYY");
        rString = simpleDateFormat.format(date);

        return rString;
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getCurrentLocation() {
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            saveLocation(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void saveLocation(Location location) {
        if (location == null) {
            return;
        }

        mCurrentLocation = location;
    }

    private Challenge getChallengeFromPosition(int position) {
        if (position < challenges.size()) {
            return challenges.get(position);
        }

        else {
            return pastChallenges.get(position - challenges.size());
        }
    }

    private void removeChallengeAtPosition(int position) {
        if (position < challenges.size()) {
            challenges.remove(position);
        }

        else {
            pastChallenges.remove(position - challenges.size());
        }
    }
}
