package com.example.n_fitness.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Adapter for timeline challenges
 */
public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder> {

    private Context context;
    private List<Challenge> challenges;
    FragmentScreen fragmentScreen;
    public static enum FragmentScreen {
        HOME,
        CURRENTPROFILE,
        USERPROFILE
    }

    public ChallengesAdapter(Context context, List<Challenge> challenges, FragmentScreen fs) {
        this.context = context;
        this.challenges = challenges;
        fragmentScreen = fs;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvFrom = itemView.findViewById(R.id.tvFrom);
        }

        @Override
        public void onClick(View view) {

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(Challenge challenge) {
            Post post = challenge.getPost();
            switch(fragmentScreen) {
                case HOME:
                    tvDeadline.setText(getTimeLeft(challenge.getDeadline().toString()));
                    break;
                case CURRENTPROFILE:
                    tvDeadline.setText(getDisplayDate(challenge.getCompleted().toString()));
                    break;
                case USERPROFILE:
                    System.out.println("High level");
                    break;
            }
            tvDescription.setText(post.getDescription());
            tvFrom.setText("Challenged by " + challenge.getFrom().getUsername());
            Glide.with(context).load(post.getImage().getUrl()).centerInside().into(ivImage);
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
}
