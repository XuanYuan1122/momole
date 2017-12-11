package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.CommentSecListActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class CommentSecListHolder extends ClickableViewHolder {

    public CommentSecListHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final CommentV2SecEntity entity, final int position){
        int size = (int) itemView.getResources().getDimension(R.dimen.x72);
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                .into((ImageView) $(R.id.iv_avatar));
        setText(R.id.tv_name,entity.getCreateUser().getUserName());
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
        $(R.id.tv_favorite).setSelected(entity.isLike());
        setText(R.id.tv_favorite,entity.getLikes() + "");
        $(R.id.tv_favorite).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(context instanceof CommentSecListActivity){
                    ((CommentSecListActivity) context).favoriteComment(entity.getCommentId(),entity.isLike(),position);
                }
            }
        });
        String tmp = "";
        if(!TextUtils.isEmpty(entity.getCommentTo())){
            tmp += "回复 <at_user user_id=" + entity.getCommentTo() + ">@" + entity.getCommentToName() + "</at_user>: ";
        }
        setText(R.id.tv_comment, TagControl.getInstance().paresToSpann(itemView.getContext(),tmp + entity.getContent()));
        ((TextView)$(R.id.tv_comment)).setMovementMethod(LinkMovementMethod.getInstance());
        if(entity.getImages().size() > 0){
            setVisible(R.id.ll_comment_img,true);
            ((LinearLayout)$(R.id.ll_comment_img)).removeAllViews();
            for (int i = 0;i < entity.getImages().size();i++){
                final int pos = i;
                Image image = entity.getImages().get(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = (int) context.getResources().getDimension(R.dimen.y10);
                if(FileUtil.isGif(image.getPath())){
                    ImageView imageView = new ImageView(context);
                    setGif(image, imageView,params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ImageBigSelectActivity.class);
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, entity.getImages());
                            intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                    pos);
                            context.startActivity(intent);
                        }
                    });
                    ((LinearLayout)$(R.id.ll_comment_img)).addView(imageView,((LinearLayout)$(R.id.ll_comment_img)).getChildCount(),params);
                }else {
                    ImageView imageView = new ImageView(context);
                    setImage(image, imageView,params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ImageBigSelectActivity.class);
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, entity.getImages());
                            intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                    pos);
                            context.startActivity(intent);
                        }
                    });
                    ((LinearLayout)$(R.id.ll_comment_img)).addView(imageView,((LinearLayout)$(R.id.ll_comment_img)).getChildCount(),params);
                }
            }
        }else {
            setVisible(R.id.ll_comment_img,false);
        }
        setText(R.id.tv_comment_time,StringUtils.timeFormat(entity.getCreateTime()));
        //sec comment
        setVisible(R.id.ll_comment_root,false);
    }

    private void setGif(Image image, ImageView gifImageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, (int) (DensityUtil.getScreenWidth(context) - context.getResources().getDimension(R.dimen.x168)));
        params.width = wh[0];
        params.height = wh[1];
        Glide.with(context)
                .load(ApiService.URL_QINIU + image.getPath())
                .asGif()
                .override(wh[0], wh[1])
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(gifImageView);
    }

    private void setImage(Image image, final ImageView imageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, (int) (DensityUtil.getScreenWidth(context) - context.getResources().getDimension(R.dimen.x168)));
        params.width = wh[0];
        params.height = wh[1];
        Glide.with(context)
                .load(StringUtils.getUrl(context,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                .override(wh[0], wh[1])
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(imageView);
    }
}
