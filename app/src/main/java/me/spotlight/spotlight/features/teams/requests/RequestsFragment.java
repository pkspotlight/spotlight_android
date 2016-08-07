package me.spotlight.spotlight.features.teams.requests;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
public class RequestsFragment extends Fragment implements RequestAdapter.ActionListener {

    @Bind(R.id.recycler_view_requests)
    RecyclerView recyclerView;
    ArrayList<TeamRequest> requests = new ArrayList<>();
    RequestAdapter requestAdapter;

    /*
        Manufacturing singleton
     */
    public static RequestsFragment newInstance(Bundle bundle) {
        RequestsFragment requestsFragment = new RequestsFragment();
        requestsFragment.setArguments(bundle);
        return requestsFragment;
    }

    @Override
    public void onAccept(TeamRequest teamRequest) {
        //
    }

    @Override
    public void onDecline(TeamRequest teamRequest) {
        //
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestAdapter = new RequestAdapter(getActivity(), requests, this);
        recyclerView.setAdapter(requestAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Requests");
    }
}
