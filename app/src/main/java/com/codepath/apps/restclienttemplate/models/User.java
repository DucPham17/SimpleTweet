package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel

public class User {

    public String name;


    public String screenName;


    public String publicImageUrl;


    public long idUser;


    public Long tweetId;
    public User(){};
    public static User fromJson(JSONObject jsonObject,Long tweetId) throws JSONException {
        User user = new User();
        user.tweetId = tweetId;
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.idUser = Long.parseLong(jsonObject.getString("id_str"));
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }

}
