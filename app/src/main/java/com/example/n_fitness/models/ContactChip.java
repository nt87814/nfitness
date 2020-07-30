package com.example.n_fitness.models;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.parse.ParseUser;
import com.pchmn.materialchips.model.ChipInterface;

/**
 * Model for users that are being selected in the create challenge fragment
 */
public class ContactChip implements ChipInterface {

    private String id;
    private String name;
    private ParseUser user;

    public ContactChip(String id, String name, ParseUser user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return null;
    }

    public ParseUser getUser() {
        return user;
    }
}
