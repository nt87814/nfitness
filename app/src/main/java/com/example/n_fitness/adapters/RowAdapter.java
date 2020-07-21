package com.example.n_fitness.adapters;

import android.app.Activity;
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
import com.example.n_fitness.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class RowAdapter extends RecyclerView.Adapter<RowAdapter.ViewHolder> {

    private List<Post> mPosts;
    Context mContext;

    public RowAdapter(Context context, List<Post> objects) {
        this.mContext = context;
        this.mPosts = objects;
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

        public ImageView ivPost;
        public TextView tvTitle;
        public View rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ivPost =(ImageView)itemView.findViewById(R.id.ivPost);
            tvTitle =(TextView)itemView.findViewById(R.id.tvTitle);
        }

        public void bind(Post post) {
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).centerInside().into(ivPost);
            }
            tvTitle.setText(post.getDescription());
        }
    }
}
