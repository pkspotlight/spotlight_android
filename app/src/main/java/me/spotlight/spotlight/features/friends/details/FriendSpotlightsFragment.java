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

import com.parse.DeleteCallback;
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
import me.spotlight.spotlight.features.spotlights.SpotlightsAdapter;
import me.spotlight.spotlight.features.spotlights.SpotlightsFragment;
import me.spotlight.spotlight.features.spotlights.details.SpotlightDetailsFragment;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/21/2016.
 */
public class FriendSpotlightsFragment extends Fragment
        implements SpotlightsAdapter.ActionListener {

    @Bind(R.id.recycler_view_friend_spotlights)
    RecyclerView recyclerView;
    List<Team> friendTeams = new ArrayList<>();
    List<Spotlight> friendSpotlights = new ArrayList<>();
    SpotlightsAdapter spotlightsAdapter;

    /*
        Manufacturing singleton
     */
    public static FriendSpotlightsFragment newInstance(Bundle args) {
        FriendSpotlightsFragment friendSpotlightsFragment = new FriendSpotlightsFragment();
        friendSpotlightsFragment.setArguments(args);
        return friendSpotlightsFragment;
    }

    public void onShowDetails(Spotlight spotlight) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", spotlight.getObjectId());
        bundle.putString("teamAvatar", spotlight.getTeamsAvatar());
        bundle.putString("teamName", spotlight.getTeam().getName());
        bundle.putString("teamGrade", spotlight.getTeam().getGrade());
        bundle.putString("teamSport", spotlight.getTeam().getSport());
        FragmentUtils.changeFragment(getActivity(), R.id.content, SpotlightDetailsFragment.newInstance(bundle), true);
    }

    public void onDelete(final Spotlight spotlight, final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle(getString(R.string.remove_spotlight))
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("deleting...");
                    progressDialog.show();
                    ParseQuery<ParseObject> q = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT);
                    q.whereEqualTo("objectId", spotlight.getObjectId());
                    q.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (null == e) {
                                ParseObject s = objects.get(0);
                                s.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (null == e) {
                                            progressDialog.dismiss();
                                            friendSpotlights.remove(position);
                                            spotlightsAdapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity(), "Spotlight deleted successfully!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            })
            .create();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_friend_details_spotlights, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        spotlightsAdapter = new SpotlightsAdapter(getActivity(), friendSpotlights, this);
        recyclerView.setAdapter(spotlightsAdapter);

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
                                    }

                                    // go on and get spotlights
                                    /////

                                    loadMySpotlights(friendTeams);

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



    private void loadMySpotlights(final List<Team> teams) {
        if (!friendSpotlights.isEmpty())
            friendSpotlights.clear();
        ParseQuery<ParseObject> spotQuery = new ParseQuery<>("Spotlight");
        spotQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        List<String> coverUrls = new ArrayList<>();
                        for (ParseObject spotlight : objects) {
                            try {
                                spotlight.fetchIfNeeded();
                            } catch (ParseException e1) {}

                            if (null != spotlight.getParseObject("team")) {
                                ParseObject team = spotlight.getParseObject("team");

                                for (Team team1 : teams) {
                                    if (team.getObjectId().equals(team1.getObjectId())) {
                                        // this is our spotlight - convert to our model and add it
                                        Spotlight spotlight1 = new Spotlight();
                                        spotlight1.setObjectId(spotlight.getObjectId());
                                        spotlight1.setTeamsAvatar(team1.getAvatarUrl());
                                        spotlight1.setTeam(team1);

                                        coverUrls.clear();
                                        for (SpotlightMedia m : SpotlightsFragment.spotlightMedias) {
                                            if (m.getParentId().equals(spotlight.getObjectId())) {
                                                coverUrls.add(m.getThumbnailUrl());
                                                spotlight1.setCover(m.getThumbnailUrl());
                                            }
                                        }

                                        spotlight1.setCoverUrl(coverUrls);

                                        friendSpotlights.add(spotlight1);
                                        spotlightsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }


                    }
                }
            }
        });


    }
}
