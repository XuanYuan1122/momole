package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/21.
 */

public class OrderViewHolder extends ClickableViewHolder {

    ImageView ivCommodity;
    TextView tvName,tvStatus,tvTime;

    public OrderViewHolder(View itemView) {
        super(itemView);
        ivCommodity = $(R.id.iv_commodity);
        tvName = $(R.id.tv_title);
        tvStatus = $(R.id.tv_order_state);
        tvTime = $(R.id.tv_time);
    }
}
