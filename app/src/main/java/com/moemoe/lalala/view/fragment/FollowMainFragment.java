package com.moemoe.lalala.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerFollowMainComponent;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.components.DaggerTrashListComponent;
import com.moemoe.lalala.di.modules.FollowMainModule;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.di.modules.TrashListModule;
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.presenter.FollowMainContract;
import com.moemoe.lalala.presenter.FollowMainPresenter;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.BagEditActivity;
import com.moemoe.lalala.view.adapter.FollowMainAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/12/15.
 */

public class FollowMainFragment extends BaseFragment  implements FollowMainContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    FollowMainPresenter mPresenter;
    private FollowMainAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static FollowMainFragment newInstance(){
        FollowMainFragment fragment = new FollowMainFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFollowMainComponent.builder()
                .followMainModule(new FollowMainModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.ll_not_show).setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new FollowMainAdapter(null);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewDocListEntity entity = mAdapter.getItem(position);
                String schema = "";
                if("DOC".equals(entity.getDetail().getType())){
                    NewDocListEntity.Doc doc = (NewDocListEntity.Doc) entity.getDetail().getTrueData();
                    schema = doc.getSchema();
                }else if("FOLLOW_DEPARTMENT".equals(entity.getDetail().getType())){
                    NewDocListEntity.FollowDepartment doc = (NewDocListEntity.FollowDepartment) entity.getDetail().getTrueData();
                    schema = doc.getSchema();
                }
                if (!TextUtils.isEmpty(schema)) {
                    if(schema.contains(getString(R.string.label_doc_path)) && !schema.contains("uuid")){
                        String begin = schema.substring(0,schema.indexOf("?") + 1);
                        String uuid = schema.substring(schema.indexOf("?") + 1);
                        schema = begin + "uuid=" + uuid + "&from_name=关注";
                    }
                    Uri uri = Uri.parse(schema);
                    IntentUtils.toActivityFromUri(getActivity(), uri,view);
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
                mPresenter.loadFollowList(mAdapter.getItem(mAdapter.getItemCount() - 1).getTime(),false,false);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadFollowList(0,false,true);
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
        mPresenter.loadFollowList(0,false,true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }

    public void changeLabelAdapter(){
        mPresenter.loadFollowList(0,true,true);
    }

    @Override
    public void onLoadFollowListSuccess(ArrayList<NewDocListEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
            mListDocs.setLoadMoreEnabled(false);
        }
        Gson gson = new Gson();
        for (NewDocListEntity detail : entities){
            if(detail.getDetail().getType().equals("DOC")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.Doc.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLDER")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowFolder.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_COMMENT")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowComment.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLLOW")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowUser.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_DEPARTMENT")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowDepartment.class));
            }
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }

    @Override
    public void onChangeListSuccess(ArrayList<NewDocListEntity> entities) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
        }
        Gson gson = new Gson();
        for (NewDocListEntity detail : entities){
            if(detail.getDetail().getType().equals("DOC")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.Doc.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLDER")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowFolder.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_COMMENT")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowComment.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLLOW")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowUser.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_DEPARTMENT")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowDepartment.class));
            }
        }
        mAdapter.setList(entities);
    }
}
