package me.spotlight.spotlight.features.spotlights.add;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.spotlight.spotlight.R;

/**
 * Created by Anatol on 7/31/2016.
 */
public class SpotCreateAdapter extends RecyclerView.Adapter<SpotCreateAdapter.CreateHolder> {

    Context context;
    List<Bitmap> bitmaps = new ArrayList<>();

    public SpotCreateAdapter(Context context, List<Bitmap> bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public CreateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CreateHolder(LayoutInflater.from(context).inflate(R.layout.item_spotlight_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(final CreateHolder createHolder, int position) {
        final Bitmap bitmap = bitmaps.get(position);
        createHolder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        if (null != bitmaps)
            return bitmaps.size();
        else
            return 0;
    }

    public class CreateHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.spot_preview)
        ImageView imageView;

        public CreateHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
