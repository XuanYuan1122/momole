package com.moemoe.lalala.view.adapter;

import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.RecommendTagEntity;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class HotTagHolder extends ClickableViewHolder {

    public HotTagHolder(View itemView) {
        super(itemView);

    }

    public void createItem(final RecommendTagEntity entity, final int position){
        setText(R.id.tv_tag,entity.getWord());
        TagUtils.setBackGround(entity.getWord(),$(R.id.tv_tag));
    }
}
