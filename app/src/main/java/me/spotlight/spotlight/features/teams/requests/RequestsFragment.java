package me.spotlight.spotlight.features.teams.requests;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.TeamRequest;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 8/4/2016.
 */
public class RequestsFragment extends Fragment implements RequestAdapter.ActionListener {

    @Bind(R.id.recycler_view_requests)
    RecyclerView recyclerView;
    ArrayList<TeamRequest> requests = new ArrayList<>();
    RequestAdapter requestAdapter;

    public static final String TAG = "requests";

    /*
        Manufacturing singleton
     */
    public static RequestsFragment newInstance(Bundle bundle) {
        RequestsFragment requestsFragment = new RequestsFragment();
        requestsFragment.setArguments(bundle);
        return requestsFragment;
    }

    @Override
    public void onAccept(final TeamRequest teamRequest) {
//        Toast.makeText(getContext(), "changing the request state to accepted " + teamRequest.getObjectId(), Toast.LENGTH_LONG).show();
        ParseQuery<ParseObject> requestQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM_REQUEST);
        requestQuery.whereEqualTo("objectId", teamRequest.getObjectId());
        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        ParseObject parseObject = objects.get(0);
                        parseObject.put("requestState", 2);
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    addRelation(teamRequest.getRequesterObjId(), teamRequest.getTeamObjectId());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void addRelation(String reqId, final String teamId) {
        ParseQuery<ParseUser> userParseQuery = ParseUser.getCurrentUser().getQuery();
        userParseQuery.whereEqualTo("objectId", reqId);
        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        final ParseUser parseUser = objects.get(0);
                        final ParseRelation<ParseObject> parseRelation = parseUser.getRelation("teams");

                        ParseQuery<ParseObject> teamQuery = new ParseQuery<ParseObject>(ParseConstants.OBJECT_TEAM);
                        teamQuery.whereEqualTo("objectId", teamId);
                        teamQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (null == e) {
                                    if (!objects.isEmpty()) {
                                        ParseObject team = objects.get(0);
                                        parseRelation.add(team);
                                        parseUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (null == e) {
                                                    getActivity().onBackPressed();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDecline(TeamRequest teamRequest) {
//        Toast.makeText(getContext(), "changing the request state to declined " + teamRequest.getObjectId(), Toast.LENGTH_LONG).show();
        ParseQuery<ParseObject> requestQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM_REQUEST);
        requestQuery.whereEqualTo("objectId", teamRequest.getObjectId());
        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        ParseObject parseObject = objects.get(0);
                        parseObject.put("requestState", 2);
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    getActivity().onBackPressed();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_requests, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestAdapter = new RequestAdapter(getActivity(), requests, this);
        recyclerView.setAdapter(requestAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Requests");
        checkNotifications();
    }





    /*
        Requests functionality
     */

    private void checkNotifications() {
        if (!requests.isEmpty())
            requests.clear();
        ParseQuery<ParseObject> notifyQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM_REQUEST);
        notifyQuery.whereEqualTo("admin", ParseUser.getCurrentUser());
        notifyQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            TeamRequest teamRequest = new TeamRequest();

                            try {
                                // avatar url
                                parseObject.fetchIfNeeded();
                                ParseObject pic = parseObject.getParseObject("PicOfRequester");
                                ParseObject team = parseObject.getParseObject("team");
                                team.fetchIfNeeded();
                                ParseUser requester = parseObject.getParseUser("user");
                                requester.fetchIfNeeded();
                                pic.fetchIfNeeded();
                                ParseFile picFile = pic.getParseFile("mediaFile");
                                teamRequest.setObjectId(parseObject.getObjectId());
                                teamRequest.setAvatarUrl(picFile.getUrl());
                                // name
                                teamRequest.setRequesterName(parseObject.getString("nameOfRequester"));
                                teamRequest.setState((int) parseObject.getNumber("requestState"));
                                teamRequest.setTeamName(parseObject.getString("teamName"));
                                // requester id
                                teamRequest.setRequesterObjId(requester.getObjectId());
                                // team id
                                teamRequest.setTeamObjectId(team.getObjectId());

                                if ((int) parseObject.getNumber("requestState") != 1) {
                                    requests.add(teamRequest);
                                }

                            } catch (ParseException e1) {
                                Log.d(TAG, "exception fetching request object");
                            }

                        }

                        requestAdapter.notifyDataSetChanged();

                    } else {
                        Log.d(TAG, "requests array empty");
                    }
                } else {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }
}
