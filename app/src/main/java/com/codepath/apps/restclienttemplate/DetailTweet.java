package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailTweet extends AppCompatActivity {
    ImageView profileImage;
    TextView name;
    TextView body;
    TextView timeago;
    ImageView emImage;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet);
        final Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        profileImage = findViewById(R.id.detailImage);
        name = findViewById(R.id.detailName);
        body = findViewById(R.id.detailBody);
        timeago = findViewById(R.id.detailTimeago);
        Glide.with(this).asBitmap().load(tweet.user.publicImageUrl).into(profileImage);
        name.setText(tweet.user.name);
        body.setText(tweet.body);
        timeago.setText(TimeFormatter.getTimeDifference(tweet.createdAt));
        emImage = findViewById(R.id.emImage);
        if(tweet.emUrl != null){
            Log.d("Detail","exist");
            Glide.with(this).asBitmap().load(tweet.emUrl).into(emImage);
        }
        else{
            Log.d("Detail","not exist");
        //    emImage.setVisibility(View.INVISIBLE);
        }
        videoView = findViewById(R.id.video_player_1);
        if(tweet.videoUrl != null){
        //    emImage.setVisibility(View.INVISIBLE);
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(tweet.videoUrl));
            videoView.start();
        }
        else {
            videoView.setVisibility(View.INVISIBLE);
        }
    }
}