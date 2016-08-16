package me.spotlight.spotlight.features.friends;

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
import me.spotlight.spotlight.models.Child;

/**
 * Created by Anatol on 8/8/2016.
 */
public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildHolder> {

    Context context;
    List<Child> children = new ArrayList<>();
    Transformation round;
    ActionListener actionListener;

    public interface ActionListener {
        void onViewChildDetails(Child child);
    }

    public ChildAdapter(Context context, List<Child> children, ActionListener actionListener) {
        this.context = context;
        this.children = children;
        this.actionListener = actionListener;
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public ChildHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChildHolder(LayoutInflater.from(context).inflate(R.layout.item_child, parent, false));
    }

    @Override
    public void onBindViewHolder(final ChildHolder childHolder, int position) {
        final Child child = children.get(position);

        childHolder.childName.setText(child.getFirstName() + " " + child.getLastName());


        String avatarUrl = "";
        if (null != child.getAvatarUrl())
            avatarUrl = child.getAvatarUrl();

        if (!"".equals(avatarUrl)) {
            Glide.with(context)
                    .load(avatarUrl)
//                    .skipMemoryCache(true)
                    .into(childHolder.childAvatar);
        } else {
            Glide.with(context)
                    .load(R.drawable.unknown_user)
//                    .skipMemoryCache(true)
                    .into(childHolder.childAvatar);
        }

        if (null != actionListener) {
            childHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionListener.onViewChildDetails(child);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (null != children)
            return children.size();
        else
            return 0;
    }

    public class ChildHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.child_avatar)
        CircleImageView childAvatar;
        @Bind(R.id.child_name)
        TextView childName;

        public ChildHolder (View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
