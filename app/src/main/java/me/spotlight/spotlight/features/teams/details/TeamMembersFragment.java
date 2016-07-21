package me.spotlight.spotlight.features.teams.details;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.friends.add.UsersAdapter;
import me.spotlight.spotlight.features.friends.details.FriendDetailsFragment;
import me.spotlight.spotlight.models.Friend;
import me.spotlight.spotlight.models.User;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/18/2016.
 */
public class TeamMembersFragment extends Fragment implements UsersAdapter.ActionListener {

    @Bind(R.id.recycler_view_team_members)
    RecyclerView teamMembersList;
    UsersAdapter teamMembersAdapter;
    List<User> members = new ArrayList<>();

    /*
        Manufacturing singleton
     */
    public static TeamMembersFragment newInstance(Bundle args) {
        TeamMembersFragment teamMembersFragment = new TeamMembersFragment();
        teamMembersFragment.setArguments(args);
        return teamMembersFragment;
    }

    public void onShowDetails(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", user.getObjectId());
        FragmentUtils.changeFragment(getActivity(), R.id.content, FriendDetailsFragment.newInstance(bundle), true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_team_details_members, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        teamMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        teamMembersAdapter = new UsersAdapter(getActivity(), members);
        teamMembersAdapter.setActionListener(this);
        teamMembersList.setAdapter(teamMembersAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadMembers();
    }

    private void loadMembers() {
        if (!members.isEmpty()) {
            members.clear();
        }
        ParseQuery<ParseObject> currentTeamQuery = new ParseQuery<>("Team");
        currentTeamQuery.whereEqualTo("objectId", getArguments().getString("objectId"));
        currentTeamQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        ParseObject currentTeam = objects.get(0);
                        try {
                            currentTeam.fetchIfNeeded();
                        } catch (ParseException parseException) {
                            Log.d("pop", "exception fetching current team");
                        }

                        ParseRelation<ParseUser> membersRel = currentTeam.getRelation("teamParticipants");
                        final ParseQuery<ParseUser> membersQuery = membersRel.getQuery();
                        membersQuery.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (null == e) {
                                    if (!objects.isEmpty()) {
                                        for (ParseUser parseUser : objects) {
                                            try {
                                                parseUser.fetchIfNeeded();
                                            } catch (ParseException parseException2) {}
                                            User user = new User();
                                            user.setObjectId(parseUser.getObjectId());
                                            user.setFirstName(parseUser.getString("firstName"));
                                            user.setLastName(parseUser.getString("lastName"));
                                            if (null != parseUser.getParseObject("profilePic")) {
                                                ParseObject profilePic = parseUser.getParseObject("profilePic");
                                                try {
                                                    profilePic.fetchIfNeeded();
                                                } catch (ParseException parseException3) {}
                                                if (null != profilePic.getParseFile("mediaFile")) {
                                                    ParseFile mediaFile = profilePic.getParseFile("mediaFile");
                                                    user.setAvatarUrl(mediaFile.getUrl());
                                                }
                                            }
                                            members.add(user);
                                        }
                                    }
                                }
                                teamMembersAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
}
