package com.example.n_fitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.daimajia.swipe.SwipeLayout;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.fragments.GenericFragment;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.ParseUser;

import java.util.List;

import static com.example.n_fitness.adapters.ChallengesAdapter.getRelativeTimeAgo;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private List<Challenge> notifications;

    public NotificationsAdapter(Context context, List<Challenge> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvDeadline;
        private TextView tvDescription;
        private TextView tvFrom;
        private ImageView ivProfile;
        private Button btnDecline;
        private Button btnAccept;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            ivProfile = itemView.findViewById(R.id.ivProfileImage);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnAccept = itemView.findViewById(R.id.btnAccept);

            tvFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser fromUser = (notifications.get(getAdapterPosition())).getFrom();
                    GenericFragment.goUserFragment(fromUser, (MainActivity) context);
                }
            });

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser fromUser = (notifications.get(getAdapterPosition())).getFrom();
                    GenericFragment.goUserFragment(fromUser, (MainActivity) context);
                }
            });

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Challenge challenge = notifications.get(position);
                        challenge.setStatus("declined");
                        challenge.saveInBackground();
                        notifications.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            });

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Challenge challenge = notifications.get(position);
                        challenge.setStatus("accepted");
                        challenge.saveInBackground();
                        notifications.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        public void bind(Challenge challenge) {
            Post post = challenge.getPost();
            tvDeadline.setText(getRelativeTimeAgo(challenge.getDeadline().toString()));
            tvDescription.setText(post.getDescription());
            if (post.getImage() != null) {
                Glide.with(context).load(post.getImage().getUrl()).transform(new CenterCrop(), new RoundedCorners(20)).into(ivImage);
            }

            Glide.with(context).load(challenge.getFrom().getParseFile("image").getUrl()).into(ivProfile);
            tvFrom.setText(challenge.getFrom().getUsername());
        }
    }
}
