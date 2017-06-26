package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.CoinShopEntity;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class CoinShopAdapter extends BaseRecyclerViewAdapter<CoinShopEntity> {

    public CoinShopAdapter(Context context) {
        super(context);
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ClickableViewHolder(mLayoutInflater.inflate(R.layout.item_coin_shop,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewAdapter.ClickableViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    class ShopItemViewHolder extends ClickableViewHolder{

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
}
