package me.spotlight.spotlight.features.teams;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.teams.add.AddTeamFragment;
import me.spotlight.spotlight.features.teams.details.TeamDetailsFragment;
import me.spotlight.spotlight.features.teams.search.SearchTeamsFragment;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class TeamsFragment extends Fragment implements TeamsAdapter.ActionListener {

    @Bind(R.id.recycler_view_myteams)
    RecyclerView myteamsList;
    TeamsAdapter myteamsAdapter;
    List<Team> myTeams = new ArrayList<>();
    @Bind(R.id.swipe_teams)
    SwipeRefreshLayout swipeFriends;

    /*
        Manufacturing singleton
     */
    public static TeamsFragment newInstance() {
        Bundle args = new Bundle();
        TeamsFragment teamsFragment = new TeamsFragment();
        teamsFragment.setArguments(args);
        return teamsFragment;
    }

    public void onShowDetails(Team team) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", team.getObjectId());
        FragmentUtils.changeFragment(getActivity(), R.id.content, TeamDetailsFragment.newInstance(bundle), true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_teams, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myteamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        myteamsAdapter = new TeamsAdapter(getActivity(), myTeams, this);
        myteamsList.setAdapter(myteamsAdapter);
        swipeFriends.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!myTeams.isEmpty())
                    myTeams.clear();
                myteamsAdapter.notifyDataSetChanged();
                loadTeams();
                swipeFriends.setRefreshing(false);
            }
        });

        loadTeams();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void addTeams() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, AddTeamFragment.newInstance(), true);
    }

    public void searchTeams() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, SearchTeamsFragment.newInstance(), true);
    }

    @OnClick(R.id.fab_add_teams)
    public void onFab() {

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.teams_dialog))
                .setItems(getResources().getTextArray(R.array.teams_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                searchTeams();
                                break;
                            case 1:
                                addTeams();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();

    }


    private void loadTeams() {
        if (!myTeams.isEmpty())
            myTeams.clear();
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
                            myTeams.add(team);
                        }
                    }
                    myteamsAdapter.notifyDataSetChanged();
                } else {
                    // TODO: handle e
                }
            }
        });
    }

}