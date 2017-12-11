package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class BagCollectionTopHolder extends ClickableViewHolder {

    ImageView iv;
    ImageView select;
   // TextView title;
    View root;


    public BagCollectionTopHolder(View itemView) {
        super(itemView);
        iv = $(R.id.iv_cover);
        select = $(R.id.iv_select);
        //title = $(R.id.tv_title);
        root = $(R.id.rl_root);
    }

    public void createItem(final ShowFolderEntity entity, final int position,boolean isSelect){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        setText(R.id.tv_title,entity.getFolderName());
        String tagStr = "";
        for(int i = 0;i < entity.getTexts().size();i++){
            String tagTmp = entity.getTexts().get(i);
            if(i == 0){
                tagStr = tagTmp;
            }else {
                tagStr += " · " + tagTmp;
            }
        }
        setText(R.id.tv_tag,tagStr);
        if(entity.getType().equals(FolderType.ZH.toString())){
            setText(R.id.tv_mark,"综合");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_zonghe);
        }else if(entity.getType().equals(FolderType.TJ.toString())){
            setText(R.id.tv_mark,"图集");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_tuji);
        }else if(entity.getType().equals(FolderType.MH.toString())){
            setText(R.id.tv_mark,"漫画");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_manhua);
        }else if(entity.getType().equals(FolderType.XS.toString())){
            setText(R.id.tv_mark,"小说");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
        }

        int width = (DensityUtil.getScreenWidth(itemView.getContext()) - (int)context.getResources().getDimension(R.dimen.x84)) / 3;
        int height = (int) context.getResources().getDimension(R.dimen.y280);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
        iv.setLayoutParams(lp);

        RecyclerView.LayoutParams lp1 = new RecyclerView.LayoutParams(width,height);
        root.setLayoutParams(lp1);
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),width,height, false, true))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(itemView.getContext(),width,height),new RoundedCornersTransformation(itemView.getContext(),(int)itemView.getContext().getResources().getDimension(R.dimen.y8),0))
                .into(iv);
    }
}
