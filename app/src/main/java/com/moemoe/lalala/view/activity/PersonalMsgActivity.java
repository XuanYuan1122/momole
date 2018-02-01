package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.model.entity.ReplyEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2017/9/26.
 */

public class PersonalMsgActivity extends BaseAppCompatActivity  implements PersonalListContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @Inject
    PersonaListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    public static void startActivity(Context context,String id){
        Intent i = new Intent(context,PersonalMsgActivity.class);
        i.putExtra(UUID,id);
        context.startActivity(i);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String id = getIntent().getStringExtra(UUID);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(PersonalMsgActivity.this,2);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(PersonalMsgActivity.this));
        mListDocs.setLoadMoreEnabled(false);
        PreferenceUtils.setNormalMsgDotNum(PersonalMsgActivity.this,0);
        PreferenceUtils.setMessageDot(PersonalMsgActivity.this,"normal",false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position > 2){
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
                                    IntentUtils.toActivityFromUri(PersonalMsgActivity.this, uri,view);
                                }
                            }else {
                                if (!TextUtils.isEmpty(bean.getSchema())){
                                    Intent i = new Intent(PersonalMsgActivity.this, CommentDetailActivity.class);
                                    i.putExtra("schema",bean.getSchema());
                                    i.putExtra("commentId",bean.getCommentId());
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }else {
                    Intent i = new Intent(PersonalMsgActivity.this, NewsDetailActivity.class);
                    if(position == 0){
                        i.putExtra("tab","user");//系统通知
                        PreferenceUtils.setMessageDot(PersonalMsgActivity.this,"system",false);
                        PreferenceUtils.setSysMsgDotNum(PersonalMsgActivity.this,0);
                        mAdapter.notifyItemChanged(0);
                    }else if(position == 2){
                        i.putExtra("tab","system");//官方通知
                        PreferenceUtils.setMessageDot(PersonalMsgActivity.this,"neta",false);
                        PreferenceUtils.setNetaMsgDotNum(PersonalMsgActivity.this,0);
                        mAdapter.notifyItemChanged(2);
                    }else {
                        i.putExtra("tab","at_user");//@通知
                        PreferenceUtils.setMessageDot(PersonalMsgActivity.this,"at_user",false);
                        PreferenceUtils.setAtUserMsgDotNum(PersonalMsgActivity.this,0);
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
        EventBus.getDefault().register(this);
        mPresenter.doRequest(id,0,4);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText("消息");
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void systemMsgEvent(SystemMessageEvent event){
        if(event.getType().equals("neta")){
            mAdapter.notifyItemChanged(2);
        }else if(event.getType().equals("system")){
            mAdapter.notifyItemChanged(0);
        }else if(event.getType().equals("at_user")){
            mAdapter.notifyItemChanged(1);
        }
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
}
