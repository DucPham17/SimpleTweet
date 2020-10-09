package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    List<Tweet> tweets;
    Context context;

    public TweetAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        holder.name.setText(tweet.user.name);
        holder.body.setText(tweet.body);
        holder.screenName.setText("@"+tweet.user.screenName);
        String timeDif = TimeFormatter.getTimeDifference(tweet.createdAt);
        holder.timeago.setText(timeDif);
        Glide.with(context).asBitmap().load(tweet.user.publicImageUrl).into(holder.profileImage);
        holder.tweetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailTweet.class);
                Parcelable tempTweet = Parcels.wrap(tweet);
                intent.putExtra("tweet",tempTweet);
                context.startActivity(intent);
            }
        });
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.tvProfileImage);
            name = itemView.findViewById(R.id.tvScreenName);
            body = itemView.findViewById(R.id.tvBody);
            timeago = itemView.findViewById(R.id.timeago);
            tweetLayout = itemView.findViewById(R.id.tweetLayout);
            screenName = itemView.findViewById(R.id.goWith);
        }
    }
}
