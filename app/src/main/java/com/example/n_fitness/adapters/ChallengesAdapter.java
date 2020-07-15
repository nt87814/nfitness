package com.example.n_fitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;

import java.util.List;

/**
 * Adapter for timeline challenges
 * */
public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder> {

    private Context context;
    private List<Challenge> challenges;

    public ChallengesAdapter(Context context, List<Challenge> challenges) {
        this.context = context;
        this.challenges = challenges;
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

        public void bind(Challenge challenge) {
            Post post = challenge.getPost();
            tvDeadline.setText(challenge.getDeadline().toString());
            tvDescription.setText(post.getDescription());
            tvFrom.setText("Challenged by " + challenge.getFrom().getUsername() + "!");
            Glide.with(context).load(post.getImage().getUrl()).centerInside().into(ivImage);
        }
    }
}
