package me.spotlight.spotlight.base;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Anatol on 7/10/2016.
 */
public class BaseApplication extends Application {

    static Context context;

    private static final String TWITTER_KEY = "W4vmDjrSiLj6znKtnQ2PAUmft";
    private static final String TWITTER_SECRET = "CYkUwY5sdkev7dlRrA2OjvrdA2MVcrQW1BxJyfe4hUckKhiEm4";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(context);
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(twitterAuthConfig));
    }

    public static Context getContext() { return context; }
}
