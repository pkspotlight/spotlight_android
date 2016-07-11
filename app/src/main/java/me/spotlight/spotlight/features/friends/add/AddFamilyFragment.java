package me.spotlight.spotlight.features.friends.add;

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
public class AddFamilyFragment extends Fragment {

    /*
        Manufacturing singleton
    */
    public static AddFamilyFragment newInstance() {
        Bundle args = new Bundle();
        AddFamilyFragment addFamilyFragment = new AddFamilyFragment();
        addFamilyFragment.setArguments(args);
        return addFamilyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_add_family, container, false);
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
        getActivity().setTitle(getString(R.string.add_family));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
