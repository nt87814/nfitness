package com.example.n_fitness;

import com.example.n_fitness.fragments.ProfileFragment;
import com.example.n_fitness.models.Category;
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.ParseQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * local unit test, which will execute on the development machine (host).
 * <p>
 * tests the correct top category given a list of challenges
 */
public class TopCategoryTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void topCategory_isCorrect() {
        List<Challenge> completedChallenges = new ArrayList<>();
        String topCategory = "endurance";
        ParseQuery<Challenge> query1 = ParseQuery.getQuery(Challenge.class);
        query1.include(Post.KEY_CATEGORY);
        query1.include(Category.KEY_NAME);
        query1.whereEqualTo(Category.KEY_NAME, topCategory);
        query1.setLimit(15);
        query1.findInBackground((challenges, e) -> {
            if (e != null) {
                return;
            }

            completedChallenges.addAll(challenges);
        });

        ParseQuery<Challenge> query2 = ParseQuery.getQuery(Challenge.class);
        query2.include(Post.KEY_CATEGORY);
        query2.include(Category.KEY_NAME);
        query2.whereEqualTo(Category.KEY_NAME, "strength");
        query2.setLimit(10);
        query2.findInBackground((challenges, e) -> {
            if (e != null) {
                return;
            }

            completedChallenges.addAll(challenges);
            assertEquals(topCategory, ProfileFragment.getTopCategory(completedChallenges));
        });

    }
}
