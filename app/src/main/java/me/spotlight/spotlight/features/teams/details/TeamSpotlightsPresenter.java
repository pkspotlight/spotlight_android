package me.spotlight.spotlight.features.teams.details;

/**
 * Created by Anatol on 8/31/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class TeamSpotlightsPresenter {

    public static final String TAG = "TeamSpotPresenter";
    private TeamSpotlightsContract contract;

    public TeamSpotlightsPresenter(TeamSpotlightsContract teamSpotlightsContract) {
        this.contract = teamSpotlightsContract;
    }
}
