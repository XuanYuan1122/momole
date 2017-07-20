package com.moemoe.lalala.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.Image;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by yi on 2017/7/18.
 */

public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Image image = (Image) path;
        Glide.with(context)
                .load(StringUtils.getUrl(context, image.getPath(), DensityUtil.getScreenWidth(context), DensityUtil.getScreenWidth(context), true, true))
                .override( DensityUtil.getScreenWidth(context), DensityUtil.getScreenWidth(context))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(imageView);
    }
}
