package me.spotlight.spotlight.features.friends.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.utils.CustomViewPager;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 8/11/2016.
 */
public class ChildDetailsFragment extends Fragment {

    public static final String TAG = "ChildDetailsFragment";
    private static final int NUM_TABS = 2;
    private String avatarUrl = "";
    private Transformation round;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter pagerAdapter;
    @Bind(R.id.child_detail_avatar)
//    ImageView childDetailAvatar;
    CircleImageView childDetailAvatar;
    @Bind(R.id.child_detail_name)
    TextView childDetailName;
    @Bind(R.id.child_detail_pager)
    CustomViewPager childDetailPager;
    @Bind(R.id.child_detail_spot)
    View childSpot;
    @Bind(R.id.child_detail_teams)
    View childTeams;

    /*
        Manufacturing singleton
     */
    public static ChildDetailsFragment newInstance(Bundle args) {
        ChildDetailsFragment childDetailsFragment = new ChildDetailsFragment();
        childDetailsFragment.setArguments(args);
        return childDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_child_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        round = new RoundedTransformationBuilder().oval(true).build();
        Bundle args = new Bundle();
//        Toast.makeText(getContext(), getArguments().getString("objectId"), Toast.LENGTH_LONG).show();
        args.putString("objectId", getArguments().getString("objectId"));
        fragments.add(ChildSpotlightsFragment.newInstance(args));
        fragments.add(ChildTeamsFragment.newInstance(args));
        pagerAdapter = new ChildDetailsPagerAdapter(getChildFragmentManager());
        childDetailPager.setAdapter(pagerAdapter);
        childDetailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    childSpot.setSelected(true);
                    childTeams.setSelected(false);
                } else {
                    childSpot.setSelected(false);
                    childTeams.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //
            }
        });
        childSpot.setSelected(true);
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
        getActivity().setTitle(R.string.title_child_details);
        loadChild(getArguments().getString("objectId"));
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

    private void loadChild(String id) {
        ParseQuery<ParseObject> childQuery = new ParseQuery<>(ParseConstants.OBJECT_CHILD);
        childQuery.whereEqualTo("objectId", getArguments().getString("objectId"));
        childQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        try {
                            objects.get(0).fetchIfNeeded();
                            populate(objects.get(0));
                        } catch (ParseException e1) {
                            Log.d(TAG, e1.getMessage());
                        }
                    }
                }
            }
        });
    }

    private void populate(ParseObject child) {
        try {
            if (null != child.getParseObject("profilePic")) {
                ParseObject profilePic = child.getParseObject("profilePic");
                profilePic.fetchIfNeeded();
                if (null != profilePic.getParseFile("mediaFile")) {
                    ParseFile parseFile = profilePic.getParseFile("mediaFile");
                    avatarUrl = parseFile.getUrl();
                    if (TextUtils.isEmpty(avatarUrl)) {
                        initEmptyAvatar();
                        Log.d(TAG, "initEmptyAvatar " + avatarUrl);
                    } else {
                        initAvatar(avatarUrl);
                        Log.d(TAG, "initAvatar " + avatarUrl);
                    }
                } else {
                    initEmptyAvatar();
                    Log.d(TAG, "initEmptyAvatar (null) " + avatarUrl);
                }
            } else {
                initEmptyAvatar();
                Log.d(TAG, "initEmptyAvatar (null) " + avatarUrl);
            }

            StringBuilder fullName = new StringBuilder();
            if (null != child.getString("firstName")) {
                fullName.append(child.getString("firstName"));
                fullName.append(" ");
            }
            if (null != child.getString("lastName")) {
                fullName.append(child.getString("lastName"));
                childDetailName.setText(fullName.toString());
            }

        } catch (ParseException e2) {
            Log.d(TAG, e2.getMessage());
        }
    }


    private void initAvatar(String avatarUrl) {
        Glide.with(getActivity())
                .load(avatarUrl)
                .into(childDetailAvatar);
    }

    private void initEmptyAvatar() {
        Glide.with(getActivity())
                .load(R.drawable.unknown_user)
                .into(childDetailAvatar);
    }

    @OnClick({R.id.child_detail_spot, R.id.child_detail_teams})
    public void tab(View view) {
        switch (view.getId()) {
            case R.id.child_detail_spot:
                childDetailPager.setCurrentItem(0, true);
                break;
            case R.id.child_detail_teams:
                childDetailPager.setCurrentItem(1, true);
                break;
            default:
                break;
        }
    }

    /*
        View pager adapter
     */
    private class ChildDetailsPagerAdapter extends FragmentPagerAdapter {

        public ChildDetailsPagerAdapter(FragmentManager fragmentManager) {
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
