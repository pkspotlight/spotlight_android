package me.spotlight.spotlight.features.friends.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/10/2016.
 */
public class FriendDetailsFragment extends Fragment {

    String avatarUrl;
    Transformation round;
    @Bind(R.id.friend_detail_avatar)
    ImageView friendDetailAvatar;
    @Bind(R.id.friend_detail_name)
    TextView friendDetailName;
    @Bind(R.id.friend_detail_pager)
    ViewPager friendDetailPager;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter pagerAdapter;
    private static final int NUM_TABS = 2;
    @Bind(R.id.friend_detail_spot)
    View friendSpot;
    @Bind(R.id.friend_detail_teams)
    View friendTeams;

    /*
        Manufacturing singleton
     */
    public static FriendDetailsFragment newInstance(Bundle args) {
        FriendDetailsFragment friendDetailsFragment = new FriendDetailsFragment();
        friendDetailsFragment.setArguments(args);
        return friendDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_friend_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = new Bundle();
        args.putString("objectId", getArguments().getString("objectId"));
        fragments.add(FriendSpotlightsFragment.newInstance(args));
        fragments.add(FriendTeamsFragment.newInstance(args));
        pagerAdapter = new FriendDetailsPagerAdapter(getChildFragmentManager());
        friendDetailPager.setAdapter(pagerAdapter);
        friendDetailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    friendSpot.setSelected(true);
                    friendTeams.setSelected(false);
                } else {
                    friendSpot.setSelected(false);
                    friendTeams.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //
            }
        });
        round = new RoundedTransformationBuilder().oval(true).build();
        friendSpot.setSelected(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        populate();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void populate() {
        ParseQuery<ParseUser> userQuery = ParseUser.getCurrentUser().getQuery();
        userQuery.whereEqualTo("objectId", getArguments().getString("objectId"));
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        ParseUser currentUser = objects.get(0);
                        try {
                            currentUser.fetchIfNeeded();
                        } catch (ParseException parseException) { /* TODO: handle this */}

                        if (null != currentUser.getParseObject("profilePic")) {
                            ParseObject profilePic = currentUser.getParseObject("profilePic");
                            try {
                                profilePic.fetchIfNeeded();
                            } catch (ParseException parseException2) { /* TODO: handle this */}

                            if (null != currentUser.getParseObject("profilePic").getParseFile("mediaFile")) {
                                avatarUrl = currentUser.getParseObject("profilePic").getParseFile("mediaFile").getUrl();
                            }
                        }
                        if (null != currentUser.getString("username")) {
                            friendDetailName.setText(currentUser.getString("username"));
                            getActivity().setTitle(currentUser.getString("username"));
                        }
                        if (!"".equals(avatarUrl))
                            initAvatar(avatarUrl);
                        else
                            initEmptyAvatar();
                    } else {
                        // handle empty result
                    }
                } else {
                    // handle error
                }
            }
        });
    }

    private void initAvatar(String url) {
        Picasso.with(getActivity())
                .load(url)
                .fit().centerCrop()
                .transform(round)
                .into(friendDetailAvatar);
    }

    private void initEmptyAvatar() {
        Picasso.with(getActivity())
                .load(R.drawable.unknown_user)
                .fit().centerCrop()
                .transform(round)
                .into(friendDetailAvatar);
    }

    @OnClick(R.id.friend_detail_spot)
    public void mem() {
        friendDetailPager.setCurrentItem(1, true);
    }

    @OnClick(R.id.friend_detail_teams)
    public void spot() {
        friendDetailPager.setCurrentItem(0, true);
    }

    /*
        View pager adapter
     */
    private class FriendDetailsPagerAdapter extends FragmentPagerAdapter {

        public FriendDetailsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }
    }
}
