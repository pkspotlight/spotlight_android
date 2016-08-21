package me.spotlight.spotlight.features.spotlights;

import com.parse.ParseObject;

import java.util.List;

import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.models.Team;

/**
 * Created by Evgheni on 8/21/2016.
 */
public interface SpotlightsContract {

    void onMediaFetched(List<SpotlightMedia> data);

    void onTeamsFetched(List<Team> data);

    void onKidsFetched(List<ParseObject> data);

    void onKidsTeamsFetched(List<Team> data);

    void onSpotlightsFetched(List<Spotlight> data);

    void showProgress(boolean show);
}
