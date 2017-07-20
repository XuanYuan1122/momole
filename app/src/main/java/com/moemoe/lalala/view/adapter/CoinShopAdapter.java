package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.CoinShopActivity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class CoinShopAdapter extends BaseRecyclerViewAdapter<CoinShopEntity,CoinShopAdapter.ShopItemViewHolder> {

    public CoinShopAdapter(Context context) {
        super(R.layout.item_coin_shop);
    }


    @Override
    protected void convert(ShopItemViewHolder helper, final CoinShopEntity item) {
        Glide.with(context)
                .load(StringUtils.getUrl(context, item.getIcon(), DensityUtil.dip2px(context,75),DensityUtil.dip2px(context,75),false,true))
                .override(DensityUtil.dip2px(context,75),DensityUtil.dip2px(context,75))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into(helper.ivCommodity);
        helper.tvTitle.setText(item.getProductName());
        helper.tvDesc.setText(item.getDesc());
        helper.tvNum.setText("库存:" + (item.getStock() - item.getFreeze() < 0 ? 0 : item.getStock() - item.getFreeze()));
        helper.tvNumDesc.setText(item.getStockDesc());
        if(item.getStock() - item.getFreeze() <= 0){
            helper.tvBuy.setText("已售罄");
            helper.tvBuy.setSelected(true);
            helper.tvBuy.setOnClickListener(null);
        }else {
            StringBuilder price = new StringBuilder();
            if(item.getRmb() > 0){
                if(item.getRmb()%100 != 0){
                    price.append((float)item.getRmb()/100);
                }else {
                    price.append(item.getRmb()/100);
                }
                price.append( "元");
            }
            if(item.getRmb() > 0 && item.getCoin() > 0) price.append(" + ");
            if(item.getCoin() > 0){
                price.append(item.getCoin()).append("节操");
            }
            helper.tvBuy.setText(price);
            helper.tvBuy.setSelected(false);
            helper.tvBuy.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ((CoinShopActivity)context).createOrder(item);
                }
            });
        }
    }

    @Override
    public int getItemType(int position) {
        return 0;
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
