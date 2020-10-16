package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeTweet extends AppCompatActivity {
    ImageView returnImage;
    TextView userNameCompose;
    TextView goWithCompose;
    ImageView userImageCompose;
    EditText textCompose;
    Button tweetButton;
    TextView countCompose;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        returnImage = findViewById(R.id.returnImage);
        userImageCompose = findViewById(R.id.userImageCompose);
        userNameCompose = findViewById(R.id.userNameCompose);
        goWithCompose = findViewById(R.id.goWithCompose);
        textCompose = findViewById(R.id.textCompose);
        tweetButton = findViewById(R.id.tweetButton);
        countCompose = findViewById(R.id.countCompose);
        client = TwitterApp.getRestClient(this);
        countCompose.setText("0/280");
        tweetButton.setEnabled(false);
        Glide.with(this).asBitmap().load(R.drawable.cancel).into(returnImage);



        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    Glide.with(ComposeTweet.this).asBitmap().load(jsonObject.getString("profile_image_url")).into(userImageCompose);
                    goWithCompose.setText("@"+jsonObject.getString("screen_name"));
                    userNameCompose.setText(jsonObject.getString("name"));
                } catch (JSONException e) {
                    Log.d("Compose","cant load data");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });


        textCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 280 || count == 0){
                    tweetButton.setEnabled(false);
                }
                else{
                    tweetButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                countCompose.setText(s.toString().length()+"/280");
            }
        });

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.postTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("Compose","good");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            Tweet tempTweet = Tweet.fromJson(jsonObject);
                            Intent intent = new Intent();
                            Parcelable tweet = Parcels.wrap(tempTweet);
                            intent.putExtra("tweet",tweet);
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Compose","bad");
                    }
                },textCompose.getText().toString());
            }
        });

        returnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });


    }
}