package com.example.n_fitness.models;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

/**
 * Model for workouts that have been challenged to a user
 */
@ParseClassName("Challenge")
public class Challenge extends ParseObject {
    public static final String KEY_DEADLINE = "deadline";
    public static final String KEY_POST = "post";
    public static final String KEY_FROM = "from";
    public static final String KEY_REC = "recipient";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_LOCATION = "latlng";

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public ParseUser getFrom() {
        return getParseUser(KEY_FROM);
    }

    public void setFrom(ParseUser user) {
        put(KEY_FROM, user);
    }

    public ParseUser getRecipient() {
        return getParseUser(KEY_REC);
    }

    public void setRecipient(ParseUser user) {
        put(KEY_REC, user);
    }

    public Date getDeadline() {
        return getDate(KEY_DEADLINE);
    }

    public void setDeadline() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7); // Adding 7 days
        put(KEY_DEADLINE, c.getTime());
    }

    public Date getCompleted() {
        return getDate(KEY_COMPLETED);
    }

    public void setCompleted() {
        Date date1 = new Date();
        put(KEY_COMPLETED, date1);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(Location location) {
        if (location != null) {
            ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            put(KEY_LOCATION, point);
        }
    }
}
