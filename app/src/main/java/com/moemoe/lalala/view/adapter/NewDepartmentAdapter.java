package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class NewDepartmentAdapter extends BaseRecyclerViewAdapter<DepartmentEntity.DepartmentDoc,NewDepartmentHolder> {

    public NewDepartmentAdapter() {
        super(R.layout.item_new_department);
    }

    @Override
    protected void convert(NewDepartmentHolder helper, final DepartmentEntity.DepartmentDoc item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
