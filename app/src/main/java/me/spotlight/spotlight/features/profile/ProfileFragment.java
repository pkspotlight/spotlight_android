package me.spotlight.spotlight.features.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseFragment;

/**
 * Created by Anatol on 7/11/2016.
 */
public class ProfileFragment extends Fragment {

    /*
        Manufacturing singleton
     */
    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
}
