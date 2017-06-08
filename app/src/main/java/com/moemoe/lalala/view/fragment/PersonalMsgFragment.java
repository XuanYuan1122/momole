package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.model.entity.ReplyEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.CommentDetailActivity;
import com.moemoe.lalala.view.activity.NewsDetailActivity;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

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

public class PersonalMsgFragment extends BaseFragment implements PersonalListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    PersonaListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static PersonalMsgFragment newInstance(String id){
        PersonalMsgFragment fragment = new PersonalMsgFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        mPresenter.release();
        super.onDestroyView();
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
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(getContext(),2);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position > 1){
                    Object object = mAdapter.getItem(position - 3);
                    if (object != null) {
                        if (object instanceof ReplyEntity) {
                            ReplyEntity bean = (ReplyEntity) object;
                            if(TextUtils.isEmpty(bean.getCommentId())){
                                if (!TextUtils.isEmpty(bean.getSchema())) {
                                    String mSchema = bean.getSchema();
                                    if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                                        String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                                        String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                                        mSchema = begin + "uuid=" + uuid + "&from_name=个人中心-消息";
                                    }
                                    Uri uri = Uri.parse(mSchema);
                                    IntentUtils.toActivityFromUri(getActivity(), uri,view);
                                }
                            }else {
                                if (!TextUtils.isEmpty(bean.getSchema())){
                                    Intent i = new Intent(getContext(), CommentDetailActivity.class);
                                    i.putExtra("schema",bean.getSchema());
                                    i.putExtra("commentId",bean.getCommentId());
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }else {
                    Intent i = new Intent(getContext(), NewsDetailActivity.class);
                    if(position == 0){
                        i.putExtra("tab","user");//系统通知
                        PreferenceUtils.setMessageDot(getContext(),"system",false);
                        mAdapter.notifyItemChanged(0);
                    }else if(position == 1){
                        i.putExtra("tab","system");//官方通知
                        PreferenceUtils.setMessageDot(getContext(),"neta",false);
                        mAdapter.notifyItemChanged(2);
                    }else {
                        i.putExtra("tab","at_user");//@通知
                        PreferenceUtils.setMessageDot(getContext(),"at_user",false);
                        mAdapter.notifyItemChanged(1);
                    }
                    startActivity(i);
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
                mPresenter.doRequest(id,mAdapter.getItemCount() - 2,4);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.doRequest(id,0,4);
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
        subscribeEvent();
        mPresenter.doRequest(id,0,4);
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

    private void subscribeEvent() {
        Subscription sysSubscription = RxBus.getInstance()
                .toObservable(SystemMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<SystemMessageEvent>() {
                    @Override
                    public void call(SystemMessageEvent event) {
                        if(event.getType().equals("neta")){
                            mAdapter.notifyItemChanged(2);
                        }else if(event.getType().equals("system")){
                            mAdapter.notifyItemChanged(0);
                        }else if(event.getType().equals("at_user")){
                            mAdapter.notifyItemChanged(1);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, sysSubscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }
}
