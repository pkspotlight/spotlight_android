package me.spotlight.spotlight.features.spotlights.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;

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

    @OnClick(R.id.create)
    public void create() {
        final String spotlightTitle = String.valueOf(System.currentTimeMillis());
        final Bundle bundle = new Bundle();
        bundle.putString("objectId", getArguments().getString("objectId"));
        bundle.putString("title", spotlightTitle);
        bundle.putString("teamName", getArguments().getString("teamName"));
        bundle.putString("teamAvatar", getArguments().getString("teamAvatar"));

        final ParseObject spotlight = new ParseObject(ParseConstants.OBJECT_SPOTLIGHT);
        spotlight.put("creatorName", ParseUser.getCurrentUser().getString("firstName") + " " +
                                        ParseUser.getCurrentUser().getString("lastName"));
        ParseRelation<ParseUser> participant = spotlight.getRelation("spotlightParticipant");
        participant.add(ParseUser.getCurrentUser());
        ParseRelation<ParseUser> creator = spotlight.getRelation("creator");
        creator.add(ParseUser.getCurrentUser());
        ParseRelation<ParseUser> moderators = spotlight.getRelation("moderators");
        moderators.add(ParseUser.getCurrentUser());

        ParseQuery<ParseObject> currentTeamQuery = new ParseQuery<ParseObject>(ParseConstants.OBJECT_TEAM);
        currentTeamQuery.whereEqualTo("objectId", getArguments().getString("objectId"));
        currentTeamQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    ParseObject currentTeam = objects.get(0);
                    Log.d("currentTeam", currentTeam.getObjectId());
                    // proceed to finishing creating spotlight
                    spotlight.put("team", currentTeam);
                    spotlight.put("title", spotlightTitle);
                    // proceed to saving the spotlight to Parse
                    spotlight.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (null == e) {
                                FragmentUtils.changeFragment(getActivity(), R.id.content, NewSpotlightFragment.newInstance(bundle), true);
                            } else {
                                Log.d("savingSpotlight", e.getMessage());
                            }
                        }
                    });
                } else { Log.d("currentTeam", e.getMessage()); }
            }
        });

    }

}
