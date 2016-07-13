package me.spotlight.spotlight.features.spotlights;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;
import me.spotlight.spotlight.features.spotlights.add.AddSpotlightFragment;
import me.spotlight.spotlight.features.teams.add.AddTeamFragment;
import me.spotlight.spotlight.features.teams.search.SearchTeamsFragment;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/10/2016.
 */
public class SpotlightsFragment extends Fragment {

    /*
        Manufacturing singleton
     */
    public static SpotlightsFragment newInstance() {
        Bundle args = new Bundle();
        SpotlightsFragment spotlightsFragment = new SpotlightsFragment();
        spotlightsFragment.setArguments(args);
        return spotlightsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_spotlights, container, false);
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


    public void addSpotlight() {
        FragmentUtils.changeFragment(getActivity(), R.id.content, AddSpotlightFragment.newInstance(), true);
    }

    @OnClick(R.id.fab_add_spotlight)
    public void onFab() {

//        addSpotlight();

        ParseQuery<ParseObject> mParseQuery = new ParseQuery("Team");
        mParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
//                    Toast.makeText(getContext(), "Teams not null", Toast.LENGTH_SHORT).show();
                    for (ParseObject parseObject : objects) {
                        Log.d("parseq", String.valueOf(parseObject.get("town")));
                    }
                } else {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
