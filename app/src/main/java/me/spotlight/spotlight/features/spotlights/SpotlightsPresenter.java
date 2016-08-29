package me.spotlight.spotlight.features.spotlights;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.Convert;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 8/21/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class SpotlightsPresenter {

    public static final String TAG = "SpotlightsPresenter";
    private SpotlightsContract contract;
    private int skip = 0;
    private int child = 0;
    final List<SpotlightMedia> data = new ArrayList<>();

    public SpotlightsPresenter(SpotlightsContract spotlightsContract) {
        this.contract = spotlightsContract;
    }

    private FindCallback<ParseObject> skippy() {
        return new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    for (ParseObject parseObject : objects) {
                        try {
                            parseObject.fetchIfNeeded();
                            SpotlightMedia spotlightMedia = new SpotlightMedia();
                            spotlightMedia.setObjectId(parseObject.getObjectId());
                            Log.d(TAG, parseObject.getObjectId());
                            if (null != parseObject.getParseFile("mediaFile")) {
                                spotlightMedia.setFileUrl(parseObject.getParseFile("mediaFile").getUrl());
                            }
                            if (null != parseObject.getParseFile("thumbnailImageFile")) {
                                if (null != parseObject.getParseFile("thumbnailImageFile").getUrl()) {
                                    spotlightMedia.setThumbnailUrl(parseObject.getParseFile("thumbnailImageFile").getUrl());
                                    Log.d(TAG, "thumbnail url: " + parseObject.getParseFile("thumbnailImageFile").getUrl());
                                }
                            }
                            if (null != parseObject.getParseObject("parent")) {
                                spotlightMedia.setParentId(parseObject.getParseObject("parent").getObjectId());
                                Log.d(TAG, "parent: " + parseObject.getParseObject("parent").getObjectId());
                            } else {
                                spotlightMedia.setParentId("null");
                            }
                            data.add(spotlightMedia);
                        } catch (ParseException e1) {
                            Log.d(TAG, (null != e1.getMessage()) ? e1.getMessage() : "");
                        }
                    }

                    int limit = 1000;
                    if (1000 == objects.size()) {
                        skip = skip + limit;
                        ParseQuery<ParseObject> mediaQuery = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
                        mediaQuery.setSkip(skip);
                        mediaQuery.setLimit(limit);
                        mediaQuery.findInBackground(skippy());
                    } else {
                        contract.showProgress(false);
                        contract.onMediaFetched(data);
                    }
                } else {
                    contract.showProgress(false);
                    Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                }
            }
        };
    }

    public void fetchMedia() {
        try {
            contract.showProgress(true);
            ParseQuery<ParseObject> mediaQuery = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
            mediaQuery.setLimit(1000);
            mediaQuery.orderByDescending("createdAt");
            mediaQuery.findInBackground(skippy());
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }

    public void fetchTeams() {
        final List<ParseObject> data = new ArrayList<>();
        try {
            contract.showProgress(true);
            final ParseRelation<ParseObject> teamsRelation = ParseUser.getCurrentUser().getRelation("teams");
            ParseQuery<ParseObject> mediaQuery = teamsRelation.getQuery();
            mediaQuery.setLimit(1000);
            mediaQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (null == e) {
                        contract.showProgress(false);
                        data.addAll(objects);
                        contract.onTeamsFetched(data);
                    } else {
                        contract.showProgress(false);
                        Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                    }
                }
            });
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }

    public void fetchKids() {
        final List<ParseObject> data = new ArrayList<>();
        try {
            contract.showProgress(true);
            final ParseRelation<ParseObject> teamsRelation = ParseUser.getCurrentUser().getRelation("children");
            ParseQuery<ParseObject> kidsQuery = teamsRelation.getQuery();
            kidsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (null == e) {
                        contract.showProgress(false);
                        data.addAll(objects);
                        contract.onKidsFetched(data);
                    } else {
                        contract.showProgress(false);
                        Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                    }
                }
            });
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }

    public void fetchKidsTeams(final List<ParseObject> kids) {
        final List<ParseObject> data = new ArrayList<>();
        try {
            contract.showProgress(true);
            for (final ParseObject kid : kids) {
                child++;
                final ParseRelation<ParseObject> teamsRelation = kid.getRelation("teams");
                teamsRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (null == e) {
                            contract.showProgress(false);
                            data.addAll(objects);
                            if (child == kids.size()) contract.onKidsTeamsFetched(data);
                        } else {
                            contract.showProgress(false);
                            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                        }
                    }
                });
            }
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }

    public void fetchSpotlights(final List<ParseObject> teams, final List<SpotlightMedia> media) {
        final List<Spotlight> data = new ArrayList<>();
        final List<String> coverUrls = new ArrayList<>();
        try {
            contract.showProgress(true);
            ParseQuery<ParseObject> spotlightsQuery = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT);
            spotlightsQuery.setLimit(1000);
            spotlightsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (null == e) {
                        contract.showProgress(false);
                        for (ParseObject parseObject : objects) {
//                            Log.d(TAG, parseObject.getObjectId());
                            try {
                                parseObject.fetchIfNeeded();
                                Date date = parseObject.getUpdatedAt();
                                int end = date.toString().length();
                                int start = end - 4;
                                for (ParseObject team : teams) {
                                    if (team.getObjectId().equals(parseObject.getParseObject("team").getObjectId())) {
//                                        Log.d(TAG, team.getObjectId());
                                        /* this is our spotlight :) */
                                        Spotlight s = new Spotlight();
                                        Team t = Convert.toTeam(team);
                                        s.setObjectId(parseObject.getObjectId());
                                        s.setTeamsAvatar(t.getAvatarUrl());
                                        s.setTeam(t);
                                        s.setYear(Integer.valueOf(date.toString().substring(start, end)));
                                        s.setMonth(date.toString().substring(4, 7));
                                        s.setDay(Integer.valueOf(date.toString().substring(8, 10)));
                                        coverUrls.clear();
                                        for (SpotlightMedia m : media) {
                                            if (null != m.getParentId()) {
                                                Log.d(TAG, "parent id not null");
                                                if (m.getParentId().equals(parseObject.getObjectId())) {
                                                    Log.d(TAG, "media match");
                                                    if (null != m.getThumbnailUrl())    s.setCover(m.getThumbnailUrl());
                                                    coverUrls.add(m.getThumbnailUrl());
                                                }
                                            } else {
                                                Log.d(TAG, "null parent");
                                            }
                                        }
                                        s.setCoverUrl(coverUrls);
                                        data.add(s);
                                    }
                                }
                            } catch (ParseException e1) {
                                Log.d(TAG, (null != e1.getMessage()) ? e1.getMessage() : "");
                            }

                        }
                        contract.onSpotlightsFetched(data);
                    } else {
                        contract.showProgress(false);
                        Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                    }
                }
            });
        } catch (Exception e) {
            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }
}
