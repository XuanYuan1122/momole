package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLuntanComponent;
import com.moemoe.lalala.di.modules.LuntanModule;
import com.moemoe.lalala.model.entity.LuntanTabEntity;
import com.moemoe.lalala.presenter.LuntanContract;
import com.moemoe.lalala.presenter.LuntanPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.LuntanFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/30.
 */

public class LuntanActivity extends BaseAppCompatActivity implements LuntanContract.View {

    private final String EXTRA_NAME = "name";

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tab_layout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.pager_person_data)
    ViewPager mPager;

    @Inject
    LuntanPresenter mPresenter;

    private TabFragmentPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_luntan;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        clickEvent("luntan");
        mTitle.setText("论坛");
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        DaggerLuntanComponent.builder()
                .luntanModule(new LuntanModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mPresenter.loadTabList();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        if(mAdapter != null) mAdapter.release();
        stayEvent("luntan");
        super.onDestroy();

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
    public void onFailure(int code,String msg) {
        ErrorCodeUtils.showErrorMsgByCode(LuntanActivity.this,code,msg);
    }

    @Override
    public void onLoadTabListSuccess(ArrayList<LuntanTabEntity> entities) {
        List<BaseFragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for(LuntanTabEntity entity : entities){
            fragmentList.add(LuntanFragment.newInstance(entity.getId(),entity.getName(),entity.isCanDoc()));
            titles.add(entity.getName());
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mPager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mPager);
    }
}
