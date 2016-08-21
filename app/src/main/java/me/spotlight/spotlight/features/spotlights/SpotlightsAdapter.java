package me.spotlight.spotlight.features.spotlights;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.utils.ImageUtils;

/**
 * Created by Anatol on 7/23/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class SpotlightsAdapter extends RecyclerView.Adapter<SpotlightsAdapter.SpotlightHolder> {

    private Context context;
    private List<Spotlight> spotlights = new ArrayList<>();
    private ActionListener actionListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface ActionListener {
        void onShowDetails(Spotlight spotlight);
        void onDelete(Spotlight spotlight, int position);
    }

    public SpotlightsAdapter(Context context, List<Spotlight> spotlights, ActionListener actionListener) {
        this.context = context;
        this.spotlights = spotlights;
        this.actionListener = actionListener;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public SpotlightHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SpotlightHolder(LayoutInflater.from(context).inflate(R.layout.item_spotlight, parent, false));
    }

    @Override
    public void onBindViewHolder(final SpotlightHolder spotlightHolder, final int position) {
        try {
            final Spotlight spotlight = spotlights.get(position);
            viewBinderHelper.bind(spotlightHolder.revealLayout, spotlight.getObjectId());
            spotlightHolder.teamInfo.setText(spotlight.getTeam().getName()
                    + " " + spotlight.getTeam().getSport()
                    + " - Grade " + spotlight.getTeam().getGrade());
            spotlightHolder.spotCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionListener.onShowDetails(spotlight);
                }
            });
            spotlightHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewBinderHelper.closeLayout(spotlight.getObjectId());
                    actionListener.onDelete(spotlight, position);
                }
            });
            spotlightHolder.spotDate.setText(getMonth(spotlight.getMonth()) + " " +
                    String.valueOf(spotlight.getDay())
                    + ", "
                    + String.valueOf(spotlight.getYear()));
            ImageUtils.into(context, spotlightHolder.spotCover, spotlight.getCover());
            ImageUtils.into(context, spotlightHolder.spotAvatar, spotlight.getTeamsAvatar());

        } catch (Exception e) {
            Log.d("adapter", "exception");
        }
    }

    @Override
    public int getItemCount() {
        if (null != spotlights)
            return spotlights.size();
        else
            return 0;
    }

    public class SpotlightHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.delete) View delete;
        @Bind(R.id.reveal_layout) SwipeRevealLayout revealLayout;
        @Bind(R.id.spotlight_avatar) CircleImageView spotAvatar;
        @Bind(R.id.spotlight_cover) ImageView spotCover;
        @Bind(R.id.spotlight_team_info) TextView teamInfo;
        @Bind(R.id.spotlight_date) TextView spotDate;

        public SpotlightHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static String getMonth(String month) {
        switch (month) {
            case "Jan":
                return "January";
            case "Feb":
                return "February";
            case "Mar":
                return "March";
            case "Apr":
                return "April";
            case "May":
                return "May";
            case "Jun":
                return "June";
            case "Jul":
                return "July";
            case "Aug":
                return "August";
            case "Sep":
                return "September";
            case "Oct":
                return "October";
            case "Nov":
                return "November";
            case "Dec":
                return "December";
            default:
                return "";
        }
    }
}
