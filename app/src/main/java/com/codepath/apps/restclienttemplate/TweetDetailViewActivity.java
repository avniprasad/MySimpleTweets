package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailViewActivity extends AppCompatActivity {

    private TwitterClient client;
    private AsyncHttpResponseHandler handler;

    // the Tweet to display
    private Tweet tweet;

    // the view objects
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvBody;
    private TextView tvHandleName;
    private TextView tvTimestamp;
    private TextView tvRetweets;
    private TextView tvLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail_view);

        // create client
        client = TwitterApp.getRestClient(this);
        handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tweet = Tweet.fromJSON(response);
                    Log.d("Reply", "This is the message " + response.getString("text"));
                    Intent i = new Intent(TweetDetailViewActivity.this, TimelineActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(20, i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        };


        // perform findViewById lookups
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUserName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvHandleName = (TextView) findViewById(R.id.tvHandleName);
        tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
        tvRetweets = (TextView) findViewById(R.id.tvRetweets);
        tvLikes = (TextView) findViewById(R.id.tvLikes);

        // unwrap the tweet passed in via intent, using its simple name as a key
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
       //  Log.d("TweetDetailsActivity", String.format("Showing details for '%s'", tweet.getTitle()));

        // set the text in the view object
        tvUsername.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvHandleName.setText("@" + tweet.getHandleName());
        tvTimestamp.setText(tweet.getCreateAt());
        tvRetweets.setText(tweet.retweet_count + " Retweets");
        tvLikes.setText(tweet.like_count + " Likes");

        GlideApp.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(75, 0))
                .into(ivProfileImage);

    }

    public void response(View v) {
        EditText etResponse = (EditText) findViewById(R.id.etResponse);
        etResponse.setText("@" + tweet.getHandleName());
    }

    public void reply(View v) {
        EditText etResponse = (EditText) findViewById(R.id.etResponse);
        String message = etResponse.getText().toString();
        client.sendTweet(message, handler);
    }
}
