package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedFollowOtherComponent;
import com.moemoe.lalala.di.modules.FeedFollowOtherModule;
import com.moemoe.lalala.model.entity.FeedFollowType2Entity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.presenter.FeedFollowOtherContract;
import com.moemoe.lalala.presenter.FeedFollowOtherPresenter;
import com.moemoe.lalala.utils.BoldSpan;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.AdminListActivity;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * Created by yi on 2018/1/16.
 */

public class FeedFollowOtherFragment extends BaseFragment implements FeedFollowOtherContract.View {

    @BindView(R.id.tv_content_num)
    TextView mTvFileNum;
    @BindView(R.id.tv_discuss_num)
    TextView mTvDocNum;

    @Inject
    FeedFollowOtherPresenter mPresenter;

    private FeedFollowOther1Fragment mItem1Fragment;
    private FeedFollowOther2Fragment mItem2Fragment;
    private String id;

    public static FeedFollowOtherFragment newInstance(String id,String title){
        FeedFollowOtherFragment fragment = new FeedFollowOtherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        bundle.putString("title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_feed_follow_other;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedFollowOtherComponent.builder()
                .feedFollowOtherModule(new FeedFollowOtherModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        id = getArguments().getString("uuid");
        mPresenter.loadData(id);
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadListSuccess(final FeedFollowType2Entity entity) {
        int w = DensityUtil.getScreenWidth(getContext());
        int h = getResources().getDimensionPixelSize(R.dimen.y300);
        Glide.with(getContext())
                .load(StringUtils.getUrl(getContext(),entity.getBg(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(getContext(),w,h)
                        ,new BlurTransformation(getContext(),10,4)
                        ,new ColorFilterTransformation(getContext(), ContextCompat.getColor(getContext(),R.color.alpha_20)))
                .into((ImageView) $(R.id.iv_bg));

        int size = getResources().getDimensionPixelSize(R.dimen.y128);
        Glide.with(getContext())
                .load(StringUtils.getUrl(getContext(),entity.getBg(),size,size,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new RoundedCornersTransformation(getContext(),getResources().getDimensionPixelSize(R.dimen.y16),0))
                .into((ImageView) $(R.id.iv_cover));

        ((TextView)$(R.id.tv_title)).setText(entity.getTitle());
        ((TextView)$(R.id.tv_desc)).setText(entity.getContent());

        if(entity.getAdmins().size() > 0){
            int adminSize = getResources().getDimensionPixelSize(R.dimen.y44);
            Glide.with(getContext())
                    .load(StringUtils.getUrl(getContext(),entity.getAdmins().get(0).getHeadPath(),adminSize,adminSize,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into((ImageView) $(R.id.iv_admin_avatar));
            ((TextView)$(R.id.tv_admin)).setText("管理员(" + entity.getAdmins().size() +") >");
        }else {
            $(R.id.fl_admin_avatar_root).setVisibility(View.GONE);
            ((TextView)$(R.id.tv_admin)).setText("管理员(0) >");
        }
        $(R.id.ll_admin_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                AdminListActivity.startActivity(getContext(),entity.getAdmins(),id);
            }
        });

        final String fileNum = "内容 " + entity.getFileNums();
        BoldSpan span = new BoldSpan(ContextCompat.getColor(getContext(),R.color.white));
        SpannableStringBuilder style = new SpannableStringBuilder(fileNum);
        style.setSpan(span, fileNum.indexOf("内容"), fileNum.indexOf("内容") + "内容".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvFileNum.setText(style);
        mTvFileNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItem1Fragment.isHidden()){
                    FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
                    mFragmentTransaction.show(mItem1Fragment).hide(mItem2Fragment);
                    mFragmentTransaction.commit();
                    mTvFileNum.setCompoundDrawablesWithIntrinsicBounds(null,null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_trends_tag_switch));
                    mTvFileNum.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.y4));
                    mTvFileNum.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
                    mTvDocNum.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                    mTvDocNum.setCompoundDrawablePadding(0);
                    mTvDocNum.setGravity(Gravity.CENTER);
                }
            }
        });

        String docNum = "讨论区 " + entity.getDocNums();
        BoldSpan span1 = new BoldSpan(ContextCompat.getColor(getContext(),R.color.white));
        SpannableStringBuilder style1 = new SpannableStringBuilder(docNum);
        style1.setSpan(span1, docNum.indexOf("讨论区"), docNum.indexOf("讨论区") + "讨论区".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvDocNum.setText(style1);
        mTvDocNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItem2Fragment.isHidden()){
                    FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
                    mFragmentTransaction.show(mItem2Fragment).hide(mItem1Fragment);
                    mFragmentTransaction.commit();
                    mTvDocNum.setCompoundDrawablesWithIntrinsicBounds(null,null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_trends_tag_switch));
                    mTvDocNum.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.y4));
                    mTvDocNum.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
                    mTvFileNum.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                    mTvFileNum.setCompoundDrawablePadding(0);
                    mTvFileNum.setGravity(Gravity.CENTER);
                }
            }
        });

        boolean isAdmin = false;
        for(UserTopEntity entity1 : entity.getAdmins()){
            if(PreferenceUtils.getUUid().equals(entity1.getUserId())){
                isAdmin = true;
                break;
            }
        }

        mItem1Fragment = FeedFollowOther1Fragment.newInstance(id,isAdmin);
        mItem2Fragment = FeedFollowOther2Fragment.newInstance(id,getArguments().getString("title"));

        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.fl_container,mItem1Fragment);
        mFragmentTransaction.add(R.id.fl_container,mItem2Fragment);
        mFragmentTransaction.show(mItem1Fragment).hide(mItem2Fragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void release() {
        if(mPresenter != null){
            mPresenter.release();
        }
    }
}
