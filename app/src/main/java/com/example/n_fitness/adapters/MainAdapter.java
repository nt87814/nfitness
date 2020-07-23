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
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Context context;
    private List<List<Post>> mRows;
    private List<Category> categories;

    public MainAdapter(Context context, List<List<Post>> objects, List<Category> categories) {
        this.context = context;
        mRows = objects;
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
        List<Post> rowPosts = mRows.get(position);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.rvRow.setLayoutManager(layoutManager);
        holder.rvRow.setHasFixedSize(true); //?
        RowAdapter rowsAdapter = new RowAdapter(context, rowPosts);
        holder.rvRow.setAdapter(rowsAdapter);
        holder.tvRowTitle.setText(categories.get(position).getName());

        final RecyclerView finalRecyclerView = holder.rvRow;
        finalRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRows.size();
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
