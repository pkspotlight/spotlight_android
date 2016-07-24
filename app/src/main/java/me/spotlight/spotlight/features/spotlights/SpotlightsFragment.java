package me.spotlight.spotlight.features.spotlights;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.features.spotlights.add.AddSpotlightFragment;
import me.spotlight.spotlight.features.spotlights.details.SpotlightDetailsFragment;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/10/2016.
 */
public class SpotlightsFragment extends Fragment
        implements SpotlightsAdapter.ActionListener {

    @Bind(R.id.recycler_view_spolights)
    RecyclerView mySpotlightsList;
    SpotlightsAdapter spotlightsAdapter;
    List<Spotlight> mySpotlights = new ArrayList<>();
    @Bind(R.id.swipe_spotlights)
    SwipeRefreshLayout swipeSpotlights;

    /*
        Manufacturing singleton
     */
    public static SpotlightsFragment newInstance() {
        Bundle args = new Bundle();
        SpotlightsFragment spotlightsFragment = new SpotlightsFragment();
        spotlightsFragment.setArguments(args);
        return spotlightsFragment;
    }

    public void onShowDetails(Spotlight spotlight) {
        Bundle bundle = new Bundle();
        bundle.putString("objectId", spotlight.getObjectId());
        FragmentUtils.changeFragment(getActivity(), R.id.content, SpotlightDetailsFragment.newInstance(bundle), true);
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
        mySpotlightsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        spotlightsAdapter = new SpotlightsAdapter(getActivity(), mySpotlights, this);
        mySpotlightsList.setAdapter(spotlightsAdapter);
        swipeSpotlights.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mySpotlights.isEmpty())
                    mySpotlights.clear();
                spotlightsAdapter.notifyDataSetChanged();
                loadSpotlights();
                swipeSpotlights.setRefreshing(false);
            }
        });

        loadSpotlights();
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
        addSpotlight();
    }

    private void loadSpotlights() {
        Spotlight spotlight = new Spotlight();
        spotlight.setObjectId("dfdfdsfsd");
        mySpotlights.add(spotlight);
        mySpotlights.add(spotlight);
        mySpotlights.add(spotlight);
        mySpotlights.add(spotlight);
        mySpotlights.add(spotlight);
        mySpotlights.add(spotlight);
        spotlightsAdapter.notifyDataSetChanged();
    }
}
