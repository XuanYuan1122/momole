package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DynamicContentEntity;
import com.moemoe.lalala.model.entity.FeedNoticeBagEntity;
import com.moemoe.lalala.model.entity.FeedNoticeDynamicEntity;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.FeedNoticeRoleEntity;
import com.moemoe.lalala.model.entity.FeedNoticeSystemEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.RetweetEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.ShareFolderEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.BoldSpan;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CreateForwardActivity;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.HongBaoListActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.PersonalFavoriteDynamicActivity;
import com.moemoe.lalala.view.activity.PersonalV2Activity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
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

public class FeedNoticeViewHolder extends ClickableViewHolder {

    public FeedNoticeViewHolder(View itemView) {
        super(itemView);

    }

    public void createItem(final FeedNoticeEntity entity, final int position){
        //top and user
        String type = entity.getNotifyType();
        Gson gson = new Gson();
        int size = (int) context.getResources().getDimension(R.dimen.y80);
        int size2 = (int) context.getResources().getDimension(R.dimen.y140);
        ((FrameLayout)$(R.id.rl_include_root)).removeAllViews();
        $(R.id.rl_include_root).setBackgroundResource(R.color.transparent);
        int x = (int) context.getResources().getDimension(R.dimen.x24);
        int y = (int) context.getResources().getDimension(R.dimen.y24);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.rightMargin = x;
        lp.bottomMargin = y;
        $(R.id.rl_include_root).setLayoutParams(lp);
        if("DYNAMIC".equals(type) || "ARTICLE".equals(type)){
            if("ARTICLE".equals(type)){
                setImageResource(R.id.iv_top_img,R.drawable.ic_feed_inform_doc);
                setText(R.id.tv_top_text,"文章");
                ((TextView)$(R.id.tv_top_text)).setTextColor(ContextCompat.getColor(context,R.color.green_6fc93a));
            }else {
                setImageResource(R.id.iv_top_img,R.drawable.ic_feed_inform_trends);
                setText(R.id.tv_top_text,"动态");
                ((TextView)$(R.id.tv_top_text)).setTextColor(ContextCompat.getColor(context,R.color.orange_f2cc2c));
            }
            final FeedNoticeDynamicEntity dynamicEntity = gson.fromJson(entity.getTargetObj(),FeedNoticeDynamicEntity.class);
            Glide.with(context)
                    .load(StringUtils.getUrl(context,dynamicEntity.getUser().getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into((ImageView) $(R.id.iv_top_img_2));
            $(R.id.iv_top_img_2).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(context,dynamicEntity.getUser().getUserId());
                }
            });

            String content = "<at_user user_id=" + dynamicEntity.getUser().getUserId() + ">@" + dynamicEntity.getUser().getUserName() + "</at_user> " + dynamicEntity.getShowMsg();
            setText(R.id.tv_top_text_2, TagControl.getInstance().paresToSpann(context,content));

            if(dynamicEntity.isDelete()){
                $(R.id.rl_include_root).setBackgroundResource(R.color.gray_e8e8e8);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.y140));
                lp1.leftMargin = x;
                lp1.rightMargin = x;
                lp1.bottomMargin = y;
                $(R.id.rl_include_root).setLayoutParams(lp1);
                FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.gravity = Gravity.CENTER;
                TextView tv = new TextView(context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
                tv.setTextColor(Color.WHITE);
                tv.setText("已删除");
                ((FrameLayout)$(R.id.rl_include_root)).addView(tv,lp2);
            }else {
                View dynamicView = LayoutInflater.from(context).inflate(R.layout.item_feed_notice_dynamic,null);
                ImageView dynamicCover = dynamicView.findViewById(R.id.iv_dynamic_cover);
                TextView dynamicTitle = dynamicView.findViewById(R.id.tv_dynamic_title);
                TextView dynamicContent = dynamicView.findViewById(R.id.tv_dynamic_content);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,dynamicEntity.getIcon(),size2,size2,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(dynamicCover);
                dynamicTitle.setText(dynamicEntity.getTitle());
                dynamicContent.setText(TagControl.getInstance().paresToSpann(context,dynamicEntity.getContent()));
                ((FrameLayout)$(R.id.rl_include_root)).addView(dynamicView);
                $(R.id.rl_include_root).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        IntentUtils.toActivityFromUri(context, Uri.parse(dynamicEntity.getSchema()),v);
                    }
                });
            }
        }else if("SYSTEM".equals(type)){
            setImageResource(R.id.iv_top_img,R.drawable.ic_feed_inform_system);
            setText(R.id.tv_top_text,"系统通知");
            ((TextView)$(R.id.tv_top_text)).setTextColor(ContextCompat.getColor(context,R.color.red_ea6142));
            FeedNoticeSystemEntity systemEntity = gson.fromJson(entity.getTargetObj(),FeedNoticeSystemEntity.class);
            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into((ImageView) $(R.id.iv_top_img_2));
            setText(R.id.tv_top_text_2, systemEntity.getMsg());
        }else if("BAG".equals(type)){
            setImageResource(R.id.iv_top_img,R.drawable.ic_feed_inform_bag);
            setText(R.id.tv_top_text,"书包");
            ((TextView)$(R.id.tv_top_text)).setTextColor(ContextCompat.getColor(context,R.color.blue_4999e8));
            final FeedNoticeBagEntity bagEntity = gson.fromJson(entity.getTargetObj(),FeedNoticeBagEntity.class);
            Glide.with(context)
                    .load(StringUtils.getUrl(context,bagEntity.getUser().getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into((ImageView) $(R.id.iv_top_img_2));
            $(R.id.iv_top_img_2).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(context,bagEntity.getUser().getUserId());
                }
            });
            String userContent = "@" + bagEntity.getUser().getUserName();
            String coinContent = "";
            if(bagEntity.getCoin() > 0){
                coinContent = " " + bagEntity.getCoin() + "节操";
            }
            String content = userContent + " " + bagEntity.getShowMsg() + coinContent;
            UserUrlSpan span = new UserUrlSpan(context,bagEntity.getUser().getUserId(),null);
            ForegroundColorSpan span1 = null;
            if(!TextUtils.isEmpty(coinContent)){
                span1 = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.pink_fb7ba2));
            }
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            style.setSpan(span, content.indexOf(userContent), content.indexOf(userContent) + userContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(span1 != null){
                style.setSpan(span1, content.indexOf(coinContent), content.indexOf(coinContent) + coinContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            setText(R.id.tv_top_text_2, style);

            if(bagEntity.isDelete()){
                $(R.id.rl_include_root).setBackgroundResource(R.color.gray_e8e8e8);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.y140));
                lp1.leftMargin = x;
                lp1.rightMargin = x;
                lp1.bottomMargin = y;
                $(R.id.rl_include_root).setLayoutParams(lp1);
                FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.gravity = Gravity.CENTER;
                TextView tv = new TextView(context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
                tv.setTextColor(Color.WHITE);
                tv.setText("已删除");
                ((FrameLayout)$(R.id.rl_include_root)).addView(tv,lp2);
            }else {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.y140));
                lp1.leftMargin = 0;
                lp1.rightMargin = 0;
                lp1.bottomMargin = y;
                $(R.id.rl_include_root).setLayoutParams(lp1);

                final ShareFolderEntity folderEntity = bagEntity.getFolder();
                View folder = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_feed_type_2_v3,null);
                folder.setBackgroundResource(R.drawable.shape_gray_f6f6f6_background_y8);
                TextView tvMark = folder.findViewById(R.id.tv_mark);
                ImageView ivCover = folder.findViewById(R.id.iv_cover);
                TextView tvTitle = folder.findViewById(R.id.tv_title);
                TextView tvTag1 = folder.findViewById(R.id.tv_tag_1);
                TextView tvTag2 = folder.findViewById(R.id.tv_tag_2);
                ImageView ivAvatar = folder.findViewById(R.id.iv_user_avatar);
                TextView tvUserName = folder.findViewById(R.id.tv_user_name);
                TextView tvCoin = folder.findViewById(R.id.tv_coin);
                TextView tvExtra = folder.findViewById(R.id.tv_extra);
                folder.findViewById(R.id.iv_play).setVisibility(View.GONE);


                int w = context.getResources().getDimensionPixelSize(R.dimen.x222);
                int h = context.getResources().getDimensionPixelSize(R.dimen.y190);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,folderEntity.getFolderCover(),w,h,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(context,w,h))
                        .into(ivCover);

                tvTitle.setText(folderEntity.getFolderName());

                //tag
                View[] tagsId = {tvTag1,tvTag2};
                tvTag1.setOnClickListener(null);
                tvTag2.setOnClickListener(null);
                if(folderEntity.getFolderTags().size() > 1){
                    tvTag1.setVisibility(View.VISIBLE);
                    tvTag2.setVisibility(View.VISIBLE);
                }else if(folderEntity.getFolderTags().size() > 0){
                    tvTag1.setVisibility(View.VISIBLE);
                    tvTag2.setVisibility(View.INVISIBLE);
                }else {
                    tvTag1.setVisibility(View.INVISIBLE);
                    tvTag2.setVisibility(View.INVISIBLE);
                }
                int tagSize = tagsId.length > folderEntity.getFolderTags().size() ? folderEntity.getFolderTags().size() : tagsId.length;
                for (int i = 0;i < tagSize;i++){
                    TagUtils.setBackGround(folderEntity.getFolderTags().get(i),tagsId[i]);
                }

                //user
                int avatarSize = context.getResources().getDimensionPixelSize(R.dimen.y32);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,folderEntity.getCreateUser().getHeadPath(),avatarSize,avatarSize,false,true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(ivAvatar);
                tvUserName.setText(folderEntity.getCreateUser().getUserName());
                ivAvatar.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(context,folderEntity.getCreateUser().getUserId());
                    }
                });
                tvUserName.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(context,folderEntity.getCreateUser().getUserId());
                    }
                });

                tvMark.setVisibility(View.VISIBLE);
                if(folderEntity.getFolderType().equals(FolderType.ZH.toString())){
                    tvMark.setText("综合");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(folderEntity.getFolderType().equals(FolderType.TJ.toString())){
                    tvMark.setText("图集");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(folderEntity.getFolderType().equals(FolderType.MH.toString())){
                    tvMark.setText("漫画");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(folderEntity.getFolderType().equals(FolderType.XS.toString())){
                    tvMark.setText("小说");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }

                tvCoin.setText(folderEntity.getCoin() + "节操");
                tvExtra.setText(folderEntity.getItems() + "项");

                ((LinearLayout)$(R.id.rl_include_root)).addView(folder);

                $(R.id.rl_include_root).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(folderEntity.getFolderType().equals(FolderType.ZH.toString())){
                            NewFileCommonActivity.startActivity(itemView.getContext(),FolderType.ZH.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.TJ.toString())){
                            NewFileCommonActivity.startActivity(itemView.getContext(),FolderType.TJ.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.MH.toString())){
                            NewFileManHuaActivity.startActivity(itemView.getContext(),FolderType.MH.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.XS.toString())){
                            NewFileXiaoshuoActivity.startActivity(itemView.getContext(),FolderType.XS.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.SP.toString())){
                            //TODO 跳转视频文件夹
                        }else if(folderEntity.getFolderType().equals(FolderType.YY.toString())){

                        }
                    }
                });
            }
        }else if("STORY".equals(type)){
            setImageResource(R.id.iv_top_img,R.drawable.ic_feed_inform_galgame);
            setText(R.id.tv_top_text,"剧情");
            ((TextView)$(R.id.tv_top_text)).setTextColor(ContextCompat.getColor(context,R.color.pink_fb7ba2));
            FeedNoticeRoleEntity roleEntity = gson.fromJson(entity.getTargetObj(),FeedNoticeRoleEntity.class);
            if("len".equals(roleEntity.getRole())){
                setImageResource(R.id.iv_top_img_2,R.drawable.ic_feed_inform_head_len);
            }else if("mei".equals(roleEntity.getRole())){
                setImageResource(R.id.iv_top_img_2,R.drawable.ic_feed_inform_head_mei);
            }else if("sari".equals(roleEntity.getRole())){
                setImageResource(R.id.iv_top_img_2,R.drawable.ic_feed_inform_head_saari);
            }else {
                setImageResource(R.id.iv_top_img_2,R.drawable.ic_feed_inform_head_gal);
            }
            String score = "好感度";
            if(roleEntity.getLike() > 0){
                score += "+";
            }
            score += roleEntity.getLike();
            String content = roleEntity.getMsg() + score;
            BoldSpan span = new BoldSpan(ContextCompat.getColor(context,R.color.black_1e1e1e));
            ForegroundColorSpan span1 = null;
            if(roleEntity.getLike() > 0){
                span1 = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.pink_fb7ba2));
            }else {
                span1 = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.purple_b55fc9));
            }
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            style.setSpan(span, content.indexOf(roleEntity.getStoryName()), content.indexOf(roleEntity.getStoryName()) + roleEntity.getStoryName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(span1, content.indexOf(score), content.indexOf(score) + score.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(R.id.tv_top_text_2, style);

        }
        setText(R.id.tv_top_time, StringUtils.timeFormat(entity.getCreateTime()));
    }
}
