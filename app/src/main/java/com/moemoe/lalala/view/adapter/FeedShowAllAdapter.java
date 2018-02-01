package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseMultiItemRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedShowAllAdapter<T> extends BaseMultiItemRecyclerViewAdapter<T,FeedShowAllHolder> {

    private String type;
    private boolean showSelect;

    public FeedShowAllAdapter(String type) {
        super(null);
        this.type = type;
        addItemType(0,R.layout.item_feed_type_3_v3);
        addItemType(1,R.layout.item_feed_type_4_v3);
        addItemType(2,R.layout.item_feed_type_1_v3);
        addItemType(3,R.layout.item_feed_type_2_v3);
    }

    @Override
    protected void convert(FeedShowAllHolder helper, final T item, int position) {
        helper.createItem(item,position,showSelect);
    }

    @Override
    public int getItemType(int position) {
        switch (type){
            case "SP":
                return 0;
            case "MH":
                return 1;
            case "WZ":
                return 2;
            case "YY":
                return 3;
        }
        return 0;
    }

    public boolean isShowSelect() {
        return showSelect;
    }

    public void setShowSelect(boolean showSelect) {
        this.showSelect = showSelect;
    }
}
