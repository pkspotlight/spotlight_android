package me.spotlight.spotlight.features.teams.requests;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.TeamRequest;

/**
 * Created by Anatol on 8/4/2016.
 */
public class RequestsFragment extends Fragment {

    @Bind(R.id.recycler_view_requests)
    RecyclerView recyclerView;
    ArrayList<TeamRequest> requests = new ArrayList<>();

    /*
        Manufacturing singleton
     */
    public static RequestsFragment newInstance(Bundle bundle) {
        RequestsFragment requestsFragment = new RequestsFragment();
        requestsFragment.setArguments(bundle);
        return requestsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_requests, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
