package me.spotlight.spotlight.features.friends.details;

import android.app.ProgressDialog;
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
 * Created by Anatol on 7/21/2016.
 */
public class FriendTeamsFragment extends Fragment implements TeamsAdapter.ActionListener {

    public static final String TAG = "FriendTeamsFragment";
    @Bind(R.id.recycler_view_friend_teams)
    RecyclerView friendTeamsList;
    TeamsAdapter friendTeamsAdapter;
    List<Team> friendTeams = new ArrayList<>();
    List<String> myTeamsIds = new ArrayList<>();
    private CharSequence[] kids;

    /*
        Manufacturing singleton
     */
    public static FriendTeamsFragment newInstance(Bundle args) {
        FriendTeamsFragment friendTeamsFragment = new FriendTeamsFragment();
        friendTeamsFragment.setArguments(args);
        return friendTeamsFragment;
    }

    public void onShowDetails(Team team) {
        try {
            if (team.isMine()) {
                Bundle bundle = new Bundle();
                bundle.putString("objectId", team.getObjectId());
                FragmentUtils.changeFragment(getActivity(), R.id.content, TeamDetailsFragment.newInstance(bundle), true);
            } else {
                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage(R.string.must_follow)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "exception showing details");
        }
    }

    public void onRequestFollow(final Team team, final int position, final boolean unfollow) {
        if (unfollow) {
            onReqFol(team, position, unfollow);
        } else {
            CharSequence[] backup = new CharSequence[1];
            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.which_child)
                    .setItems((kids == null) ? backup : kids, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    // TODO: add relation
                                    Toast.makeText(getActivity(), kids[0], Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    // TODO: add relation
                                    Toast.makeText(getActivity(), kids[1], Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .setPositiveButton(R.string.none_follow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            onReqFol(team, position, unfollow);
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private void onReqFol(final Team team, int position, final boolean unfollow) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(unfollow ? R.string.unfollow : R.string.follow_sure)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (!unfollow) {
                            createRequest(team);
                        } else {
                            unfollowTeam(team);
                        }
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void unfollowTeam(Team team) {
        Toast.makeText(getActivity(), "Unfollowing " + team.getName(), Toast.LENGTH_SHORT).show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM);
        parseQuery.whereEqualTo("objectId", team.getObjectId());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        try {

                            ParseObject team = objects.get(0);
                            team.fetchIfNeeded();
                            ParseRelation<ParseObject> teamsRel = ParseUser.getCurrentUser().getRelation("teams");
                            teamsRel.remove(team);
                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (null == e) {
                                        getActivity().onBackPressed();
                                    }
                                }
                            });

                        } catch (ParseException e1) {
                            Log.d(TAG, e1.getMessage());
                        }
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_friend_details_teams, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        friendTeamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendTeamsAdapter = new TeamsAdapter(getActivity(), friendTeams, this, false);
        friendTeamsList.setAdapter(friendTeamsAdapter);
        getKids();
        loadMyTeamsIds();
    }

    private void loadMyTeamsIds() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        ParseRelation teamsRel = ParseUser.getCurrentUser().getRelation("teams");
        ParseQuery<ParseObject> teamsQ = teamsRel.getQuery();
        teamsQ.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            myTeamsIds.add(parseObject.getObjectId());
                        }

                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTeams();
    }

    private void loadTeams() {
        if (!friendTeams.isEmpty())
            friendTeams.clear();
        ParseQuery<ParseUser> query = ParseUser.getCurrentUser().getQuery();
        query.whereEqualTo("objectId", getArguments().getString("objectId"));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        ParseUser parseUser = objects.get(0);
                        try {
                            parseUser.fetchIfNeeded();
                        } catch (ParseException e1) {}

                        ParseRelation<ParseObject> teamRel = parseUser.getRelation("teams");
                        ParseQuery<ParseObject> teamQuery = teamRel.getQuery();
                        teamQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (null == e) {
                                    if (!objects.isEmpty()) {
                                        for (ParseObject parseObject : objects) {
                                            Team team = new Team();
                                            team.setObjectId(parseObject.getObjectId());

                                            for (String string : myTeamsIds) {
                                                if (string.equals(parseObject.getObjectId())) {
                                                    team.setMine(true);
                                                }
                                            }

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
                                            friendTeams.add(team);
                                        }

                                        Collections.sort(friendTeams, comparator);
                                        friendTeamsAdapter.notifyDataSetChanged();

                                    } else {
                                        Log.d(TAG, "empty teams query");
                                    }


                                } else {
                                    // TODO: handle e
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    Comparator<Team> comparator = new Comparator<Team>() {
        public static final String TAG = "teamComparator";
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
                Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
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
                                                        getActivity().onBackPressed();
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

    private void getKids() {
        final List<String> kids = new ArrayList<>();
        ParseRelation<ParseObject> kidsRel = ParseUser.getCurrentUser().getRelation("children");
        ParseQuery<ParseObject> kidsQuery = kidsRel.getQuery();
        kidsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            try {
                                parseObject.fetchIfNeeded();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(parseObject.getString("firstName"));
                                stringBuilder.append(" ");
                                stringBuilder.append(parseObject.getString("lastName"));
                                kids.add(stringBuilder.toString());
                            } catch (ParseException e1) {
                                Log.d(TAG, e1.getMessage());
                            }
                        }
                        toCharseq(kids);
                    }
                }
            }
        });
    }

    private void toCharseq(List<String> data) {
        kids = new CharSequence[data.size()];
        for (int i = 0; i < data.size(); i++) {
            kids[i] = data.get(i);
        }
    }

}
