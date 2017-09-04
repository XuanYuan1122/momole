package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBagDynamicComponent;
import com.moemoe.lalala.di.modules.BagDynamicModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DynamicEntity;
import com.moemoe.lalala.model.entity.DynamicTopEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.presenter.BagDynamicContract;
import com.moemoe.lalala.presenter.BagDynamicPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DynamicDecoration;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.adapter.DynamicAdapter;
import com.moemoe.lalala.view.adapter.DynamicTopAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.view.AutoPollRecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/15.
 */

public class DynamicFragment extends BaseFragment implements BagDynamicContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.ll_not_show)
    View mLlShow;
    @Inject
    BagDynamicPresenter mPresenter;
    private DynamicAdapter mAdapter;
    private DynamicTopAdapter mTopAdapter;
    private AutoPollRecyclerView autoPollRecyclerView;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static DynamicFragment newInstance(){
        DynamicFragment fragment = new DynamicFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerBagDynamicComponent.builder()
                .bagDynamicModule(new BagDynamicModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.setVisibility(View.VISIBLE);
        mLlShow.setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new DynamicAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.getRecyclerView().setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
        GridLayoutManager manager = new GridLayoutManager(getContext(),2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if(i == 0){
                    return 2;
                }else {
                    return 1;
                }
            }
        });
        mListDocs.setLayoutManager(manager);
        mListDocs.getRecyclerView().addItemDecoration(new DynamicDecoration(DensityUtil.dip2px(getContext(),4)));
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadList(mAdapter.getList().get(mAdapter.getList().size() - 1).getLastTime());
            }

            @Override
            public void onRefresh() {

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
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DynamicEntity entity = mAdapter.getItem(position);
                if(entity.getFolderType().equals(FolderType.ZH.toString())){
                    NewFileCommonActivity.startActivity(getContext(), FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getFolderType().equals(FolderType.TJ.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getFolderType().equals(FolderType.MH.toString())){
                    NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getFolderType().equals(FolderType.XS.toString())){
                    NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        View top = LayoutInflater.from(getContext()).inflate(R.layout.item_dynamic_top, null);
        autoPollRecyclerView = (AutoPollRecyclerView) top.findViewById(R.id.top_list);
        autoPollRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        autoPollRecyclerView.addItemDecoration(new MenuVItemDecoration(DensityUtil.dip2px(getContext(),8)));
        mTopAdapter = new DynamicTopAdapter();
        autoPollRecyclerView.setAdapter(mTopAdapter);
        mTopAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int lastPosition = ((LinearLayoutManager)autoPollRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if(lastPosition < mTopAdapter.getList().size() - 1){
                    mTopAdapter.setSize(Integer.MAX_VALUE);
                    autoPollRecyclerView.start();
                }
            }
        });
        mAdapter.addHeaderView(top);
        TextView tv = new TextView(getContext());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
        tv.getPaint().setFakeBoldText(true);
        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.black_1e1e1e));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = DensityUtil.dip2px(getContext(),18);
        lp.leftMargin = DensityUtil.dip2px(getContext(),12);
        tv.setLayoutParams(lp);
        tv.setText("收藏动态");
        mAdapter.addHeaderView(tv);
        mPresenter.loadTop();
        mPresenter.loadList(0);
    }


    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onLoadTopSuccess(ArrayList<DynamicTopEntity> entities) {
        mTopAdapter.setSize(entities.size());
        mTopAdapter.setList(entities);
    }

    @Override
    public void onLoadListSuccess(ArrayList<DynamicEntity> entities) {
        isLoading = false;
        mListDocs.setComplete();
        mAdapter.addList(entities);
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }
    }
}
