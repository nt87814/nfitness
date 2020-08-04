package com.example.n_fitness.adapters;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.n_fitness.R;
import com.example.n_fitness.activities.MainActivity;
import com.example.n_fitness.fragments.GenericFragment;
import com.example.n_fitness.models.Challenge;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

/**
 * Adapter for Friends Activity
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    private Context context;
    private List<ParseUser> friends;

    public FriendsAdapter(Context context, List<ParseUser> friends) {
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       ParseUser user = friends.get(position);
       holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivProfileImage;
        TextView tvScreenName;
        ParseUser user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
        }

        public void bind(ParseUser parseUser) {
            user = parseUser;
            tvScreenName.setText(user.getUsername());
            ParseFile profileImage = user.getParseFile("image");
            if ( profileImage != null) {
                Glide.with(context).load(profileImage.getUrl()).centerInside().into(ivProfileImage);
            }
        }

        @Override
        public void onClick(View view) {
            Activity activity = (Activity) context;
            GenericFragment.goUserFragment(user, activity);
        }
    }
}
