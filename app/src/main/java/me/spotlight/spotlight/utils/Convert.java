package me.spotlight.spotlight.utils;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import me.spotlight.spotlight.models.Child;
import me.spotlight.spotlight.models.Friend;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.models.User;

/**
 * Created by Anatol on 8/16/2016.
 */
public class Convert {

    public static final String TAG = "Convert";

    public static User toUser(ParseUser parseUser) {
        User user = new User();
        try {
            parseUser.fetchIfNeeded();
            if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC)) {
                ParseObject profilePic = parseUser.getParseObject(ParseConstants.FIELD_USER_PIC);
                profilePic.fetchIfNeeded();
                user.setAvatarUrl(profilePic.getParseFile("mediaFile").getUrl());
            }
            user.setObjectId(parseUser.getObjectId());
            user.setFirstName(parseUser.getString("firstName"));
            user.setLastName(parseUser.getString("lastName"));
        } catch (ParseException e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching user : parse exception");
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching user : exception");
        }
        return user;
    }

    public static Friend toFriend(ParseUser parseUser) {
        Friend user = new Friend();
        try {
            parseUser.fetchIfNeeded();
            if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC)) {
                ParseObject profilePic = parseUser.getParseObject(ParseConstants.FIELD_USER_PIC);
                profilePic.fetchIfNeeded();
                user.setAvatarUrl(profilePic.getParseFile("mediaFile").getUrl());
            }
            user.setObjectId(parseUser.getObjectId());
            user.setFirstName(parseUser.getString("firstName"));
            user.setLastName(parseUser.getString("lastName"));
        } catch (ParseException e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching user : parse exception");
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching user : exception");
        }
        return user;
    }

    public static Child toChild(ParseObject parseObject) {
        Child child = new Child();
        try {
            parseObject.fetchIfNeeded();
            if (null != parseObject.getParseObject("profilePic")) {
                ParseObject profilePic = parseObject.getParseObject("profilePic");
                profilePic.fetchIfNeeded();
                ParseFile mediaFile = profilePic.getParseFile("mediaFile");
                child.setAvatarUrl(mediaFile.getUrl());
            }
            child.setObjectId(parseObject.getObjectId());
            if (null != parseObject.getString(ParseConstants.FIELD_CHILD_FIRST))
                child.setFirstName(parseObject.getString(ParseConstants.FIELD_CHILD_FIRST));
            if (null != parseObject.getString(ParseConstants.FIELD_CHILD_LAST))
                child.setLastName(parseObject.getString(ParseConstants.FIELD_CHILD_LAST));
        } catch (ParseException e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching child : parse exception");
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching child : exception");
        }
        return child;
    }

    public static Team toTeam(ParseObject parseObject) {
        Team team = new Team();
        try {
            parseObject.fetchIfNeeded();
            team.setObjectId(parseObject.getObjectId());
            if (null != parseObject.getString(ParseConstants.FIELD_TEAM_NAME)) {
                if (!"".equals(parseObject.getString(ParseConstants.FIELD_TEAM_NAME))) {
                    team.setName(parseObject.getString(ParseConstants.FIELD_TEAM_NAME));
                }
            }
            if (null != parseObject.getString(ParseConstants.FIELD_TEAM_GRADE)) {
                if (!"".equals(parseObject.getString(ParseConstants.FIELD_TEAM_GRADE))) {
                    team.setGrade(parseObject.getString(ParseConstants.FIELD_TEAM_GRADE));
                }
            }
            if (null != parseObject.getString(ParseConstants.FIELD_TEAM_SPORT)) {
                if (!"".equals(parseObject.getString(ParseConstants.FIELD_TEAM_SPORT))) {
                    team.setSport(parseObject.getString(ParseConstants.FIELD_TEAM_SPORT));
                }
            }
            if (null != parseObject.getString(ParseConstants.FIELD_TEAM_SEASON)) {
                if (!"".equals(parseObject.getString(ParseConstants.FIELD_TEAM_SEASON))) {
                    team.setSeason(parseObject.getString(ParseConstants.FIELD_TEAM_SEASON));
                }
            }
            if (null != parseObject.getString(ParseConstants.FIELD_TEAM_YEAR)) {
                if (!"".equals(parseObject.getString(ParseConstants.FIELD_TEAM_YEAR))) {
                    team.setYear(parseObject.getString(ParseConstants.FIELD_TEAM_YEAR));
                }
            }
            if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA)) {
                parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).fetchIfNeeded();
                if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile")) {
                    if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl()) {
                        team.setAvatarUrl(parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl());
                    }
                }
            }
        } catch (ParseException e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching team : parse exception");
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : " fetching team : exception");
        }
        return team;
    }
}
