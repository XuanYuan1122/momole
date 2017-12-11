package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.RejectEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.UserRejectListActivity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class RejectListAdapter extends BaseRecyclerViewAdapter<RejectEntity,RejectViewHolder> {

    public RejectListAdapter() {
        super(R.layout.item_reject_list);
    }


    @Override
    protected void convert(RejectViewHolder helper, final RejectEntity item,int position) {
        Glide.with(context)
                .load(StringUtils.getUrl(context, ApiService.URL_QINIU + item.getHeadPath(), (int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100),false,true))
                .override((int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100))
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .into(helper.ivAvatar);
        helper.tvName.setText(item.getUserName());
        helper.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UserRejectListActivity)context).removeBlack(item);
            }
        });
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
