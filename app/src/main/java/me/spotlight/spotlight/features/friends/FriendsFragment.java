package me.spotlight.spotlight.features.friends;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;
import me.spotlight.spotlight.features.friends.add.AddFamilyFragment;
import me.spotlight.spotlight.features.friends.add.AddSpotlightersFragment;
import me.spotlight.spotlight.features.teams.add.AddTeamFragment;
import me.spotlight.spotlight.features.teams.search.SearchTeamsFragment;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/11/2016.
 */
public class FriendsFragment extends Fragment {

    /*
        Manufacturing singleton
    */
    public static FriendsFragment newInstance() {
        Bundle args = new Bundle();
        FriendsFragment friendsFragment = new FriendsFragment();
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_friends, container, false);
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


    public void addSpotlighters() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, AddSpotlightersFragment.newInstance(), true);
    }

    public void addFamily() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, AddFamilyFragment.newInstance(), true);
    }

    @OnClick(R.id.fab_add_spotlighters)
    public void onFab() {

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.friends_dialog))
                .setItems(getResources().getTextArray(R.array.friends_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                addSpotlighters();
                                break;
                            case 1:
                                addFamily();
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
