package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.Live2dModelEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/6/27.
 */

public class FukuSelectAdapter extends BaseRecyclerViewAdapter<Live2dModelEntity,FukuSelectAdapter.ItemViewHolder> {

    private String mModel;

    public FukuSelectAdapter(String mModel){
        super(R.layout.item_fuku);
        this.mModel = mModel;
    }

    public void setModel(String model){
        mModel = model;
    }

    @Override
    protected void convert(ItemViewHolder itemViewHolder, Live2dModelEntity item) {
        itemViewHolder.name.setText(item.getName());
        itemViewHolder.info.setText(item.getInfo());
        itemViewHolder.req.setText(item.getCondition());
        Glide.with(context)
                .load(StringUtils.getUrl(context, item.getImg(), DensityUtil.dip2px(context,55),DensityUtil.dip2px(context,55),false,true))
                .override(DensityUtil.dip2px(context,55),DensityUtil.dip2px(context,55))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into(itemViewHolder.fuku);
        if(item.getLocalPath().equals(mModel)){
            itemViewHolder.root.setBackgroundResource(R.drawable.bg_rect_corner_cyan_5);
            itemViewHolder.select.setImageResource(R.drawable.ic_select_hover);
        }else {
            itemViewHolder.root.setBackgroundResource(R.drawable.bg_rect_corner_gray);
            itemViewHolder.select.setImageResource(R.drawable.ic_select_normal);
        }
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    class ItemViewHolder extends ClickableViewHolder {

        TextView name,info,req;
        ImageView fuku,select;
        RelativeLayout root;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = $(R.id.tv_fuku_name);
            info = $(R.id.tv_fuku_info);
            req = $(R.id.tv_fuku_req);
            fuku = $(R.id.iv_fuku);
            select = $(R.id.iv_select);
            root = $(R.id.rl_root);
        }
    }
}
