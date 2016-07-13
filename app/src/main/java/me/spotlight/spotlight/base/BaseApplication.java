package me.spotlight.spotlight.base;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Anatol on 7/10/2016.
 */
public class BaseApplication extends Application {

    static Context context;

    private static final String PARSE_APP_ID = "nuNuhBJQp4cYfeUnWlNFo27QUCKeAgWBX5D74r4F";
    private static final String PARSE_CLIENT_KEY = "vMH2XfoFKQAy8vbOYzgXZtJrRJ8LjCD5933k3kPF";

    private static final String TWITTER_KEY = "W4vmDjrSiLj6znKtnQ2PAUmft";
    private static final String TWITTER_SECRET = "CYkUwY5sdkev7dlRrA2OjvrdA2MVcrQW1BxJyfe4hUckKhiEm4";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        FacebookSdk.sdkInitialize(context);
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(twitterAuthConfig));
    }

    public static Context getContext() { return context; }
}
