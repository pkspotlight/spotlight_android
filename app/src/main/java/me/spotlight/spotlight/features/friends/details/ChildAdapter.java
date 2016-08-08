package me.spotlight.spotlight.features.friends.details;

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
import me.spotlight.spotlight.models.Child;

/**
 * Created by Anatol on 8/8/2016.
 */
public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildHolder> {

    Context context;
    List<Child> children = new ArrayList<>();
    Transformation round;
    String avatarUrl = "";
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

        if (null != child.getAvatarUrl())
            avatarUrl = child.getAvatarUrl();

        if (!"".equals(avatarUrl)) {
            Picasso.with(context)
                    .load(avatarUrl)
                    .fit().centerCrop()
                    .transform(round)
                    .into(childHolder.childAvatar);
        } else {
            Picasso.with(context)
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
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
        ImageView childAvatar;
        @Bind(R.id.child_name)
        TextView childName;

        public ChildHolder (View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
