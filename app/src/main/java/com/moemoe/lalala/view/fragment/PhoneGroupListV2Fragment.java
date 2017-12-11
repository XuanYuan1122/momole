package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneGroupListComponent;
import com.moemoe.lalala.di.modules.PhoneGroupListModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.GroupEntity;
import com.moemoe.lalala.presenter.PhoneGroupListContract;
import com.moemoe.lalala.presenter.PhoneGroupListPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.PhoneGroupListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.rong.imlib.model.Conversation;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GROUP_DETAIL;

/**
 * 手机群组界面
 * Created by yi on 2017/9/4.
 */

public class PhoneGroupListV2Fragment extends BaseFragment implements IPhoneFragment, PhoneGroupListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    PhoneGroupListPresenter mPresenter;

    private PhoneGroupListAdapter mAdapter;
    private boolean isLoading = false;
    private boolean isUser;
    private String title;

    public static PhoneGroupListV2Fragment newInstance(boolean isUser,String title){
        PhoneGroupListV2Fragment fragment = new PhoneGroupListV2Fragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isUser",isUser);
        fragment.setTitle(title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setTitle(String title){
        this.title = title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneGroupListComponent.builder()
                .phoneGroupListModule(new PhoneGroupListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        isUser = getArguments().getBoolean("isUser");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mAdapter= new PhoneGroupListAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GroupEntity item = mAdapter.getItem(position);
                if(isUser){
                    Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                            .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName())
                            .appendQueryParameter("targetId", item.getId())
                            .appendQueryParameter("title", item.getGroupName()).build();
                    ((PhoneMainV2Activity)getContext()).toFragment(KiraConversationFragment.newInstance(item.getId(),item.getGroupName(),Conversation.ConversationType.GROUP.getName(),uri));
                }else {
                    ((PhoneMainV2Activity)getContext()).toFragment(PhoneGroupDetailV2Fragment.newInstance(item.getId()));
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
                mPresenter.loadGroupList(mAdapter.getItemCount(),isUser);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadGroupList(0,isUser);
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
        mPresenter.loadGroupList(0,isUser);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) mPresenter.release();
    }

    @Override
    public void onLoadGroupListSuccess(ArrayList<GroupEntity> entities, boolean isPull) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GROUP_DETAIL && resultCode == RESULT_OK){
            if(data != null){
                data.getBooleanExtra("is_quit",false);
                mPresenter.loadGroupList(0,isUser);
            }
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
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
}
