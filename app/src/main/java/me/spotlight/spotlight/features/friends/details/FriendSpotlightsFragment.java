package me.spotlight.spotlight.features.friends.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.spotlight.spotlight.R;

/**
 * Created by Anatol on 7/21/2016.
 */
public class FriendSpotlightsFragment extends Fragment {

    /*
        Manufacturing singleton
     */
    public static FriendSpotlightsFragment newInstance(Bundle args) {
        FriendSpotlightsFragment friendSpotlightsFragment = new FriendSpotlightsFragment();
        friendSpotlightsFragment.setArguments(args);
        return friendSpotlightsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_friend_details_spotlights, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
