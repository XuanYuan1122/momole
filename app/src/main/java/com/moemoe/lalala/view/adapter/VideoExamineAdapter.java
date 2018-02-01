package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.StreamFileEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class VideoExamineAdapter extends BaseRecyclerViewAdapter<StreamFileEntity,VideoExamineHolder> {

    public VideoExamineAdapter() {
        super(R.layout.item_submission);
    }


    @Override
    protected void convert(VideoExamineHolder helper, final StreamFileEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
