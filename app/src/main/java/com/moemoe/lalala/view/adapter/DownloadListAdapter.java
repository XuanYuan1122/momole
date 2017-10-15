package com.moemoe.lalala.view.adapter;

import android.view.ViewGroup;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DownloadEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class DownloadListAdapter extends BaseRecyclerViewAdapter<DownloadEntity,DownloadListHolder> {


    public DownloadListAdapter() {
        super(R.layout.download_manager_item);
    }

    @Override
    protected void convert(DownloadListHolder helper, final DownloadEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected DownloadListHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return new DownloadListHolder(getItemView(R.layout.download_manager_item, parent),this);
    }
}
