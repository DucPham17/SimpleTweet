package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "idUser", childColumns = "userId"))
public class Tweet {
    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @Ignore
    public User user;

    @ColumnInfo
    public String emUrl;

    @ColumnInfo
    public String videoUrl;

    @ColumnInfo
    public long userId;

    @ColumnInfo
    public boolean retweeted;

    public Tweet(){}
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = Long.parseLong(jsonObject.getString("id_str"));
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"),tweet.id);
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.userId = tweet.user.idUser;
        if(!jsonObject.getJSONObject("entities").isNull("media")){
            //Log.d("data",String.valueOf(jsonObject.getJSONObject("entities").getJSONArray("media")));
            tweet.emUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
        }
        else{
            tweet.emUrl = null;
        }
        if(!jsonObject.isNull("extended_entities")){
            Log.d("data",String.valueOf(jsonObject.getJSONObject("extended_entities").getJSONArray("media")));
            if(String.valueOf(jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).get("type")).equals("video")){

                tweet.videoUrl = jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).getString("url");
            }

        }
        else{
            tweet.videoUrl = null;
        }

  //      tweet.url = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> list = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            Tweet temp = fromJson( jsonArray.getJSONObject(i));
            list.add(temp);

        }
        return list;
    }
}
