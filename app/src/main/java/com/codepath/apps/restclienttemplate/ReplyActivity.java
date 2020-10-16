package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {
    EditText replyText;
    Button repButton;
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        replyText = findViewById(R.id.replyText);
        repButton = findViewById(R.id.repButton);
        client = TwitterApp.getRestClient(this);
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        final String name = "@"+tweet.user.screenName;
        final String id = String.valueOf(tweet.id);
        Log.d("Reply",id);
        repButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.replyTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("Reply","good");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Reply","Fail");
                    }
                },name+replyText.getText(),id);
            }
        });

    }
}