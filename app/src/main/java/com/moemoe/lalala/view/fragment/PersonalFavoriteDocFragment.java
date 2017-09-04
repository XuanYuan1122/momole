package com.moemoe.lalala.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalFavoriteDocFragment extends BaseFragment  implements PersonalListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.ll_not_show)
    View mLlShow;
    @BindView(R.id.tv_secret_not_show)
    TextView mTvNotShow;
    @Inject
    PersonaListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static PersonalFavoriteDocFragment newInstance(String id,boolean isShow){
        PersonalFavoriteDocFragment fragment = new PersonalFavoriteDocFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        bundle.putBoolean("show",isShow);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String id = getArguments().getString("uuid");
        boolean show = getArguments().getBoolean("show");
        if(show){
            mListDocs.setVisibility(View.VISIBLE);
            mLlShow.setVisibility(View.GONE);
            mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
            mAdapter = new PersonListAdapter(getContext(),0);
            mListDocs.getRecyclerView().setAdapter(mAdapter);
            mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
            mListDocs.setLoadMoreEnabled(false);
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Object object = mAdapter.getItem(position);
                    if (object != null) {
                        if (object instanceof PersonDocEntity) {
                            PersonDocEntity bean = (PersonDocEntity) object;
                            if (!TextUtils.isEmpty(bean.getSchema())) {
                                String mSchema = bean.getSchema();
                                if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                                    String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                                    String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                                    mSchema = begin + "uuid=" + uuid + "&from_name=个人中心-收藏";
                                }
                                Uri uri = Uri.parse(mSchema);
                                IntentUtils.toActivityFromUri(getActivity(), uri,view);
                            }
                        }
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
                    mPresenter.doRequest(id,mAdapter.getItemCount(),2);
                }

                @Override
                public void onRefresh() {
                    isLoading = true;
                    mPresenter.doRequest(id,0,2);
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
            mPresenter.doRequest(id,0,2);
        }else {
            mListDocs.setVisibility(View.GONE);
            mLlShow.setVisibility(View.VISIBLE);
            mTvNotShow.setText("隐私是很重要的\n" + "别人不让看就别深究吧…");
        }
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onSuccess(Object o,boolean isPull) {
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
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }
}
