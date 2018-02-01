package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerDepartComponent;
import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.presenter.DepartContract;
import com.moemoe.lalala.presenter.DepartPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.DepartmentFragment;
import com.moemoe.lalala.view.fragment.LuntanFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/12/2.
 */

public class DepartmentV3Activity extends BaseAppCompatActivity implements DepartContract.View{

    private final String EXTRA_NAME = "name";
    @BindView(R.id.iv_back)
    ImageView IvBack;
    @BindView(R.id.pager_person_data)
    ViewPager mDataPager;
    @BindView(R.id.indicator_person_data)
    CommonTabLayout mPageIndicator;
    @BindView(R.id.rl_role_root)
    RelativeLayout mRoleRoot;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;

    private TabFragmentPagerAdapter mAdapter;

    @Inject
    DepartPresenter mPresenter;

    private String roomId;
    private int mIsFollow;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_wall;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        DaggerDepartComponent.builder()
                .departModule(new DepartModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        roomId = getIntent().getStringExtra(UUID);
        String title = getIntent().getStringExtra(EXTRA_NAME);
        clickEvent(roomId);
        mRoleRoot.setVisibility(View.GONE);
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(DepartmentFragment.newInstance(roomId,title));
        fragmentList.add(LuntanFragment.newInstance(roomId,title,true));
        List<String> titles = new ArrayList<>();
        titles.add(title);
        titles.add("讨论区");

        String[] mTitles = {title,"讨论区"};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (String mTitle : mTitles) {
            mTabEntities.add(new TabEntity(mTitle, R.drawable.ic_personal_bag, R.drawable.ic_personal_bag));
        }

        mPageIndicator.setTabWidth(76);
        mPageIndicator.setIndicatorWidth(56);

        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setTabData(mTabEntities);
        mPresenter.loadIsFollow(roomId);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setImageResource(R.drawable.btn_follow_department);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (DialogUtils.checkLoginAndShowDlg(DepartmentV3Activity.this) && mIsFollow != -1) {
                    mPresenter.followDepartment(roomId, mIsFollow == 0);
                }
            }
        });
    }

    @Override
    protected void initListeners() {
        IvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mPageIndicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mDataPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mDataPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicator.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.release();
        if(mAdapter != null) mAdapter.release();
        stayEvent(roomId);
        super.onDestroy();
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {

    }

    @Override
    public void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities) {

    }

    @Override
    public void onDocLoadSuccess(Object entity, boolean pull) {

    }

    @Override
    public void onChangeSuccess(Object entity) {

    }

    @Override
    public void onFollowDepartmentSuccess(boolean follow) {
        mIsFollow = follow ? 0 : 1;
        mIvMenu.setSelected(follow);
    }

    @Override
    public void onSubmissionSuccess() {

    }

    @Override
    public void onLoadGroupSuccess(ArrayList<DepartmentGroupEntity> entity) {

    }

    @Override
    public void onJoinSuccess(String id, String name) {

    }
}
