package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FileManHuaAdapter extends BaseRecyclerViewAdapter<ManHua2Entity,FileManHua2ViewHolder> {

    private boolean isSelect;
    private boolean isButy;

    public FileManHuaAdapter() {
        super(R.layout.item_folder_manhua_2);
        isSelect = false;
        isButy = true;
    }


    @Override
    protected void convert(FileManHua2ViewHolder helper, final ManHua2Entity item, int position) {
        helper.createItem(item,isSelect,isButy);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setBuy(boolean buy) {
        isButy = buy;
    }
}
