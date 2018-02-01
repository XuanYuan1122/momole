package com.moemoe.lalala.view.adapter;

import android.support.annotation.LayoutRes;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.StreamFileEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class StreamFileAdapter extends BaseRecyclerViewAdapter<StreamFileEntity,StreamFileHolder> {

    private boolean isSelect;

    public StreamFileAdapter(@LayoutRes int layoutId) {
        super(layoutId);
    }
    
    @Override
    protected void convert(StreamFileHolder helper, final StreamFileEntity item, int position) {
        helper.createItem(item,position,isSelect);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
