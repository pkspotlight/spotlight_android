package me.spotlight.spotlight.features.teams.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import me.spotlight.spotlight.R;

/**
 * Created by Anatol on 7/18/2016.
 */
public class TeamSpotlightsFragment extends Fragment {

    /*
        Manufacturing singleton
     */
    public static TeamSpotlightsFragment newInstance(Bundle args) {
        TeamSpotlightsFragment teamSpotlightsFragment = new TeamSpotlightsFragment();
        teamSpotlightsFragment.setArguments(args);
        return teamSpotlightsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_team_details_spotlights, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}