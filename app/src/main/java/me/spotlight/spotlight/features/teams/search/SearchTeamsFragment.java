package me.spotlight.spotlight.features.teams.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;

/**
 * Created by Anatol on 7/11/2016.
 */
public class SearchTeamsFragment extends Fragment {


    /*
        Manufacturing singleton
    */
    public static SearchTeamsFragment newInstance() {
        Bundle args = new Bundle();
        SearchTeamsFragment searchTeamsFragment = new SearchTeamsFragment();
        searchTeamsFragment.setArguments(args);
        return searchTeamsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_search_teams, container, false);
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
        getActivity().setTitle(R.string.search_teams);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
