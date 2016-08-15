package me.spotlight.spotlight.features.spotlights;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    public static final String TAG = "SpotlightsFragment";

    @Bind(R.id.recycler_view_spolights)
    RecyclerView mySpotlightsList;
    SpotlightsAdapter spotlightsAdapter;
    List<Spotlight> mySpotlights = new ArrayList<>();
    List<Spotlight> myKidsSpotlights = new ArrayList<>();
    List<Team> myTeams = new ArrayList<>();
    List<Team> kidsTeams = new ArrayList<>();
    public static List<SpotlightMedia> spotlightMedias = new ArrayList<>();
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
        try {
            Bundle bundle = new Bundle();
            bundle.putString("objectId", spotlight.getObjectId());
            bundle.putString("teamAvatar", spotlight.getTeamsAvatar());
            bundle.putString("teamName", spotlight.getTeam().getName());
            bundle.putString("teamGrade", spotlight.getTeam().getGrade());
            bundle.putString("teamSport", spotlight.getTeam().getSport());
            bundle.putString("date", SpotlightsAdapter.getMonth(spotlight.getMonth())
                    + " " + String.valueOf(spotlight.getDay()) + ", "
                    + String.valueOf(spotlight.getYear()));
            FragmentUtils.addFragment(getActivity(), R.id.content, this, SpotlightDetailsFragment.newInstance(bundle), true);
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
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

        preloadSpotlightMedia();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.tabs_spotlights));
        if (getActivity().getPreferences(Context.MODE_PRIVATE).contains("first" + ParseUser.getCurrentUser().getObjectId())) {
            // don't show onboarding
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
            getActivity().getPreferences(Context.MODE_PRIVATE)
                    .edit().putBoolean("first" + ParseUser.getCurrentUser().getObjectId(), true)
                    .commit();
        }
//        initSwipe(mySpotlightsList, spotlightsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean ret = true;
        if (menuItem.getItemId() == android.R.id.home) {
            ret = false;
        }
        if (menuItem.getItemId() == R.id.action_add) {
            menuItem.setVisible(false);
            FragmentUtils.addFragment(getActivity(), R.id.content, this, AddSpotlightFragment.newInstance(), true);
            ret = true;
        }
        return ret;
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



    private void preloadSpotlightMedia() {
        ParseQuery<ParseObject> mediaQ = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
        mediaQ.setLimit(1000);
        mediaQ.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        Log.d(TAG, String.valueOf(objects.size()));
                        for (ParseObject spotMedia : objects) {

                            try {
                                spotMedia.fetchIfNeeded();

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
                                } else {
                                    spotlightMedia.setParentId("null");
                                }

                                spotlightMedias.add(spotlightMedia);

                            } catch (Exception e1) {
                                Log.d(TAG, "exception:" + " preloadSpotlightMedia");
                            }
                        }

                        loadTeams();
                        getKids();
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

                                    try {
                                        myTeams.add(convertTeam(parseObject));

                                        Log.d(TAG, parseObject.getString(ParseConstants.FIELD_TEAM_NAME));
                                    } catch (Exception e1) {
                                        Log.d(TAG, "crash");
                                    }
                                }

                                proceed();
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

    private void getKids() {
        final List<ParseObject> kids = new ArrayList<>();
        ParseRelation<ParseObject> kidsRel = ParseUser.getCurrentUser().getRelation("children");
        ParseQuery<ParseObject> kidsQuery = kidsRel.getQuery();
        kidsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            kids.add(parseObject);
                        }

                        // go on
                        getKidsTeams(kids);
                    }
                }
            }
        });
    }

    private void getKidsTeams(List<ParseObject> kids) {
        final List<Team> kidsTeams = new ArrayList<>();
        for (ParseObject kid : kids) {
            ParseRelation<ParseObject> teamsRel = kid.getRelation("teams");
            teamsRel.getQuery().findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (null == e) {
                        if (!objects.isEmpty()) {
                            for (ParseObject team : objects) {
                                try {

                                    kidsTeams.add(convertTeam(team));
                                } catch (Exception e1) {}
                            }

                            loadKidsSpotlights(kidsTeams);
                        }
                    }
                }
            });
        }
    }


    private void loadMySpotlights(final List<Team> teams) {
        if (!mySpotlights.isEmpty())
            mySpotlights.clear();
        ParseQuery<ParseObject> spotQuery = new ParseQuery<>("Spotlight");
        spotQuery.setLimit(500);
        spotQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        Log.d(TAG, String.valueOf(objects.size()));
                        List<String> coverUrls = new ArrayList<>();
                        for (ParseObject spotlight : objects) {

                            try {

                                spotlight.fetchIfNeeded();

                                Date date = spotlight.getUpdatedAt();
                                int end = date.toString().length();
                                int start = end - 4;
//                                Log.d(TAG, date.toString());
//                                Log.d(TAG, String.valueOf(date.toString().length()));

                                if (null != spotlight.getParseObject("team")) {
                                    ParseObject team = spotlight.getParseObject("team");

                                    for (Team team1 : teams) {
                                        if (team.getObjectId().equals(team1.getObjectId())) {
                                            // this is our spotlight - convert to our model and add it
                                            Spotlight mSpot = new Spotlight();
                                            mSpot.setObjectId(spotlight.getObjectId());
                                            mSpot.setTeamsAvatar(team1.getAvatarUrl());
                                            mSpot.setTeam(team1);
                                            mSpot.setYear(Integer.valueOf(date.toString().substring(start, end)));
                                            mSpot.setDay(Integer.valueOf(date.toString().substring(8, 10)));
                                            mSpot.setMonth(date.toString().substring(4, 7));
//                                            Log.d(TAG, date.toString().substring(start, end));
//                                            Log.d(TAG, date.toString().substring(8, 10));
//                                            Log.d(TAG, date.toString().substring(4, 7));

                                            coverUrls.clear();
                                            for (SpotlightMedia m : spotlightMedias) {
                                                if (null != m.getParentId()) {
                                                    if (m.getParentId().equals(spotlight.getObjectId())) {
                                                        coverUrls.add(m.getThumbnailUrl());
                                                        mSpot.setCover(m.getThumbnailUrl());
                                                    }
                                                }
                                            }

                                            mSpot.setCoverUrl(coverUrls);

                                            mySpotlights.add(mSpot);
                                        }
                                    }

                                    Collections.sort(mySpotlights, comparator);
                                    spotlightsAdapter.notifyDataSetChanged();
                                }

                            } catch (Exception e1) {
                                Log.d(TAG, e1.getMessage());
                            }
                        }
                        Log.d(TAG, String.valueOf(mySpotlights.size()));


                    }
                }
            }
        });
    }

    private void loadKidsSpotlights(final List<Team> teams) {
        if (!myKidsSpotlights.isEmpty())
            myKidsSpotlights.clear();
        ParseQuery<ParseObject> spotQuery = new ParseQuery<>("Spotlight");
        spotQuery.setLimit(500);
        spotQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        List<String> coverUrls = new ArrayList<>();
                        for (ParseObject spotlight : objects) {

                            try {

                                spotlight.fetchIfNeeded();

                                Date date = spotlight.getUpdatedAt();
                                int end = date.toString().length();
                                int start = end - 4;

                                if (null != spotlight.getParseObject("team")) {
                                    ParseObject team = spotlight.getParseObject("team");

                                    for (Team team1 : teams) {
                                        if (team.getObjectId().equals(team1.getObjectId())) {
                                            // this is our spotlight - convert to our model and add it
                                            Spotlight mSpot = new Spotlight();
                                            mSpot.setObjectId(spotlight.getObjectId());
                                            mSpot.setTeamsAvatar(team1.getAvatarUrl());
                                            mSpot.setTeam(team1);
                                            mSpot.setYear(Integer.valueOf(date.toString().substring(start, end)));
                                            mSpot.setDay(Integer.valueOf(date.toString().substring(8, 10)));
                                            mSpot.setMonth(date.toString().substring(4, 7));

                                            coverUrls.clear();
                                            for (SpotlightMedia m : spotlightMedias) {
                                                if (null != m.getParentId()) {
                                                    if (m.getParentId().equals(spotlight.getObjectId())) {
                                                        coverUrls.add(m.getThumbnailUrl());
                                                        mSpot.setCover(m.getThumbnailUrl());
                                                    }
                                                }
                                            }

                                            mSpot.setCoverUrl(coverUrls);

                                            myKidsSpotlights.add(mSpot);
                                        }
                                    }
                                }

                            } catch (Exception e1) {
                                Log.d(TAG, e1.getMessage());
                            }
                        }

                        mySpotlights.addAll(myKidsSpotlights);
                        Collections.sort(mySpotlights, comparator);
                        spotlightsAdapter.notifyDataSetChanged();
                        Log.d(TAG, String.valueOf(mySpotlights.size()));


                    }
                }
            }
        });
    }


    Comparator<Spotlight> comparator = new Comparator<Spotlight>() {
        public static final String TAG = "SpotlightsComparator";
        @Override
        public int compare(Spotlight one, Spotlight two) {
            int result = 0;

            try {

                if (one.getYear() == two.getYear()) {

                    String month1 = one.getMonth();
                    String month2 = two.getMonth();

                    if (month1.equals(month2)) {

                        if (one.getDay() == two.getDay()) {
                            result = 0;
                        } else {
                            result = two.getDay() - one.getDay();
                        }

                    } else {
                        int month1Priority = getMonthPriority(month1);
                        int month2Priority = getMonthPriority(month2);
                        result = month2Priority - month1Priority;
                    }

                } else {
                    result = two.getYear() - one.getYear();
                }

            } catch (Exception e) {
                Log.d(TAG, "exception");
            }

            return result;
        }
    };

    private int getMonthPriority(String month) {
        switch (month) {
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
            default:
                return 0;
        }
    }


    private Team convertTeam(ParseObject parseObject) {
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
        return team;
    }

    private void proceed() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                        loadMySpotlights(myTeams);
                    }
                    else
                        Log.d("crash", "crash");
                }
            });
        } catch (Exception e3) {
            Log.d(TAG, "crash");
        }
    }

}
