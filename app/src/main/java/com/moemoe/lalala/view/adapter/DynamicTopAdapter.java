package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.model.entity.DynamicTopEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class DynamicTopAdapter extends BaseRecyclerViewAdapter<DynamicTopEntity,DynamicTopViewHolder> {

    private int size;

    public DynamicTopAdapter() {
        super(R.layout.item_dynamic_top_item);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void onBindViewHolder(DynamicTopViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType){
            case 0:
                int i ;
                if(position % list.size() - 1 < 0){
                    i = list.size() - 1;
                }else {
                    i = position % list.size() - 1;
                }

                convert(holder, list.get(i), holder.getLayoutPosition() - getHeaderLayoutCount());
                break;
        }
    }

    @Override
    protected void convert(DynamicTopViewHolder helper, final DynamicTopEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (list.size() == 0){
            return 0;
        }else {
            return size;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = getHeaderLayoutCount();
        if(position < numHeaders){
            return HEADER_VIEW;
        }else {
            return getItemType(position);
        }
    }
}
