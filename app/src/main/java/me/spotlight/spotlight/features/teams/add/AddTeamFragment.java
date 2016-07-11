package me.spotlight.spotlight.features.teams.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;

/**
 * Created by Anatol on 7/10/2016.
 */
public class AddTeamFragment extends Fragment {

    /*
        Manufacturing singleton
    */
    public static AddTeamFragment newInstance() {
        Bundle args = new Bundle();
        AddTeamFragment addTeamFragment = new AddTeamFragment();
        addTeamFragment.setArguments(args);
        return addTeamFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_add_teams, container, false);
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
