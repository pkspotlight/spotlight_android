package me.spotlight.spotlight.features.teams.requests;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.models.Team;
import me.spotlight.spotlight.models.TeamRequest;

/**
 * Created by Anatol on 8/5/2016.
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

    Context context;
    List<TeamRequest> requests = new ArrayList<>();
    Transformation round;
    String avatarUrl = "";
    ActionListener actionListener;

    public interface ActionListener {
        void onDecline(TeamRequest teamRequest);
        void onAccept(TeamRequest teamRequest);
    }

    public RequestAdapter(Context context, List<TeamRequest> requests, ActionListener actionListener) {
        this.context = context;
        this.requests = requests;
        this.actionListener = actionListener;
        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RequestHolder(LayoutInflater.from(context).inflate(R.layout.item_request, parent, false));
    }


    @Override
    public void onBindViewHolder(final RequestHolder requestHolder, int position) {
        final TeamRequest teamRequest = requests.get(position);

        requestHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != actionListener)
                    actionListener.onAccept(teamRequest);
            }
        });

        requestHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != actionListener)
                    actionListener.onDecline(teamRequest);
            }
        });

        requestHolder.textView.setText("");
    }


    @Override
    public int getItemCount() {
        if (null != requests) {
            return requests.size();
        } else {
            return 0;
        }
    }


    public class RequestHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.request_accept)
        View accept;
        @Bind(R.id.request_decline)
        View decline;
        @Bind(R.id.requester_avatar)
        ImageView avatar;
        @Bind(R.id.requester_text)
        TextView textView;

        public RequestHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
