package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CreateForwardActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.NewDocLabelAdapter;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

@SuppressWarnings("deprecation")
public class NewDepartmentHolder extends ClickableViewHolder {

    public NewDepartmentHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final DepartmentEntity.DepartmentDoc docBean){
        View article = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_feed_type_5_v3,null);
        ImageView ivCover = article.findViewById(R.id.iv_cover);
        ImageView ivAvatar = article.findViewById(R.id.iv_user_avatar);
        TextView tvMark = article.findViewById(R.id.tv_mark);
        TextView tvUserName = article.findViewById(R.id.tv_user_name);
        TextView tvReadNum = article.findViewById(R.id.tv_read_num);
        TextView tvTag1 = article.findViewById(R.id.tv_tag_1);
        TextView tvTag2 = article.findViewById(R.id.tv_tag_2);
        TextView tvTitle = article.findViewById(R.id.tv_title);

        int w = DensityUtil.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.x48);
        int h = getResources().getDimensionPixelSize(R.dimen.y400);
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),docBean.getIcon().getPath(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(itemView.getContext(),w,h))
                .into(ivCover);

        tvMark.setText("文章");
        tvTitle.setText(docBean.getTitle());
        int size = (int) itemView.getContext().getResources().getDimension(R.dimen.y32);
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),docBean.getHeadIcon(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                .into(ivAvatar);

        ivAvatar.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(itemView.getContext(),docBean.getUserId());
            }
        });

        tvUserName.setText(docBean.getUsername());
        tvReadNum.setText("阅读" + docBean.getReadNum());

        //tag
        View[] tagsId = {tvTag1,tvTag2};
        tvTag1.setOnClickListener(null);
        tvTag2.setOnClickListener(null);
        if(docBean.getTexts().size() > 1){
            tvTag1.setVisibility(View.VISIBLE);
            tvTag2.setVisibility(View.VISIBLE);
        }else if(docBean.getTexts().size() > 0){
            tvTag1.setVisibility(View.VISIBLE);
            tvTag2.setVisibility(View.INVISIBLE);
        }else {
            tvTag1.setVisibility(View.INVISIBLE);
            tvTag2.setVisibility(View.INVISIBLE);
        }
        int tagSize = tagsId.length > docBean.getTexts().size() ? docBean.getTexts().size() : tagsId.length;
        for (int i = 0;i < tagSize;i++){
            TagUtils.setBackGround(docBean.getTexts().get(i).getText(),tagsId[i]);
        }
        ((LinearLayout)$(R.id.ll_card_root)).removeAllViews();
        ((LinearLayout)$(R.id.ll_card_root)).addView(article);
    }
}
