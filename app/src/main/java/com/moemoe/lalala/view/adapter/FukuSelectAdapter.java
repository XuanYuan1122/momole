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

/**
 * Created by yi on 2017/6/27.
 */

public class FukuSelectAdapter extends BaseRecyclerViewAdapter<Live2dModelEntity>{

    private String mModel;

    public FukuSelectAdapter(Context context,String mModel){
        super(context);
        this.mModel = mModel;
    }

    public void setmModel(String model){
        mModel = model;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_fuku,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewAdapter.ClickableViewHolder holder, final int position) {
        super.onBindViewHolder(holder,position);
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Live2dModelEntity item = getItem(position);
            itemViewHolder.name.setText(item.getName());
            itemViewHolder.info.setText(item.getInfo());
            itemViewHolder.req.setText(item.getCondition());
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + item.getImg(), DensityUtil.dip2px(context,55),DensityUtil.dip2px(context,55),false,true))
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
    }

    class ItemViewHolder extends ClickableViewHolder{

        TextView name,info,req;
        ImageView fuku,select;
        RelativeLayout root;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_fuku_name);
            info = (TextView) itemView.findViewById(R.id.tv_fuku_info);
            req = (TextView) itemView.findViewById(R.id.tv_fuku_req);
            fuku = (ImageView) itemView.findViewById(R.id.iv_fuku);
            select = (ImageView) itemView.findViewById(R.id.iv_select);
            root = (RelativeLayout) itemView.findViewById(R.id.rl_root);
        }
    }
}
