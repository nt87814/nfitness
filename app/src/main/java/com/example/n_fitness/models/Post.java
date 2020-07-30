package com.example.n_fitness.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Model to represent workouts
 */
@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_NUM_LIKES = "numLikes";


    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public Category getCategory() {
        return (Category) getParseObject(KEY_CATEGORY);
    }

    public void setCategory(Category category) {
        put(KEY_CATEGORY, category);
    }

    public ArrayList<ParseUser> getLikes() {
        ArrayList<ParseUser> userlikes = new ArrayList<>();
        ParseRelation likes = getRelation(KEY_LIKES);
        ParseQuery queryLikes = likes.getQuery();
        try {
            userlikes = (ArrayList<ParseUser>) queryLikes.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return userlikes;
    }

    public void addLike(ParseUser user) {
        ParseRelation likes = getRelation(KEY_LIKES);
        likes.add(user);
        int newNumLikes = getNumLikes() + 1;
        put(KEY_NUM_LIKES, newNumLikes);
    }

    public int getNumLikes() {
        return getInt(KEY_NUM_LIKES);
    }

}
