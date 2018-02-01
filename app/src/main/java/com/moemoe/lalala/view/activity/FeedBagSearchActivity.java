package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerRecommendTagComponent;
import com.moemoe.lalala.di.modules.RecommendTagModule;
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.model.entity.RecommendTagEntity;
import com.moemoe.lalala.presenter.RecommendTagContract;
import com.moemoe.lalala.presenter.RecommendTagPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StartDecoration;
import com.moemoe.lalala.utils.TopDecoration;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.HotTagAdapter;
import com.moemoe.lalala.view.adapter.RecommendTagAdapter;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.FeedBagSearchFragment;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.FlowLayoutManager;
import com.moemoe.lalala.view.widget.view.KiraTabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 * Created by yi on 2018/1/31.
 */

public class FeedBagSearchActivity extends BaseAppCompatActivity implements RecommendTagContract.View {

    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.ll_hot_root)
    View mHotRoot;
    @BindView(R.id.list_2)
    RecyclerView mList2;
    @BindView(R.id.ll_bag_root)
    View mBagRoot;
    @BindView(R.id.tab_layout)
    KiraTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Inject
    RecommendTagPresenter mPresenter;

    private HotTagAdapter mHotAdapter;
    private RecommendTagAdapter mAdapter;
    private TabFragmentPagerAdapter mTabAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_feed_bag_search;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerRecommendTagComponent.builder()
                .recommendTagModule(new RecommendTagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);

        mList.setLayoutManager(new FlowLayoutManager());
        mList.addItemDecoration(new TopDecoration(getResources().getDimensionPixelSize(R.dimen.y24)));
        mList.addItemDecoration(new StartDecoration(getResources().getDimensionPixelSize(R.dimen.x24)));
        mHotAdapter = new HotTagAdapter();
        mList.setAdapter(mHotAdapter);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if(!TextUtils.isEmpty(content)){
                    mPresenter.loadKeyWordTag(content);
                }else {
                    mHotRoot.setVisibility(View.VISIBLE);
                }
            }
        });
        mTvCancel.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mList2.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecommendTagAdapter();
        mList2.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mList2.setVisibility(View.GONE);
                mBagRoot.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new SearchChangedEvent(mAdapter.getItem(position).getWord()));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPresenter.loadRecommendTag("bag");
        List<String> titles = new ArrayList<>();
        titles.add("合集");
        titles.add("视频");
        titles.add("音乐");
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(FeedBagSearchFragment.newInstance("FOLDER"));
        fragmentList.add(FeedBagSearchFragment.newInstance("MOVIE"));
        fragmentList.add(FeedBagSearchFragment.newInstance("MUSIC"));
        mTabAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setViewPager(mViewPager);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_clear})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_clear:
                mEtSearch.setText("");
                mHotRoot.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadRecommendTagSuccess(ArrayList<RecommendTagEntity> entities) {
        if(entities.size() > 0){
            mList.setVisibility(View.VISIBLE);
            mHotAdapter.setList(entities);
        }
    }

    @Override
    public void onLoadKeyWordTagSuccess(ArrayList<RecommendTagEntity> entities) {
        mHotRoot.setVisibility(View.GONE);
        mList2.setVisibility(View.VISIBLE);
        mBagRoot.setVisibility(View.GONE);
        mAdapter.setList(entities);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        if(mTabAdapter != null) mTabAdapter.release();
        super.onDestroy();
    }
}
