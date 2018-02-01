package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerPhoneMsgListComponent;
import com.moemoe.lalala.di.modules.PhoneMsgListModule;
import com.moemoe.lalala.event.GroupMsgChangeEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.GroupNoticeEntity;
import com.moemoe.lalala.presenter.PhoneMsgListContract;
import com.moemoe.lalala.presenter.PhoneMsgListPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.adapter.PhoneMsgListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class PhoneMsgListV2Fragment extends BaseFragment implements IPhoneFragment,PhoneMsgListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    PhoneMsgListPresenter mPresenter;
    private PhoneMsgListAdapter mAdapter;
    private boolean isLoading = false;

    public static PhoneMsgListV2Fragment newInstance(){
        return new PhoneMsgListV2Fragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneMsgListComponent.builder()
                .phoneMsgListModule(new PhoneMsgListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        PreferenceUtils.setGroupDotNum(getContext(),0);
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mAdapter= new PhoneMsgListAdapter(this);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int pos = mAdapter.getShowPosition();
                if(pos == position){
                    mAdapter.setShowPosition(-1);
                }else {
                    if(mAdapter.getItem(position).getState()){
                        mAdapter.setShowPosition(position);
                    }else {
                        mAdapter.setShowPosition(-1);
                    }
                    if(pos != -1){
                        mAdapter.notifyItemChanged(pos);
                    }
                }
                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadMsgList(mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadMsgList(0);
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
        mPresenter.loadMsgList(0);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onLoadMsgListSuccess(ArrayList<GroupNoticeEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
        int i = 0;
        for (GroupNoticeEntity entity : entities){
            if(entity.isState()){
                i++;
            }
        }
        PreferenceUtils.setGroupDotNum(getContext(),PreferenceUtils.getGroupDotNum(getContext()) + i);
        EventBus.getDefault().post(new GroupMsgChangeEvent());
    }

    @Override
    public void onResponseSuccess(int position) {
        mAdapter.getItem(position).setState(false);
        mAdapter.setShowPosition(-1);
        mAdapter.notifyItemChanged(position);
        PreferenceUtils.setGroupDotNum(getContext(),PreferenceUtils.getGroupDotNum(getContext()) - 1);
        EventBus.getDefault().post(new GroupMsgChangeEvent());
        ToastUtils.showShortToast(getContext(),"操作成功");
    }

    public void responseNotice(boolean res,String id,int position){
        mPresenter.responseNotice(res,id,position);
    }

    @Override
    public int getMenu() {
        return 0;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onMenuClick() {

    }

    @Override
    public String getTitle() {
        return "群通知";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }
}
