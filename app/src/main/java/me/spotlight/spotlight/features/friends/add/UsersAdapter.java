package me.spotlight.spotlight.features.friends.add;

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
import me.spotlight.spotlight.models.Friend;
import me.spotlight.spotlight.models.User;

/**
 * Created by Anatol on 7/15/2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {

    Context context;
    List<User> users = new ArrayList<>();
    Transformation round;
    ActionListener actionListener;

    public interface ActionListener {
        void onShowDetails(User user);
        void onFollow(User user, int position);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public UsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserHolder userHolder, final int position) {
        final User user = users.get(position);

        // populating name
        userHolder.friendName.setText(user.getFirstName() + " " + user.getLastName());

        // showing avatar
//        if (null != friend.getProfilePic()) {
//            if (null != friend.getProfilePic().getMediaFile()) {
//                if (null != friend.getProfilePic().getMediaFile().getUrl()) {
//                    avatarUrl = friend.getProfilePic().getMediaFile().getUrl();
//                }
//            }
//        }
        String avatarUrl = "";
        if (null != user.getAvatarUrl())
            avatarUrl = user.getAvatarUrl();
        // TODO: introduce is valid url check
//        if (!"".equals(avatarUrl)) {
//            Picasso.with(context).load(avatarUrl).fit().centerCrop()
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .transform(round).into(userHolder.friendAvatar);
//        } else {
//            Picasso.with(context).load(R.drawable.unknown_user).fit().centerCrop()
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .transform(round).into(userHolder.friendAvatar);
//        }

        if (!"".equals(avatarUrl)) {
            Glide.with(context)
                    .load(avatarUrl)
                    .into(userHolder.friendAvatar);
        } else {
            Glide.with(context)
                    .load(R.drawable.unknown_user)
                    .into(userHolder.friendAvatar);
        }

        // for now always showing -- later introduce a boolean
        userHolder.friendFollowing.setVisibility(View.VISIBLE);

        if (user.isFriend()) {
            userHolder.friendFollowing.setText("Following");
        } else {
            userHolder.friendFollowing.setText("Follow");
        }

        userHolder.fol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onFollow(user, position);
            }
        });

        userHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onShowDetails(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null != users)
            return users.size();
        else
            return 0;
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.friend_avatar)
//        ImageView friendAvatar;
        CircleImageView friendAvatar;
        @Bind(R.id.friend_name)
        TextView friendName;
        @Bind(R.id.friend_following)
        TextView friendFollowing;
        @Bind(R.id.fol)
        View fol;

        public UserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
