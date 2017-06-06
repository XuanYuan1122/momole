package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBadgeComponent;
import com.moemoe.lalala.di.modules.BadgeModule;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.presenter.BadgeContract;
import com.moemoe.lalala.presenter.BadgePresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/1/11.
 */

public class BadgeActivity extends BaseAppCompatActivity implements BadgeContract.View{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    @BindView(R.id.rv_list)
    PullAndLoadView mRvList;
    @BindView(R.id.tv_all_badge)
    TextView mTvAll;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;

    @Inject
    BadgePresenter mPresenter;
    private PersonListAdapter mAdapter;
    private PersonListAdapter mAllAdapter;
    private boolean isLoading = false;
    private int mCurType;//0 my 1 all

    @Override
    protected int getLayoutId() {
        return R.layout.ac_select_normal;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerBadgeComponent.builder()
                .badgeModule(new BadgeModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTvTitle.setText("我的徽章");
        mTvDone.setText("保存");
        mTvDone.setVisibility(View.VISIBLE);
        mTvAll.setVisibility(View.VISIBLE);
        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,5);
        mRvList.getRecyclerView().setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.setLoadMoreEnabled(false);
        mCurType = 0;

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    public void onBackPressed() {
        if(mCurType == 0){
            super.onBackPressed();
        }else if(mCurType == 1){
            mCurType = 0;
            mTvAll.setVisibility(View.VISIBLE);
            mTvTitle.setText("我的徽章");
            mTvDone.setVisibility(View.VISIBLE);
            mRvList.getRecyclerView().setAdapter(mAdapter);
        }
    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mCurType == 0){
                    finish();
                }else if(mCurType == 1){
                    mCurType = 0;
                    mTvAll.setVisibility(View.VISIBLE);
                    mTvTitle.setText("我的徽章");
                    mTvDone.setVisibility(View.VISIBLE);
                    mRvList.getRecyclerView().setAdapter(mAdapter);
                }
            }
        });
        mTvAll.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mCurType = 1;
                mTvAll.setVisibility(View.GONE);
                mTvTitle.setText("全部徽章");
                mTvDone.setVisibility(View.GONE);
                if(mAllAdapter == null){
                    mAllAdapter = new PersonListAdapter(BadgeActivity.this,6);
                    mPresenter.requestAllBadge(0);
                }
                mRvList.getRecyclerView().setAdapter(mAllAdapter);
            }
        });
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                HashMap<Integer,BadgeEntity> hashMap = mAdapter.getCurSelectNum();
                ArrayList<String> ids = new ArrayList<>();
                for(int i = 1;i <= hashMap.size();i++){
                    BadgeEntity set = hashMap.get(i);
                    ids.add(set.getId());
                }
                hashMap.values();
                mPresenter.saveBadge(ids);
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BadgeEntity entity = (BadgeEntity) mAdapter.getItem(position);
                if(entity.getRank() > 0){
                    mAdapter.decreaseSelectNum(entity.getRank());
                    entity.setRank(0);
                    mAdapter.notifyItemChanged(position);
                }else if(mAdapter.getSelectNum() < 3){
                    entity.setRank(mAdapter.increaseSelectNum());
                    mAdapter.notifyItemChanged(position);
                }else {
                    showToast("最多只能选3个啦");
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if(mCurType == 0){
                    mPresenter.requestMyBadge(mAdapter.getItemCount());
                }else if(mCurType == 1){
                    mPresenter.requestAllBadge(mAllAdapter.getItemCount());
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                if(mCurType == 0){
                    mPresenter.requestMyBadge(0);
                }else if(mCurType == 1){
                    mPresenter.requestAllBadge(0);
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.requestMyBadge(0);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mRvList.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void loadMyBadgeSuccess(ArrayList<BadgeEntity> entities, boolean pull) {
        isLoading = false;
        mRvList.setComplete();
        if(entities.size() == 0){
            mRvList.setLoadMoreEnabled(false);
        }else {
            mRvList.setLoadMoreEnabled(true);
        }
        if(pull){
            mAdapter.setData(entities);
        }else {
            mAdapter.addData(entities);
        }
    }

    @Override
    public void loadAllBadgeSuccess(ArrayList<BadgeEntity> entities, boolean pull) {
        isLoading = false;
        mRvList.setComplete();
        if(entities.size() == 0){
            mRvList.setLoadMoreEnabled(false);
        }else {
            mRvList.setLoadMoreEnabled(true);
        }
        if(pull){
            mAllAdapter.setData(entities);
        }else {
            mAllAdapter.addData(entities);
        }
    }

    @Override
    public void saveSuccess() {
        Intent i = new Intent();
        ArrayList<BadgeEntity> entities = new ArrayList<>();
        HashMap<Integer,BadgeEntity> hashMap = mAdapter.getCurSelectNum();
//        for (BadgeEntity set : hashMap.keySet()){
//           // BadgeEntity entity = (BadgeEntity) mAdapter.getItem(set);
//            entities.add(set);
//        }
        for(int n = 1;n <= hashMap.size();n++){
            entities.add(hashMap.get(n));
        }
        i.putParcelableArrayListExtra("list",entities);
        setResult(RESULT_OK,i);
        finish();
    }

    public void buyBadge(final int position, final String id){
        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
        alertDialogUtil.createNoticeDialog(this,"提示","确认购买");
        alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                alertDialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                mPresenter.buyBadge(id,position);
                alertDialogUtil.dismissDialog();
            }
        });
        alertDialogUtil.showDialog();

    }

    @Override
    public void buySuccess(int position) {
        showToast("购买成功");
        BadgeEntity entity = (BadgeEntity) mAllAdapter.getItem(position);
        entity.setHave(true);
        entity.setRank(0);
        mAllAdapter.notifyItemChanged(position);
        mAdapter.addData(0,entity);
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }
}
