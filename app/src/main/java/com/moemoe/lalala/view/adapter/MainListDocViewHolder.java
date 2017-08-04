package com.moemoe.lalala.view.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.utils.CustomUrlSpan;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.FolderActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
import com.moemoe.lalala.view.fragment.FollowMainFragment;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.NewDocLabelAdapter;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/7/28.
 */

public class MainListDocViewHolder extends ClickableViewHolder {

    public ImageView ivAvatar;
    public LinearLayout container;
    public View[] huiZhangRoots;
    public TextView[] huiZhangTexts;
    public DocLabelView docLabel;
    public NewDocLabelAdapter docLabelAdapter;
    private Context context;

    public MainListDocViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ivAvatar = $(R.id.iv_post_creator);
        huiZhangRoots = new View[]{$(R.id.fl_huizhang_1),$(R.id.fl_huizhang_2),$(R.id.fl_huizhang_3)};
        huiZhangTexts = new TextView[]{$(R.id.tv_huizhang_1),$(R.id.tv_huizhang_2),$(R.id.tv_huizhang_3)};
        container = $(R.id.ll_container);
        docLabel = $(R.id.dv_doc_label_root);
        docLabelAdapter = new NewDocLabelAdapter(itemView.getContext(),true);
        if(AppSetting.SUB_TAG)docLabel.setmMaxLines(3);
    }

    public void createFollowDoc(NewDocListEntity item){
        final NewDocListEntity.Doc doc = (NewDocListEntity.Doc) item.getDetail().getTrueData();
        if(!TextUtils.isEmpty(doc.getDocFrom())){
            setVisible(R.id.rl_from_top, true);
            setText(R.id.tv_from_name,doc.getDocFrom());
            if(!TextUtils.isEmpty(doc.getFromSchema())){
                $(R.id.tv_from_name).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Uri uri = Uri.parse(doc.getFromSchema());
                        IntentUtils.toActivityFromUri(context, uri,v);
                    }
                });
            }else {
                $(R.id.tv_from_name).setOnClickListener(null);
            }
        }else {
            setVisible(R.id.rl_from_top, false);
        }
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
        setText(R.id.tv_post_update_time,StringUtils.timeFormate(item.getCreateTime()));
        //user end
        if(!TextUtils.isEmpty(doc.getTitle())){
            setVisible(R.id.tv_post_title,true);
            setText(R.id.tv_post_title,doc.getTitle());
        }else {
            setVisible(R.id.tv_post_title,false);
        }
        setVisible(R.id.tv_post_brief,true);
        setText(R.id.tv_post_brief,doc.getContent());
        setVisible(R.id.tv_extra,false);

        container.removeAllViews();
        if(doc.getImages().size() > 0){
            container.setVisibility(View.VISIBLE);
            for (int i = 0;i < doc.getImages().size();i++){
                Image image = doc.getImages().get(i);
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
                        intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, doc.getImages());
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
        setText(R.id.tv_tag_num, StringUtils.getNumberInLengthLimit(doc.getLikes(), 3));
        setText(R.id.tv_comment_num, StringUtils.getNumberInLengthLimit(doc.getComments(), 3));
        $(R.id.fl_add_tag_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
            if (context instanceof WallBlockActivity){
                ((WallBlockActivity)context).createTagPre(doc.getDocId());
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
    }

    public void createFolderDoc(NewDocListEntity item){
        final NewDocListEntity.FollowFolder doc = (NewDocListEntity.FollowFolder) item.getDetail().getTrueData();
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
        setText(R.id.tv_post_update_time,StringUtils.timeFormate(doc.getFolder().getUpdateTime()));
        //user end
        setVisible(R.id.tv_post_title,false);
        setVisible(R.id.tv_post_brief,false);

        if(!TextUtils.isEmpty(doc.getExtra())){
            setVisible(R.id.tv_extra,true);
            String content = doc.getExtra();
            String colorStr = doc.getExtraColorContent();
            if(!TextUtils.isEmpty(colorStr)){
                ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.main_cyan));
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(span, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(R.id.tv_extra, style);
                ((TextView)$(R.id.tv_extra)).setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                setText(R.id.tv_extra, content);
                ((TextView)$(R.id.tv_extra)).setMovementMethod(null);
            }
        }else {
            setVisible(R.id.tv_extra,false);
        }
        container.removeAllViews();
        container.setVisibility(View.VISIBLE);
        View v = LayoutInflater.from(context).inflate(R.layout.item_main_list_folder,null);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(context,120)));
        ImageView bg = (ImageView) v.findViewById(R.id.iv_bg);
        TextView num = (TextView) v.findViewById(R.id.tv_num);
        TextView name = (TextView) v.findViewById(R.id.tv_name);
        Glide.with(context)
                .load(StringUtils.getUrl(context,doc.getFolder().getCover(), DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,24), DensityUtil.dip2px(context,120), false, true))
                .override(DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,24), DensityUtil.dip2px(context,120))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .transform(new GlideRoundTransform(context,5))
                .into(bg);
        num.setText(doc.getFolder().getNumber() + "项");
        name.setText(doc.getFolder().getName());
        v.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(context,FolderActivity.class);
                i.putExtra("info",doc.getFolder());
                i.putExtra("show_more",true);
                i.putExtra("uuid", doc.getUserId());
                context.startActivity(i);
            }
        });
        container.addView(v);
        docLabel.setVisibility(View.GONE);
        setVisible(R.id.rl_list_bottom_root,false);
    }

    public void createCommentDoc(NewDocListEntity item){
        final NewDocListEntity.FollowComment doc = (NewDocListEntity.FollowComment) item.getDetail().getTrueData();
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
        setVisible(R.id.tv_post_title,false);
        setVisible(R.id.tv_post_brief,false);

        if(!TextUtils.isEmpty(doc.getExtra())){
            setVisible(R.id.tv_extra,true);
            String content = doc.getExtra();
            String colorStr = doc.getExtraColorContent();
            if(!TextUtils.isEmpty(colorStr)){
                ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.main_cyan));
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(span, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(R.id.tv_extra, style);
                ((TextView)$(R.id.tv_extra)).setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                setText(R.id.tv_extra, content);
                ((TextView)$(R.id.tv_extra)).setMovementMethod(null);
            }
        }else {
            setVisible(R.id.tv_extra,false);
        }
        container.removeAllViews();
        container.setVisibility(View.VISIBLE);

        View v = LayoutInflater.from(context).inflate(R.layout.item_main_list_comment,null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(context,100)));
        ImageView iv = (ImageView) v.findViewById(R.id.iv_img);
        TextView title = (TextView) v.findViewById(R.id.tv_title);
        TextView content = (TextView) v.findViewById(R.id.tv_content);
        TextView time = (TextView) v.findViewById(R.id.tv_time);
        TextView tagNum = (TextView) v.findViewById(R.id.tv_post_pants_num);
        TextView comments = (TextView) v.findViewById(R.id.tv_post_comment_num);

        Glide.with(context)
                .load(StringUtils.getUrl(context, doc.getDocIcon().getPath(), DensityUtil.dip2px(context,80),DensityUtil.dip2px(context,80),false,true))
                .override(DensityUtil.dip2px(context,80),DensityUtil.dip2px(context,80))
                .error(R.drawable.bg_cardbg_nopic)
                .placeholder(R.drawable.bg_cardbg_nopic)
                .into(iv);
        title.setText(doc.getDocTitle());
        content.setText(doc.getDocContent());
        time.setText(StringUtils.timeFormate(doc.getUpdateTime()));
        // 点赞/评论
        comments.setText(StringUtils.getNumberInLengthLimit(doc.getComments(), 3));
        tagNum.setText(StringUtils.getNumberInLengthLimit(doc.getLikes(), 3));
        v.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (!TextUtils.isEmpty(doc.getSchema())) {
                    String mSchema = doc.getSchema();
                    if(mSchema.contains(context.getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                        String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                        String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                        mSchema = begin + "uuid=" + uuid + "&from_name=关注";
                    }
                    Uri uri = Uri.parse(mSchema);
                    IntentUtils.toActivityFromUri(context, uri,v);
                }
            }
        });
        container.addView(v);
        docLabel.setVisibility(View.GONE);
        setVisible(R.id.rl_list_bottom_root,false);
    }

    public void createFollowUser(NewDocListEntity item){
        final NewDocListEntity.FollowUser doc = (NewDocListEntity.FollowUser) item.getDetail().getTrueData();
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
        setText(R.id.tv_post_update_time,StringUtils.timeFormate(item.getCreateTime()));
        //user end
        setVisible(R.id.tv_post_title,false);
        setVisible(R.id.tv_post_brief,false);
        container.removeAllViews();
        container.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(doc.getExtra())){
            setVisible(R.id.tv_extra,true);
            String content = doc.getExtra();
            String colorStr = doc.getExtraColorContent();
            if(!TextUtils.isEmpty(colorStr)){
                CustomUrlSpan span = new CustomUrlSpan(context,"",doc.getUserId());
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(span, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(R.id.tv_extra, style);
                ((TextView)$(R.id.tv_extra)).setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                setText(R.id.tv_extra, content);
                ((TextView)$(R.id.tv_extra)).setMovementMethod(null);
            }
        }else {
            setVisible(R.id.tv_extra,false);
        }
        docLabel.setVisibility(View.GONE);
        setVisible(R.id.rl_list_bottom_root,false);
    }

    private void showMenu(final NewDocListEntity.Doc doc){
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
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, doc.getContent());
                    intent.putExtra(JuBaoActivity.UUID,doc.getDocId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC.toString());
                    context.startActivity(intent);
                }
            }
        });
        fragment.show(((BaseAppCompatActivity)context).getSupportFragmentManager(),"CommentMenu");
    }
}
