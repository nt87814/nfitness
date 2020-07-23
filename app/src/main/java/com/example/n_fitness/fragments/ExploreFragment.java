package com.example.n_fitness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.n_fitness.R;
import com.example.n_fitness.adapters.MainAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment";

    private RecyclerView rvRootView;
    private RecyclerView.Adapter adapter;
    private MainAdapter mainAdapter;
    private LinearLayoutManager layoutManager;
    private List<Category> allCategories;
    List<List<Post>> listOfListOfPosts;


    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRootView = view.findViewById(R.id.rvRootView);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRootView.setLayoutManager(layoutManager);
        rvRootView.setHasFixedSize(true);

        listOfListOfPosts = new ArrayList<List<Post>>();

        allCategories = new ArrayList<>();

        mainAdapter = new MainAdapter(getContext(), listOfListOfPosts, allCategories);
        rvRootView.setAdapter(mainAdapter);
        rvRootView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        queryCategories();
    }

    private void queryCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.include(Category.KEY_NAME);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting categories", e);
                    Toast.makeText(getContext(), "Issue with getting categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Category category : categories) {
                    queryPosts(category);
                    Log.i(TAG, "Category: " + category.getName());
                }
                allCategories.addAll(categories);
            }
        });
    }

    private void queryPosts(Category c) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_CATEGORY);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_DESCRIPTION);
        query.include(Post.KEY_LIKES);
        query.whereEqualTo(Post.KEY_CATEGORY, c);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting categories", e);
                    Toast.makeText(getContext(), "Issue with getting posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Post post : objects) {
                    Log.i(TAG, "Post: " + post.getDescription());
                }

                listOfListOfPosts.add(objects);
                mainAdapter.notifyDataSetChanged();
            }
        });
    }
}