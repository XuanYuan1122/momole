//package com.moemoe.lalala.view.activity;
//
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.view.View;
//import android.widget.TextView;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.di.components.DaggerSnowComponent;
//import com.moemoe.lalala.di.modules.SnowModule;
//import com.moemoe.lalala.model.entity.SnowEntity;
//import com.moemoe.lalala.model.entity.SnowInfo;
//import com.moemoe.lalala.presenter.SnowContract;
//import com.moemoe.lalala.presenter.SnowPresenter;
//import com.moemoe.lalala.utils.ErrorCodeUtils;
//import com.moemoe.lalala.view.adapter.DonationAdapter;
//import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
//import com.moemoe.lalala.view.widget.recycler.PullCallback;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
///**
// * Created by yi on 2016/12/20.
// */
//
//public class SnowActivity extends BaseAppCompatActivity implements SnowContract.View{
//
//    @BindView(R.id.tv_snow_num)
//    TextView mTvSnowNum;
//    @BindView(R.id.tv_rank)
//    TextView mTvRank;
//    @BindView(R.id.rl_snow_main)
//    View mRlMain;
//    @BindView(R.id.ll_snow_rank)
//    View mLlRank;
//    @BindView(R.id.pl_list)
//    PullAndLoadView mRvList;
//    @BindView(R.id.tv_snow_my_num)
//    TextView mTvMyNum;
//    @BindView(R.id.tv_snow_my_rank)
//    TextView mTvMyRank;
//    private DonationAdapter mAdapter;
//    @Inject
//    SnowPresenter mPresenter;
//    private boolean mIsLoading = false;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ac_snow;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        DaggerSnowComponent.builder()
//                .snowModule(new SnowModule(this))
//                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
//                .build()
//                .inject(this);
//        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
//        mAdapter = new DonationAdapter(this,1);
//        mRvList.getRecyclerView().setAdapter(mAdapter);
//        mRvList.setLayoutManager(new LinearLayoutManager(this));
//        mRvList.setLoadMoreEnabled(false);
//    }
//
//    @Override
//    protected void initToolbar(Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        mPresenter.release();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void initListeners() {
//        mRvList.setPullCallback(new PullCallback() {
//            @Override
//            public void onLoadMore() {
//                mIsLoading = true;
//                mPresenter.requestSnowRankInfo(mAdapter.getItemCount());
//            }
//
//            @Override
//            public void onRefresh() {
//                mIsLoading = true;
//                mPresenter.requestSnowRankInfo(0);
//            }
//
//            @Override
//            public boolean isLoading() {
//                return mIsLoading;
//            }
//
//            @Override
//            public boolean hasLoadedAllItems() {
//                return false;
//            }
//        });
//    }
//
//    @Override
//    protected void initData() {
//        mPresenter.requestSnowInfo();
//    }
//
//    @OnClick({R.id.iv_close,R.id.btn_rank,R.id.tv_exit})
//    public void onClick(View v){
//        switch (v.getId()){
//            case R.id.iv_close:
//                finish();
//                break;
//            case R.id.btn_rank:
//                mRlMain.setVisibility(View.GONE);
//                mLlRank.setVisibility(View.VISIBLE);
//                mPresenter.requestSnowRankInfo(0);
//                break;
//            case R.id.tv_exit:
//                mRlMain.setVisibility(View.VISIBLE);
//                mLlRank.setVisibility(View.GONE);
//                break;
//        }
//    }
//
//    @Override
//    public void updateSnowView(SnowEntity entity) {
//        mTvSnowNum.setText(getString(R.string.label_my_snow_num,entity.getSnowNum()));
//        if(entity.getSnowNum() > 0){
//            mTvRank.setVisibility(View.VISIBLE);
//            mTvRank.setText(getString(R.string.label_snow_rank,entity.getRankNum()));
//        }else {
//            mTvRank.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void updateSnowList(SnowInfo entity, boolean pull) {
//        mIsLoading = false;
//        mRvList.setComplete();
//        mRvList.setLoadMoreEnabled(true);
//        mTvMyNum.setText(getString(R.string.label_snow_my_num, entity.getMyNumber()));
//        if (entity.getMyNumber() == 0) {
//            mTvMyRank.setVisibility(View.GONE);
//        } else {
//            mTvMyRank.setVisibility(View.VISIBLE);
//            mTvMyRank.setText(getString(R.string.label_snow_my_rank, entity.getMyRank()));
//        }
//        if (pull){
//            mAdapter.setRankList(entity.getRankList());
//        }else {
//            mAdapter.addRankList(entity.getRankList());
//        }
//    }
//
//    @Override
//    public void onFailure(int code, String msg) {
//        mIsLoading = false;
//        mRvList.setComplete();
//        ErrorCodeUtils.showErrorMsgByCode(SnowActivity.this,code,msg);
//    }
//}
