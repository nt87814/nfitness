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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.fragments.DetailsFragment;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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

    private Context context;
    private List<Challenge> challenges;
    FragmentScreen fragmentScreen;
    private Location mCurrentLocation;


    public enum FragmentScreen {
        HOME,
        CURRENTPROFILE,
        USERPROFILE
    }

    public ChallengesAdapter(Context context, List<Challenge> challenges, FragmentScreen fs) {
        this.context = context;
        this.challenges = challenges;
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
        Challenge challenge = challenges.get(position);
        holder.bind(challenge);
    }

    @Override
    public int getItemCount() {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivImage;
        private TextView tvDeadline;
        private TextView tvDescription;
        private TextView tvFrom;
        private TextView tvComplete;
        private SwipeLayout swipeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewHolder v = this;
            itemView.setOnClickListener(v);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeItem);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.bottom_wrapper));

            ivImage = itemView.findViewById(R.id.ivImage);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvComplete = itemView.findViewById(R.id.tvComplete);

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                    itemView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemView.setOnClickListener(v);
                        }
                    }, 500);
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {
                    itemView.setOnClickListener(null);
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    //when the BottomView totally show.
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });

            tvComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Challenge challenge = challenges.get(position);
                        challenge.setCompleted();
                        challenge.setLocation(mCurrentLocation);
                        challenge.saveInBackground();
                        challenges.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Challenge challenge = challenges.get(position);
                Post post = challenge.getPost();
                DetailsFragment detailsFragment = new DetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("challenge", challenge);
                bundle.putParcelable("post", post);
                bundle.putString("screenFrom", fragmentScreen.name());
                detailsFragment.setArguments(bundle);
                switchFragment(R.id.flContainer, detailsFragment);
            }
        }

        public void bind(Challenge challenge) {
            Post post = challenge.getPost();
            switch (fragmentScreen) {
                case HOME:
                    tvDeadline.setText(getTimeLeft(challenge.getDeadline().toString()));    //need to make required
                    break;
                case CURRENTPROFILE:
                case USERPROFILE:
                    swipeLayout.setSwipeEnabled(false);
                    tvDeadline.setText(getDisplayDate(challenge.getCompleted().toString()));
                    tvFrom.setVisibility(View.GONE);
                    break;
            }
            tvDescription.setText(post.getDescription());
            tvFrom.setText("Challenged by " + challenge.getFrom().getUsername());
            if (post.getImage() != null) {
                Glide.with(context).load(post.getImage().getUrl()).centerInside().into(ivImage);
            }
        }
    }

    public void switchFragment(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.loadFragment(id, fragment);
        }

    }

    public static String getTimeLeft(String rawJsonDate) {
        if (rawJsonDate == null) {
            return "NULL";
        }
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        long diff = 0;
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            Date date1 = sf.parse(rawJsonDate);
            Date date2 = new Date(System.currentTimeMillis());
            diff = date1.getTime() - date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " days left";
    }

    public static String getDisplayDate(String rawJsonDate) {
        String rString = "";
        if (rawJsonDate == null) {
            return "NULL";
        }
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        Date date = new Date();
        try {
            date = sf.parse(rawJsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
        rString += simpleDateFormat.format(date).toUpperCase() + " ";

        simpleDateFormat = new SimpleDateFormat("dd");
        rString += simpleDateFormat.format(date).toUpperCase() + " ";


        simpleDateFormat = new SimpleDateFormat("YYYY");
        rString += simpleDateFormat.format(date).toUpperCase();

        return "Completed on " + rString;
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
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
