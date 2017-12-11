package com.moemoe.lalala.view.adapter;

import android.view.ViewGroup;

import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.view.widget.adapter.BaseMultiItemRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class DiscoverAdapter extends BaseMultiItemRecyclerViewAdapter<DiscoverEntity,DiscoverHolder> {

    private String type;

    public DiscoverAdapter(String type) {
        super(null);
        this.type = type;
        addItemType(0,R.layout.item_empty);
        addItemType(1,R.layout.item_new_feed_list);
        addItemType(2,R.layout.item_discover);
    }

    @Override
    protected void convert(DiscoverHolder helper, final DiscoverEntity item, int position) {
        if("dynamic".equals(item.getType())){
            NewDynamicEntity entity = new Gson().fromJson(item.getObj(),NewDynamicEntity.class);
            helper.createItem(entity,position,item.getFrom());
        }else if("doc".equals(item.getType())){
            DepartmentEntity.DepartmentDoc departmentDoc = new Gson().fromJson(item.getObj(),DepartmentEntity.DepartmentDoc.class);
            helper.createItem(departmentDoc,item.getFrom(),type);
        }
    }

    @Override
    public int getItemType(int position) {
        DiscoverEntity entity = getItem(position);
        if("dynamic".equals(entity.getType())){
            return 1;
        }else if("doc".equals(entity.getType())){
            return 2;
        }else {
            return 0;
        }
    }
}
