package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.event.FollowUserEvent;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import org.greenrobot.eventbus.EventBus;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class FeedRecommendUserHolder extends ClickableViewHolder {

    private int[] mBackGround = { R.drawable.shape_rect_label_cyan_right_y17,
            R.drawable.shape_rect_label_yellow_right_y17,
            R.drawable.shape_rect_label_orange_right_y17,
            R.drawable.shape_rect_label_pink_right_y17,
            R.drawable.shape_rect_border_green_right_y17,
            R.drawable.shape_rect_label_purple_right_y17,
            R.drawable.shape_rect_label_tab_blue_right_y17};

    public FeedRecommendUserHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final FeedRecommendUserEntity entity, final int position){

        int bgW = (int) context.getResources().getDimension(R.dimen.x260);
        int bgH = (int) context.getResources().getDimension(R.dimen.y120);
        int roundSize = (int) context.getResources().getDimension(R.dimen.y8);

        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getBg(),bgW,bgH,false,true))
                .bitmapTransform(new RoundedCornersTransformation(context,roundSize,0, RoundedCornersTransformation.CornerType.TOP))
                .into((ImageView) $(R.id.iv_user_bg));

        setText(R.id.tv_user_mark,entity.getMark());
        int index = StringUtils.getHashOfString(entity.getMark(), mBackGround.length);
        $(R.id.tv_user_mark).setBackgroundResource(mBackGround[index]);

        int userSize = (int) context.getResources().getDimension(R.dimen.y100);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getUserIcon(),userSize,userSize,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_user_avatar));

        setText(R.id.tv_user_name ,entity.getUserName());
        setText(R.id.tv_user_sign,entity.getSignature());

        $(R.id.tv_follow).setSelected(entity.isFollow());
        setText(R.id.tv_follow,entity.isFollow()?"已关注" : "关注");
        $(R.id.tv_follow).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                EventBus.getDefault().post(new FollowUserEvent(entity.getUserId(),entity.isFollow(),position));
            }
        });

    }
}
