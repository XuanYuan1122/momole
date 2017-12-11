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
        View article = LayoutInflater.from(context).inflate(R.layout.item_new_wenzhang_zhuan,null);
        TextView title = article.findViewById(R.id.tv_title);
        TextView articleContent = article.findViewById(R.id.tv_content);
        ImageView cover = article.findViewById(R.id.iv_cover);
        TextView mark = article.findViewById(R.id.tv_mark);
        TextView readNum = article.findViewById(R.id.tv_read_num);
        View scoreRoot = article.findViewById(R.id.fl_score_root);
        TextView tvScore = article.findViewById(R.id.tv_score);

        article.findViewById(R.id.tv_folder_name).setVisibility(View.GONE);
        article.findViewById(R.id.tv_tag).setVisibility(View.GONE);


        if(docBean.getCoin() != 0 || docBean.getScore() != 0){
            scoreRoot.setVisibility(View.VISIBLE);
            String tmpStr = "";
            if(docBean.getCoin() != 0){
                tmpStr = docBean.getCoin() + "节操+";
            }
            tmpStr = tmpStr + docBean.getScore() + "学分";
            tvScore.setText(tmpStr);
        }else {
            scoreRoot.setVisibility(View.GONE);
        }
        int w = (int) (DensityUtil.getScreenWidth(context) - context.getResources().getDimension(R.dimen.x48));
        int h = (int) context.getResources().getDimension(R.dimen.y400);
        Glide.with(context)
                .load(StringUtils.getUrl(context,docBean.getIcon().getPath(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(context,w,h))
                .into(cover);
        mark.setText("文章");
        if(!TextUtils.isEmpty(docBean.getTitle())){
            title.setVisibility(View.VISIBLE);
            title.setText(docBean.getTitle());
        }else {
            title.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(docBean.getContent())){
            articleContent.setVisibility(View.VISIBLE);
            articleContent.setText(TagControl.getInstance().paresToSpann(context,docBean.getContent()));
        }else {
            articleContent.setVisibility(View.GONE);
        }
        ImageView avatar = article.findViewById(R.id.iv_avatar);
        TextView userName = article.findViewById(R.id.tv_user_name);
        TextView time = article.findViewById(R.id.tv_time);
        int size = (int) context.getResources().getDimension(R.dimen.x44);
        Glide.with(context)
                .load(StringUtils.getUrl(context,docBean.getHeadIcon(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(avatar);
        avatar.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,docBean.getUserId());
            }
        });
        readNum.setVisibility(View.VISIBLE);
        readNum.setText("阅读 " + docBean.getReadNum());
        userName.setText(docBean.getUsername());
        time.setText(StringUtils.timeFormat(docBean.getUpdateTime()));
        ((LinearLayout)$(R.id.ll_card_root)).removeAllViews();
        ((LinearLayout)$(R.id.ll_card_root)).addView(article);
    }
}
