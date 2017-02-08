package com.moemoe.lalala.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CommentsListActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.activity.PersonalLevelActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalMainFragment extends BaseFragment implements PersonalListContract.View{

    public static int REQ_BADGE = 10001;

    @BindView(R.id.tv_sign)
    TextView mTvSign;
    @BindView(R.id.pb_score)
    ProgressBar mBar;
    @BindView(R.id.tv_need_score)
    TextView mTvNeedScore;
    @BindView(R.id.tv_level)
    TextView mTvLevel;
    @BindView(R.id.iv_level_name_details)
    ImageView mIvLevel;
    @BindView(R.id.tv_liuyan_num)
    TextView mTvNum;
    @BindView(R.id.ll_comments_root)
    LinearLayout mLlRoot;
    @BindView(R.id.tv_all_liuyan)
    TextView mTvAllComments;
    @BindView(R.id.fl_more_root)
    View mMoreBadge;



    private PersonalMainEntity entity;
    private String uuid;

    @Inject
    PersonaListPresenter mPresenter;
    @Override
    protected int getLayoutId() {
        return R.layout.frag_person_main;
    }

    public static PersonalMainFragment newInstance(String id){
        PersonalMainFragment fragment = new PersonalMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        uuid = getArguments().getString("uuid");
        if(uuid.equals(PreferenceUtils.getUUid())){
            mIvLevel.setVisibility(View.VISIBLE);
            mMoreBadge.setVisibility(View.VISIBLE);
            mMoreBadge.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    //TODO 查看所有徽章
                    Intent i = new Intent(getContext(), BadgeActivity.class);
                    getActivity().startActivityForResult(i,REQ_BADGE);
                }
            });
        }else {
            mIvLevel.setVisibility(View.GONE);
            mMoreBadge.setVisibility(View.INVISIBLE);
        }
        mTvNum.setText(getString(R.string.label_liuyan,0));
        mPresenter.doRequest(uuid,0,0);
    }

    @OnClick({R.id.iv_level_name_details,R.id.tv_all_liuyan})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_level_name_details:
                if(entity != null) PersonalLevelActivity.startActivity(getContext(),entity.getLevelName(),entity.getLevelColor(),entity.getScore(),entity.getLevelScoreStart(),entity.getLevelScoreEnd(),entity.getLevel());
                break;
            case R.id.tv_all_liuyan:
                Intent i = new Intent(getContext(), CommentsListActivity.class);
                i.putExtra("uuid",uuid);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onSuccess(Object o,boolean is) {
        entity = (PersonalMainEntity) o;
        mTvSign.setText(entity.getSignature());
        mTvLevel.setText(getString(R.string.label_level_1,entity.getLevel()));
        mTvLevel.setTextColor(StringUtils.readColorStr(entity.getLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
        mTvNeedScore.setText((entity.getLevelScoreEnd() - entity.getScore())+"");
        mBar.setMax(entity.getLevelScoreEnd() - entity.getLevelScoreStart());
        mBar.setProgress(entity.getScore() - entity.getLevelScoreStart());

        int radius = DensityUtil.dip2px(getContext(),9);
        float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundRectShape1 = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable1 = new ShapeDrawable();
        shapeDrawable1.setShape(roundRectShape1);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(entity.getLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
        ClipDrawable clipDrawable = new ClipDrawable(shapeDrawable1, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        RoundRectShape roundRectShape0 = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(roundRectShape0);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(),R.color.gray_d7d7d8));

        Drawable[] layers = new Drawable[]{shapeDrawable,clipDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setId(0,android.R.id.background);
        layerDrawable.setId(1,android.R.id.progress);
        mBar.setProgressDrawable(layerDrawable);

        mTvNum.setText(getString(R.string.label_liuyan,entity.getCommentCount()));
        if(entity.getCommentList().size() == 0){
            mTvAllComments.setText("谁来温暖这位寂寞的同学？");
        }else {
            mTvAllComments.setText("查看全部");
        }
        mLlRoot.removeAllViews();
        for (final NewCommentEntity commentEntity : entity.getCommentList()){
            final View v = View.inflate(getContext(),R.layout.item_post_comment,null);
            ImageView ivCreator = (ImageView) v.findViewById(R.id.iv_comment_creator);
            TextView tvCreatorName = (TextView) v.findViewById(R.id.tv_comment_creator_name);
            TextView tvTime = (TextView) v.findViewById(R.id.tv_comment_time);
            TextView tvContent = (TextView) v.findViewById(R.id.tv_comment);
            View ivOwnerFlag = v.findViewById(R.id.iv_club_owner_flag);
            View ivLevelColor = v.findViewById(R.id.rl_level_bg);
            TextView tvLevel = (TextView)v.findViewById(R.id.tv_level);
            TextView tvHuiZhang1 = (TextView)v.findViewById(R.id.tv_huizhang_1);
            TextView tvHuiZhang2 = (TextView)v.findViewById(R.id.tv_huizhang_2);
            TextView tvHuiZhang3 = (TextView)v.findViewById(R.id.tv_huizhang_3);
            View rlHuiZhang1 = v.findViewById(R.id.fl_huizhang_1);
            View rlHuiZhang2 = v.findViewById(R.id.fl_huizhang_2);
            View rlHuiZhang3 = v.findViewById(R.id.fl_huizhang_3);
            View[] huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            TextView[] huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};

            Glide.with(getActivity())
                    .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU + commentEntity.getFromUserIcon().getPath(), DensityUtil.dip2px(getContext(),35), DensityUtil.dip2px(getContext(),35), false, false))
                    .override(DensityUtil.dip2px(getContext(),35), DensityUtil.dip2px(getContext(),35))
                    .placeholder(R.drawable.bg_default_circle)
                    .error(R.drawable.bg_default_circle)
                    .transform(new GlideCircleTransform(getContext()))
                    .into(ivCreator);
            tvCreatorName.setText(commentEntity.getFromUserName());
            tvTime.setText(StringUtils.timeFormate(commentEntity.getCreateTime()));
            if(commentEntity.isDeleteFlag()){
                tvContent.setText(getContext().getString(R.string.label_comment_already));
            }else {
                String comm;
                if (!TextUtils.isEmpty(commentEntity.getToUserName()) ) {
                    comm = "回复 " + (TextUtils.isEmpty(commentEntity.getToUserName()) ? "" :commentEntity.getToUserName()) + ": "
                            + commentEntity.getContent();
                } else {
                    comm = commentEntity.getContent();
                }
                tvContent.setText(StringUtils.getUrlClickableText(getContext(), comm));
                tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            }
            ivOwnerFlag.setVisibility(View.GONE);
            tvLevel.setText(String.valueOf(commentEntity.getFromUserLevel()));

            int radius1 = DensityUtil.dip2px(getContext(),5);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape2 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable2 = new ShapeDrawable();
            shapeDrawable2.setShape(roundRectShape2);
            shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(commentEntity.getFromUserLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
            ivLevelColor.setBackgroundDrawable(shapeDrawable2);
            if(commentEntity.getBadgeList().size() > 0){
                for (int i = 0;i < commentEntity.getBadgeList().size();i++){
                    BadgeEntity badgeEntity = commentEntity.getBadgeList().get(i);
                    TextView tv = huiZhangTexts[i];
                    tv.setText(badgeEntity.getTitle());
                    tv.setText(badgeEntity.getTitle());
                    tv.setBackgroundResource(R.drawable.bg_badge_cover);
                    int px = DensityUtil.dip2px(getContext(),4);
                    tv.setPadding(px,0,px,0);
                    int radius2 = DensityUtil.dip2px(getContext(),2);
                    float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                    RoundRectShape roundRectShape3 = new RoundRectShape(outerR2, null, null);
                    ShapeDrawable shapeDrawable3 = new ShapeDrawable();
                    shapeDrawable3.setShape(roundRectShape3);
                    shapeDrawable3.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable3.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
                    huiZhangRoots[i].setBackgroundDrawable(shapeDrawable3);
                }
            }

            ivCreator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(commentEntity.getId()) && !commentEntity.getId().equals(PreferenceUtils.getUUid())) {
                        Intent i = new Intent(getContext(),NewPersonalActivity.class);
                        i.putExtra(BaseAppCompatActivity.UUID,commentEntity.getId());
                        getActivity().startActivity(i);
                    }
                }
            });
            mLlRoot.addView(v);
        }
        int[] imgIds = {R.id.iv_huizhang_1,R.id.iv_huizhang_2,R.id.iv_huizhang_3};
        int[] tvIds = {R.id.tv_huizhang_1,R.id.tv_huizhang_2,R.id.tv_huizhang_3};
        int[] flIds = {R.id.fl_huizhang_1,R.id.fl_huizhang_2,R.id.fl_huizhang_3};
        int len = 3;
        if(entity.getBadgeList().size() < 3){
            len = entity.getBadgeList().size();
        }
        for (int i = 0;i < len;i++){
            BadgeEntity badgeEntity = entity.getBadgeList().get(i);
            TextView tv = (TextView)rootView.findViewById(tvIds[i]);
            tv.setText(badgeEntity.getTitle());
            tv.setBackgroundResource(R.drawable.bg_badge_cover);
            int px = DensityUtil.dip2px(getContext(),4);
            tv.setPadding(px,0,px,0);
            int radius1 = DensityUtil.dip2px(getContext(),2);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape2 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable2 = new ShapeDrawable();
            shapeDrawable2.setShape(roundRectShape2);
            shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
            rootView.findViewById(flIds[i]).setBackgroundDrawable(shapeDrawable2);
            Glide.with(getActivity())
                    .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU + badgeEntity.getImg(), DensityUtil.dip2px(getContext(),45), DensityUtil.dip2px(getContext(),45), false, false))
                    .override(DensityUtil.dip2px(getContext(),45), DensityUtil.dip2px(getContext(),45))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into((ImageView) rootView.findViewById(imgIds[i]));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_BADGE && resultCode == Activity.RESULT_OK){
            ArrayList<BadgeEntity> entities = data.getParcelableArrayListExtra("list");
            int len = 3;
            if(entities.size() < 3){
                len = entities.size();
            }
            int[] imgIds = {R.id.iv_huizhang_1,R.id.iv_huizhang_2,R.id.iv_huizhang_3};
            int[] tvIds = {R.id.tv_huizhang_1,R.id.tv_huizhang_2,R.id.tv_huizhang_3};
            int[] flIds = {R.id.fl_huizhang_1,R.id.fl_huizhang_2,R.id.fl_huizhang_3};
            for (int i = 0;i < len;i++){
                BadgeEntity badgeEntity = entities.get(i);
                TextView tv = (TextView)rootView.findViewById(tvIds[i]);
                tv.setText(badgeEntity.getTitle());
                tv.setBackgroundResource(R.drawable.bg_badge_cover);
                int px = DensityUtil.dip2px(getContext(),4);
                tv.setPadding(px,0,px,0);
                int radius1 = DensityUtil.dip2px(getContext(),2);
                float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
                RoundRectShape roundRectShape2 = new RoundRectShape(outerR1, null, null);
                ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                shapeDrawable2.setShape(roundRectShape2);
                shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
                rootView.findViewById(flIds[i]).setBackgroundDrawable(shapeDrawable2);
                Glide.with(getActivity())
                        .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU + badgeEntity.getImg(), DensityUtil.dip2px(getContext(),45), DensityUtil.dip2px(getContext(),45), false, false))
                        .override(DensityUtil.dip2px(getContext(),45), DensityUtil.dip2px(getContext(),45))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into((ImageView) rootView.findViewById(imgIds[i]));
            }
        }
    }

    @Override
    public void onFailure(int code,String msg) {

    }
}
