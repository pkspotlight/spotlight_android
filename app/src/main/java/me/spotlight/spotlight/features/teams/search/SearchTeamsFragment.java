package me.spotlight.spotlight.features.teams.search;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
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
public class SearchTeamsFragment extends Fragment implements TeamsAdapter.ActionListener {

    @Bind(R.id.teams_search)
    EditText searchTeams;
    @Bind(R.id.recycler_view_teams)
    RecyclerView teamsList;
    List<Team> teams = new ArrayList<>();
    TeamsAdapter teamsAdapter;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    Thread teamsThread;

    /*
        Manufacturing singleton
    */
    public static SearchTeamsFragment newInstance() {
        Bundle args = new Bundle();
        SearchTeamsFragment searchTeamsFragment = new SearchTeamsFragment();
        searchTeamsFragment.setArguments(args);
        return searchTeamsFragment;
    }

    public void onShowDetails(Team team) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", team.getObjectId());
        FragmentUtils.changeFragment(getActivity(), R.id.content, TeamDetailsFragment.newInstance(bundle), true);
    }

    public void onRequestFollow(Team team) {
        createRequest(team);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_search_teams, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.GONE);
        teamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        teamsAdapter = new TeamsAdapter(getActivity(), teams, this, false);
        teamsList.setAdapter(teamsAdapter);
        searchTeams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadTeams(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

//        loadTeams();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.search_teams);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != teamsThread)
            teamsThread.interrupt();
    }

    private void loadTeams(String param) {
        if (!teams.isEmpty())
            teams.clear();
        progressBar.setVisibility(View.VISIBLE);
        final ParseQuery<ParseObject> teamsQuery = new ParseQuery<ParseObject>(ParseConstants.OBJECT_TEAM);
        teamsQuery.whereStartsWith("teamName", param);
        teamsQuery.setLimit(12);
        teamsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {

                        Runnable getTeams = new Runnable() {
                            @Override
                            public void run() {
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
                                        } catch (ParseException e1) {}
                                        if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile")) {
                                            if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl()) {
                                                team.setAvatarUrl(parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl());
                                            }
                                        }
                                    }
                                    teams.add(team);
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        teamsAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        };


                        teamsThread = new Thread(getTeams);
                        teamsThread.start();

                    }
                } else {
                    // TODO: handle e
                }
            }
        });
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
}
