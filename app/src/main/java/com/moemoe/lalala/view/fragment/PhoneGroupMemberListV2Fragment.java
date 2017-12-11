package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneMemberComponent;
import com.moemoe.lalala.di.modules.PhoneGroupMemberModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.GroupMemberDelEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.presenter.PhoneGroupMemberContract;
import com.moemoe.lalala.presenter.PhoneGroupMemberPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.PhoneGroupMemberListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 手机群组界面
 * Created by yi on 2017/9/4.
 */

public class PhoneGroupMemberListV2Fragment extends BaseFragment implements IPhoneFragment,PhoneGroupMemberContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    PhoneGroupMemberPresenter mPresenter;
    private PhoneGroupMemberListAdapter mAdapter;
    private boolean isLoading = false;
    private boolean isCheck;
    private SparseArray<String> selectMap;
    private String groupId;
    private boolean isOwn;
    private int menu;

    public void setMenu(int menu) {
        this.menu = menu;
    }

    public static PhoneGroupMemberListV2Fragment newInstance(String groupId, String ownId, boolean isOwn){
        PhoneGroupMemberListV2Fragment fragment = new PhoneGroupMemberListV2Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("groupId",groupId);
        bundle.putString("ownId",ownId);
        bundle.putBoolean("isOwn",isOwn);
        if(isOwn){
            fragment.setMenu(R.drawable.btn_menu_normal);
        }else {
            fragment.setMenu(0);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneMemberComponent.builder()
                .phoneGroupMemberModule(new PhoneGroupMemberModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        selectMap = new SparseArray<>();
        isOwn= getArguments().getBoolean("isOwn");
        groupId = getArguments().getString("groupId");
        String ownId = getArguments().getString("ownId");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mAdapter= new PhoneGroupMemberListAdapter(ownId);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String userId = mAdapter.getItem(position).getUserId();
                if(isCheck){
                    if(userId.equals(PreferenceUtils.getUUid())){
                        ToastUtils.showShortToast(getContext(),"不能删除自己");
                    }else {
                        if(selectMap.indexOfValue(userId) != -1){
                            selectMap.remove(position);
                        }else {
                            selectMap.put(position,userId);
                        }
                        mAdapter.getItem(position).setCheck(selectMap.indexOfValue(userId) != -1);
                        mAdapter.notifyItemChanged(position);
                    }
                }else {
                    ViewUtils.toPersonal(getContext(),userId);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mListDocs.setPullCallback(new PullCallback() {

            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadMemberList(groupId,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadMemberList(groupId,0);
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

        mPresenter.loadMemberList(groupId,0);
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
    public String getTitle() {
        return "全部成员";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return menu;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        if(isCheck){
            ((PhoneMainV2Activity)getContext()).setTitle("全部成员");
            ((PhoneMainV2Activity)getContext()).setMenu(R.drawable.btn_menu_normal);
            mAdapter.setCheck(false);
            mAdapter.notifyDataSetChanged();
            isCheck = false;
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onMenuClick() {
        if(isOwn){
            if(isCheck){
                if(selectMap.size() != 0){
                    GroupMemberDelEntity entity = new GroupMemberDelEntity();
                    entity.groupId = groupId;
                    ArrayList<String> userIds = new ArrayList<>();
                    for(int i = 0;i < selectMap.size();i++){
                        userIds.add(selectMap.valueAt(i));
                    }
                    entity.users = userIds;
                    mPresenter.delMembers(entity);
                }
            }else {
                isCheck = true;
                ((PhoneMainV2Activity)getContext()).setTitle("删除群成员");
                ((PhoneMainV2Activity)getContext()).setMenu(R.drawable.btn_alarm_save);
                mAdapter.setCheck(true);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoadMemberListSuccess(ArrayList<UserTopEntity> entities, boolean isPull) {
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
    }

    @Override
    public void onDelMembersSuccess() {
        ArrayList<UserTopEntity> delPos = new ArrayList<>();
        for(int i = 0;i < selectMap.size();i++){
            int pos = selectMap.keyAt(i);
            delPos.add(mAdapter.getItem(pos));
        }
        mAdapter.getList().removeAll(delPos);
        mAdapter.notifyDataSetChanged();
    }
}
