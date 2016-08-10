package me.spotlight.spotlight.features.friends;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
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
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;
import me.spotlight.spotlight.features.friends.add.AddFamilyFragment;
import me.spotlight.spotlight.features.friends.add.AddSpotlightersFragment;
import me.spotlight.spotlight.features.friends.details.ChildAdapter;
import me.spotlight.spotlight.features.friends.details.FriendDetailsFragment;
import me.spotlight.spotlight.features.teams.add.AddTeamFragment;
import me.spotlight.spotlight.features.teams.details.TeamDetailsFragment;
import me.spotlight.spotlight.features.teams.search.SearchTeamsFragment;
import me.spotlight.spotlight.models.Child;
import me.spotlight.spotlight.models.Friend;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class FriendsFragment extends Fragment implements FriendsAdapter.ActionListener,
        ChildAdapter.ActionListener {

    @Bind(R.id.recycler_view_friends)
    RecyclerView friendsList;
    @Bind(R.id.recycler_view_family)
    RecyclerView familyList;
    FriendsAdapter friendsAdapter;
    ChildAdapter childAdapter;
    List<Friend> friends = new ArrayList<>();
    List<Child> children = new ArrayList<>();
    @Bind(R.id.swipe_friends)
    SwipeRefreshLayout swipeFriends;

    /*
        Manufacturing singleton
    */
    public static FriendsFragment newInstance() {
        Bundle args = new Bundle();
        FriendsFragment friendsFragment = new FriendsFragment();
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    public void onShowDetails(Friend friend) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", friend.getObjectId());
        FragmentUtils.addFragment(getActivity(), R.id.content, this, FriendDetailsFragment.newInstance(bundle), true);
    }

    public void onUnfollow(final Friend friend) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.follow_sure)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        unfollow(friend);
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

    public void onViewChildDetails(Child child) {
        //
    }

    private void unfollow(Friend friend) {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage(getString(R.string.please_wait));
        progress.setCancelable(false);
        progress.show();
        ParseQuery<ParseUser> friendQuery = ParseUser.getCurrentUser().getQuery();
        friendQuery.whereEqualTo("objectId", friend.getObjectId());
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
                                    // refresh
                                    progress.dismiss();
                                    loadFriends();
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
        View view = layoutInflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        friendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        familyList.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsAdapter = new FriendsAdapter(getActivity(), friends);
        friendsAdapter.setActionListener(this);
        childAdapter = new ChildAdapter(getActivity(), children, this);
        friendsList.setAdapter(friendsAdapter);
        familyList.setAdapter(childAdapter);
        swipeFriends.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!friends.isEmpty())
                    friends.clear();
                friendsAdapter.notifyDataSetChanged();
                loadFriends();
                swipeFriends.setRefreshing(false);
            }
        });

        loadFamily();
        loadFriends();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.tabs_friends));
        if (getActivity().getPreferences(Context.MODE_PRIVATE).contains("first2" + ParseUser.getCurrentUser().getObjectId())) {
            //don't show
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.friends_message))
                    .setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            getActivity().getPreferences(Context.MODE_PRIVATE)
                    .edit().putBoolean("first2" + ParseUser.getCurrentUser().getObjectId(), true)
                    .commit();
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.btn_tab_friends).getWindowToken(), 0);
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



    public void addSpotlighters() {
//        FragmentUtils.changeFragment(getActivity(), R.id.content, AddSpotlightersFragment.newInstance(), true);
        FragmentUtils.addFragment(getActivity(), R.id.content, this, AddSpotlightersFragment.newInstance(), true);
    }

    public void addFamily() {
//        FragmentUtils.changeFragment(getActivity(), R.id.content, AddFamilyFragment.newInstance(), true);
        FragmentUtils.addFragment(getActivity(), R.id.content, this, AddFamilyFragment.newInstance(), true);
    }

//    @OnClick(R.id.fab_add_spotlighters)
    public void onFab() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.friends_dialog))
                .setItems(getResources().getTextArray(R.array.friends_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                addSpotlighters();
                                break;
                            case 1:
                                addFamily();
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

    private void loadFriends() {
        if (!friends.isEmpty())
            friends.clear();
        final ParseRelation<ParseUser> friendRelations = ParseUser.getCurrentUser().getRelation("friends");
        ParseQuery<ParseUser> friendsQuery = friendRelations.getQuery();
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseUser parseUser : objects) {
                            Friend friend = new Friend();
                            friend.setObjectId(parseUser.getObjectId());
                            if (null != parseUser.getString("firstName")) {
                                if (!"".equals(parseUser.getString("firstName"))) {
                                    friend.setFirstName(parseUser.getString("firstName"));
                                }
                            }
                            if (null != parseUser.getString("lastName")) {
                                if (!"".equals(parseUser.getString("lastName"))) {
                                    friend.setLastName(parseUser.getString("lastName"));
                                }
                            }
                            if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC)) {
                                try {
                                    parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).fetchIfNeeded();
                                } catch (ParseException e1) {}
                                if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).getParseFile("mediaFile")) {
                                    if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).getParseFile("mediaFile").getUrl()) {
                                        friend.setAvatarUrl(parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).getParseFile("mediaFile").getUrl());
                                    }
                                }
                            }
                            friends.add(friend);
                        }

                        friendsAdapter.notifyDataSetChanged();
                    }
                } else {
                    // TODO: handle e
                }
            }
        });
    }

    private void loadFamily() {
        if (!children.isEmpty())
            children.clear();
        final ParseRelation<ParseObject> childrenRelation = ParseUser.getCurrentUser().getRelation("children");
        final ParseQuery<ParseObject> childrenQuery = childrenRelation.getQuery();
        childrenQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {

                            Child child = new Child();
                            try {
                                parseObject.fetchIfNeeded();
                                child.setObjectId(parseObject.getObjectId());
                                if (null != parseObject.getParseObject("profilePic")) {
                                    ParseObject profilePic = parseObject.getParseObject("profilePic");
                                    profilePic.fetchIfNeeded();
                                    ParseFile mediaFile = profilePic.getParseFile("mediaFile");
                                    if (null != mediaFile.getUrl())
                                        child.setAvatarUrl(mediaFile.getUrl());
                                }
                                if (null != parseObject.getString("firstName"))
                                    child.setFirstName(parseObject.getString("firstName"));
                                if (null != parseObject.getString("lastName"))
                                    child.setLastName(parseObject.getString("lastName"));

                                children.add(child);
                                childAdapter.notifyDataSetChanged();

                            } catch (ParseException e1) {
                                //
                            }
                        }
                    } else {
                        //
                    }
                } else {
                    //
                }
            }
        });
    }




















    // TODO: remove this
    private void addingFriends() {

        ParseQuery<ParseUser> usersQuery = ParseUser.getCurrentUser().getQuery();
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                ParseRelation<ParseUser> friends = ParseUser.getCurrentUser().getRelation("friends");

                if (null == e)
                    Toast.makeText(getActivity(), "Shit!" + String.valueOf(objects.size()), Toast.LENGTH_SHORT).show();

                int i = 0;
                for (ParseUser parseUser : objects) {
                    i++;
                    if (i % 4 == 0)
                        friends.add(parseUser);
                    Log.d("users", parseUser.getUsername());
                }

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (null == e) {
                            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void gettingAllUsers() {
        ParseQuery<ParseUser> usersQuery = ParseUser.getCurrentUser().getQuery();
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (null == e) {

                    for (ParseUser parseUser : objects) {

                        Friend friend = new Friend();
                        if (null != parseUser.getString("firstName")) {
                            if (!"".equals(parseUser.getString("firstName"))) {
                                friend.setFirstName(parseUser.getString("firstName"));
                                friend.setLastName(parseUser.getString("firstName"));
                            }
                        }
                        friends.add(friend);
                    }

                    Log.d("fg", String.valueOf(friends.size()));
                    friendsAdapter.notifyDataSetChanged();
                }

            }
        });
    }
}
