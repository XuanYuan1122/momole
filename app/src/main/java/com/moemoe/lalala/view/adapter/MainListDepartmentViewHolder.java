package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/7/28.
 */

public class MainListDepartmentViewHolder extends ClickableViewHolder {

    private Context context;

    public MainListDepartmentViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
    }

    public void createDoc(NewDocListEntity item){
        final NewDocListEntity.FollowDepartment doc = (NewDocListEntity.FollowDepartment) item.getDetail().getTrueData();
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

        Glide.with(context)
                .load( StringUtils.getUrl(context,doc.getDocIcon().getPath(), DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,200), false, true))
                .override(DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,200))
                .centerCrop()
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_icon));
        setText(R.id.tv_title,doc.getTitle());
        setText(R.id.tv_content,doc.getContent());
        Glide.with(context)
                .load( StringUtils.getUrl(context, doc.getUserIcon().getPath(), DensityUtil.dip2px(context,20), DensityUtil.dip2px(context,20), false, true))
                .override(DensityUtil.dip2px(context,20), DensityUtil.dip2px(context,20))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_avatar));
        $(R.id.iv_avatar).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,doc.getUserId());
            }
        });
        setText(R.id.tv_user_name,doc.getUserName());
        setText(R.id.tv_update_time,StringUtils.timeFormate(item.getCreateTime()));

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

    private void showMenu(final NewDocListEntity.FollowDepartment doc){
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
