package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.model.entity.MapHistoryEntity;
import com.moemoe.lalala.model.entity.MapUserImageEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/21.
 */

public class MapSelectImageHolder extends ClickableViewHolder {

    public MapSelectImageHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final MapUserImageEntity entity, boolean showSelect){
        int w = (int) context.getResources().getDimension(R.dimen.x180);
        int h = (int) context.getResources().getDimension(R.dimen.y220);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getUrl(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_img));
        if(showSelect){
            setVisible(R.id.iv_select,true);
        }else {
            setVisible(R.id.iv_select,false);
        }
    }

    public void createItem(final MapHistoryEntity entity,String useId,boolean showSelect){
        int w = (int) context.getResources().getDimension(R.dimen.x180);
        int h = (int) context.getResources().getDimension(R.dimen.y220);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getPicUrl(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_img));
        if(entity.isSelect() || showSelect){
            setVisible(R.id.iv_select,true);
        }else {
            setVisible(R.id.iv_select,false);
        }
        if(useId.equals(entity.getId())){
            setVisible(R.id.tv_edit_role,true);
        }else {
            setVisible(R.id.tv_edit_role,false);
        }
        setVisible(R.id.ll_bottom_root,true);
        $(R.id.iv_like).setSelected(entity.isLike());
        setText(R.id.tv_like_num,String.valueOf(entity.getLikes()));
    }
}
