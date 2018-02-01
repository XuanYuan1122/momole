package com.moemoe.lalala.view.adapter;

import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.event.TagSelectEvent;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class TagHolder extends ClickableViewHolder {

    public TagHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final UserFollowTagEntity entity, final int position, ArrayList<UserFollowTagEntity> userIds,boolean showClose){
        setText(R.id.tv_content,entity.getText());
        if(userIds != null){
            if(userIds.contains(entity)){
                $(R.id.tv_content).setSelected(true);
            }else {
                $(R.id.tv_content).setSelected(false);
            }
            setVisible(R.id.iv_close,false);
            $(R.id.iv_close).setOnClickListener(null);
        }else {
            $(R.id.tv_content).setSelected(entity.isSelect());
            if(showClose){
                if(entity.isSelect()){
                    setVisible(R.id.iv_close,true);
                    $(R.id.iv_close).setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            EventBus.getDefault().post(new TagSelectEvent("del_user",entity.getId(),entity.getText(),position));
                        }
                    });
                }else {
                    setVisible(R.id.iv_close,false);
                    $(R.id.iv_close).setOnClickListener(null);
                }
            }else {
                setVisible(R.id.iv_close,false);
                $(R.id.iv_close).setOnClickListener(null);
            }
        }
    }
}
