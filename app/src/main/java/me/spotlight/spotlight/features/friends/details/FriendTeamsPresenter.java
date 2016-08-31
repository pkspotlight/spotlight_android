package me.spotlight.spotlight.features.friends.details;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anatol on 8/31/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class FriendTeamsPresenter {

    public static final String TAG = "FriendTeamsPresenter";
    private FriendTeamsContract contract;
    private List<String> myTeamsIds = new ArrayList<>();

    public FriendTeamsPresenter(FriendTeamsContract friendTeamsContract) {
        this.contract = friendTeamsContract;
    }

    public void fetchTeamsIds() {
        try {
            contract.showProgress(true);
            ParseRelation teamsRel = ParseUser.getCurrentUser().getRelation("teams");
            ParseQuery<ParseObject> teamsQ = teamsRel.getQuery();
            teamsQ.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (null == e) {
                        if (!objects.isEmpty()) {
                            for (ParseObject parseObject : objects) {
                                myTeamsIds.add(parseObject.getObjectId());
                            }
                            contract.onTeamsIdsFetched(myTeamsIds);
                        } else {
                            //
                        }
                    } else {
                        //
                    }
                }
            });
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }
}
