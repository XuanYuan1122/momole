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
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPersonMainComponent;
import com.moemoe.lalala.di.modules.PersonMainModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.PersonMainContract;
import com.moemoe.lalala.presenter.PersonMainPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.BagOpenActivity;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CommentsListActivity;
import com.moemoe.lalala.view.activity.CreateMapImageActivity;
import com.moemoe.lalala.view.activity.NewBagActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.PersonalV2Activity;
import com.moemoe.lalala.view.activity.PersonalLevelActivity;
import com.moemoe.lalala.view.activity.SelectMapImageActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalMainFragment extends BaseFragment implements PersonMainContract.View{

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
    @BindView(R.id.ll_bag_root)
    View mFolderRoot;
    @BindView(R.id.tv_more_add)
    TextView mTvMore;
    @BindView(R.id.ll_folder_root)
    LinearLayout mFolderAddRoot;
    @BindView(R.id.ll_level_root)
    View mLevelRoot;
    @BindView(R.id.ll_huizhang_root)
    View mHuiZhangRoot;
    @BindView(R.id.ll_huizhang_all_root)
    View mAllHuizhangRoot;
    @BindView(R.id.tv_history_role)
    TextView mTvEditRole;
    @BindView(R.id.rl_role_root)
    View mRlRoleRoot;
    @BindView(R.id.ll_role_root)
    View mLlRoleRoot;
    @BindView(R.id.iv_role_favorite)
    ImageView mIvFavoriteMap;
    @BindView(R.id.tv_role_favorite_num)
    TextView mTvMapLikeNum;
    @BindView(R.id.tv_role_history_score)
    TextView mTvHistorySocre;
    @BindView(R.id.fl_role_root)
    FrameLayout mFlRoleRoot;
    @BindView(R.id.tv_edit_role)
    View mEdit;
    @BindView(R.id.iv_role)
    ImageView mIvRole;

    private TextView tvHuiZhang1;
    private TextView tvHuiZhang2;
    private TextView tvHuiZhang3;
    private ImageView ivHuiZhang1;
    private ImageView ivHuiZhang2;
    private ImageView ivHuiZhang3;
    private View rlHuiZhang1;
    private View rlHuiZhang2;
    private View rlHuiZhang3;
    private PersonalMainEntity entity;
    private String uuid;
    private boolean isOpenBag;
    private boolean isCreate;

    @Inject
    PersonMainPresenter mPresenter;
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

    public boolean isOpenBag() {
        return isOpenBag;
    }

    public void setOpenBag(boolean openBag) {
        isOpenBag = openBag;
        if(isCreate){
            if(isOpenBag){
                mTvMore.setText("显示全部");
                mTvMore.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
                mTvMore.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_bag_more),null);
                mTvMore.setCompoundDrawablePadding((int)getResources().getDimension(R.dimen.x8));
            }else {
                mTvMore.setText("未开通书包");
                mTvMore.setTextColor(ContextCompat.getColor(getContext(),R.color.gray_d7d7d7));
                mTvMore.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                mTvMore.setCompoundDrawablePadding(0);
            }
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        DaggerPersonMainComponent.builder()
                .personMainModule(new PersonMainModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        isCreate = true;
        uuid = getArguments().getString("uuid");
        if(uuid.equals(PreferenceUtils.getUUid())){
            mIvLevel.setVisibility(View.VISIBLE);
            mMoreBadge.setVisibility(View.VISIBLE);
            mMoreBadge.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(getContext(), BadgeActivity.class);
                    getActivity().startActivityForResult(i,REQ_BADGE);
                }
            });
        }else {
            mIvLevel.setVisibility(View.GONE);
            mMoreBadge.setVisibility(View.INVISIBLE);
            mLevelRoot.setVisibility(View.GONE);
        }
        if(isOpenBag){
            mTvMore.setText("显示全部");
            mTvMore.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
            mTvMore.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_bag_more),null);
            mTvMore.setCompoundDrawablePadding((int)getResources().getDimension(R.dimen.x8));
        }else {
            mTvMore.setText("未开通书包");
            mTvMore.setTextColor(ContextCompat.getColor(getContext(),R.color.gray_d7d7d7));
            mTvMore.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            mTvMore.setCompoundDrawablePadding(0);
        }
        mTvNum.setText(getString(R.string.label_liuyan,0));
        mPresenter.loadInfo(uuid);
    }

    @Override
    protected void init() {
        super.init();
    }

    @OnClick({R.id.iv_level_name_details,R.id.tv_all_liuyan,R.id.tv_more_add,R.id.tv_all_huizhang})
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
            case R.id.tv_more_add:
                if(isOpenBag){
                    Intent i2 = new Intent(getContext(),NewBagActivity.class);
                    i2.putExtra("uuid",uuid);
                    startActivity(i2);
                }else {
                    if(uuid.equals(PreferenceUtils.getUUid())){
                        if(NetworkUtils.checkNetworkAndShowError(getContext()) && DialogUtils.checkLoginAndShowDlg(getContext())){
                            Intent i2 = new Intent(getContext(),BagOpenActivity.class);
                            startActivity(i2);
                        }
                    }
                }
                break;
            case R.id.tv_all_huizhang:
                    Intent i3 = new Intent(getContext(), BadgeActivity.class);
                    getActivity().startActivityForResult(i3,REQ_BADGE);
                break;
        }
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_BADGE && resultCode == Activity.RESULT_OK){
            ArrayList<BadgeEntity> entities = data.getParcelableArrayListExtra("list");
            int len = 3;
            if(entities.size() < 3){
                len = entities.size();
            }
            final View[] huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            final TextView[] huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
            final ImageView[] huiZhangImgs = new ImageView[]{ivHuiZhang1,ivHuiZhang2,ivHuiZhang3};
            Observable.range(0,3)
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(Integer i) {
                            huiZhangTexts[i].setVisibility(View.INVISIBLE);
                            huiZhangRoots[i].setVisibility(View.INVISIBLE);
                            huiZhangImgs[i].setVisibility(View.INVISIBLE);
                        }
                    });
            for (int i = 0;i < len;i++){
                huiZhangTexts[i].setVisibility(View.VISIBLE);
                huiZhangRoots[i].setVisibility(View.VISIBLE);
                huiZhangImgs[i].setVisibility(View.VISIBLE);
                BadgeEntity badgeEntity = entities.get(i);
                TextView tv = huiZhangTexts[i];
                tv.setText(badgeEntity.getTitle());
                tv.setBackgroundResource(R.drawable.bg_badge_cover);
                int px = (int)getResources().getDimension(R.dimen.x8);
                tv.setPadding(px,0,px,0);
                int radius1 = (int)getResources().getDimension(R.dimen.y4);
                float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
                RoundRectShape roundRectShape2 = new RoundRectShape(outerR1, null, null);
                ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                shapeDrawable2.setShape(roundRectShape2);
                shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
                huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
                Glide.with(getActivity())
                        .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU + badgeEntity.getImg(), (int)getResources().getDimension(R.dimen.y90), (int)getResources().getDimension(R.dimen.y90), false, false))
                        .override((int)getResources().getDimension(R.dimen.y90), (int)getResources().getDimension(R.dimen.y90))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(huiZhangImgs[i]);
            }
        }
    }

    @Override
    public void onFailure(int code,String msg) {

    }

    @Override
    public void onLoadInfoSuccess(final PersonalMainEntity o) {
        entity = o;
        if(!uuid.equals(PreferenceUtils.getUUid())){
            if(TextUtils.isEmpty(entity.getSignature().trim())){
                mTvSign.setText("这位同学啥也没写");
            }else {
                mTvSign.setText(entity.getSignature());
            }
            if(TextUtils.isEmpty(entity.getPicPath())){
                mLlRoleRoot.setVisibility(View.GONE);
            }else {
                mFlRoleRoot.setBackground(null);
                mEdit.setVisibility(View.GONE);
                int w = (int) getResources().getDimension(R.dimen.x180);
                int h = (int) getResources().getDimension(R.dimen.y220);
                Glide.with(getContext())
                        .load(StringUtils.getUrl(getContext(),entity.getPicPath(),w,h,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(mIvRole);
            }
            mIvFavoriteMap.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!TextUtils.isEmpty(entity.getUseArtworkId())){
                        mPresenter.likeMapRole(entity.isLike(),entity.getUseArtworkId());
                    }else {
                        ToastUtils.showLongToast(getContext(),"非自定义形象不能点赞");
                    }

                }
            });

            mTvEditRole.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(getContext(),SelectMapImageActivity.class);
                    i.putExtra(SelectMapImageActivity.SELECT_TYPE,SelectMapImageActivity.IS_HISTORY_FAVORITE);
                    i.putExtra("uuid",uuid);
                    i.putExtra("use_id",entity.getUseArtworkId());
                    startActivity(i);
                }
            });
            //mTvEditRole.setVisibility(View.GONE);
        }else {
            mFlRoleRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i5 = new Intent(getContext(),CreateMapImageActivity.class);
                    startActivity(i5);
                }
            });
            if(TextUtils.isEmpty(entity.getPicPath())){
                mRlRoleRoot.setVisibility(View.GONE);
                mTvEditRole.setText("上传形象");
                mTvEditRole.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Intent i5 = new Intent(getContext(),CreateMapImageActivity.class);
                        startActivity(i5);
                    }
                });
            }else {
                int w = (int) getResources().getDimension(R.dimen.x180);
                int h = (int) getResources().getDimension(R.dimen.y220);
                Glide.with(getContext())
                        .load(StringUtils.getUrl(getContext(),entity.getPicPath(),w,h,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(mIvRole);
                mTvEditRole.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Intent i = new Intent(getContext(),SelectMapImageActivity.class);
                        i.putExtra(SelectMapImageActivity.SELECT_TYPE,SelectMapImageActivity.IS_HISTORY_DELETE);
                        i.putExtra("uuid",uuid);
                        i.putExtra("use_id",entity.getUseArtworkId());
                        startActivity(i);
                    }
                });
            }
            mTvSign.setText(entity.getSignature());
        }
        mIvFavoriteMap.setSelected(entity.isLike());
        mTvMapLikeNum.setText(entity.getPicLikes() + " 喜欢");
        mTvHistorySocre.setText("(历史总分: " + entity.getPicAllLikes() + ")");
        mTvLevel.setText(getString(R.string.label_level_1,entity.getLevel()));
        mTvLevel.setTextColor(StringUtils.readColorStr(entity.getLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
        mTvNeedScore.setText((entity.getLevelScoreEnd() - entity.getScore())+"");
        mBar.setMax(entity.getLevelScoreEnd() - entity.getLevelScoreStart());
        mBar.setProgress(entity.getScore() - entity.getLevelScoreStart());
        if (uuid.equals(PreferenceUtils.getUUid())) {
            PreferenceUtils.getAuthorInfo().setLevel(entity.getLevel());
        }

        // int radius = DensityUtil.dip2px(getContext(),9);
        int radius = (int) getResources().getDimension(R.dimen.y9);
        float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundRectShape1 = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable1 = new ShapeDrawable();
        shapeDrawable1.setShape(roundRectShape1);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(entity.getLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
        ClipDrawable clipDrawable = new ClipDrawable(shapeDrawable1, Gravity.START, ClipDrawable.HORIZONTAL);

        RoundRectShape roundRectShape0 = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(roundRectShape0);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(),R.color.gray_d7d7d7));

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
            ImageView ivCreator = v.findViewById(R.id.iv_comment_creator);
            TextView tvCreatorName = v.findViewById(R.id.tv_comment_creator_name);
            TextView tvTime = v.findViewById(R.id.tv_comment_time);
            TextView tvContent = v.findViewById(R.id.tv_comment);
            View ivLevelColor = v.findViewById(R.id.rl_level_bg);
            TextView tvLevel = v.findViewById(R.id.tv_level);
            TextView tvHuiZhang1 = v.findViewById(R.id.tv_huizhang_1);
            TextView tvHuiZhang2 = v.findViewById(R.id.tv_huizhang_2);
            TextView tvHuiZhang3 = v.findViewById(R.id.tv_huizhang_3);
            View rlHuiZhang1 = v.findViewById(R.id.fl_huizhang_1);
            View rlHuiZhang2 = v.findViewById(R.id.fl_huizhang_2);
            View rlHuiZhang3 = v.findViewById(R.id.fl_huizhang_3);
            View[] huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            TextView[] huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};

            Glide.with(getActivity())
                    .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU + commentEntity.getFromUserIcon().getPath(), (int)getResources().getDimension(R.dimen.y70), (int)getResources().getDimension(R.dimen.y70), false, false))
                    .override((int)getResources().getDimension(R.dimen.y70), (int)getResources().getDimension(R.dimen.y70))
                    .placeholder(R.drawable.bg_default_circle)
                    .error(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(ivCreator);
            tvCreatorName.setText(commentEntity.getFromUserName());
            tvTime.setText(StringUtils.timeFormat(commentEntity.getCreateTime()));
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
            tvLevel.setText(String.valueOf(commentEntity.getFromUserLevel()));

            int radius1 = (int)getResources().getDimension(R.dimen.y10);
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
                    int px = (int)getResources().getDimension(R.dimen.x8);
                    tv.setPadding(px,0,px,0);
                    int radius2 = (int)getResources().getDimension(R.dimen.y4);
                    float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                    RoundRectShape roundRectShape3 = new RoundRectShape(outerR2, null, null);
                    ShapeDrawable shapeDrawable3 = new ShapeDrawable();
                    shapeDrawable3.setShape(roundRectShape3);
                    shapeDrawable3.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable3.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
                    huiZhangRoots[i].setVisibility(View.VISIBLE);
                    huiZhangRoots[i].setBackgroundDrawable(shapeDrawable3);
                }
            }

            ivCreator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(commentEntity.getFromUserId()) && !commentEntity.getFromUserId().equals(PreferenceUtils.getUUid())) {
                        Intent i = new Intent(getContext(),PersonalV2Activity.class);
                        i.putExtra(BaseAppCompatActivity.UUID,commentEntity.getFromUserId());
                        getActivity().startActivity(i);
                    }
                }
            });
            mLlRoot.addView(v);
        }
        rlHuiZhang1 = rootView.findViewById(R.id.fl_huizhang_1);
        rlHuiZhang2 = rootView.findViewById(R.id.fl_huizhang_2);
        rlHuiZhang3 = rootView.findViewById(R.id.fl_huizhang_3);
        tvHuiZhang1 = rootView.findViewById(R.id.tv_huizhang_1);
        tvHuiZhang2 = rootView.findViewById(R.id.tv_huizhang_2);
        tvHuiZhang3 = rootView.findViewById(R.id.tv_huizhang_3);
        ivHuiZhang1 = rootView.findViewById(R.id.iv_huizhang_1);
        ivHuiZhang2 = rootView.findViewById(R.id.iv_huizhang_2);
        ivHuiZhang3 = rootView.findViewById(R.id.iv_huizhang_3);
        final View[] huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
        final TextView[] huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        final ImageView[] huiZhangImgs = new ImageView[]{ivHuiZhang1,ivHuiZhang2,ivHuiZhang3};
        Observable.range(0,3)
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        huiZhangTexts[i].setVisibility(View.INVISIBLE);
                        huiZhangRoots[i].setVisibility(View.INVISIBLE);
                        huiZhangImgs[i].setVisibility(View.INVISIBLE);
                    }
                });
        if(entity.getBadgeList().size() == 0){
            mHuiZhangRoot.setVisibility(View.GONE);
            if(uuid.equals(PreferenceUtils.getUUid())){
                mAllHuizhangRoot.setVisibility(View.VISIBLE);
            }
        }else {
            mHuiZhangRoot.setVisibility(View.VISIBLE);
        }
        int len = 3;
        if(entity.getBadgeList().size() < 3){
            len = entity.getBadgeList().size();
        }
        for (int i = 0;i < len;i++){
            huiZhangTexts[i].setVisibility(View.VISIBLE);
            huiZhangRoots[i].setVisibility(View.VISIBLE);
            huiZhangImgs[i].setVisibility(View.VISIBLE);
            BadgeEntity badgeEntity = entity.getBadgeList().get(i);
            TextView tv = huiZhangTexts[i];
            tv.setText(badgeEntity.getTitle());
            tv.setBackgroundResource(R.drawable.bg_badge_cover);
            int px = (int)getResources().getDimension(R.dimen.x8);
            tv.setPadding(px,0,px,0);
            int radius1 = (int)getResources().getDimension(R.dimen.y4);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape2 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable2 = new ShapeDrawable();
            shapeDrawable2.setShape(roundRectShape2);
            shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
            huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
            Glide.with(getActivity())
                    .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU + badgeEntity.getImg(), (int)getResources().getDimension(R.dimen.y90), (int)getResources().getDimension(R.dimen.y90), false, false))
                    .override((int)getResources().getDimension(R.dimen.y90), (int)getResources().getDimension(R.dimen.y90))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(huiZhangImgs[i]);
        }

        //folder
        mFolderRoot.setVisibility(View.VISIBLE);
        if(entity.getFolderList().size() > 0){
            mFolderAddRoot.setVisibility(View.VISIBLE);
            for (int n = 0;n < entity.getFolderList().size();n++){
                final ShowFolderEntity item = entity.getFolderList().get(n);
                View v = LayoutInflater.from(getContext()).inflate(R.layout.item_bag_cover, null);
                ImageView iv = v.findViewById(R.id.iv_cover);
                TextView mark = v.findViewById(R.id.tv_mark);
                TextView title = v.findViewById(R.id.tv_title);
                TextView tag = v.findViewById(R.id.tv_tag);
                title.setText(item.getFolderName());
                String tagStr = "";
                for(int i = 0;i < item.getTexts().size();i++){
                    String tagTmp = item.getTexts().get(i);
                    if(i == 0){
                        tagStr = tagTmp;
                    }else {
                        tagStr += " · " + tagTmp;
                    }
                }
                tag.setText(tagStr);

                if(item.getType().equals("ZH")){
                    mark.setText("综合");
                    mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(item.getType().equals("TJ")){
                    mark.setText("图集");
                    mark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(item.getType().equals("MH")){
                    mark.setText("漫画");
                    mark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(item.getType().equals("XS")){
                    mark.setText("小说");
                    mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }else if(item.getType().equals("WZ")){
                    mark.setText("文章");
                    mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }
                int width = (DensityUtil.getScreenWidth(getContext()) - (int)getResources().getDimension(R.dimen.x84)) / 3;
                int height = (int)getResources().getDimension(R.dimen.y280);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
                RecyclerView.LayoutParams lp2;
                if(n == 1 || n == 2){
                    lp2 = new RecyclerView.LayoutParams(width + (int)getResources().getDimension(R.dimen.x18),height);
                    v.setPadding((int)getResources().getDimension(R.dimen.x18),0,0,0);
                }else {
                    lp2 = new RecyclerView.LayoutParams(width,height);
                    v.setPadding(0,0,0,0);
                }
                v.setLayoutParams(lp2);
                iv.setLayoutParams(lp);
                Glide.with(getContext())
                        .load(StringUtils.getUrl(getContext(),item.getCover(),width,height, false, true))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(getContext(),width,height),new RoundedCornersTransformation(getContext(),(int)getResources().getDimension(R.dimen.y8),0))
                        .into(iv);
                v.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(item.getType().equals("ZH")){
                            NewFileCommonActivity.startActivity(getContext(), FolderType.ZH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals("TJ")){
                            NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals("MH")){
                            NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals("XS")){
                            NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),item.getFolderId(),item.getCreateUser());
                        }
                    }
                });
                mFolderAddRoot.addView(v);
            }
        }else {
            mFolderAddRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLikeSuccess(boolean isLike) {
        entity.setLike(isLike);
        if(isLike){
            entity.setPicLikes(entity.getPicLikes() + 1);
            entity.setPicAllLikes(entity.getPicAllLikes() + 1);
        }else {
            entity.setPicLikes(entity.getPicLikes() - 1);
            entity.setPicAllLikes(entity.getPicAllLikes() - 1);
        }
        mTvMapLikeNum.setText(entity.getPicLikes() + " 喜欢");
        mTvHistorySocre.setText("(历史总分: " + entity.getPicAllLikes() + ")");
        mIvFavoriteMap.setSelected(entity.isLike());
    }
}
