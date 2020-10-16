package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    // @Embedded notation flattens the properties of the User object into the object, preserving encapsulation.
    @Embedded
    User user;

    // Prefix is needed to resolve ambiguity between fields: user.id and tweet.id, user.createdAt and tweet.createdAt
    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetWithUser(List<TweetWithUser> list){
        List<Tweet> res = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            Tweet tweet = list.get(i).tweet;
            User user = list.get(i).user;
            tweet.user = user;
            res.add(tweet);
        }

        return res;
    }
}
