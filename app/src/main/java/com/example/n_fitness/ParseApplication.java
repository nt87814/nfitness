package com.example.n_fitness;

import android.app.Application;

/**
 * Class for for Application level setup
 *
 * This is used to set up the database to share across entire application
 *
 * */
import android.app.Application;

//import com.example.parstagram.models.Post;
//import com.parse.Parse;
//import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        ParseObject.registerSubclass(Post.class);
//
//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId("n-parstagram")
//                .clientKey("CodepathMoveFastParse")
//                .server("https://n-parstagram.herokuapp.com/parse/").build());
    }
}
