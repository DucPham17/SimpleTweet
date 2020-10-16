package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    @PrimaryKey
    public long idUser;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String publicImageUrl;


    @ColumnInfo
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

    public static List<User> fromJsonTweetArray(List<Tweet> list){
        List<User> res = new ArrayList<>();
        for(Tweet tweet : list){
            res.add(tweet.user);
        }
        return res;
    }

}
