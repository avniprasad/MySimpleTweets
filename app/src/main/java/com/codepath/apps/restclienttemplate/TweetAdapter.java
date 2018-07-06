package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private final Handlers mHandler;
    private List<Tweet> mTweets;
    private Context context;

    AsyncHttpResponseHandler handler = new JsonHttpResponseHandler();
    TwitterClient client = TwitterApp.getRestClient(context);

    interface Handlers {
        void onItemClicked(Tweet t, Context c);
    }

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, Handlers handler) {
        mTweets = tweets;
        mHandler = handler;
    }

    // for each row, inflate the layout and cache reference into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        String date = getRelativeTimeAgo(tweet.createAt);
        holder.tvTimestamp.setText(date);
        holder.tvHandleName.setText("@" + tweet.handleName);
        holder.tvRetweets.setText(tweet.retweet_count);
        holder.tvLikes.setText(tweet.like_count);

        holder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.retweetTweet(mTweets.get(position).getUid(), handler);
                Toast.makeText(context, "Retweeted", Toast.LENGTH_LONG).show();
            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               client.likeTweet(mTweets.get(position).getUid(), handler);
                Toast.makeText(context, "Liked Tweet", Toast.LENGTH_LONG).show();
            }
        });

        GlideApp.with(context)
                .load(tweet.user.getProfileImageUrl())
                .transform(new RoundedCornersTransformation(75, 0))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvHandleName;
        public TextView tvTimestamp;
        public TextView tvRetweets;
        public TextView tvLikes;
        public ImageView ibRetweet;
        public ImageView ibLike;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvHandleName = (TextView) itemView.findViewById(R.id.tvHandleName);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            tvRetweets = (TextView) itemView.findViewById(R.id.tvRetweets);
            tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
            ibRetweet = (ImageView) itemView.findViewById(R.id.ibRetweet);
            ibLike = (ImageView) itemView.findViewById(R.id.iBLike);

            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show TweetDetailViewActivity for the selected tweet
        @Override
        public void onClick(View view) {
            // gets item position
            Log.d("TweetAdapter", String.format("Something was clicked"));
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                Log.d("TweetAdapter", String.format("Got the tweet"));
                mHandler.onItemClicked(tweet, context);
            }
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
