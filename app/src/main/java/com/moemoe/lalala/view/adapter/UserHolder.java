package com.moemoe.lalala.view.adapter;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class UserHolder extends ClickableViewHolder {

    public UserHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final UserTopEntity entity, final int position){
        int size = (int) getResources().getDimension(R.dimen.x80);
        Glide.with(context)
                .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_avatar));
        setVisible(R.id.tv_follow,false);

        if(entity.isVip()){
            setVisible(R.id.iv_vip,true);
        }else {
            setVisible(R.id.iv_vip,false);
        }
        setImageResource(R.id.iv_sex,entity.getSex().equalsIgnoreCase("M")?R.drawable.ic_user_girl:R.drawable.ic_user_boy);
        setText(R.id.tv_name,entity.getUserName());
        setText(R.id.tv_time,entity.getSignature());
        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(context,R.color.white),getResources().getDimension(R.dimen.x12));
        String content = "LV" + entity.getLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(R.id.tv_level,style);
        float radius2 = getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(entity.getLevelColor(), ContextCompat.getColor(context, R.color.main_cyan)));
        $(R.id.tv_level).setBackgroundDrawable(shapeDrawable2);
        View [] huiZhangRoots = new View[]{$(R.id.fl_huizhang_1)};
        TextView[] huiZhangTexts = new TextView[]{$(R.id.tv_huizhang_1)};
        ArrayList<BadgeEntity> list = new ArrayList<>();
        list.add(entity.getBadge());
        ViewUtils.badge(context,huiZhangRoots,huiZhangTexts,list);
    }
}
