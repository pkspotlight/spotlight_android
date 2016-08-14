package me.spotlight.spotlight.features.teams;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.spotlights.add.AddSpotlightFragment;
import me.spotlight.spotlight.features.teams.add.AddTeamFragment;
import me.spotlight.spotlight.features.teams.details.TeamDetailsFragment;
import me.spotlight.spotlight.features.teams.requests.RequestsFragment;
import me.spotlight.spotlight.features.teams.search.SearchTeamsFragment;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.DialogUtils;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class TeamsFragment extends Fragment implements TeamsAdapter.ActionListener {

    public static final String TAG = "TeamsFragment";

    @Bind(R.id.recycler_view_myteams)
    RecyclerView myteamsList;
    TeamsAdapter myteamsAdapter;
    List<Team> myTeams = new ArrayList<>();
    @Bind(R.id.swipe_teams)
    SwipeRefreshLayout swipeFriends;
    @Bind(R.id.notification)
    TextView notification;

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
        FragmentUtils.addFragment(getActivity(), R.id.content, this, TeamDetailsFragment.newInstance(bundle), true);
    }

    public void onRequestFollow(final Team team, final int position, boolean boo) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("Would you like to unfollow this team?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        unFollowTeam(team, position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
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
        setHasOptionsMenu(true);
        myteamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        myteamsAdapter = new TeamsAdapter(getActivity(), myTeams, this, true);
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
        getActivity().setTitle(getString(R.string.tabs_teams));
        if (getActivity().getPreferences(Context.MODE_PRIVATE).contains("first3" + ParseUser.getCurrentUser().getObjectId())) {
            //don't show
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.teams_message))
                    .setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            getActivity().getPreferences(Context.MODE_PRIVATE)
                    .edit().putBoolean("first3" + ParseUser.getCurrentUser().getObjectId(), true)
                    .commit();
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.btn_tab_teams).getWindowToken(), 0);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
//        final MenuItem item = menu.findItem(R.id.action_add);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean ret = true;
        if (menuItem.getItemId() == android.R.id.home) {
            ret = false;
        }
        if (menuItem.getItemId() == R.id.action_add) {
            menuItem.setVisible(false);
            onFab();
            ret = true;
        }
        return ret;
    }


    public void addTeams() {
//        FragmentUtils.changeFragment(getActivity(), R.id.content, AddTeamFragment.newInstance(), true);
        FragmentUtils.addFragment(getActivity(), R.id.content, this, AddTeamFragment.newInstance(), true);
    }

    public void searchTeams() {
//        FragmentUtils.changeFragment(getActivity(), R.id.content, SearchTeamsFragment.newInstance(), true);
        FragmentUtils.addFragment(getActivity(), R.id.content, this, SearchTeamsFragment.newInstance(), true);
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

                                myTeams.add(team);
                            } catch (Exception e1) {
                                Log.d(TAG, "crash");
                            }
                        }
                    }

                    Collections.sort(myTeams, comparator);
                    myteamsAdapter.notifyDataSetChanged();

                    checkNotifications();

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

    /*
        Requests functionality
     */

    private void checkNotifications() {
        ParseQuery<ParseObject> notifyQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM_REQUEST);
        notifyQuery.whereEqualTo("admin", ParseUser.getCurrentUser());
        notifyQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        notification.setVisibility(View.VISIBLE);
                        boolean more = objects.size() > 1 ? true : false;
                        if (more)
                            notification.setText("You have " + String.valueOf(objects.size()) + " pending requests.");
                        else
                            notification.setText("You have " + String.valueOf(objects.size()) + " pending request.");
//                        Toast.makeText(getContext(), "Found something " + objects.get(0).getString("nameOfRequester") , Toast.LENGTH_LONG).show();
                    } else {
                        notification.setVisibility(View.GONE);
//                        Toast.makeText(getContext(), "Found nothing", Toast.LENGTH_LONG).show();
                    }



                } else {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.notification)
    public void showNotifications() {
        Bundle bundle = new Bundle();
        FragmentUtils.addFragment(getActivity(), R.id.content, this, RequestsFragment.newInstance(bundle), true);
    }


    private void unFollowTeam(final Team team, final int position) {
        ParseQuery<ParseObject> teamQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM);
        teamQuery.whereEqualTo("objectId", team.getObjectId());
        teamQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {

                        ParseObject teamToUnfollow = objects.get(0);
                        ParseRelation<ParseObject> teamsRel = ParseUser.getCurrentUser().getRelation("teams");
                        teamsRel.remove(teamToUnfollow);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    DialogUtils.showAlertDialog(getActivity(), "You are no longer following " + team.getName());
                                    myTeams.remove(position);
                                    myteamsAdapter.notifyDataSetChanged();
                                } else {
                                    DialogUtils.showAlertDialog(getActivity(), e.getMessage());
                                }
                            }
                        });

                    } else {
                        Log.d(TAG, "found nothing");
                    }
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }
}