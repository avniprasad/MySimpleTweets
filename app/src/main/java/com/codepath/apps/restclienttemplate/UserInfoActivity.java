package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UserInfoActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvHandleName;
    TextView tvFollowers;
    TextView tvFollowing;
    TextView tvLikes;
    TextView tvTweets;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUserName);
        tvHandleName = (TextView) findViewById(R.id.tvHandleName);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        tvLikes = (TextView) findViewById(R.id.tvLikes);
        tvTweets = (TextView) findViewById(R.id.tvTweets);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        user = tweet.user;

        tvUsername.setText(user.getName());
        tvHandleName.setText("@" + user.getScreenName());
        tvFollowers.setText("Followers: " + user.followers);
        tvFollowing.setText("Following: " + user.followingNum);
        tvLikes.setText("Likes: " + user.likes);
        tvTweets.setText("Tweets: " + user.tweets);

        GlideApp.with(this)
                .load(user.getProfileImageUrl())
                .transform(new RoundedCornersTransformation(75, 0))
                .into(ivProfileImage);
    }
}
