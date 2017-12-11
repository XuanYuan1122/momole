package com.moemoe.lalala.view.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.moemoe.lalala.model.entity.FeedNoticeBagEntity;
import com.moemoe.lalala.model.entity.FeedNoticeDynamicEntity;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.FeedNoticeRoleEntity;
import com.moemoe.lalala.model.entity.FeedNoticeSystemEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShareFolderEntity;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.utils.BoldSpan;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

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
        $(R.id.rl_include_root).setVisibility(View.GONE);
        $(R.id.rl_card_root).setVisibility(View.GONE);
        ((LinearLayout)$(R.id.ll_card_root)).removeAllViews();
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
                $(R.id.rl_include_root).setVisibility(View.VISIBLE);
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
                $(R.id.rl_include_root).setVisibility(View.VISIBLE);
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
                final ShareFolderEntity folderEntity = bagEntity.getFolder();
                View folder = LayoutInflater.from(context).inflate(R.layout.item_new_wenzhang_zhuan,null);
                folder.findViewById(R.id.tv_title).setVisibility(View.GONE);
                folder.findViewById(R.id.tv_content).setVisibility(View.GONE);
                ImageView cover = folder.findViewById(R.id.iv_cover);
                TextView mark = folder.findViewById(R.id.tv_mark);
                TextView name = folder.findViewById(R.id.tv_folder_name);
                TextView tag = folder.findViewById(R.id.tv_tag);
                int w = (int) (DensityUtil.getScreenWidth(context) - itemView.getResources().getDimension(R.dimen.x48));
                int h = (int) itemView.getResources().getDimension(R.dimen.y400);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,folderEntity.getFolderCover(),w,h,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(context,w,h))
                        .into(cover);
                if(folderEntity.getFolderType().equals(FolderType.ZH.toString())){
                    mark.setText("综合");
                    mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(folderEntity.getFolderType().equals(FolderType.TJ.toString())){
                    mark.setText("图集");
                    mark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(folderEntity.getFolderType().equals(FolderType.MH.toString())){
                    mark.setText("漫画");
                    mark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(folderEntity.getFolderType().equals(FolderType.XS.toString())){
                    mark.setText("小说");
                    mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }
                name.setText(folderEntity.getFolderName());
                String tagStr = "";
                for(int i = 0;i < folderEntity.getFolderTags().size();i++){
                    String tagTmp = folderEntity.getFolderTags().get(i);
                    if(i == 0){
                        tagStr = tagTmp;
                    }else {
                        tagStr += " · " + tagTmp;
                    }
                }
                tag.setText(tagStr);

                ImageView avatar = folder.findViewById(R.id.iv_avatar);
                TextView userName = folder.findViewById(R.id.tv_user_name);
                TextView time = folder.findViewById(R.id.tv_time);
                size = (int) context.getResources().getDimension(R.dimen.x44);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,folderEntity.getCreateUser().getHeadPath(),size,size,false,true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(avatar);
                avatar.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(context,folderEntity.getCreateUser().getUserId());
                    }
                });
                userName.setText(folderEntity.getCreateUser().getUserName());
                time.setText("上一次更新:" + StringUtils.timeFormat(folderEntity.getUpdateTime()));
                $(R.id.rl_card_root).setVisibility(View.VISIBLE);
                ((LinearLayout)$(R.id.ll_card_root)).addView(folder);
                $(R.id.rl_card_root).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(folderEntity.getFolderType().equals(FolderType.ZH.toString())){
                            NewFileCommonActivity.startActivity(context,FolderType.ZH.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.TJ.toString())){
                            NewFileCommonActivity.startActivity(context,FolderType.TJ.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.MH.toString())){
                            NewFileManHuaActivity.startActivity(context,FolderType.MH.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                        }else if(folderEntity.getFolderType().equals(FolderType.XS.toString())){
                            NewFileXiaoshuoActivity.startActivity(context,FolderType.XS.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
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
