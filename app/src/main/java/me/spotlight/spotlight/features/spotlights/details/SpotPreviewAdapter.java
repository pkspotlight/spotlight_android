package me.spotlight.spotlight.features.spotlights.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;

/**
 * Created by Anatol on 7/25/2016.
 */
public class SpotPreviewAdapter extends RecyclerView.Adapter<SpotPreviewAdapter.PreviewHolder> {

    Context context;
    List<String> urls;

    public SpotPreviewAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PreviewHolder(LayoutInflater.from(context).inflate(R.layout.item_spotlight_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(final PreviewHolder previewHolder, int position) {
        final String url = urls.get(position);

        Glide.with(context).load(url)
                .fitCenter()
                .into(previewHolder.preview);
    }

    @Override
    public int getItemCount() {
        if (null != urls)
            return urls.size();
        else
            return 0;
    }


    public class PreviewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.spot_preview)
        ImageView preview;

        public PreviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
