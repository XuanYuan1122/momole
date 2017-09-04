package com.moemoe.lalala.view.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.WenZhangFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class BagWenZhangHolder extends ClickableViewHolder {

    ImageView iv;
    ImageView select;
    TextView title;
    TextView content;
    TextView time;
    TextView readNum;
    TextView commentNum;
    TextView fromName;


    public BagWenZhangHolder(View itemView) {
        super(itemView);
        iv = $(R.id.iv_cover);
        select = $(R.id.iv_select);
        title = $(R.id.tv_title);
        content = $(R.id.tv_content);
        time = $(R.id.tv_time);
        readNum = $(R.id.tv_read_num);
        commentNum = $(R.id.tv_comment_num);
        fromName = $(R.id.tv_from_name);
    }

    public void createItem(final WenZhangFolderEntity entity, final int position, boolean isSelect){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        title.setText(entity.getTitle());
        content.setText(entity.getContent());
        time.setText(StringUtils.timeFormate(entity.getCreateTime()));
        readNum.setText("阅读 " + entity.getRead());
        commentNum.setText("评论 " + entity.getComments());
        if(!entity.getDocType().equals("书包")){
            setVisible(R.id.tv_tmp,true);
            fromName.setVisibility(View.VISIBLE);
            fromName.setText(entity.getDocType());
            fromName.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (!TextUtils.isEmpty(entity.getDocTypeSchema())) {
                        Uri uri = Uri.parse(entity.getDocTypeSchema());
                        IntentUtils.toActivityFromUri(itemView.getContext(), uri,v);
                    }
                }
            });
        }else {
            setVisible(R.id.tv_tmp,false);
            fromName.setVisibility(View.GONE);
            fromName.setOnClickListener(null);
        }
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74), false, true))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74)),new RoundedCornersTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),4),0))
                .into(iv);
    }
}
