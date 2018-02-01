package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.StreamFileEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class VideoExamineHolder extends ClickableViewHolder {



    public VideoExamineHolder(View itemView) {
        super(itemView);

    }

    public void createItem(final StreamFileEntity entity, final int position){
        int size = (int) context.getResources().getDimension(R.dimen.x112);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getCover(),size,size,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_cover));
        setText(R.id.tv_doc_title,entity.getFileName());

        TextView state = $(R.id.tv_state);
        if(entity.getState() == 0){//状态:0:待审核，1：审核通过，-1审核失败
            state.setTextColor(ContextCompat.getColor(context,R.color.orange_f2cc2c));
            state.setText("审核中");
        }else if(entity.getState() == 1){
            state.setTextColor(ContextCompat.getColor(context,R.color.green_6fc93a));
            state.setText("恭喜你通过审核~");
        }else if(entity.getState() == -1){
            state.setTextColor(ContextCompat.getColor(context,R.color.main_red));
            state.setText("投稿未通过，视频内容不符合规范");
        }
        setText(R.id.tv_time,"申请时间: " + StringUtils.timeFormat(entity.getUpdateTime()));
        setVisible(R.id.tv_to_department,false);
    }
}
