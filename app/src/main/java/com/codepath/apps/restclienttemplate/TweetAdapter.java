package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    public static final int REQUEST_CODE = 21;
    private static final int RESULT_OK = -1;
    List<Tweet> tweets;
    Context context;
    TwitterClient client;

    public TweetAdapter(Context context, List<Tweet> tweets, TwitterClient client){
        this.context = context;
        this.tweets = tweets;
        this.client = client;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        holder.name.setText(tweet.user.name);
        holder.body.setText(tweet.body);
        holder.screenName.setText("@"+tweet.user.screenName);
        String timeDif = TimeFormatter.getTimeDifference(tweet.createdAt);
        holder.timeago.setText(timeDif);
        Glide.with(context).asBitmap().load(tweet.user.publicImageUrl).into(holder.profileImage);
        if(tweet.emUrl != null){
            Glide.with(context).asBitmap().load(tweet.emUrl).into(holder.embedImageScreen);
        }
        else{
            holder.embedImageScreen.setMaxHeight(0);
        }
        holder.tweetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailTweet.class);
                Parcelable tempTweet = Parcels.wrap(tweet);
                intent.putExtra("tweet",tempTweet);
                context.startActivity(intent);
            }
        });

        Glide.with(context).asBitmap().load(R.drawable.retweet).into(holder.reTweet);
        if(tweet.retweeted == true){
            holder.reTweet.setColorFilter(Color.GREEN);
        }
        holder.reTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.retweeted == false){
                    client.reTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d("Retweet", "Success");
                            tweet.retweeted = true;
                            holder.reTweet.setColorFilter(Color.GREEN);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d("Retweet", "Fail");
                        }
                    },String.valueOf(tweet.id));
                }
                else{
                    client.unreTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d("unRetweet", "Success");
                            tweet.retweeted = false;
                            holder.reTweet.setColorFilter(R.color.aqua);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d("unRetweet", "Fail");
                        }
                    },String.valueOf(tweet.id));
                }

            }
        });

        Glide.with(context).asBitmap().load(R.drawable.reply).into(holder.reply);
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ReplyActivity.class);
                Activity temp = (Activity) context;
                Parcelable tempTweet = Parcels.wrap(tweet);
                intent.putExtra("tweet",tempTweet);
                temp.startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Toast.makeText(context,"replied",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list){
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;
        TextView body;
        TextView timeago;
        View tweetLayout;
        TextView screenName;
        ImageView embedImageScreen;
        ImageView reTweet;
        ImageView reply;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.tvProfileImage);
            name = itemView.findViewById(R.id.tvScreenName);
            body = itemView.findViewById(R.id.tvBody);
            timeago = itemView.findViewById(R.id.timeago);
            tweetLayout = itemView.findViewById(R.id.tweetLayout);
            screenName = itemView.findViewById(R.id.goWith);
            embedImageScreen = itemView.findViewById(R.id.embedImageScreen);
            reTweet = itemView.findViewById(R.id.reTweet);
            reply = itemView.findViewById(R.id.reply);
        }
    }
}
