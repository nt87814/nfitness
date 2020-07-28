package com.example.n_fitness.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Model for categories that workouts correspond to
 */
@ParseClassName("Category")
public class Category extends ParseObject {
    public static final String KEY_NAME = "name";


    public String getName() {
        return (String) get(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }
}
