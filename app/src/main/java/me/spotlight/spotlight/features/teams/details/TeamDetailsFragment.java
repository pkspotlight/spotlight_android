package me.spotlight.spotlight.features.teams.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;

/**
 * Created by Anatol on 7/11/2016.
 */
public class TeamDetailsFragment extends BaseFragment {


    /*
        Manufacturing singleton
    */
    public static TeamDetailsFragment newInstance() {
        Bundle args = new Bundle();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.add_teams));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
