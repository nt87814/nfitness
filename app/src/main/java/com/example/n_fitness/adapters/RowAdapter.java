package com.example.n_fitness.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.fragments.AddChallengeFragment;
import com.example.n_fitness.models.Post;
import com.google.android.material.card.MaterialCardView;
import com.parse.ParseFile;

import java.util.List;

/**
 * Adapter for posts within category rows in explore page
 */
public class RowAdapter extends RecyclerView.Adapter<RowAdapter.ViewHolder> {

    private static Context mContext;
    private static List<Post> mPosts;

    public RowAdapter(Context context, List<Post> objects) {
        mContext = context;
        mPosts = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPost;
        private TextView tvTitle;
        private View rootView;
        private MaterialCardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ivPost = (ImageView) itemView.findViewById(R.id.ivPost);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            card = itemView.findViewById(R.id.card);
        }

        public void bind(Post post) {
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).centerInside().into(ivPost);
            }
            tvTitle.setText(post.getDescription());

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    AddChallengeFragment addChallengeDialogFragment = AddChallengeFragment.newInstance("Add to my Challenges?");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("post", post);
                    addChallengeDialogFragment.setArguments(bundle);
                    addChallengeDialogFragment.show(fm, "fragment_add_challenge");
                }
            });

        }

    }
}
