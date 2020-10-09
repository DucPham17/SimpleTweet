package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;


import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static final String TAG = "TimelineActivity";
    TwitterClient client;
    RecyclerView recyclerView;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    long max = Long.MAX_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        toolbar = findViewById(R.id.toolbar);
        client = TwitterApp.getRestClient(this);
        recyclerView = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(TimelineActivity.this,tweets);
        recyclerView.setAdapter(tweetAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TimelineActivity.this);

        recyclerView.setLayoutManager(linearLayoutManager);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        populateHomeTimeline();
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreData();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void populateHomeTimeline(){
        tweetAdapter.clear();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                JSONArray jsonArray = json.jsonArray;
                try {
                    Log.d(TAG,"load data" + jsonArray.length());
                    List<Tweet> temp = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(temp);
                    tweetAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.d(TAG,"cant load data");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG,"fail");
            }
        });
    }
    private void loadMoreData(){
        Log.d(TAG,"infinite" + "process");
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    Log.d(TAG,"infinite" + jsonArray.length());
                    List<Tweet> temp = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(temp);
                    tweetAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.d(TAG,"cant load data");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG,"infinite" + "fail");
            }
        },Long.parseLong(String.valueOf(tweets.get(tweets.size()-1).id)));
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
}