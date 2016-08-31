package me.spotlight.spotlight.features.teams.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.spotlights.add.FinishSpotlightFragment;
import me.spotlight.spotlight.utils.CustomViewPager;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class TeamDetailsFragment extends Fragment {

    public static final String TAG = "TeamDetailsFragment";
    String avatarUrl = "";
    Transformation round;
    @Bind(R.id.team_detail_avatar) CircleImageView teamDetailAvatar;
    @Bind(R.id.team_detail_name) TextView teamDetailName;
    @Bind(R.id.team_detail_pager) CustomViewPager teamDetailPager;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter pagerAdapter;
    private static final int NUM_TABS = 2;
    @Bind(R.id.team_detail_member) View teamMembers;
    @Bind(R.id.team_detail_spot) View teamSpot;
    String teamName = "";

    /*
        Manufacturing singleton
    */
    public static TeamDetailsFragment newInstance(Bundle args) {
        TeamDetailsFragment teamDetailsFragment = new TeamDetailsFragment();
        teamDetailsFragment.setArguments(args);
        return teamDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_team_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = new Bundle();
        args.putString("objectId", getArguments().getString("objectId"));
        fragments.add(TeamSpotlightsFragment.newInstance(args));
        fragments.add(TeamMembersFragment.newInstance(args));
        pagerAdapter = new TeamDetailsPagerAdapter(getChildFragmentManager());
        teamDetailPager.setAdapter(pagerAdapter);
        teamDetailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    teamMembers.setSelected(false);
                    teamSpot.setSelected(true);
                } else {
                    teamMembers.setSelected(true);
                    teamSpot.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //
            }
        });
        round = new RoundedTransformationBuilder().oval(true).build();
        teamSpot.setSelected(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.team_details));
        populate();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void populate() {
        ParseQuery<ParseObject> teamQuery = new ParseQuery<>(ParseConstants.OBJECT_TEAM);
        teamQuery.whereEqualTo("objectId", getArguments().getString("objectId"));
        teamQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        try {
                            ParseObject currentTeam = objects.get(0);

                            currentTeam.fetchIfNeeded();

                            if (null != currentTeam.getParseObject("teamLogoMedia")) {
                                ParseObject teamLogoMedia = currentTeam.getParseObject("teamLogoMedia");

                                teamLogoMedia.fetchIfNeeded();

                                if (null != currentTeam.getParseObject("teamLogoMedia").getParseFile("mediaFile")) {
                                    avatarUrl = currentTeam.getParseObject("teamLogoMedia").getParseFile("mediaFile").getUrl();
                                }
                            }
                            if (null != currentTeam.getString("teamName")) {
                                teamName = currentTeam.getString("teamName");
                                teamDetailName.setText(currentTeam.getString("teamName"));
                            }
                            if (!"".equals(avatarUrl))
                                initAvatar(avatarUrl);
                            else
                                initEmptyAvatar();
                        } catch (Exception e1) {
                            Log.d(TAG, "exception");
                        }
                    }
                }
            }
        });
    }

    private void initAvatar(String url) {
        Glide.with(getActivity())
                .load(url)
                .into(teamDetailAvatar);
    }

    private void initEmptyAvatar() {
        Glide.with(getActivity())
                .load(R.drawable.unknown_user)
                .into(teamDetailAvatar);
    }

    @OnClick(R.id.team_detail_member)
    public void mem() {
        teamDetailPager.setCurrentItem(1, true);
    }

    @OnClick(R.id.team_detail_spot)
    public void spot() {
        teamDetailPager.setCurrentItem(0, true);
    }

    @OnClick(R.id.team_detail_add_spot)
    public void addSpotlight() {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", getArguments().getString("objectId"));
        bundle.putString("teamName", teamName);
        bundle.putString("teamAvatar", avatarUrl);
        FragmentUtils.changeFragment(getActivity(), R.id.content, FinishSpotlightFragment.newInstance(bundle), true);
    }


    /*
        View pager adapter
     */
    private class TeamDetailsPagerAdapter extends FragmentPagerAdapter {

        public TeamDetailsPagerAdapter(FragmentManager fragmentManager) {
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
