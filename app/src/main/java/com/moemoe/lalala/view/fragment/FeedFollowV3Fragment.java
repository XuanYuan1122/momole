package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedFollowComponent;
import com.moemoe.lalala.di.modules.FeedFollowModule;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.presenter.FeedFollowContract;
import com.moemoe.lalala.presenter.FeedFollowPresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.FeedTagSelectActivity;
import com.moemoe.lalala.view.activity.LoginActivity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.adapter.TagAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.FlowLayoutManager;
import com.moemoe.lalala.view.widget.view.KiraTabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_LOGIN;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_SELECT_TAG;
import static com.moemoe.lalala.view.activity.LoginActivity.RESPONSE_LOGIN_SUCCESS;

/**
 *  Feed流关注页
 * Created by yi on 2018/1/11.
 */

public class FeedFollowV3Fragment extends BaseFragment implements FeedFollowContract.View{

    @BindView(R.id.tab_layout)
    KiraTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.ll_top_root)
    View mLlTop;
    @BindView(R.id.rv_all_tag)
    RecyclerView mRvTags;
    @BindView(R.id.view_alpha)
    View mAlpha;

    View mLogin;

    @Inject
    FeedFollowPresenter mPresenter;

    private TabFragmentPagerAdapter mAdapter;
    private TagAdapter tagAdapter;
    private int mPreItem;
    private ArrayList<UserFollowTagEntity> mUserTags = new ArrayList<>();

    public static FeedFollowV3Fragment newInstance(){
        return new FeedFollowV3Fragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_feed_follow_v3;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedFollowComponent.builder()
                .feedFollowModule(new FeedFollowModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        if(PreferenceUtils.isLogin()){
            mPresenter.loadUserTags();
        }else {
            mLlTop.setVisibility(View.GONE);
            ViewStub stub = rootView.findViewById(R.id.stub_login);
            View view = stub.inflate();
            mLogin = view.findViewById(R.id.iv_to_login);
            mLogin.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(i,REQ_LOGIN);
                }
            });
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mUserTags.get(mPreItem).setSelect(false);
                tagAdapter.notifyItemChanged(mPreItem);
                mUserTags.get(position).setSelect(true);
                tagAdapter.notifyItemChanged(position);
                mPreItem = position;
                mRvTags.setVisibility(View.GONE);
                mAlpha.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_LOGIN && resultCode == RESPONSE_LOGIN_SUCCESS){
            mPresenter.loadUserTags();
        }else if(requestCode == REQ_SELECT_TAG && resultCode == RESULT_OK){
            mPresenter.loadUserTags();
        }
    }

    @OnClick({R.id.iv_show_all_follow_tag,R.id.iv_add_follow_tag,R.id.view_alpha})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_add_follow_tag:
                Intent i = new Intent(getContext(), FeedTagSelectActivity.class);
                startActivityForResult(i,REQ_SELECT_TAG);
                break;
            case R.id.iv_show_all_follow_tag:
                if(mRvTags.getVisibility() == View.VISIBLE){
                    mRvTags.setVisibility(View.GONE);
                    mAlpha.setVisibility(View.GONE);
                }else {
                    mRvTags.setVisibility(View.VISIBLE);
                    mAlpha.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.view_alpha:
                if(mRvTags.getVisibility() == View.VISIBLE){
                    mRvTags.setVisibility(View.GONE);
                    mAlpha.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadUserTagsSuccess(final ArrayList<UserFollowTagEntity> entities) {
        mUserTags.clear();
        mUserTags.addAll(entities);
        mLlTop.setVisibility(View.VISIBLE);
        if(mLogin != null){
            mLogin.setVisibility(View.GONE);
        }
        List<String> titles = new ArrayList<>();
        List<BaseFragment> fragmentList = new ArrayList<>();
        for(UserFollowTagEntity entity : mUserTags){
            titles.add(entity.getText());
            if("all".equals(entity.getId())){
                fragmentList.add(FeedFollowAllFragment.newInstance());
            }else {
                fragmentList.add(FeedFollowOtherFragment.newInstance(entity.getId(),entity.getText()));
            }
        }
        if(mAdapter == null){
            mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(),fragmentList,titles);
        }else {
            mAdapter.setFragments(getChildFragmentManager(),fragmentList,titles);
        }

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mViewPager);

//        View view = mTabLayout.getTabView(0);
//        TextView tv = view.findViewById(R.id.tv_tab_msg);
//        tv.setVisibility(View.VISIBLE);
//        tv.setText("400");
        if(mUserTags.size() > 0){
            mUserTags.get(0).setSelect(true);
            mPreItem = 0;
        }
        tagAdapter = null;
        tagAdapter = new TagAdapter();
        tagAdapter.setShowClose(false);
        tagAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position != mPreItem){
                    mViewPager.setCurrentItem(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvTags.setLayoutManager(new FlowLayoutManager());
        mRvTags.setAdapter(tagAdapter);
        tagAdapter.setList(mUserTags);
    }

    @Override
    public void release() {
        if(mPresenter != null){
            mPresenter.release();
        }
        if(mAdapter != null){
            mAdapter.release();
        }
    }

}
