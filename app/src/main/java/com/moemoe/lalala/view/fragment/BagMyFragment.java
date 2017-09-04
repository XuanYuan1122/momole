package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerBagMyComponent;
import com.moemoe.lalala.di.modules.BagMyModule;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.presenter.BagMyContract;
import com.moemoe.lalala.presenter.BagMyPresenter;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.adapter.BagCollectionTopAdapter;
import com.moemoe.lalala.view.adapter.BagMyAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_CREATE_FOLDER;

/**
 * Created by yi on 2016/12/15.
 */

public class BagMyFragment extends BaseFragment implements BagMyContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.ll_not_show)
    View mLlShow;
    @Inject
    BagMyPresenter mPresenter;
    private BagMyAdapter mAdapter;
    private View mTop;
    private BagCollectionTopAdapter mTopAdapter;
    private String type;
    private String userId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static BagMyFragment newInstance(String type,String userId){
        BagMyFragment fragment = new BagMyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putString("uuid",userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerBagMyComponent.builder()
                .bagMyModule(new BagMyModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        type = getArguments().getString("type");
        userId = getArguments().getString("uuid");
        mListDocs.setVisibility(View.VISIBLE);
        mLlShow.setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new BagMyAdapter(userId,type);
        mListDocs.getRecyclerView().setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListDocs.setLayoutManager(layoutManager);
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        if("collection".equals(type)){
            mTop = LayoutInflater.from(getContext()).inflate(R.layout.item_collection_top, null);
            RecyclerView rv = (RecyclerView) mTop.findViewById(R.id.rv_list);
            LinearLayoutManager m = new LinearLayoutManager(getContext());
            m.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv.setLayoutManager(m);
            mTopAdapter = new BagCollectionTopAdapter();
            rv.setAdapter(mTopAdapter);
        }
        mPresenter.loadContent(type,userId);
    }


    @Override
    public void onFailure(int code,String msg) {
    }

    @Override
    public void onLoadSuccess(ArrayList<BagMyShowEntity> entities) {
        if(type.equals("my")){
            mAdapter.setList(entities);
        }else {
            BagMyShowEntity shoucang = entities.get(0);
            mTopAdapter.setList(shoucang.getItems());
            entities.remove(0);
            mAdapter.setList(entities);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == REQUEST_CODE_CREATE_DOC || requestCode == REQ_CREATE_FOLDER) && resultCode == BaseAppCompatActivity.RESULT_OK){
            mPresenter.loadContent(type,userId);
        }
    }
}
