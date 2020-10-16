package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 20;

    Toolbar toolbar;
    public static final String TAG = "TimelineActivity";
    TwitterClient client;
    RecyclerView recyclerView;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    TweetDao tweetDao;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        toolbar = findViewById(R.id.toolbar);
        client = TwitterApp.getRestClient(this);
        floatingActionButton = findViewById(R.id.floatingButton);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        recyclerView = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(TimelineActivity.this,tweets,client);
        recyclerView.setAdapter(tweetAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TimelineActivity.this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this,ComposeTweet.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("Show data based","showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> list = TweetWithUser.getTweetWithUser(tweetWithUsers);
                tweets.clear();
                tweets.addAll(list);
                tweetAdapter.notifyDataSetChanged();
            }
        });
        populateHomeTimeline();
    }

    private void populateHomeTimeline(){
        tweetAdapter.clear();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                JSONArray jsonArray = json.jsonArray;
                try {
                    Log.d(TAG,"load data" + jsonArray.length());
                    final List<Tweet> tweetFromNetwork = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(tweetFromNetwork);
                    tweetAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Show data based","showing data into database");
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetFromNetwork.toArray(new Tweet[0]));
                        }
                    });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.miCompose){
            Intent intent = new Intent(this,ComposeTweet.class);
            startActivityForResult(intent,REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Tweet tempTweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0,tempTweet);
            tweetAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}