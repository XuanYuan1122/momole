package com.moemoe.lalala.view.adapter;

import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.view.widget.adapter.BaseMultiItemRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedNoticeAdapter extends BaseMultiItemRecyclerViewAdapter<FeedNoticeEntity,FeedNoticeViewHolder> {

    public FeedNoticeAdapter() {
        super(null);
        addItemType(0,R.layout.item_empty);
        addItemType(1,R.layout.item_new_feed_list);
        addItemType(2,R.layout.item_discover);
        addItemType(3,R.layout.item_feed_type_1);
    }

    @Override
    protected void convert(FeedNoticeViewHolder helper, final FeedNoticeEntity item, int position) {
        if("dynamic".equals(item.getNotifyType())){
            NewDynamicEntity entity = new Gson().fromJson(item.getTargetObj(),NewDynamicEntity.class);
            helper.createItem(entity,position,item.getFrom());
        }else if("doc".equals(item.getNotifyType())){
            DepartmentEntity.DepartmentDoc departmentDoc = new Gson().fromJson(item.getTargetObj(),DepartmentEntity.DepartmentDoc.class);
            helper.createItem(departmentDoc,item.getFrom());
        }else {
            helper.createItem(item,position);
        }
    }

    @Override
    public int getItemType(int position) {
        FeedNoticeEntity entity = getItem(position);
        if("dynamic".equals(entity.getNotifyType())){
            return 1;
        }else if("doc".equals(entity.getNotifyType())){
            return 2;
        }else {
            return 3;
        }
    }
}
