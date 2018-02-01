package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedFollowOther2Component;
import com.moemoe.lalala.di.modules.FeedFollowOther2Module;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.presenter.FeedFollowOther2Contract;
import com.moemoe.lalala.presenter.FeedFollowOther2Presenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.adapter.OldDocAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class FeedFollowOther2Fragment extends BaseFragment implements FeedFollowOther2Contract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_to_wen)
    ImageView mCreateDoc;

    @Inject
    FeedFollowOther2Presenter mPresenter;
    private OldDocAdapter mAdapter;
    private boolean isLoading;
    private String id;

    public static FeedFollowOther2Fragment newInstance(String id,String name){
        FeedFollowOther2Fragment fragment = new FeedFollowOther2Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("name",name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedFollowOther2Component.builder()
                .feedFollowOther2Module(new FeedFollowOther2Module(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        id = getArguments().getString("id");
        final String name = getArguments().getString("name");
        mCreateDoc.setImageResource(R.drawable.btn_send_wen);
        mCreateDoc.setVisibility(View.VISIBLE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mAdapter = new OldDocAdapter();
        mListDocs.getRecyclerView().addItemDecoration(new MenuVItemDecoration((int) getResources().getDimension(R.dimen.y24)));
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mCreateDoc.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(getContext(), CreateRichDocActivity.class);
                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,4);
                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,name);
                intent.putExtra("departmentId",id);
                intent.putExtra("from_name","");
                intent.putExtra("from_schema","");
                startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DocResponse docResponse = mAdapter.getItem(position);
                Intent i = new Intent(getContext(),NewDocDetailActivity.class);
                i.putExtra("uuid",docResponse.getId());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadList(id,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadList(id,0);
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
        isLoading = true;
        mPresenter.loadList(id,0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CREATE_DOC && resultCode == RESULT_OK){
            mPresenter.loadList(id,0);
        }
    }

    @Override
    public void onLoadListSuccess(ArrayList<DocResponse> responses, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        mListDocs.setLoadMoreEnabled(true);
        if(isPull){
            mAdapter.setList(responses);
        }else {
            mAdapter.addList(responses);
        }
    }
}
