package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.utils.CustomUrlSpan;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.FolderActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.NewDocLabelAdapter;

import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/7/28.
 */

public class MainClassViewHolder extends ClickableViewHolder {

    RelativeLayout mainRoot;
    public ImageView ivAvatar;
    public LinearLayout container;
    public View[] huiZhangRoots;
    public TextView[] huiZhangTexts;
    public DocLabelView docLabel;
    public NewDocLabelAdapter docLabelAdapter;
    private Context context;

    public MainClassViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ivAvatar = $(R.id.iv_post_creator);
        huiZhangRoots = new View[]{$(R.id.fl_huizhang_1),$(R.id.fl_huizhang_2),$(R.id.fl_huizhang_3)};
        huiZhangTexts = new TextView[]{$(R.id.tv_huizhang_1),$(R.id.tv_huizhang_2),$(R.id.tv_huizhang_3)};
        container = $(R.id.ll_container);
        mainRoot = $(R.id.rl_post_root);
        docLabel = $(R.id.dv_doc_label_root);
        docLabelAdapter = new NewDocLabelAdapter(itemView.getContext(),true);
        if(AppSetting.SUB_TAG)docLabel.setmMaxLines(3);
    }

    public void createFollowDoc(final DocListEntity doc){
        setVisible(R.id.rl_from_top, false);
        //user
        Glide.with(context)
                .load(StringUtils.getUrl(context, doc.getUserIcon().getPath(), DensityUtil.dip2px(context,44), DensityUtil.dip2px(context,44),false,false))
                .override(DensityUtil.dip2px(context,44), DensityUtil.dip2px(context,44))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(ivAvatar);
        ivAvatar.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,doc.getUserId());
            }
        });
        setText(R.id.tv_post_creator_name,doc.getUserName());
        setText(R.id.tv_level,doc.getUserLevel() + "");
        int radius1 = DensityUtil.dip2px(context,5);
        float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
        RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
        ShapeDrawable shapeDrawable1 = new ShapeDrawable();
        shapeDrawable1.setShape(roundRectShape1);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(doc.getUserLevelColor(), ContextCompat.getColor(context, R.color.main_cyan)));
        setBackgroundDrawable(R.id.rl_level_bg, shapeDrawable1);
        ViewUtils.badge(context,huiZhangRoots,huiZhangTexts,doc.getBadgeList());
        setText(R.id.tv_post_update_time,StringUtils.timeFormate(doc.getUpdateTime()));
        //user end
        if(!TextUtils.isEmpty(doc.getDesc().getTitle())){
            setVisible(R.id.tv_post_title,true);
            setText(R.id.tv_post_title,doc.getDesc().getTitle());
        }else {
            setVisible(R.id.tv_post_title,false);
        }
        setVisible(R.id.tv_post_brief,true);
        setText(R.id.tv_post_brief,doc.getDesc().getContent());
        setVisible(R.id.tv_extra,false);

        container.removeAllViews();
        if(doc.getDesc().getImages().size() > 0){
            container.setVisibility(View.VISIBLE);
            for (int i = 0;i < doc.getDesc().getImages().size();i++){
                Image image = doc.getDesc().getImages().get(i);
                View v = LayoutInflater.from(context).inflate(R.layout.item_list_image, null);
                ImageView iv = (ImageView) v.findViewById(R.id.iv_post_image);
                View gif = v.findViewById(R.id.iv_post_image_1_gif_flag);
                if (FileUtil.isGif(image.getPath())) {
                    gif.setVisibility(View.VISIBLE);
                } else {
                    gif.setVisibility(View.GONE);
                }
                int width = (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,30)) / 3;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,width);
                if(i == 2 || i == 1){
                    lp.leftMargin = DensityUtil.dip2px(context,3);
                }
                iv.setLayoutParams(lp);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,image.getPath(),width,width, false, true))
                        .asBitmap()
                        .override(width,width)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .into(iv);
                final int finalI = i;
                iv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Intent intent = new Intent(context, ImageBigSelectActivity.class);
                        intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, doc.getDesc().getImages());
                        intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, finalI);
                        // 以后可选择 有返回数据
                        context.startActivity(intent);
                    }
                });
                container.addView(v);
            }
        }else {
            container.setVisibility(View.GONE);
        }
        if(docLabel != null && doc.getTags() != null){
            docLabel.setDocLabelAdapter(docLabelAdapter);
            docLabelAdapter.setData(doc.getTags(),false);
            if(doc.getTags().size()>0) {
                docLabel.setVisibility(View.VISIBLE);
            }else{
                docLabel.setVisibility(View.GONE);
            }
        }else {
            if(docLabel != null) docLabel.setVisibility(View.GONE);
        }
        setVisible(R.id.rl_list_bottom_root,true);
        setText(R.id.tv_tag_num, StringUtils.getNumberInLengthLimit(doc.getDesc().getLikes(), 3));
        setText(R.id.tv_comment_num, StringUtils.getNumberInLengthLimit(doc.getDesc().getComments(), 3));
        $(R.id.fl_add_tag_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
            if (context instanceof WallBlockActivity){
                ((WallBlockActivity)context).createTagPre(doc.getDesc().getId());
            }
            }
        });
        $(R.id.fl_show_comment_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //TODO 进入帖子详情回复界面
            }
        });
        $(R.id.fl_menu_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu(doc);
            }
        });
        for (int i = 1;i < mainRoot.getChildCount();i++){
            mainRoot.removeViewAt(i);
        }
        for (int i = 0; i < doc.getEggs(); i++){
            int[] local;
            if(itemView.getWidth() > 0 && itemView.getHeight() > 0){
                local = getEggPosition(itemView.getWidth() - 200, itemView.getHeight() - 200);
            }else {
                local = getEggPosition(DensityUtil.getScreenWidth(context) - 200,  DensityUtil.dip2px(context, 150) - 200);
            }
            ImageView iv = new ImageView(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
            layoutParams.topMargin = local[0] > 0 ? local[0] : 0;
            layoutParams.leftMargin = local[1] > 0 ? local[1] : 0;
            iv.setLayoutParams(layoutParams);
            iv.setImageResource(R.drawable.ic_doclist_egg);
            mainRoot.addView(iv);
        }
    }

    private int[] getEggPosition(int r, int b){
        Random rand = new Random();
        int x = rand.nextInt(b + 1);
        int y = rand.nextInt(r + 1);
        return new int[]{x, y};
    }

    private void showMenu(final DocListEntity doc){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0,context.getString(R.string.label_jubao));
        items.add(item);
        BottomMenuFragment fragment = new BottomMenuFragment();
        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent intent = new Intent(context, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, doc.getUserName());
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, doc.getDesc().getContent());
                    intent.putExtra(JuBaoActivity.UUID,doc.getDesc().getId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC.toString());
                    context.startActivity(intent);
                }
            }
        });
        fragment.show(((BaseAppCompatActivity)context).getSupportFragmentManager(),"CommentMenu");
    }
}
