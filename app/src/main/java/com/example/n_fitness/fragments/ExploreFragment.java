package com.example.n_fitness.fragments;

import android.os.Bundle;
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
import com.example.n_fitness.adapters.ExploreMainAdapter;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment for viewing all of the workouts by category
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";

    private ExploreMainAdapter mainAdapter;
    private List<Category> allCategories;
    List<List<Post>> listOfListOfPosts;
    Map<Category, List<Post>> mapOfPostLists;


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
        RecyclerView rvRootView = view.findViewById(R.id.rvRootView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRootView.setLayoutManager(layoutManager);
        rvRootView.setHasFixedSize(true);

        listOfListOfPosts = new ArrayList<>();
        mapOfPostLists = new HashMap<>();
        allCategories = new ArrayList<>();

        mainAdapter = new ExploreMainAdapter(getContext(), mapOfPostLists, allCategories, listOfListOfPosts);
        rvRootView.setAdapter(mainAdapter);
        queryCategories();
        queryPopularPosts();
    }

    private void queryCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.include(Category.KEY_NAME);
        query.orderByAscending(Category.KEY_NAME);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Category category : categories) {
                    queryPosts(category);
                }
                allCategories.addAll(categories);
                mainAdapter.notifyDataSetChanged();
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
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                listOfListOfPosts.add(objects);
                mapOfPostLists.put(c, objects);
                mainAdapter.notifyDataSetChanged();
            }
        });
    }

    private void queryPopularPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_DESCRIPTION);
        query.include(Post.KEY_LIKES);
        query.orderByDescending(Post.KEY_NUM_LIKES);
        query.setLimit(5);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                listOfListOfPosts.add(0, objects);
                mapOfPostLists.put(new Category(), objects);
                mainAdapter.notifyDataSetChanged();
            }
        });
    }
}