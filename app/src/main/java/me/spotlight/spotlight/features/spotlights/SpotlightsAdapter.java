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

/**
 * Created by gherg012 on 7/23/2016.
 */
public class SpotlightsAdapter extends RecyclerView.Adapter<SpotlightsAdapter.SpotlightHolder> {

    Context context;
    List<Spotlight> spotlights = new ArrayList<>();
    Transformation round;
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
        try {

            final Spotlight spotlight = spotlights.get(position);

            spotlightHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.d("touch", String.valueOf(motionEvent.getAction()));
                    Log.d("touch", String.valueOf(motionEvent.getX()));
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        xStart = motionEvent.getX();
                        Log.d("touch", String.valueOf(motionEvent.getX()));
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (motionEvent.getX() > (xStart)) {
                            Log.d("touch", String.valueOf(motionEvent.getX()));
                            spotlightHolder.group.setVisibility(View.VISIBLE);
                            spotlightHolder.group2.setVisibility(View.GONE);
                        } else if (motionEvent.getX() < (xStart)) {
                            Log.d("touch", String.valueOf(motionEvent.getX()));
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
                    Glide.with(context)
                            .load(spotlight.getCover())
                            .centerCrop()
                            .into(spotlightHolder.spotCover);
                    Glide.with(context)
                            .load(spotlight.getCover())
                            .centerCrop()
                            .into(spotlightHolder.spotCover2);
                } else {
                    // TODO:
                }
            } else {
                // TODO:
            }


            //team avatar
            if (null != spotlight.getTeamsAvatar()) {
                if (!"".equals(spotlight.getTeamsAvatar())) {
                    Glide.with(context)
                            .load(spotlight.getTeamsAvatar())
                            .into(spotlightHolder.spotAvatar);
                    Glide.with(context)
                            .load(spotlight.getTeamsAvatar())
                            .into(spotlightHolder.spotAvatar2);
                } else {
                    Glide.with(context)
                            .load(R.drawable.unknown_user)
                            .into(spotlightHolder.spotAvatar);
                    Glide.with(context)
                            .load(R.drawable.unknown_user)
                            .into(spotlightHolder.spotAvatar2);
                }
            } else {
                Glide.with(context)
                        .load(R.drawable.unknown_user)
                        .into(spotlightHolder.spotAvatar);
                Glide.with(context)
                        .load(R.drawable.unknown_user)
                        .into(spotlightHolder.spotAvatar2);
            }

//            spotlightHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    actionListener.onShowDetails(spotlight);
//                }
//            });
            spotlightHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spotlightHolder.group.setVisibility(View.VISIBLE);
                    spotlightHolder.group2.setVisibility(View.GONE);
                    actionListener.onDelete(spotlight, position);
                }
            });

            spotlightHolder.spot_date.setText(getMonth(spotlight.getMonth()) + " " +
                    String.valueOf(spotlight.getDay())
                    + ", "
                    + String.valueOf(spotlight.getYear()));

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

    public void removeItem(int position) {
//        spotlights.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, spotlights.size());
    }

    public class SpotlightHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.spotlight_avatar)
//        ImageView spotAvatar;
        CircleImageView spotAvatar;
        @Bind(R.id.spotlight_cover)
        ImageView spotCover;
        @Bind(R.id.spotlight_team_info)
        TextView teamInfo;
        @Bind(R.id.spotlight_date)
        TextView spot_date;
        @Bind(R.id.spotlight_avatar2)
//        ImageView spotAvatar2;
        CircleImageView spotAvatar2;
        @Bind(R.id.spotlight_cover2)
        ImageView spotCover2;
        @Bind(R.id.spotlight_team_info2)
        TextView teamInfo2;
        @Bind(R.id.spotlight_date2)
        TextView spotDate2;
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
