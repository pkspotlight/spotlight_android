package me.spotlight.spotlight.features.spotlights.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;
import me.spotlight.spotlight.features.teams.TeamsAdapter;
import me.spotlight.spotlight.features.teams.details.TeamDetailsFragment;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class AddSpotlightFragment extends Fragment implements TeamsAdapter.ActionListener {

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
                        }
                    }
                    teamsAdapter.notifyDataSetChanged();
                } else {
                    // TODO: handle e
                }
            }
        });
    }
}
