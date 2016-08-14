package me.spotlight.spotlight.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.api.Token;
import me.spotlight.spotlight.base.BaseActivity;
import me.spotlight.spotlight.features.friends.FriendsFragment;
import me.spotlight.spotlight.features.friends.add.AddFamilyFragment;
import me.spotlight.spotlight.features.profile.ProfileFragment;
import me.spotlight.spotlight.features.spotlights.SpotlightsFragment;
import me.spotlight.spotlight.features.spotlights.add.AddSpotlightFragment;
import me.spotlight.spotlight.features.teams.TeamsFragment;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.models.User;
import me.spotlight.spotlight.utils.CustomViewPager;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.QuickAction;


/**
 * Created by Anatol on 7/11/2016.
 */
public class MainActivity extends AppCompatActivity {

    /*
        Intent manufacturing
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Bind(R.id.navigation)
    CustomViewPager mViewPager;

    @Bind(R.id.btn_tab_spotlights)
    View spotlights;
    @Bind(R.id.btn_tab_friends)
    View friends;
    @Bind(R.id.btn_tab_teams)
    View teams;
    @Bind(R.id.btn_tab_profile)
    View profile;

    private PagerAdapter mPagerAdapter;
    private List<Fragment> tabViews = new ArrayList<>();
    private static final int NUM_TABS = 4;


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    View lastSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);


//        try {
//            ParseInstallation.getCurrentInstallation().delete();
//        } catch (ParseException e) {}

//        ParseUser.enableAutomaticUser();
//        ParseInstallation.getCurrentInstallation().put("GCMSenderId", "606167943462");
//        List<String> strings = new ArrayList<>();
//        strings.add("global");
//        ParseInstallation.getCurrentInstallation().put("channels", strings);
//        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (null == e) {
//                    Toast.makeText(getApplicationContext(), "S", Toast.LENGTH_LONG).show();
//                } else {
//
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        if (null == ParseUser.getCurrentUser()) {
            startActivity(LoginActivity.getStartIntent(getApplication()));
            finish();
        } else {
            FragmentUtils.changeFragment(this, R.id.inner_content, SpotlightsFragment.newInstance(), false);

            lastSelected = spotlights;
            spotlights.setSelected(true);
        }


//        tabViews.add(SpotlightsFragment.newInstance());
//        tabViews.add(FriendsFragment.newInstance());
//        tabViews.add(TeamsFragment.newInstance());
//        tabViews.add(ProfileFragment.newInstance());
//
//
//        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
////        mViewPager = new CustomViewPager(getApplicationContext());
//        mViewPager.setPagingEnabled(false);
//        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.d("debug", "Selected ViewPager page number" + position);
//                lastSelected.setSelected(false);
//                switch (position) {
//                    case 0:
//                        lastSelected = spotlights;
//                        lastSelected.setSelected(true);
////                        showSpotOnboarding();
//                        (MainActivity.this).setTitle(getString(R.string.tabs_spotlights));
//                        break;
//                    case 1:
//                        lastSelected = friends;
//                        lastSelected.setSelected(true);
////                        showFriendsOnboarding();
//                        (MainActivity.this).setTitle(getString(R.string.tabs_friends));
//                        break;
//                    case 2:
//                        lastSelected = teams;
//                        lastSelected.setSelected(true);
////                        showTeamsOnboarding();
//                        (MainActivity.this).setTitle(getString(R.string.tabs_teams));
//                        break;
//                    case 3:
//                        lastSelected = profile;
//                        lastSelected.setSelected(true);
//                        (MainActivity.this).setTitle(getString(R.string.tabs_profile));
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });



    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }






    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean ret = false;
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            ret = true;
        }
        if (menuItem.getItemId() == R.id.action_add) {
            ret = false;
        }
        return ret;
    }

    /*
        Backstack listener
     */
    private FragmentManager.OnBackStackChangedListener backStackListener =
            new FragmentManager.OnBackStackChangedListener() {
        public void onBackStackChanged() {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
//            mViewPager.setPagingEnabled(!(getSupportFragmentManager().getBackStackEntryCount() > 0));
//            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                switch (mViewPager.getCurrentItem()) {
//                    case 0:
//                        setTitle(getString(R.string.tabs_spotlights));
//                        break;
//                    case 1:
//                        setTitle(getString(R.string.tabs_friends));
//                        break;
//                    case 2:
//                        setTitle(getString(R.string.tabs_teams));
//                        break;
//                    case 3:
//                        setTitle(getString(R.string.tabs_profile));
//                        break;
//                    default:
//                        break;
//                }
//            }
        }
    };


    /*
        View pager adapter
     */

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return tabViews.get(position);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }
    }



    /*
        Lower tabs actions
     */
    @OnClick({R.id.btn_tab_spotlights, R.id.btn_tab_friends, R.id.btn_tab_teams, R.id.btn_tab_profile})
    protected void changeTab(View view) {


        lastSelected.setSelected(false);
        lastSelected = view;
        lastSelected.setSelected(true);
        switch (view.getId()) {

            case R.id.btn_tab_spotlights:
//                mViewPager.setCurrentItem(0);
                FragmentUtils.changeFragment(this, R.id.inner_content, SpotlightsFragment.newInstance(), false);
                (MainActivity.this).setTitle(getString(R.string.tabs_spotlights));
                break;
            case R.id.btn_tab_friends:
//                mViewPager.setCurrentItem(1);
                FragmentUtils.changeFragment(this, R.id.inner_content, FriendsFragment.newInstance(), false);
                (MainActivity.this).setTitle(getString(R.string.tabs_friends));
                break;
            case R.id.btn_tab_teams:
//                mViewPager.setCurrentItem(2);
                FragmentUtils.changeFragment(this, R.id.inner_content, TeamsFragment.newInstance(), false);
                (MainActivity.this).setTitle(getString(R.string.tabs_teams));
                break;
            case R.id.btn_tab_profile:
//                mViewPager.setCurrentItem(3);
                FragmentUtils.changeFragment(this, R.id.inner_content, ProfileFragment.newInstance(), false);
                (MainActivity.this).setTitle(getString(R.string.tabs_profile));
                break;
            default:
                break;
        }
        FragmentUtils.popBackStack(this);
    }


}
