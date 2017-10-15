package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.FileXiaoShuoEntity;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FileXiaoShuoAdapter extends BaseRecyclerViewAdapter<FileXiaoShuoEntity,FileXiaoShuoViewHolder> {

    private boolean isSelect;
    private boolean isBuy;

    public FileXiaoShuoAdapter() {
        super(R.layout.item_file_xiaoshuo);
        isSelect = false;
    }


    @Override
    protected void convert(FileXiaoShuoViewHolder helper, final FileXiaoShuoEntity item, int position) {
        helper.createItem(item,isSelect,isBuy);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }
}
