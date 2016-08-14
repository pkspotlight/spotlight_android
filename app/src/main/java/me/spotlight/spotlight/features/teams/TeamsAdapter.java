package me.spotlight.spotlight.features.teams;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.Team;

/**
 * Created by Anatol on 7/18/2016.
 */
public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.TeamHolder> {

    Context context;
    List<Team> teams = new ArrayList<>();
    Transformation round;
    ActionListener actionListener;
    boolean following;

    public interface ActionListener {
        void onShowDetails(Team team);
        void onRequestFollow(Team team, int position, boolean unfollow);
    }

    public TeamsAdapter(Context context, List<Team> teams, ActionListener actionListener, boolean following) {
        this.context = context;
        this.teams = teams;
        this.actionListener = actionListener;
        this.following = following;
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TeamHolder(LayoutInflater.from(context).inflate(R.layout.item_team, parent, false));
    }

    @Override
    public void onBindViewHolder(final TeamHolder teamHolder, final int position) {
        final Team team = teams.get(position);

        teamHolder.teamName.setText(team.getName());
        teamHolder.teamGrade.setText(team.getGrade());
        teamHolder.teamSport.setText(team.getSport());
        teamHolder.teamSeason.setText(team.getSeason() + " " + team.getYear());

        String avatarUrl = "";
        if (null != team.getAvatarUrl())
            avatarUrl = team.getAvatarUrl();
        // TODO: introduce is valid url check
//        if (!"".equals(avatarUrl)) {
//            Picasso.with(context)
//                    .load(avatarUrl)
//                    .fit().centerCrop()
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .transform(round)
//                    .into(teamHolder.teamAvatar);
//        } else {
//            Picasso.with(context)
//                    .load(R.drawable.unknown_user)
//                    .fit().centerCrop()
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .transform(round)
//                    .into(teamHolder.teamAvatar);
//        }

        if (!"".equals(avatarUrl)) {
            Glide.with(context)
                    .load(avatarUrl)
                    .into(teamHolder.teamAvatar);
        } else {
            Glide.with(context)
                    .load(R.drawable.unknown_user)
                    .into(teamHolder.teamAvatar);
        }

        teamHolder.fol2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onRequestFollow(team, position, team.isMine());
            }
        });

        if (following)
            teamHolder.teamFollowing.setText("Following");
        else
            teamHolder.teamFollowing.setText("Follow");

        if (team.isMine())
            teamHolder.teamFollowing.setText("Following");

        teamHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onShowDetails(team);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (null != teams)
            return teams.size();
        else
            return 0;
    }

    public class TeamHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.fol2)
        View fol2;
        @Bind(R.id.team_avatar)
//        ImageView teamAvatar;
        CircleImageView teamAvatar;
        @Bind(R.id.team_name)
        TextView teamName;
        @Bind(R.id.team_grade)
        TextView teamGrade;
        @Bind(R.id.team_sport)
        TextView teamSport;
        @Bind(R.id.team_season)
        TextView teamSeason;
        @Bind(R.id.team_following)
        TextView teamFollowing;

        public TeamHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
