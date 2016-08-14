package me.spotlight.spotlight.features.spotlights.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.teams.TeamsAdapter;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class AddSpotlightFragment extends Fragment implements TeamsAdapter.ActionListener {

    public static final String TAG = "AddSpotlightFragment";

    @Bind(R.id.recycler_view_add_spotlight)
    RecyclerView recyclerView;
    TeamsAdapter teamsAdapter;
    List<Team> pickTeams = new ArrayList<>();

    /*
        Manufacturing singleton
    */
    public static AddSpotlightFragment newInstance() {
        Bundle args = new Bundle();
        AddSpotlightFragment addSpotlightFragment = new AddSpotlightFragment();
        addSpotlightFragment.setArguments(args);
        return addSpotlightFragment;
    }

    public void onShowDetails(Team team) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", team.getObjectId());
        bundle.putString("teamName", team.getName());
        bundle.putString("teamAvatar", team.getAvatarUrl());
        FragmentUtils.changeFragment(getActivity(), R.id.content, FinishSpotlightFragment.newInstance(bundle), true);
    }

    public void onRequestFollow(final Team team, int position, boolean unfollow) {
        // do nothing for now
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_add_spotlight, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        teamsAdapter = new TeamsAdapter(getActivity(), pickTeams, this, true);
        recyclerView.setAdapter(teamsAdapter);

        loadTeams();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.add_spotlight));
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void loadTeams() {
        if (!pickTeams.isEmpty())
            pickTeams.clear();
        final ParseRelation<ParseObject> myteamsRelation = ParseUser.getCurrentUser().getRelation("teams");
        ParseQuery<ParseObject> myteamsQuery = myteamsRelation.getQuery();
        myteamsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {

                            try {

                                Team team = new Team();
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
                                    try {
                                        parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).fetchIfNeeded();
                                    } catch (ParseException e1) {
                                    }
                                    if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile")) {
                                        if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl()) {
                                            team.setAvatarUrl(parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl());
                                        }
                                    }
                                }
                                pickTeams.add(team);
                            } catch (Exception e1) {
                                Log.d(TAG, "crash");
                            }

                        }
                    }

                    Collections.sort(pickTeams, comparator);
                    teamsAdapter.notifyDataSetChanged();


                } else {
                    // TODO: handle e
                }
            }
        });
    }


    Comparator<Team> comparator = new Comparator<Team>() {
        @Override
        public int compare(Team team1, Team team2) {
            int result = 0;


            try {

                String year1 = team1.getYear();
                String year2 = team2.getYear();


                if (year1.equals(year2)) {
                    String season1 = team1.getSeason();
                    String season2 = team2.getSeason();
                    if (season1.equals(season2)) {
                        result = 0;
                    } else {
                        int priority1 = getSeasonPriority(season1);
                        int priority2 = getSeasonPriority(season2);
                        result = priority2 - priority1;
                    }
                } else {
                    result = (int) Integer.valueOf(year2) - Integer.valueOf(year1);
                }
            } catch (Exception e) {
                Log.d(TAG, "crash");
            }



            return result;
        }
    };

    private int getSeasonPriority(String season) {
        switch (season) {
            case Constants.SEASON_FALL:
                return 1;
            case Constants.SEASON_WINTER:
                return 4;
            case Constants.SEASON_SPRING:
                return 3;
            case Constants.SEASON_SUMMER:
                return 2;
            default:
                //
                break;
        }
        return 0;
    }
}
