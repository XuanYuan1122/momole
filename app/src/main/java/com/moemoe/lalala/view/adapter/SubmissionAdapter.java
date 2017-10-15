package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class SubmissionAdapter extends BaseRecyclerViewAdapter<SubmissionItemEntity,SubmissionHolder> {
    public SubmissionAdapter() {
        super(R.layout.item_submission);
    }


    @Override
    protected void convert(SubmissionHolder helper, final SubmissionItemEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
