package me.spotlight.spotlight.features.spotlights;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

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

        // TODO: bind the fields here

        public SpotlightHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
