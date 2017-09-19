package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class MateSelectAdapter extends BaseRecyclerViewAdapter<Integer,ClickableViewHolder> {

    private int select;

    public MateSelectAdapter() {
        super(R.layout.item_img);
    }


    @Override
    protected void convert(ClickableViewHolder helper, final Integer item, int position) {
        ImageView iv = helper.$(R.id.iv_img);
        iv.setImageResource(item);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) helper.itemView.getLayoutParams();
        if(position > 0){
            lp.leftMargin = (int) - context.getResources().getDimension(R.dimen.x76);
        }else {
            lp.leftMargin = 0;
        }
        helper.itemView.setLayoutParams(lp);
        if(position == select){
            helper.setVisible(R.id.view_select,true);
        }else {
            helper.setVisible(R.id.view_select,false);
        }
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
