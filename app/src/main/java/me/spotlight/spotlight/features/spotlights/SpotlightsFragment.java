package me.spotlight.spotlight.features.spotlights;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.MainActivity;
import me.spotlight.spotlight.features.spotlights.add.AddSpotlightFragment;
import me.spotlight.spotlight.features.spotlights.details.SpotlightDetailsFragment;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;
import me.spotlight.spotlight.utils.QuickAction;

/**
 * Created by Anatol on 7/10/2016.
 */
public class SpotlightsFragment extends Fragment
        implements SpotlightsAdapter.ActionListener {

    @Bind(R.id.recycler_view_spolights)
    RecyclerView mySpotlightsList;
    SpotlightsAdapter spotlightsAdapter;
    List<Spotlight> mySpotlights = new ArrayList<>();
    List<Team> myTeams = new ArrayList<>();
    public static List<SpotlightMedia> spotlightMedias = new ArrayList<>();
    @Bind(R.id.swipe_spotlights)
    SwipeRefreshLayout swipeSpotlights;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    Thread loadTeamsThread;

    /*
        Manufacturing singleton
     */
    public static SpotlightsFragment newInstance() {
        Bundle args = new Bundle();
        SpotlightsFragment spotlightsFragment = new SpotlightsFragment();
        spotlightsFragment.setArguments(args);
        return spotlightsFragment;
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
                                                mySpotlights.remove(position);
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
        View view = layoutInflater.inflate(R.layout.fragment_spotlights, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mySpotlightsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        spotlightsAdapter = new SpotlightsAdapter(getActivity(), mySpotlights, this);
        mySpotlightsList.setAdapter(spotlightsAdapter);
        swipeSpotlights.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mySpotlights.isEmpty())
                    mySpotlights.clear();
                spotlightsAdapter.notifyDataSetChanged();
                loadTeams();
                swipeSpotlights.setRefreshing(false);
            }
        });

        preloadSpotlightMedia();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.tabs_spotlights));
        if (getActivity().getPreferences(Context.MODE_PRIVATE).contains("first")) {
            // dont show
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.welcome_spotlight))
                    .setMessage(getString(R.string.welcome_message))
                    .setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            getActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean("first", true).commit();
        }
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
//            FragmentUtils.changeFragment(getActivity(), R.id.content, AddSpotlightFragment.newInstance(), true);
            FragmentUtils.addFragment(getActivity(), R.id.content, this, AddSpotlightFragment.newInstance(), true);
            ret = true;
        }
        return ret;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("spotFrag", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("spotFrag", "onStop");
    }


    private void loadSpotlights() {
        if (!mySpotlights.isEmpty())
            mySpotlights.clear();
        ParseQuery<ParseObject> spotQuery = new ParseQuery<>("Spotlight");
        spotQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject spotlight : objects) {
                            try {
                                spotlight.fetchIfNeeded();
                            } catch (ParseException e1) {}
                            if (null != spotlight.getParseObject("team")) {
                                ParseObject team = spotlight.getParseObject("team");
//                                Log.d("spotteams", team.getObjectId());
                            }
                            Log.d("spotteams", spotlight.getObjectId());
                        }
                    }
                }
            }
        });
    }



    private void preloadSpotlightMedia() {
        ParseQuery<ParseObject> mediaQ = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
//        mediaQ.whereEqualTo("isVideo", true);
        mediaQ.setLimit(420);
        mediaQ.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject spotMedia : objects) {
                            try {
                                spotMedia.fetchIfNeeded();
                            } catch (ParseException e1) {}

                            SpotlightMedia spotlightMedia = new SpotlightMedia();
                            spotlightMedia.setObjectId(spotMedia.getObjectId());

                            if (null != spotMedia.getParseFile("mediaFile")) {
                                spotlightMedia.setFileUrl(spotMedia.getParseFile("mediaFile").getUrl());
                            }
                            if (null != spotMedia.getParseFile("thumbnailImageFile")) {
                                if (null != spotMedia.getParseFile("thumbnailImageFile").getUrl()) {
                                    spotlightMedia.setThumbnailUrl(spotMedia.getParseFile("thumbnailImageFile").getUrl());
                                }
                            }
                            if (null != spotMedia.getParseObject("parent")) {
                                spotlightMedia.setParentId(spotMedia.getParseObject("parent").getObjectId());
                            }


                            spotlightMedias.add(spotlightMedia);
                            Log.d("mainspotmedias", spotlightMedia.getObjectId());
                        }

                        loadTeams();
                    }
                }
            }
        });
    }


    private void loadTeams() {
        if (!myTeams.isEmpty())
            myTeams.clear();
        progressBar.setVisibility(View.VISIBLE);
        final ParseRelation<ParseObject> myteamsRelation = ParseUser.getCurrentUser().getRelation("teams");
        ParseQuery<ParseObject> myteamsQuery = myteamsRelation.getQuery();
        myteamsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {

                        Runnable loadTeamsRunnable = new Runnable() {
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
                                        } catch (ParseException e1) {
                                        }
                                        if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile")) {
                                            if (null != parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl()) {
                                                team.setAvatarUrl(parseObject.getParseObject(ParseConstants.FIELD_TEAM_MEDIA).getParseFile("mediaFile").getUrl());
                                            }
                                        }
                                    }
                                    myTeams.add(team);
                                    Log.d("spotteams", parseObject.getString(ParseConstants.FIELD_TEAM_NAME));
                                }


                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressBar.setVisibility(View.GONE);
                                        loadMySpotlights(myTeams);
                                    }
                                });
                            }
                        };


                        loadTeamsThread = new Thread(loadTeamsRunnable);
                        loadTeamsRunnable.run();

                    } else {
                        Log.d("spotteams", "Empty");
                    }
                } else {
                    // TODO: handle e
                    Log.d("spotteams", "Error");
                }
            }
        });
    }


    private void loadMySpotlights(final List<Team> teams) {
        if (!mySpotlights.isEmpty())
            mySpotlights.clear();
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
                                        for (SpotlightMedia m : spotlightMedias) {
                                            if (m.getParentId().equals(spotlight.getObjectId())) {
                                                coverUrls.add(m.getThumbnailUrl());
                                                spotlight1.setCover(m.getThumbnailUrl());
                                            }
                                        }

                                        spotlight1.setCoverUrl(coverUrls);

                                        mySpotlights.add(spotlight1);
                                        spotlightsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        Log.d("spotteams", String.valueOf(mySpotlights.size()));


                    }
                }
            }
        });


    }
}
