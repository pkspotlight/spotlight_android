package me.spotlight.spotlight.features.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import me.spotlight.spotlight.models.Friend;
import me.spotlight.spotlight.models.Team;

/**
 * Created by Anatol on 7/15/2016.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    Context context;
    List<Friend> friends = new ArrayList<>();
    Transformation round;
    String avatarUrl = "";
    ActionListener actionListener;

    public interface ActionListener {
        void onShowDetails(Friend friend);
        void onUnfollow(Friend friend);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public FriendsAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendHolder(LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(final FriendHolder friendHolder, int position) {
        final Friend friend = friends.get(position);

        // populating name
        friendHolder.friendName.setText(friend.getFirstName() + " " + friend.getLastName());

        // showing avatar
//        if (null != friend.getProfilePic()) {
//            if (null != friend.getProfilePic().getMediaFile()) {
//                if (null != friend.getProfilePic().getMediaFile().getUrl()) {
//                    avatarUrl = friend.getProfilePic().getMediaFile().getUrl();
//                }
//            }
//        }
        if (null != friend.getAvatarUrl())
            avatarUrl = friend.getAvatarUrl();
        // TODO: introduce is valid url check
        if (!"".equals(avatarUrl)) {
            Picasso.with(context).load(avatarUrl).fit().centerCrop()
                    .transform(round).into(friendHolder.friendAvatar);
        } else {
            Picasso.with(context).load(R.drawable.unknown_user).fit().centerCrop()
                    .transform(round).into(friendHolder.friendAvatar);
        }

        // for now always showing -- later introduce a boolean
        friendHolder.friendFollowing.setVisibility(View.VISIBLE);

        friendHolder.fol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onUnfollow(friend);
            }
        });

        friendHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onShowDetails(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null != friends)
            return friends.size();
        else
            return 0;
    }

    public class FriendHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.friend_avatar)
        ImageView friendAvatar;
        @Bind(R.id.friend_name)
        TextView friendName;
        @Bind(R.id.friend_following)
        TextView friendFollowing;
        @Bind(R.id.fol)
        View fol;

        public FriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
