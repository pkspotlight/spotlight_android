package me.spotlight.spotlight.features.spotlights.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;

/**
 * Created by Evgheni on 7/26/2016.
 */
public class FinishSpotlightFragment extends Fragment {

    @Bind(R.id.team_detail_avatar)
    ImageView teamAvatar;
    @Bind(R.id.team_detail_name)
    TextView teamName;
    Transformation round;

    /*
        Manufacturing singleton
    */
    public static FinishSpotlightFragment newInstance(Bundle args) {
        FinishSpotlightFragment finishSpotlightFragment = new FinishSpotlightFragment();
        finishSpotlightFragment.setArguments(args);
        return finishSpotlightFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_finish_spotlight, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Create Spotlight");
        initAvatar();
        populateFields();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initAvatar() {
        if (null != getArguments().getString("teamAvatar")) {
            if (!"".equals(getArguments().getString("teamAvatar"))) {
                Picasso.with(getContext())
                        .load(getArguments().getString("teamAvatar"))
                        .fit().centerCrop()
                        .transform(round)
                        .into(teamAvatar);
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.unknown_user)
                        .fit().centerCrop()
                        .transform(round)
                        .into(teamAvatar);
            }
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
                    .into(teamAvatar);
        }
    }

    private void populateFields() {
        teamName.setText(getArguments().getString("teamName"));
    }
}
