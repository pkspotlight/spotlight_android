package me.spotlight.spotlight.features.teams.details;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 8/31/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class TeamMembersPresenter {

    public static final String TAG = "TeamMembersPresenter";
    private TeamMembersContract contract;
    List<String> friendIds = new ArrayList<>();

    public TeamMembersPresenter(TeamMembersContract teamMembersContract) {
        this.contract = teamMembersContract;
    }

    public void fetchFriendIds() {
        try {
            contract.showProgress(true);
            if (!friendIds.isEmpty())
                friendIds.clear();
            ParseRelation friendsRel = ParseUser.getCurrentUser().getRelation(ParseConstants.FIELD_USER_FRIENDS);
            ParseQuery<ParseUser> friendsQuery = friendsRel.getQuery();
            friendsQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (null == e) {
                        if (!objects.isEmpty()) {
                            for (ParseUser parseUser : objects) {
                                try {
                                    friendIds.add(parseUser.getObjectId());
                                } catch (Exception e1) {
                                    Log.d(TAG, "");
                                }
                            }
                            contract.onFriendIdsFetched(friendIds);
                            Log.d(TAG, "Number of friends: " + String.valueOf(friendIds.size()));
                        } else {
                            Log.d(TAG, "");
                        }
                    } else {
                        Log.d(TAG, "");
                    }
                }
            });
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }
}
