package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.PhoneAlbumEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class PhoneAlbumHolder extends ClickableViewHolder {

    public PhoneAlbumHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final PhoneAlbumEntity entity, final int position){
       Glide.with(itemView.getContext())
               .load(StringUtils.getUrl(itemView.getContext(),entity.getCover().getPath(),(int)itemView.getContext().getResources().getDimension(R.dimen.x200),(int)itemView.getContext().getResources().getDimension(R.dimen.x200),false,true))
               .error(R.drawable.bg_default_square)
               .placeholder(R.drawable.bg_default_square)
               .bitmapTransform(new CropSquareTransformation(itemView.getContext()))
               .into((ImageView) $(R.id.iv_cover));
        setText(R.id.tv_name,entity.getName());
    }
}
