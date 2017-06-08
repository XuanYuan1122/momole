package com.moemoe.lalala.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.model.entity.NetaMsgEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/1.
 */

public class NewsDetailActivity extends BaseAppCompatActivity implements PersonalListContract.View{

    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;

    @Inject
    PersonaListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;
    private String mType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_select_normal;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = getIntent().getStringExtra("tab");
        if(TextUtils.isEmpty(mType)){
            finish();
            return;
        }
        mTvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        if(mType.equals("user")){
            mTvTitle.setText("系统通知");
        }else if(mType.equals("system")){
            mTvTitle.setText("Neta官方");
        }else if(mType.equals("at_user")){
            mTvTitle.setText("@我的");
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
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
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,9);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NetaMsgEntity entity = (NetaMsgEntity) mAdapter.getItem(position);
                if(entity != null && !TextUtils.isEmpty(entity.getSchema())){
                    String mSchema = entity.getSchema();
                    if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                        String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                        String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                        mSchema = begin + "uuid=" + uuid + "&from_name=" + mTvTitle.getText().toString();
                    }
                    Uri uri = Uri.parse(mSchema);
                    IntentUtils.toActivityFromUri(NewsDetailActivity.this, uri,view);
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
                mPresenter.doRequest(mType,mAdapter.getItemCount(),6);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.doRequest(mType,0,6);
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
        mPresenter.doRequest(mType,0,6);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onSuccess(Object o, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(((ArrayList<Object>) o).size() == 0){
            mListDocs.setLoadMoreEnabled(false);
        }else {
            mListDocs.setLoadMoreEnabled(true);
        }
        if(isPull){
            mAdapter.setData((ArrayList<Object>) o);
        }else {
            mAdapter.addData((ArrayList<Object>) o);
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
