package me.spotlight.spotlight.features.spotlights;

import com.parse.ParseObject;

import java.util.List;

import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.models.Team;

/**
 * Created by Anatol on 8/21/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public interface SpotlightsContract {

    void onMediaFetched(List<SpotlightMedia> data);

    void onTeamsFetched(List<ParseObject> data);

    void onKidsFetched(List<ParseObject> data);

    void onKidsTeamsFetched(List<ParseObject> data);

    void onSpotlightsFetched(List<Spotlight> data);

    void showProgress(boolean show);
}
