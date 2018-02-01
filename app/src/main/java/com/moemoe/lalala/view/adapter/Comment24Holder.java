package com.moemoe.lalala.view.adapter;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class Comment24Holder extends ClickableViewHolder {

    TextView userName;
    TextView userContent;
    TextView userFavorite;
    ImageView cover;
    TextView dynamicContent;

    public Comment24Holder(View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.tv_user_name);
        userContent = itemView.findViewById(R.id.tv_user_content);
        userFavorite = itemView.findViewById(R.id.tv_favorite);
        cover = itemView.findViewById(R.id.iv_cover);
        dynamicContent = itemView.findViewById(R.id.tv_dynamic_content);
    }

    public void createItem(final Comment24Entity entity, final int position){
        userName.setText("@" + entity.getCommentCreateUserName());
        userName.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,entity.getCommentCreateUser());
            }
        });
        userContent.setText(TagControl.getInstance().paresToSpann(context,": "+entity.getCommentText()));
        userContent.setMovementMethod(LinkMovementMethod.getInstance());
        userFavorite.setText(entity.getLikes() + "");
        int size = context.getResources().getDimensionPixelSize(R.dimen.x100);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getDynamicIcon(),size,size,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropSquareTransformation(context))
                .into(cover);
        String res = "<at_user user_id="+ entity.getDynamicCreateUser() + ">" + entity.getDynamicCreateUserName() + ":</at_user>" +  entity.getDynamicText();
        dynamicContent.setText(TagControl.getInstance().paresToSpann(context,res));
        dynamicContent.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
