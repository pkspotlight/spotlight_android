package me.spotlight.spotlight.features.spotlights.details;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.TheaterActivity;
import me.spotlight.spotlight.features.spotlights.SpotlightsFragment;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;

/**
 * Created by Anatol on 7/22/2016.
 */
public class SpotlightDetailsFragment extends Fragment {

    @Bind(R.id.recycler_view_details)
    RecyclerView details;
    SpotPreviewAdapter spotPreviewAdapter;
    List<String> urls = new ArrayList<>();
    Transformation round;
    @Bind(R.id.spotlight_avatar)
    ImageView spotlightAvatar;
    @Bind(R.id.spot_details_date)
    TextView spotlightDate;
    @Bind(R.id.spot_details_info)
    TextView spotlightInfo;
    @Bind(R.id.spot_details_name)
    TextView spotlightName;
    public static List<String> movs = new ArrayList<>();

    /*
        Manufacturing singleton
    */
    public static SpotlightDetailsFragment newInstance(Bundle args) {
        SpotlightDetailsFragment spotlightDetailsFragment = new SpotlightDetailsFragment();
        spotlightDetailsFragment.setArguments(args);
        return spotlightDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_spotlight_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        round = new RoundedTransformationBuilder().oval(true).build();
        details.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        spotPreviewAdapter = new SpotPreviewAdapter(getActivity(), urls);
        details.setAdapter(spotPreviewAdapter);

        loadSpotDetails();
    }

    @Override
    public void onResume() {
        super.onResume();

        initTeamAvatar();
        populateInfo();
    }







    @OnClick(R.id.view)
    public void view(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.view_dialog))
                .setItems(getResources().getTextArray(R.array.spot_view), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
//                                Toast.makeText(getContext(), "Cool Kids", Toast.LENGTH_LONG).show();
                                startActivity(TheaterActivity.getStartIntent(getContext()));
                                break;
                            case 1:
                                Toast.makeText(getContext(), "Disney Funk", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getContext(), "Every single night", Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                Toast.makeText(getContext(), "Ready 2 Go", Toast.LENGTH_LONG).show();
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

    @OnClick(R.id.share)
    public void share(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Jobularity");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing spotlight!");
        getActivity().startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }




    private void loadSpotDetails() {
        for (SpotlightMedia spotlightMedia : SpotlightsFragment.spotlightMedias) {
            if (spotlightMedia.getParentId().equals(getArguments().getString("objectId"))) {
                urls.add(spotlightMedia.getThumbnailUrl());
                movs.add(spotlightMedia.getFileUrl());
            }
        }
        spotPreviewAdapter.notifyDataSetChanged();
    }



    private void initTeamAvatar() {
        if (null != getArguments().getString("teamAvatar")) {
            if (!"".equals(getArguments().getString("teamAvatar"))) {
                Picasso.with(getContext())
                        .load(getArguments().getString("teamAvatar"))
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightAvatar);
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.unknown_user)
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightAvatar);
            }
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
                    .into(spotlightAvatar);
        }
    }

    private void populateInfo() {
        spotlightInfo.setText("Grade " + getArguments().getString("teamGrade") + " "
                                    + getArguments().getString("teamSport"));
        spotlightName.setText(getArguments().getString("teamName"));
    }
}
