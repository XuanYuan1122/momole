package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class InviteHolder extends ClickableViewHolder {

    public InviteHolder(View itemView) {
        super(itemView);

    }

    public void createItem(final InviteUserEntity entity, final int position){
        setText(R.id.tv_name,entity.getUserName());
        setText(R.id.tv_id,"ID: " + entity.getUserNo());
    }
}
