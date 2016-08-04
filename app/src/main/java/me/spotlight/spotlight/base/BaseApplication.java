package me.spotlight.spotlight.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseSession;
import com.parse.PushService;
import com.parse.SaveCallback;
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

    private static final String GCM_API_KEY = "AIzaSyDpmBCKxEDNkxSIF2v-nGY9AWDFBoUD3ic";
    private static final String GCM_PROJECT_NUMBER = "606167943462";
    private static final String GCM_PROJECT_ID = "spotlight-1470214291491n";

    private static final String TWITTER_KEY = "OYaQ9xpbGKHyUUnZRMqfSK2uP";
    private static final String TWITTER_SECRET = "JVzIk5VT6YAMkrU7lwJRDLXsm39TgPG6HSMr9ZEygX1gRP55bQ";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Parse.enableLocalDatastore(getContext());
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (null == e) {
                    Log.d("push", "push success");
                } else {
                    Log.d("push", "push success");
                }
            }
        });


        FacebookSdk.sdkInitialize(context);
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(twitterAuthConfig));
    }

    public static Context getContext() { return context; }
}
