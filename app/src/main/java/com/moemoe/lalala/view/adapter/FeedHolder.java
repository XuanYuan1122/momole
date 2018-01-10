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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.DynamicContentEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.RetweetEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.ShareFolderEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
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
import com.moemoe.lalala.view.activity.PersonalV2Activity;
import com.moemoe.lalala.view.activity.PersonalFavoriteDynamicActivity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

@SuppressWarnings("deprecation")
public class FeedHolder extends ClickableViewHolder {


    public FeedHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final NewDynamicEntity entity,final int position){
        //from
        if(!TextUtils.isEmpty(entity.getFrom())){
            setVisible(R.id.rl_from_top, true);
            setText(R.id.tv_from_name,entity.getFrom());
            if(!TextUtils.isEmpty(entity.getFromSchema())){
                $(R.id.tv_from_name).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Uri uri = Uri.parse(entity.getFromSchema());
                        IntentUtils.toActivityFromUri(itemView.getContext(), uri,v);
                    }
                });
            }else {
                $(R.id.tv_from_name).setOnClickListener(null);
            }
        }else {
            setVisible(R.id.rl_from_top, false);
        }

        //user top
        if(entity.getCreateUser().isVip()){
            setVisible(R.id.iv_vip,true);
        }else {
            setVisible(R.id.iv_vip,false);
        }
        int size = (int) itemView.getContext().getResources().getDimension(R.dimen.x80);
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                .into((ImageView) $(R.id.iv_avatar));
        $(R.id.iv_avatar).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(itemView.getContext(),entity.getCreateUser().getUserId());
            }
        });
        setText(R.id.tv_name,entity.getCreateUser().getUserName());
        setImageResource(R.id.iv_sex,entity.getCreateUser().getSex().equalsIgnoreCase("M")?R.drawable.ic_user_girl:R.drawable.ic_user_boy);
        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(itemView.getContext(),R.color.white),itemView.getContext().getResources().getDimension(R.dimen.x12));
        final String content = "LV" + entity.getCreateUser().getLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(R.id.tv_level, style);
        float radius2 = itemView.getContext().getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(entity.getCreateUser().getLevelColor(), ContextCompat.getColor(itemView.getContext(), R.color.main_cyan)));
        $(R.id.tv_level).setBackgroundDrawable(shapeDrawable2);
        View[] huizhang = {$(R.id.fl_huizhang_1)};
        TextView[] huizhangT = {$(R.id.tv_huizhang_1)};
        if(entity.getCreateUser().getBadge() != null){
            ArrayList<BadgeEntity> badgeEntities = new ArrayList<>();
            badgeEntities.add(entity.getCreateUser().getBadge());
            ViewUtils.badge(itemView.getContext(),huizhang,huizhangT,badgeEntities);
        }else {
            huizhang[0].setVisibility(View.GONE);
            huizhangT[0].setVisibility(View.GONE);
        }

        $(R.id.iv_more).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu(entity.getCreateUser().getUserName(),entity.getText(),entity.getId());
            }
        });
        setText(R.id.tv_time,StringUtils.timeFormat(entity.getCreateTime()));
        //content
        setText(R.id.tv_content, TagControl.getInstance().paresToSpann(itemView.getContext(),entity.getText()));
        ((TextView)$(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());
        $(R.id.tv_content).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(!TextUtils.isEmpty(entity.getId())){
                    DynamicActivity.startActivity(context,entity.getId());
                }
            }
        });
        //extra
        setVisible(R.id.ll_img_root,false);
        setVisible(R.id.rl_card_root,false);
        ((LinearLayout)$(R.id.ll_img_root)).removeAllViews();
        $(R.id.ll_img_root).setOnClickListener(null);
        ((LinearLayout)$(R.id.ll_card_root)).removeAllViews();
        $(R.id.rl_card_root).setOnClickListener(null);
        boolean showHongbao = false;
        if("DELETE".equals(entity.getType())){//已被删除
            setVisible(R.id.ll_img_root,true);
            $(R.id.ll_img_root).setBackgroundColor(Color.WHITE);
            TextView tv = new TextView(itemView.getContext());
            tv.setText("该内容已被删除");
            tv.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.white));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,itemView.getContext().getResources().getDimension(R.dimen.x36));
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.gray_e8e8e8));
            int h = (int) itemView.getResources().getDimension(R.dimen.y320);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
            tv.setLayoutParams(lp);
            ((LinearLayout)$(R.id.ll_img_root)).addView(tv);
        }else if("DYNAMIC".equals(entity.getType())){
            setVisible(R.id.ll_img_root,true);
            $(R.id.ll_img_root).setBackgroundColor(Color.WHITE);
            DynamicContentEntity dynamicContentEntity = new Gson().fromJson(entity.getDetail(),DynamicContentEntity.class);
            $(R.id.ll_img_root).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                if(!TextUtils.isEmpty(entity.getId())){
                    DynamicActivity.startActivity(context,entity.getId());
                }
                }
            });
            if(dynamicContentEntity.getImages() != null && dynamicContentEntity.getImages().size() > 0){
                setImg(dynamicContentEntity.getImages());
            }else {
                setVisible(R.id.ll_img_root,false);
            }
        }else if("FOLDER".equals(entity.getType())){
            setVisible(R.id.rl_card_root,true);
            final ShareFolderEntity folderEntity = new Gson().fromJson(entity.getDetail(),ShareFolderEntity.class);
            View folder = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_new_wenzhang_zhuan,null);
            folder.findViewById(R.id.tv_title).setVisibility(View.GONE);
            folder.findViewById(R.id.tv_content).setVisibility(View.GONE);
            ImageView cover = folder.findViewById(R.id.iv_cover);
            TextView mark = folder.findViewById(R.id.tv_mark);
            TextView name = folder.findViewById(R.id.tv_folder_name);
            TextView tag = folder.findViewById(R.id.tv_tag);
            int w = (int) (DensityUtil.getScreenWidth(itemView.getContext()) - itemView.getResources().getDimension(R.dimen.x48));
            int h = (int) itemView.getResources().getDimension(R.dimen.y400);
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),folderEntity.getFolderCover(),w,h,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(itemView.getContext(),w,h))
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
            size = (int) itemView.getContext().getResources().getDimension(R.dimen.x44);
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),folderEntity.getCreateUser().getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                    .into(avatar);
            avatar.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(itemView.getContext(),folderEntity.getCreateUser().getUserId());
                }
            });
            userName.setText(folderEntity.getCreateUser().getUserName());
            time.setText("上一次更新:" + StringUtils.timeFormat(folderEntity.getUpdateTime()));
            ((LinearLayout)$(R.id.ll_card_root)).addView(folder);

            $(R.id.rl_card_root).setOnClickListener(new NoDoubleClickListener() {
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
                    }
                }
            });
        }else if("ARTICLE".equals(entity.getType())){
            setVisible(R.id.rl_card_root,true);
            final ShareArticleEntity folderEntity = new Gson().fromJson(entity.getDetail(),ShareArticleEntity.class);
            View article = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_new_wenzhang_zhuan,null);
            TextView title = article.findViewById(R.id.tv_title);
            TextView articleContent = article.findViewById(R.id.tv_content);
            ImageView cover = article.findViewById(R.id.iv_cover);
            TextView mark = article.findViewById(R.id.tv_mark);
            article.findViewById(R.id.tv_folder_name).setVisibility(View.GONE);
            article.findViewById(R.id.tv_tag).setVisibility(View.GONE);
            int w = (int) (DensityUtil.getScreenWidth(itemView.getContext()) - itemView.getResources().getDimension(R.dimen.x48));
            int h = (int) itemView.getResources().getDimension(R.dimen.y400);
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),folderEntity.getCover(),w,h,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(itemView.getContext(),w,h))
                    .into(cover);
            mark.setText("文章");
            title.setText(folderEntity.getTitle());
            articleContent.setText(TagControl.getInstance().paresToSpann(context,folderEntity.getContent()));
            ImageView avatar = article.findViewById(R.id.iv_avatar);
            TextView userName = article.findViewById(R.id.tv_user_name);
            TextView time = article.findViewById(R.id.tv_time);
            size = (int) itemView.getContext().getResources().getDimension(R.dimen.x44);
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),folderEntity.getDocCreateUser().getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                    .into(avatar);
            avatar.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(itemView.getContext(),folderEntity.getDocCreateUser().getUserId());
                }
            });
            userName.setText(folderEntity.getDocCreateUser().getUserName());
            time.setText(StringUtils.timeFormat(folderEntity.getCreateTime()));
            ((LinearLayout)$(R.id.ll_card_root)).addView(article);
            $(R.id.rl_card_root).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (!TextUtils.isEmpty(folderEntity.getDocId())) {
                        Intent i = new Intent(itemView.getContext(), NewDocDetailActivity.class);
                        i.putExtra("uuid",folderEntity.getDocId());
                        itemView.getContext().startActivity(i);
                    }
                }
            });
        }else if("RETWEET".equals(entity.getType())){
            setVisible(R.id.ll_img_root,true);
            final RetweetEntity retweetEntity = new Gson().fromJson(entity.getDetail(),RetweetEntity.class);
            if(!TextUtils.isEmpty(retweetEntity.getContent())){
                TextView tv = new TextView(itemView.getContext());
                tv.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.black_1e1e1e));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,itemView.getResources().getDimension(R.dimen.x30));
                tv.setLineSpacing(itemView.getResources().getDimension(R.dimen.y12),1);
                tv.setMaxLines(10);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                String res = "<at_user user_id="+ retweetEntity.getCreateUserId() + ">" + retweetEntity.getCreateUserName() + ":</at_user>" +  retweetEntity.getContent();
                tv.setText(TagControl.getInstance().paresToSpann(context,res));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(!TextUtils.isEmpty(retweetEntity.getOldDynamicId())){
                            DynamicActivity.startActivity(context,retweetEntity.getOldDynamicId());
                        }
                    }
                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) itemView.getResources().getDimension(R.dimen.y24);
                lp.bottomMargin = (int) itemView.getResources().getDimension(R.dimen.y24);
                tv.setLayoutParams(lp);
                ((LinearLayout)$(R.id.ll_img_root)).addView(tv);
                $(R.id.ll_img_root).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                    if(!TextUtils.isEmpty(retweetEntity.getOldDynamicId())){
                        DynamicActivity.startActivity(context,retweetEntity.getOldDynamicId());
                    }
                    }
                });
            }
            if(retweetEntity.getImages() != null && retweetEntity.getImages().size() > 0){
                setImg(retweetEntity.getImages());
            }else {
                if(TextUtils.isEmpty(retweetEntity.getContent())){
                    setVisible(R.id.ll_img_root,false);
                }
            }
            setText(R.id.tv_retweet_time,StringUtils.timeFormat(retweetEntity.getCreateTime()));
            setVisible(R.id.ll_retweet_bottom_root,true);
            $(R.id.ll_retweet_bottom_root).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!TextUtils.isEmpty(retweetEntity.getOldDynamicId())){
                        DynamicActivity.startActivity(context,retweetEntity.getOldDynamicId());
                    }
                }
            });
            if(retweetEntity.getRtNum() == 0){
                setText(R.id.tv_retweet_forward_num, "转发");
            }else {
                setText(R.id.tv_retweet_forward_num, StringUtils.getNumberInLengthLimit(retweetEntity.getRtNum(), 3));
            }
            if(retweetEntity.getComments() == 0){
                setText(R.id.tv_retweet_comment_num, "评论");
            }else {
                setText(R.id.tv_retweet_comment_num, StringUtils.getNumberInLengthLimit(retweetEntity.getComments(), 3));
            }
            if(retweetEntity.getLikes() == 0){
                setText(R.id.tv_retweet_tag_num, "点赞");
            }else {
                setText(R.id.tv_retweet_tag_num, StringUtils.getNumberInLengthLimit(retweetEntity.getLikes(), 3));
            }
            if(retweetEntity.getCoins() > 0){
                showHongbao = true;
            }
            showHongBao(true,retweetEntity.getCoins(),retweetEntity.getSurplus(),retweetEntity.getOldDynamicId(),retweetEntity.getCreateUserHead(),retweetEntity.getUsers());
        }
        //coins
        if(!showHongbao) {
            showHongBao(false, entity.getCoins(), entity.getSurplus(), entity.getId(), entity.getCreateUser().getHeadPath(), entity.getUsers());
        }
        //label
        if(entity.isTag()){
            $(R.id.ll_img_root).setBackgroundColor(Color.TRANSPARENT);
            $(R.id.rl_card_root).setBackgroundColor(Color.TRANSPARENT);
            if("ARTICLE".equals(entity.getType()) || "FOLDER".equals(entity.getType())){
                int py = (int) context.getResources().getDimension(R.dimen.y24);
                $(R.id.rl_card_root).setPadding(0,0,0,py);
            }
        }else {
            $(R.id.ll_img_root).setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.cyan_eefdff));
            $(R.id.rl_card_root).setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.cyan_eefdff));
            if("ARTICLE".equals(entity.getType()) || "FOLDER".equals(entity.getType())){
                int py = (int) context.getResources().getDimension(R.dimen.y24);
                $(R.id.rl_card_root).setPadding(0,py,0,py);
            }
        }
        if(!"RETWEET".equals(entity.getType())){
            setVisible(R.id.ll_retweet_bottom_root,false);
        }

        //bottom
        setVisible(R.id.rl_list_bottom_root,false);
        setVisible(R.id.rl_list_bottom_root_2,true);
        if(entity.getRetweets() == 0){
            setText(R.id.tv_forward_num_2, "转发");
        }else {
            setText(R.id.tv_forward_num_2, StringUtils.getNumberInLengthLimit(entity.getRetweets(), 3));
        }
        if(entity.getComments() == 0){
            setText(R.id.tv_comment_num_2, "评论");
        }else {
            setText(R.id.tv_comment_num_2, StringUtils.getNumberInLengthLimit(entity.getComments(), 3));
        }

        $(R.id.iv_like_item).setSelected(entity.isThumb());
        $(R.id.tv_like_item).setSelected(entity.isThumb());

        if(entity.getThumbs() == 0){
            setVisible(R.id.fl_tag_root_2,true);
            setVisible(R.id.rl_tag_root_2,false);
        }else {
            setVisible(R.id.fl_tag_root_2,false);
            setVisible(R.id.rl_tag_root_2,true);
            setText(R.id.tv_like_num, StringUtils.getNumberInLengthLimit(entity.getThumbs(), 3));
            int trueSize = (int) context.getResources().getDimension(R.dimen.y48);
            int imgSize = (int) context.getResources().getDimension(R.dimen.y44);
            int startMargin = (int) -context.getResources().getDimension(R.dimen.y10);
            int showSize = 4;
            if(entity.getThumbUsers().size() < showSize){
                showSize = entity.getThumbUsers().size();
            }
            ((LinearLayout)$(R.id.ll_like_user_root)).removeAllViews();
            if(showSize == 4){
                ImageView iv = new ImageView(context);
                LinearLayout.LayoutParams  lp = new LinearLayout.LayoutParams(trueSize,trueSize);
                lp.leftMargin = startMargin;
                iv.setLayoutParams(lp);
                iv.setImageResource(R.drawable.btn_feed_like_more);
                ((LinearLayout)$(R.id.ll_like_user_root)).addView(iv);
            }
            for(int i = showSize - 1;i >= 0;i--){
                final SimpleUserEntity userEntity  = entity.getThumbUsers().get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(trueSize,trueSize);
                if(i != 0){
                    lp.leftMargin = startMargin;
                }
                View likeUser = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_white_border_img,null);
                likeUser.setLayoutParams(lp);
                ImageView img = likeUser.findViewById(R.id.iv_img);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,userEntity.getUserIcon(),imgSize,imgSize,false,true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(img);
                img.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(context,userEntity.getUserId());
                    }
                });
                ((LinearLayout)$(R.id.ll_like_user_root)).addView(likeUser);
            }
        }
        $(R.id.fl_forward_root_2).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateForwardActivity.startActivityForResult(context,entity);
            }
        });
        $(R.id.fl_comment_root_2).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DynamicActivity.startActivity(context,entity,true);
            }
        });
        $(R.id.fl_tag_root_2).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
            if(context instanceof WallBlockActivity){
                ((WallBlockActivity) context).likeDynamic(entity.getId(),entity.isThumb(),position);
            }else if(context instanceof PersonalFavoriteDynamicActivity){
                ((PersonalFavoriteDynamicActivity) context).likeDynamic(entity.getId(),entity.isThumb(),position);
            }else if(context instanceof PersonalV2Activity){
                ((PersonalV2Activity) context).likeDynamic(entity.getId(),entity.isThumb(),position);
            }
            }
        });
        $(R.id.iv_like_item).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(context instanceof WallBlockActivity){
                    ((WallBlockActivity) context).likeDynamic(entity.getId(),entity.isThumb(),position);
                }else if(context instanceof PersonalFavoriteDynamicActivity){
                    ((PersonalFavoriteDynamicActivity) context).likeDynamic(entity.getId(),entity.isThumb(),position);
                }else if(context instanceof PersonalV2Activity){
                    ((PersonalV2Activity) context).likeDynamic(entity.getId(),entity.isThumb(),position);
                }
            }
        });
    }

    private void showHongBao(boolean rt,final int coins, int surplus, final String id, final String icon, final int users){
        if(coins > 0){
            if(rt){
                $(R.id.fl_hongbao_root).setBackgroundColor(ContextCompat.getColor(context,R.color.cyan_eefdff));
            }else {
                $(R.id.fl_hongbao_root).setBackgroundColor(ContextCompat.getColor(context,R.color.white));
            }
            setVisible(R.id.fl_hongbao_root,true);
            setText(R.id.tv_hongbao_coin,String.format(context.getString(R.string.label_hongbao_total_coin),coins));
            if(surplus > 0){
                $(R.id.rl_hongbao_root).setBackgroundColor(ContextCompat.getColor(context,R.color.orange_f2cc2c));
                ((TextView)$(R.id.tv_hongbao_coin)).setTextColor(ContextCompat.getColor(context,R.color.orange_f2cc2c));
                setText(R.id.tv_left_num,"剩余：" + surplus);
                setText(R.id.tv_desc,"转发即可领取红包");
            }else {
                $(R.id.rl_hongbao_root).setBackgroundColor(ContextCompat.getColor(context,R.color.gray_d7d7d7));
                ((TextView)$(R.id.tv_hongbao_coin)).setTextColor(ContextCompat.getColor(context,R.color.gray_d7d7d7));
                setText(R.id.tv_left_num,"已被抢完");
                setText(R.id.tv_desc,users + "领取了红包");
            }
            $(R.id.fl_hongbao_root).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    HongBaoListActivity.startActivity(context,id,icon,coins,users);
                }
            });
        }else {
            $(R.id.fl_hongbao_root).setOnClickListener(null);
            setVisible(R.id.fl_hongbao_root,false);
        }
    }

    private void setImg(ArrayList<Image> images){
        if(images.size() == 1){
            Image image = images.get(0);
            int[] wh;
            if(image.getW() > image.getH()){
                wh = BitmapUtils.getDocIconSizeFromW(image.getW(),image.getH(), (int) itemView.getResources().getDimension(R.dimen.x460));
            }else {
                wh = BitmapUtils.getDocIconSizeFromH(image.getW(),image.getH(), (int) itemView.getResources().getDimension(R.dimen.x460));
            }
            ImageView iv = new ImageView(itemView.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wh[0],wh[1]);
            lp.bottomMargin = (int) itemView.getResources().getDimension(R.dimen.y24);
            iv.setLayoutParams(lp);
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),image.getPath(),wh[0],wh[1],false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .into(iv);
            showImg(iv,images,0);
            ((LinearLayout)$(R.id.ll_img_root)).addView(iv);
        }else if(images.size() == 2){
            LinearLayout layout = new LinearLayout(itemView.getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(lp);
            int w = (int) ((DensityUtil.getScreenWidth(itemView.getContext()) - itemView.getResources().getDimension(R.dimen.x54)))/2;
            for(int i = 0;i < images.size();i++){
                Image image = images.get(i);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(w,w);
                if(i == 0){
                    lp1.rightMargin = (int) itemView.getResources().getDimension(R.dimen.x6);
                }
                ImageView iv = new ImageView(itemView.getContext());
                iv.setLayoutParams(lp1);
                Glide.with(itemView.getContext())
                        .load(StringUtils.getUrl(itemView.getContext(),image.getPath(),w,w,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(iv);
                layout.addView(iv);
                showImg(iv,images,i);
            }
            ((LinearLayout)$(R.id.ll_img_root)).addView(layout);
        }else if(images.size() == 4){
            int w = (int) ((DensityUtil.getScreenWidth(itemView.getContext()) - itemView.getResources().getDimension(R.dimen.x54)))/2;
            LinearLayout layout = null;
            for(int i = 0;i < images.size();i++){
                Image image = images.get(i);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(w,w);
                if(i == 0 || i == 2){
                    lp1.rightMargin = (int) itemView.getResources().getDimension(R.dimen.x6);
                    layout = new LinearLayout(itemView.getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(i == 2)lp.topMargin = (int) context.getResources().getDimension(R.dimen.y6);
                    layout.setLayoutParams(lp);
                }
                ImageView iv = new ImageView(itemView.getContext());
                iv.setLayoutParams(lp1);
                Glide.with(itemView.getContext())
                        .load(StringUtils.getUrl(itemView.getContext(),image.getPath(),w,w,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(iv);
                layout.addView(iv);
                showImg(iv,images,i);
                if(i == 1 || i == 3){
                    ((LinearLayout)$(R.id.ll_img_root)).addView(layout);
                }
            }
        }else {
            int w = (int) ((DensityUtil.getScreenWidth(itemView.getContext()) - itemView.getResources().getDimension(R.dimen.x60)))/3;
            LinearLayout layout = null;
            for(int i = 0;i < images.size();i++){
                Image image = images.get(i);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(w,w);
                if(i == 0 || i == 3 || i == 6){
                    layout = new LinearLayout(itemView.getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(i == 3 || i == 6){
                        lp.topMargin = (int) context.getResources().getDimension(R.dimen.y6);
                        layout.setLayoutParams(lp);
                    }
                }
                if(i % 3 != 2){
                    lp1.rightMargin = (int) itemView.getResources().getDimension(R.dimen.x6);
                }
                ImageView iv = new ImageView(itemView.getContext());
                iv.setLayoutParams(lp1);
                Glide.with(itemView.getContext())
                        .load(StringUtils.getUrl(itemView.getContext(),image.getPath(),w,w,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(iv);
                layout.addView(iv);
                showImg(iv,images,i);
                if(i % 3 == 2 || images.size() == i + 1){
                    ((LinearLayout)$(R.id.ll_img_root)).addView(layout);
                }
            }
        }
    }

    private void showImg(ImageView iv, final ArrayList<Image> list, final int position){
        iv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, list);
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, position);
                // 以后可选择 有返回数据
                itemView.getContext().startActivity(intent);
            }
        });
    }

    private void showMenu(final String name, final String content, final String id){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0,itemView.getContext().getString(R.string.label_jubao));
        items.add(item);
        BottomMenuFragment fragment = new BottomMenuFragment();
        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent intent = new Intent(itemView.getContext(), JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, name);
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, content);
                    intent.putExtra(JuBaoActivity.UUID,id);
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC.toString());
                    itemView.getContext().startActivity(intent);
                }
            }
        });
        fragment.show(((BaseAppCompatActivity)itemView.getContext()).getSupportFragmentManager(),"CommentMenu");
    }
}
