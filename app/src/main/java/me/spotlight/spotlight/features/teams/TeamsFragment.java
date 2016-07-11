package me.spotlight.spotlight.features.teams;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;
import me.spotlight.spotlight.features.teams.add.AddTeamFragment;
import me.spotlight.spotlight.features.teams.search.SearchTeamsFragment;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/11/2016.
 */
public class TeamsFragment extends Fragment {

    /*
        Manufacturing singleton
     */
    public static TeamsFragment newInstance() {
        Bundle args = new Bundle();
        TeamsFragment teamsFragment = new TeamsFragment();
        teamsFragment.setArguments(args);
        return teamsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_teams, container, false);
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


    public void addTeams() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, AddTeamFragment.newInstance(), true);
    }

    public void searchTeams() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, SearchTeamsFragment.newInstance(), true);
    }

    @OnClick(R.id.fab_add_teams)
    public void onFab() {

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.teams_dialog))
                .setItems(getResources().getTextArray(R.array.teams_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                searchTeams();
                                break;
                            case 1:
                                addTeams();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();

    }
}
