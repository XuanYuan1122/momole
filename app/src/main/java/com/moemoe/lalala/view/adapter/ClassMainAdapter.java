package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.CoinShopActivity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class ClassMainAdapter extends BaseRecyclerViewAdapter<DocListEntity,MainClassViewHolder> {

    public ClassMainAdapter(Context context) {
        super(R.layout.item_doc_main_1);
    }


    @Override
    protected void convert(MainClassViewHolder helper, final DocListEntity item, int position) {
        helper.createFollowDoc(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
