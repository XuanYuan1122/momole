package com.moemoe.lalala.view.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/7/30.
 */

public class XianChongListAdapter extends BaseRecyclerViewAdapter<XianChongEntity,ClickableViewHolder>{


    public XianChongListAdapter() {
        super(R.layout.item_main_xianchong);
    }

    @Override
    protected void convert(ClickableViewHolder helper, XianChongEntity item, int position) {
        helper.setText(R.id.tv_name, item.getNickName());
        Glide.with(context)
                .load(StringUtils.getUrl(context, item.getHeadPath(),(int)context.getResources().getDimension(R.dimen.y100), (int)context.getResources().getDimension(R.dimen.y100),false,false))
                .override((int)context.getResources().getDimension(R.dimen.y100), (int)context.getResources().getDimension(R.dimen.y100))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) helper.$(R.id.iv_avatar));
        if(position == 0){
            helper.setVisible(R.id.iv_top,true);
            helper.setImageResource(R.id.iv_top,R.drawable.ic_feed_crown_gold);
        }else if(position == 1){
            helper.setVisible(R.id.iv_top,true);
            helper.setImageResource(R.id.iv_top,R.drawable.ic_feed_crown_silver);
        }else if(position == 2){
            helper.setVisible(R.id.iv_top,true);
            helper.setImageResource(R.id.iv_top,R.drawable.ic_feed_crown_copper);
        }else {
            helper.setVisible(R.id.iv_top,false);
        }
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
