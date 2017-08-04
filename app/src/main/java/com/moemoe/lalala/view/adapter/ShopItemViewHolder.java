package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/21.
 */

public class ShopItemViewHolder extends ClickableViewHolder {

    ImageView ivCommodity;
    TextView tvTitle,tvDesc,tvNum,tvNumDesc,tvBuy;

    public ShopItemViewHolder(View itemView) {
        super(itemView);
        ivCommodity = $(R.id.iv_commodity);
        tvTitle = $(R.id.tv_commodity_title);
        tvDesc = $(R.id.tv_commodity_desc);
        tvNum = $(R.id.tv_commodity_num);
        tvNumDesc = $(R.id.tv_commodity_num_desc);
        tvBuy = $(R.id.tv_commodity_buy);
    }
}
