package me.spotlight.spotlight.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import me.spotlight.spotlight.R;

/**
 * Created by Evgheni on 8/20/2016.
 */
public class ImageUtils {

    public static final String TAG = "ImageUtils";

    public static void into(Context context, CircleImageView circleImageView, String url) {
        try {
            if (null != url) {
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(context)
                            .load(url)
                            .into(circleImageView);
                } else {
                    Glide.with(context)
                            .load(R.drawable.unknown_user)
                            .into(circleImageView);
                }
            } else {
                Glide.with(context)
                        .load(R.drawable.unknown_user)
                        .into(circleImageView);
            }
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }

    public static void into(Context context, ImageView imageView, String url) {
        try {
            if (null != url) {
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(context)
                            .load(url)
                            .centerCrop()
                            .into(imageView);
                } else {
                    Glide.with(context)
                            .load(R.drawable.unknown_user)
                            .centerCrop()
                            .into(imageView);
                }
            } else {
                Glide.with(context)
                        .load(R.drawable.unknown_user)
                        .centerCrop()
                        .into(imageView);
            }
        } catch (Exception e) {
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }
}
