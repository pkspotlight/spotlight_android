package me.spotlight.spotlight.features.spotlights;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.Spotlight;

/**
 * Created by gherg012 on 7/23/2016.
 */
public class SpotlightsAdapter extends RecyclerView.Adapter<SpotlightsAdapter.SpotlightHolder> {

    Context context;
    List<Spotlight> spotlights = new ArrayList<>();
    Transformation round;
    String avatarUrl = "";
    ActionListener actionListener;

    public interface ActionListener {
        void onShowDetails(Spotlight spotlight);
    }

    public SpotlightsAdapter(Context context, List<Spotlight> spotlights, ActionListener actionListener) {
        this.context = context;
        this.spotlights = spotlights;
        this.actionListener = actionListener;
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public SpotlightHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SpotlightHolder(LayoutInflater.from(context).inflate(R.layout.item_spotlight, parent, false));
    }

    @Override
    public void onBindViewHolder(final SpotlightHolder spotlightHolder, int position) {
        final Spotlight spotlight = spotlights.get(position);


        spotlightHolder.teamInfo.setText(spotlight.getTeam().getName()
                                           + " " + spotlight.getTeam().getSport()
                                            + " - Grade " + spotlight.getTeam().getGrade());



        // cover
        if (null != spotlight.getCover()) {
            if (!"".equals(spotlight.getCover())) {
                Picasso.with(context)
                        .load(spotlight.getCover())
                        .fit().centerCrop()
                        .into(spotlightHolder.spotCover);
            } else {
                Picasso.with(context)
                        .load(R.drawable.spot_placeholder)
                        .fit().centerCrop()
                        .into(spotlightHolder.spotCover);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.spot_placeholder)
                    .fit().centerCrop()
                    .into(spotlightHolder.spotCover);
        }


        //team avatar
        if (null != spotlight.getTeamsAvatar()) {
            if (!"".equals(spotlight.getTeamsAvatar())) {
                Picasso.with(context)
                        .load(spotlight.getTeamsAvatar())
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightHolder.spotAvatar);
            } else {
                Picasso.with(context)
                        .load(R.drawable.unknown_user)
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightHolder.spotAvatar);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
                    .into(spotlightHolder.spotAvatar);
        }

        spotlightHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onShowDetails(spotlight);
            }
        });
        // TODO: implementation
    }

    @Override
    public int getItemCount() {
        if (null != spotlights)
            return spotlights.size();
        else
            return 0;
    }

    public class SpotlightHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.spotlight_avatar)
        ImageView spotAvatar;
        @Bind(R.id.spotlight_cover)
        ImageView spotCover;
        @Bind(R.id.spotlight_team_info)
        TextView teamInfo;

        public SpotlightHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
