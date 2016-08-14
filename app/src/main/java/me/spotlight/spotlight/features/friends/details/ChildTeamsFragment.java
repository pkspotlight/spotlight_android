package me.spotlight.spotlight.features.friends.details;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.teams.TeamsAdapter;
import me.spotlight.spotlight.features.teams.details.TeamDetailsFragment;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Evgheni on 8/11/2016.
 */
public class ChildTeamsFragment extends Fragment implements TeamsAdapter.ActionListener {

    public static final String TAG = "ChildTeamsFragment";
    @Bind(R.id.recycler_view_child_teams)
    RecyclerView recyclerView;
    TeamsAdapter teamsAdapter;
    List<Team> childTeams = new ArrayList<>();

    /*
        Manufacturing singleton
     */
    public static ChildTeamsFragment newInstance(Bundle args) {
        ChildTeamsFragment childTeamsFragment = new ChildTeamsFragment();
        childTeamsFragment.setArguments(args);
        return childTeamsFragment;
    }

    public void onShowDetails(final Team team) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("objectId", team.getObjectId());
            FragmentUtils.changeFragment(getActivity(), R.id.content, TeamDetailsFragment.newInstance(bundle), true);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void onRequestFollow(final Team team, int position, boolean unfollow) {
        // you are following all your kids teams by default


//        final AlertDialog dialog = new AlertDialog.Builder(getContext())
//                .setMessage(R.string.follow_sure)
//                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        createRequest(team);
//                    }
//                })
//                .setPositiveButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .create();
//        dialog.show();
    }

    private void unfollowTeam(Team team) {
        Toast.makeText(getActivity(), "Unfollowing " + team.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_child_teams, container, true);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Toast.makeText(getContext(), getArguments().getString("objectId"), Toast.LENGTH_LONG).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        teamsAdapter = new TeamsAdapter(getActivity(), childTeams, this, false);
        recyclerView.setAdapter(teamsAdapter);

        loadTeams();
    }


    private void loadTeams() {
        if (!childTeams.isEmpty())
            childTeams.clear();
        ParseQuery<ParseObject> childQuery = new ParseQuery<>(ParseConstants.OBJECT_CHILD);
        childQuery.whereEqualTo("objectId", getArguments().getString("objectId"));
        childQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {

                        ParseObject mChild = objects.get(0);
                        try {
                            mChild.fetchIfNeeded();
                        } catch (Exception e1) {
                            Log.d(TAG, "parse exception");
                        }

                        ParseRelation<ParseObject> teamsRel = mChild.getRelation("teams");
                        ParseQuery<ParseObject> teamsQuery = teamsRel.getQuery();
                        teamsQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (null == e) {
                                    if (!objects.isEmpty()) {

                                        for (ParseObject parseObject : objects) {
                                            try {
                                                Team team = new Team();
                                                team.setObjectId(parseObject.getObjectId());

                                                if (null != parseObject.getString(ParseConstants.FIELD_TEAM_NAME)) {
                                                    team.setName(parseObject.getString(ParseConstants.FIELD_TEAM_NAME));
                                                    team.setTeamName(parseObject.getString(ParseConstants.FIELD_TEAM_NAME));
                                                }

                                                if (null != parseObject.getString(ParseConstants.FIELD_TEAM_GRADE)) {
                                                    team.setGrade(parseObject.getString(ParseConstants.FIELD_TEAM_GRADE));
                                                }
                                                if (null != parseObject.getString(ParseConstants.FIELD_TEAM_SPORT)) {
                                                    team.setSport(parseObject.getString(ParseConstants.FIELD_TEAM_SPORT));
                                                }
                                                if (null != parseObject.getString(ParseConstants.FIELD_TEAM_SEASON)) {
                                                    team.setSeason(parseObject.getString(ParseConstants.FIELD_TEAM_SEASON));
                                                }
                                                if (null != parseObject.getString(ParseConstants.FIELD_TEAM_YEAR)) {
                                                    team.setYear(parseObject.getString(ParseConstants.FIELD_TEAM_YEAR));
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

                                                childTeams.add(team);

                                            } catch (Exception e3) {
                                                Log.d(TAG, e3.getMessage());
                                            }
                                        }

                                        Collections.sort(childTeams, comparator);
                                        teamsAdapter.notifyDataSetChanged();

                                    } else {
                                        Log.d(TAG, "empty teams query");
                                    }
                                } else {
                                    Log.d(TAG, "exception finding teams");
                                }
                            }
                        });

                    } else {
                        Log.d(TAG, "empty query for current child ");
                    }
                } else {
                    Log.d(TAG, "finding current child exception");
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
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

    private void createRequest(Team team) {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.OBJECT_TEAM);
        query.whereEqualTo("objectId", team.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        final ParseObject team = objects.get(0);
                        team.getRelation("moderators").getQuery().findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (null == e) {
                                    if (!objects.isEmpty()) {
//                                        Toast.makeText(getContext(), objects.get(0).getObjectId(), Toast.LENGTH_LONG).show();
                                        String adminUserId = objects.get(0).getObjectId();
                                        finishRequest(team, adminUserId);
                                    } else {
//                                        Toast.makeText(getContext(), "empty", Toast.LENGTH_LONG).show();
                                        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.no_admin))
                                                .setNegativeButton("Got it", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                    }
                                } else {
//                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    final AlertDialog dialog = new AlertDialog.Builder(getContext())
                                            .setMessage(e.getMessage())
                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .create();
                                    dialog.show();
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private void finishRequest(final ParseObject team, String adminUserId) {

        ParseQuery<ParseUser> reqQ = ParseUser.getQuery();
        reqQ.whereEqualTo("objectId", adminUserId);
        reqQ.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        final ParseUser admin = objects.get(0);
                        ParseObject parseObject = new ParseObject(ParseConstants.OBJECT_TEAM_REQUEST);

                        if (null != ParseUser.getCurrentUser().getParseObject("profilePic")) {
                            ParseObject profilePic = ParseUser.getCurrentUser().getParseObject("profilePic");
                            try {
                                profilePic.fetchIfNeeded();
                            } catch (ParseException ee) {}

                            parseObject.put("PicOfRequester", profilePic);
                            parseObject.put("admin", admin);
                            parseObject.put("requestState", 0);
                            parseObject.put("nameOfRequester", ParseUser.getCurrentUser().getString("firstName") + " "
                                    + ParseUser.getCurrentUser().getString("lastName"));
                            parseObject.put("team", team);
                            parseObject.put("teamName", team.getString("teamName"));
                            parseObject.put("timeStamp", String.valueOf(System.currentTimeMillis()));
                            parseObject.put("user", ParseUser.getCurrentUser());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (null == e) {
//                                        Toast.makeText(getContext(), "Request created", Toast.LENGTH_LONG).show();
                                        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                                                .setMessage("The request to follow has been sent")
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        /// getActivity().onBackPressed();
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                    } else {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }



                    } else {
                        Toast.makeText(getContext(), "empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });



    }

}
