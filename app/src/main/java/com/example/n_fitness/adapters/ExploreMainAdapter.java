package com.example.n_fitness.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.n_fitness.R;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Post;

import java.util.List;

/**
 * Adapter for List of rows in the explore page for viewing all posts (workouts) by category
 */
public class ExploreMainAdapter extends RecyclerView.Adapter<ExploreMainAdapter.ViewHolder> {

    private Context context;
    private List<List<Post>> rows;
    private List<Category> categories;

    public ExploreMainAdapter(Context context, List<List<Post>> objects, List<Category> categories) {
        this.context = context;
        rows = objects;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.row, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Post> rowPosts = rows.get(position);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.rvRow.setLayoutManager(layoutManager);
        holder.rvRow.setHasFixedSize(true);
        ExploreRowAdapter rowsAdapter = new ExploreRowAdapter(context, rowPosts);
        holder.rvRow.setAdapter(rowsAdapter);
        if (position == 0) {
            //popular workouts
            holder.tvRowTitle.setText("Popular");
        } else {
            holder.tvRowTitle.setText(categories.get(position - 1).getName());
        }

    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView rvRow;
        public TextView tvRowTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvRow = itemView.findViewById(R.id.rvRow);
            tvRowTitle = itemView.findViewById(R.id.tvRowTitle);
        }

    }
}
