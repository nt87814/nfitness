package com.example.n_fitness;

import android.app.Application;

/**
 * Class for for Application level setup
 *
 * This is used to set up the database to share across entire application
 *
 * */
import com.example.n_fitness.models.Challenge;
import com.example.n_fitness.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Challenge.class);
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("n-fitness")
                .clientKey("CodepathMoveFastParse")
                .server("https://n-fitness.herokuapp.com/parse/").build());
    }
}
