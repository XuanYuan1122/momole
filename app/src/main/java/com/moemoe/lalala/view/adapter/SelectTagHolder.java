package com.moemoe.lalala.view.adapter;

import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class SelectTagHolder extends ClickableViewHolder {

    public SelectTagHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final OfficialTag entity, final int position, boolean isSelect){
        setText(R.id.tv_tag,entity.getText());
        if (isSelect){
            setBackgroundColor(R.id.tv_tag,R.color.white);
            setVisible(R.id.view_end_step,false);
        }else {
            setBackgroundColor(R.id.tv_tag,R.color.bg_f6f6f6);
            setVisible(R.id.view_end_step,true);
        }
    }
}
