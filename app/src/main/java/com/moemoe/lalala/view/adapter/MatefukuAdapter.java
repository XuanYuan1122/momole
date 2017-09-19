package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.PhoneFukuEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class MatefukuAdapter extends BaseRecyclerViewAdapter<PhoneFukuEntity,ClickableViewHolder> {

    private int select;

    public MatefukuAdapter() {
        super(R.layout.item_img);
    }


    @Override
    protected void convert(ClickableViewHolder helper, final PhoneFukuEntity item, int position) {
        ImageView iv = helper.$(R.id.iv_img);
        Glide.with(context)
                .load(StringUtils.getUrl(context,item.getImage(),(int)context.getResources().getDimension(R.dimen.x284),(int)context.getResources().getDimension(R.dimen.y386),false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into(iv);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) helper.itemView.getLayoutParams();
        if(position > 0){
            lp.leftMargin = (int) - context.getResources().getDimension(R.dimen.x76);
        }else {
            lp.leftMargin = 0;
        }
        if(position == select){
            helper.setVisible(R.id.view_select,true);
        }else {
            helper.setVisible(R.id.view_select,false);
        }
        helper.itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }
}
