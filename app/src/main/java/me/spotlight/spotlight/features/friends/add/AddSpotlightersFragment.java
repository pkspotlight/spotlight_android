package me.spotlight.spotlight.features.friends.add;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
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
import me.spotlight.spotlight.features.friends.details.FriendDetailsFragment;
import me.spotlight.spotlight.models.User;
import me.spotlight.spotlight.utils.Convert;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class AddSpotlightersFragment extends Fragment implements UsersAdapter.ActionListener {

    public static final String TAG = "AddSpotlightersFragment";
    @Bind(R.id.friends_search)
    EditText searchFriends;
    @Bind(R.id.recycler_view_users)
    RecyclerView usersList;
    private List<User> friends = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    UsersAdapter usersAdapter;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    Thread friendsThread;
    @Bind(R.id.nodata)
    View nodata;

    /*
        Manufacturing singleton
    */
    public static AddSpotlightersFragment newInstance() {
        Bundle args = new Bundle();
        AddSpotlightersFragment addSpotlightersFragment = new AddSpotlightersFragment();
        addSpotlightersFragment.setArguments(args);
        return addSpotlightersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_add_spotlighters, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onShowDetails(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", user.getObjectId());
        FragmentUtils.changeFragment(getActivity(), R.id.content, FriendDetailsFragment.newInstance(bundle), true);
    }

    public void onFollow(final User user, int position) {
        if (user.isFriend()) {
            unfollow(user);
        } else {
            follow(user);
        }
    }

    private void unfollow(final User user) {
        String name = (user.getFirstName() != null) ? user.getFirstName() : "";
        String title = "Unfollow " + name + " ?";
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        forgeRelation(user);
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

    private void follow(final User user) {
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
                        addRelation(user);
                    }
                })
                .create();
        dialog.show();
    }

    private void addRelation(final User user) {
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
                                        getActivity().onBackPressed();
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

    private void forgeRelation(final User user) {
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
                                    getActivity().onBackPressed();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.GONE);
        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersAdapter = new UsersAdapter(getActivity(), users);
        usersAdapter.registerAdapterDataObserver(adapterDataObserver);
        usersAdapter.setActionListener(this);
        usersList.setAdapter(usersAdapter);
        searchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ("".equals(charSequence.toString())) {
                    users.clear();
                    usersAdapter.notifyDataSetChanged();
                } else {
                    loadUsers(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.add_spotlighters));
        loadFriendIds();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != friendsThread)
            friendsThread.interrupt();
    }

    private void loadFriendIds() {
        if (!friends.isEmpty())
            friends.clear();
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage(getString(R.string.please_wait));
        progress.setCancelable(false);
        progress.show();
        ParseRelation<ParseUser> friendsRel = ParseUser.getCurrentUser().getRelation("friends");
        friendsRel.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        try {
                            for (ParseUser friend : objects) {
                                User user = new User();
                                user.setObjectId(friend.getObjectId());
                                friends.add(user);
                            }
                            progress.dismiss();
                        } catch (Exception e1) {
                            Log.d(TAG, (null != e1.getMessage()) ? e1.getMessage() : "friends relation query exception");
                            progress.dismiss();
                        }
                    } else {
                        progress.dismiss();
                    }
                } else {
                    progress.dismiss();
                }
            }
        });
    }

    private void loadUsers(String param) {
        if (!users.isEmpty())
            users.clear();
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseUser> usersQuery = ParseUser.getCurrentUser().getQuery();
        usersQuery.whereStartsWith("firstName", param);
        usersQuery.setLimit(12);
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {


                        Runnable getUsers = new Runnable() {
                            @Override
                            public void run() {
                                for (ParseUser parseUser : objects) {

                                    try {
                                        User user = Convert.toUser(parseUser);

                                        for (User u : friends) {
                                            if (u.getObjectId().equals(parseUser.getObjectId())) {
                                                user.setFriend(true);
                                            }
                                        }

                                        users.add(user);
                                    } catch (Exception e1) {
                                        Log.d(TAG, "crash");
                                    }

                                }


                                try {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            usersAdapter.notifyDataSetChanged();
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.d(TAG, "crash");
                                }

                            }
                        };

                        /// post here
                        friendsThread = new Thread(getUsers);
                        friendsThread.start();

                    }
                } else {
                    // TODO: handle e
                }
            }
        });
    }


    /*
        Showing the "no data" block if there's no data
    */
    RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (users.isEmpty()) {
                nodata.setVisibility(View.VISIBLE);
            } else {
                nodata.setVisibility(View.GONE);
            }
        }
    };
}
