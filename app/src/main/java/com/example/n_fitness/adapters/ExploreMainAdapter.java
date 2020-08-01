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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapter for List of rows in the explore page for viewing all posts (workouts) by category
 */
public class ExploreMainAdapter extends RecyclerView.Adapter<ExploreMainAdapter.ViewHolder> {

    private Context context;
    private Map<Category, List<Post>> rows;
    private List<Category> categories;
    List<List<Post>> otherRows;

    public ExploreMainAdapter(Context context, Map<Category, List<Post>> objects, List<Category> categories, List<List<Post>> posts) {
        this.context = context;
        this.rows = objects;
        this.categories = categories;
        this.otherRows = posts;
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
        List<Post> rowPosts = new ArrayList<>();
        ExploreRowAdapter rowsAdapter = new ExploreRowAdapter(context, rowPosts);
        if (position == 0) {
            //popular workouts
            holder.tvRowTitle.setText("Popular");
            rowPosts.addAll(otherRows.get(0));
            rowsAdapter.notifyDataSetChanged();
        } else {
            Category category = categories.get(position - 1);
            holder.tvRowTitle.setText(category.getName());
            if (rows.get(category) != null) {
                rowPosts.addAll(rows.get(category));
                rowsAdapter.notifyDataSetChanged();
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.rvRow.setLayoutManager(layoutManager);
        holder.rvRow.setHasFixedSize(true);
        holder.rvRow.setAdapter(rowsAdapter);

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
