package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.RecommendTagEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class RecommendTagHolder extends ClickableViewHolder {

    private int[] colors = { R.color.blue_39d8d8,
            R.color.yellow_f2cc2c,
            R.color.orange_ed853e,
            R.color.pink_fb7ba2,
            R.color.green_93d856,
            R.color.purple_cd8add,
            R.color.blue_4fc3f7
    };

    public RecommendTagHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final RecommendTagEntity entity, final int position){
        setText(R.id.tv_tag,entity.getWord());
        int index = StringUtils.getHashOfString(entity.getWord(), colors.length);
        ((TextView)$(R.id.tv_tag)).setTextColor(ContextCompat.getColor(context,colors[index]));
    }
}
