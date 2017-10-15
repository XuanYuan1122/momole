package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class SubmissionHolder extends ClickableViewHolder {

    TextView state;

    public SubmissionHolder(View itemView) {
        super(itemView);
        state = $(R.id.tv_state);
    }

    public void createItem(final SubmissionItemEntity entity, final int position){
        int size = (int) context.getResources().getDimension(R.dimen.x112);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getIcon(),size,size,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_cover));
        setText(R.id.tv_doc_title,entity.getTitle());
        state.setText(entity.getStatusShow());
        if(entity.getStatus() == 0){//状态:1:投稿成功  0:投稿中 2:被拒绝
            state.setTextColor(ContextCompat.getColor(context,R.color.orange_f2cc2c));
        }else if(entity.getStatus() == 1){
            state.setTextColor(ContextCompat.getColor(context,R.color.green_6fc93a));
        }else if(entity.getStatus() == 2){
            state.setTextColor(ContextCompat.getColor(context,R.color.main_red));
        }
        setText(R.id.tv_time,"申请时间: " + StringUtils.timeFormate(entity.getCreateTime()));
        setText(R.id.tv_department,entity.getDepartmentName());
    }
}
