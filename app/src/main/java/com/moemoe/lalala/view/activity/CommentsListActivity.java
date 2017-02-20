package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerCommentListComponent;
import com.moemoe.lalala.di.modules.CommentListModule;
import com.moemoe.lalala.model.entity.CommentListSendEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.presenter.CommentListContract;
import com.moemoe.lalala.presenter.CommentListPresenter;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/29.
 */

public class CommentsListActivity extends BaseAppCompatActivity  implements CommentListContract.View{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;

    @Inject
    CommentListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean mIsLoading = false;
    private String uuid;
    private String mToUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_comments_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerCommentListComponent.builder()
                .commentListModule(new CommentListModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        uuid = getIntent().getStringExtra(UUID);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.label_liuyan,0));
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,4);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mEdtCommentInput.setHint("输入留言");
        mTvSendComment.setEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mTvSendComment.setEnabled(false);
                } else {
                    mTvSendComment.setEnabled(true);
                }

            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewCommentEntity bean = (NewCommentEntity) mAdapter.getItem(position);
                reply(bean);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    String temp = mEdtCommentInput.getText().toString();
                    if(TextUtils.isEmpty(temp)){
                        mToUserId = "";
                        mEdtCommentInput.setHint(R.string.a_hint_input_comment);
                    }
                }
            }
        });
        mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.doRequest(mAdapter.getItemCount(),uuid);
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.doRequest(0,uuid);
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.doRequest(0,uuid);
    }

    public void reply(NewCommentEntity bean){
        mToUserId = bean.getFromUserId();
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("回复 " + bean.getFromUserName() + ": ");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
    }

    private void sendComment() {
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            String content = mEdtCommentInput.getText().toString();
            if(TextUtils.isEmpty(content)){
                showToast(R.string.msg_doc_comment_not_empty);
                return;
            }
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            createDialog();
            CommentListSendEntity bean = new CommentListSendEntity(content,mToUserId, uuid);
            mPresenter.sendComment(bean);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        mIsLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onSuccess(ArrayList<NewCommentEntity> entities,boolean pull) {
        finalizeDialog();
        mIsLoading = false;
        mListDocs.setComplete();
        if(entities.size() == 0){
            mListDocs.isLoadMoreEnabled(false);
        }else {
            mListDocs.isLoadMoreEnabled(true);
        }
        if(pull){
            mAdapter.setData(entities);
        }else {
            mAdapter.addData(entities);
        }
        if(mAdapter.getItemCount() > 0){
            NewCommentEntity entity = (NewCommentEntity) mAdapter.getItem(0);
            mTvTitle.setText(getString(R.string.label_liuyan,entity.getIdx()));
        }
    }

    @Override
    public void onSendSuccess() {
        finalizeDialog();
        mPresenter.doRequest(0,uuid);
        mToUserId = "";
        mEdtCommentInput.setHint(R.string.a_hint_input_comment);
        mEdtCommentInput.setText("");
    }
}
