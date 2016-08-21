package me.spotlight.spotlight.features.spotlights;

import java.util.List;

import me.spotlight.spotlight.models.Team;

/**
 * Created by Anatol on 8/21/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class SpotlightsPresenter {

    public static final String TAG = "SpotlightsPresenter";
    private SpotlightsContract contract;

    public SpotlightsPresenter(SpotlightsContract spotlightsContract) {
        this.contract = spotlightsContract;
    }

    public void fetchMedia() {
        //
    }

    public void fetchTeams() {
        //
    }

    public void fetchKids() {
        //
    }

    public void fetchKidsTeams() {
        //
    }

    public void fetchSpotlights(List<Team> data) {
        //
    }
}
