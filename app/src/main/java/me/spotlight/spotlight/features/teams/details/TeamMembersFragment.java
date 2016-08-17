package me.spotlight.spotlight.features.teams.details;

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
import me.spotlight.spotlight.features.friends.add.UsersAdapter;
import me.spotlight.spotlight.features.friends.details.FriendDetailsFragment;
import me.spotlight.spotlight.models.Friend;
import me.spotlight.spotlight.models.User;
import me.spotlight.spotlight.utils.Convert;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/18/2016.
 */
public class TeamMembersFragment extends Fragment implements UsersAdapter.ActionListener {

    public static final String TAG = "TeamMembersFragment";
    @Bind(R.id.recycler_view_team_members)
    RecyclerView teamMembersList;
    UsersAdapter teamMembersAdapter;
    List<User> members = new ArrayList<>();
    List<String> friendIds = new ArrayList<>();

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

    public void onFollow(final User user, int position) {
        if (user.isFriend()) {
            unfollow(user, position);
        } else {
            follow(user, position);
        }
    }

    private void unfollow(final User user, final int position) {
        String name = (user.getFirstName() != null) ? user.getFirstName() : "";
        String title = "Unfollow " + name + " ?";
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        forgeRelation(user, position);
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

    private void follow(final User user, final int position) {
        String name = (user.getFirstName() != null) ? user.getFirstName() : "";
        String title = "Follow " + name + " ?";
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addRelation(user, position);
                    }
                })
                .create();
        dialog.show();
    }

    private void addRelation(final User user, final int position) {
        ParseQuery<ParseUser> findUser = ParseUser.getCurrentUser().getQuery();
        findUser.whereEqualTo("objectId", user.getObjectId());
        findUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseRelation<ParseUser> rel = ParseUser.getCurrentUser().getRelation(ParseConstants.FIELD_USER_FRIENDS);
                if (null == e) {
                    if (!objects.isEmpty()) {
                        rel.add(objects.get(0));
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    try {
                                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();
                                        user.setFriend(true);
                                        teamMembersAdapter.notifyItemChanged(position);
                                    } catch (Exception e1) {
                                        Log.d(TAG, e1.getMessage());
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void forgeRelation(final User user, final int position) {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage(getString(R.string.please_wait));
        progress.setCancelable(false);
        progress.show();
        ParseQuery<ParseUser> friendQuery = ParseUser.getCurrentUser().getQuery();
        friendQuery.whereEqualTo("objectId", user.getObjectId());
        friendQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        ParseRelation<ParseUser> friendsRel = ParseUser.getCurrentUser().getRelation("friends");
                        friendsRel.remove(objects.get(0));
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (null == e) {
                                    progress.dismiss();
                                    user.setFriend(false);
                                    teamMembersAdapter.notifyItemChanged(position);
                                } else {
                                    progress.dismiss();
                                }
                            }
                        });
                    } else {
                        progress.dismiss();
                    }
                } else {
                    progress.dismiss();
                }
            }
        });
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
        Log.d(TAG, "onActivityCreated");
        loadFriendIds();
        teamMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        teamMembersAdapter = new UsersAdapter(getActivity(), members);
        teamMembersAdapter.setActionListener(this);
        teamMembersList.setAdapter(teamMembersAdapter);
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
        loadMembers();
    }

    private void loadMembers() {
        if (!members.isEmpty()) {
            members.clear();
        }
        ParseQuery<ParseObject> currentTeamQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM);
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
                                                User user = Convert.toUser(parseUser);
                                                for (String id : friendIds) {
                                                    if (id.equals(parseUser.getObjectId())) {
                                                        user.setFriend(true);
                                                    }
                                                }
                                                members.add(user);
                                            } catch (Exception e1) {
                                                Log.d(TAG, (null != e1.getMessage()) ? e1.getMessage() : " exception");
                                            }
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

    private void loadFriendIds() {

        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage(getString(R.string.please_wait));
        progress.setCancelable(false);
        progress.show();
        ParseRelation friendsRel = ParseUser.getCurrentUser().getRelation(ParseConstants.FIELD_USER_FRIENDS);
        ParseQuery<ParseUser> friendsQuery = friendsRel.getQuery();
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseUser parseUser : objects) {
                            try {
                                friendIds.add(parseUser.getObjectId());
                            } catch (Exception e1) {
                                Log.d(TAG, "");
                            }
                        }
                        progress.dismiss();
                        Log.d(TAG, "Number of friends: " + String.valueOf(friendIds.size()));
                    } else {
                        progress.dismiss();
                    }
                } else {
                    progress.dismiss();
                }
            }
        });
    }
}
