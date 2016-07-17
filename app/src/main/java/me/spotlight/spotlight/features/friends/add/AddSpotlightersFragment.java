package me.spotlight.spotlight.features.friends.add;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.User;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class AddSpotlightersFragment extends Fragment {

    @Bind(R.id.friends_search)
    EditText searchFriends;
    @Bind(R.id.recycler_view_users)
    RecyclerView usersList;
    List<User> users = new ArrayList<>();
    UsersAdapter usersAdapter;

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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersAdapter = new UsersAdapter(getActivity(), users);
        usersList.setAdapter(usersAdapter);

        loadUsers();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.add_spotlighters));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadUsers() {
        if (!users.isEmpty())
            users.clear();
        ParseQuery<ParseUser> usersQuery = ParseUser.getCurrentUser().getQuery();
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    for (ParseUser parseUser : objects) {
                        User user = new User();
                        if (null != parseUser.getString("firstName")) {
                            if (!"".equals(parseUser.getString("firstName"))) {
                                user.setFirstName(parseUser.getString("firstName"));
                            }
                        }
                        if (null != parseUser.getString("lastName")) {
                            if (!"".equals(parseUser.getString("lastName"))) {
                                user.setLastName(parseUser.getString("lastName"));
                            }
                        }
                        if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC)) {
                            try {
                                parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).fetchIfNeeded();
                            } catch (ParseException e1) {}
                            if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).getParseFile("mediaFile")) {
                                if (null != parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).getParseFile("mediaFile").getUrl()) {
                                    user.setAvatarUrl(parseUser.getParseObject(ParseConstants.FIELD_USER_PIC).getParseFile("mediaFile").getUrl());
                                }
                            }
                        }
                        users.add(user);
                    }
                    usersAdapter.notifyDataSetChanged();
                } else {
                    // TODO: handle e
                }
            }
        });
    }
}
