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
    float xStart;
    float xFinish;

    public interface ActionListener {
        void onShowDetails(Spotlight spotlight);
        void onDelete(Spotlight spotlight, int position);
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
    public void onBindViewHolder(final SpotlightHolder spotlightHolder, final int position) {
        final Spotlight spotlight = spotlights.get(position);

        spotlightHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("touch", String.valueOf(motionEvent.getAction()));
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    xStart = motionEvent.getX();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getX() > xStart) {
                        spotlightHolder.group.setVisibility(View.VISIBLE);
                        spotlightHolder.group2.setVisibility(View.GONE);
                    } else if (motionEvent.getX() < xStart) {
                        spotlightHolder.group2.setVisibility(View.VISIBLE);
                        spotlightHolder.group.setVisibility(View.GONE);
                    } else {
                        actionListener.onShowDetails(spotlight);
                    }
                }
                return true;
            }
        });


        spotlightHolder.teamInfo.setText(spotlight.getTeam().getName()
                + " " + spotlight.getTeam().getSport()
                + " - Grade " + spotlight.getTeam().getGrade());
        spotlightHolder.teamInfo2.setText(spotlight.getTeam().getName()
                + " " + spotlight.getTeam().getSport()
                + " - Grade " + spotlight.getTeam().getGrade());



        // cover
        if (null != spotlight.getCover()) {
            if (!"".equals(spotlight.getCover())) {
                Picasso.with(context)
                        .load(spotlight.getCover())
                        .fit().centerCrop()
                        .into(spotlightHolder.spotCover);
                Picasso.with(context)
                        .load(spotlight.getCover())
                        .fit().centerCrop()
                        .into(spotlightHolder.spotCover2);
            } else {
                Picasso.with(context)
                        .load(R.drawable.spot_placeholder)
                        .fit().centerCrop()
                        .into(spotlightHolder.spotCover);
                Picasso.with(context)
                        .load(R.drawable.spot_placeholder)
                        .fit().centerCrop()
                        .into(spotlightHolder.spotCover2);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.spot_placeholder)
                    .fit().centerCrop()
                    .into(spotlightHolder.spotCover);
            Picasso.with(context)
                    .load(R.drawable.spot_placeholder)
                    .fit().centerCrop()
                    .into(spotlightHolder.spotCover2);
        }


        //team avatar
        if (null != spotlight.getTeamsAvatar()) {
            if (!"".equals(spotlight.getTeamsAvatar())) {
                Picasso.with(context)
                        .load(spotlight.getTeamsAvatar())
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightHolder.spotAvatar);
                Picasso.with(context)
                        .load(spotlight.getTeamsAvatar())
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightHolder.spotAvatar2);
            } else {
                Picasso.with(context)
                        .load(R.drawable.unknown_user)
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightHolder.spotAvatar);
                Picasso.with(context)
                        .load(R.drawable.unknown_user)
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightHolder.spotAvatar2);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
                    .into(spotlightHolder.spotAvatar);
            Picasso.with(context)
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
                    .into(spotlightHolder.spotAvatar2);
        }

        spotlightHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onShowDetails(spotlight);
            }
        });
        spotlightHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onDelete(spotlight, position);
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

    public void removeItem(int position) {
//        spotlights.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, spotlights.size());
    }

    public class SpotlightHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.spotlight_avatar)
        ImageView spotAvatar;
        @Bind(R.id.spotlight_cover)
        ImageView spotCover;
        @Bind(R.id.spotlight_team_info)
        TextView teamInfo;
        @Bind(R.id.spotlight_avatar2)
        ImageView spotAvatar2;
        @Bind(R.id.spotlight_cover2)
        ImageView spotCover2;
        @Bind(R.id.spotlight_team_info2)
        TextView teamInfo2;
        @Bind(R.id.item_spot_frame)
        View group;
        @Bind(R.id.item_spot_frame2)
        View group2;
        @Bind(R.id.remove)
        View remove;

        public SpotlightHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
