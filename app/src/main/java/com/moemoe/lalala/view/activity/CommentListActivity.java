package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCommentsListComponent;
import com.moemoe.lalala.di.modules.CommentsListModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.presenter.CommentsListContract;
import com.moemoe.lalala.presenter.CommentsListPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.CommentListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/22.
 */

public class CommentListActivity extends BaseAppCompatActivity implements CommentsListContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvFrom;
    @BindView(R.id.tv_sort)
    TextView mTvSort;
    @BindView(R.id.tv_to_comment)
    TextView mTvToComment;
    @BindView(R.id.rv_list)
    PullAndLoadView mRvList;
    @Inject
    CommentsListPresenter mPresenter;

    private CommentListAdapter mAdapter;
    private boolean mIsLoading;
    private String mDocId;
    private String mUserId;
    private boolean sortTime;
    private BottomMenuFragment fragment;

    public static void startActivity(Context context,String mDocId,String mUserId){
        Intent i = new Intent(context,CommentListActivity.class);
        i.putExtra(UUID,mDocId);
        i.putExtra("userId",mUserId);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_comment_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerCommentsListComponent.builder()
                .commentsListModule(new CommentsListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mDocId = getIntent().getStringExtra(UUID);
        mUserId = getIntent().getStringExtra("userId");
        fragment = new BottomMenuFragment();
        mRvList.setLoadMoreEnabled(false);
        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mRvList.getSwipeRefreshLayout().setEnabled(false);
        mAdapter = new CommentListAdapter(mDocId);
        mRvList.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showCommentMenu(mAdapter.getItem(position),position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvList.setLayoutManager(layoutManager);
        mRvList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.loadCommentsList(mDocId,sortTime,mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
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
        mTvToComment.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateCommentActivity.startActivity(CommentListActivity.this,mDocId,false,"");
            }
        });
        mTvSort.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                sortTime = !sortTime;
                mTvSort.setText(sortTime?"时间排序":"热门排序");
                mPresenter.loadCommentsList(mDocId,sortTime,0);
            }
        });
        mPresenter.loadCommentsList(mDocId,sortTime,0);
    }


    public void favoriteComment(String id,boolean isFavorite,int position){
        mPresenter.favoriteComment(mDocId,id,isFavorite,position);
    }

    private void showCommentMenu(final CommentV2Entity bean, final int position){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item;
        item = new MenuItem(0,getString(R.string.label_reply));
        items.add(item);

        item = new MenuItem(1,getString(R.string.label_copy_dust));
        items.add(item);

        item = new MenuItem(2,getString(R.string.label_jubao));
        items.add(item);

        if(TextUtils.equals(PreferenceUtils.getUUid(), bean.getCreateUser().getUserId()) ){
            item = new MenuItem(3,getString(R.string.label_delete));
            items.add(item);
        }else if( TextUtils.equals(PreferenceUtils.getUUid(), mUserId)){
            item = new MenuItem(4,getString(R.string.label_delete));
            items.add(item);
        }

        fragment.setShowTop(true);
        fragment.setTopContent(bean.getContent());
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 0) {
                    CreateCommentActivity.startActivity(CommentListActivity.this,bean.getCommentId(),true,bean.getCreateUser().getUserId());
                } else if (itemId == 2) {
                    Intent intent = new Intent(CommentListActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.getCreateUser().getUserName());
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.getContent());
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE, 4);
                    intent.putExtra(JuBaoActivity.UUID,bean.getCommentId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, "COMMENT");
                    startActivity(intent);
                } else if (itemId == 3) {
                    mPresenter.deleteComment(mDocId,bean.getCommentId(),position);
                }else if(itemId == 1){
                    String content = bean.getContent();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("回复内容", content);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(CommentListActivity.this, getString(R.string.label_level_copy_success));
                }else if(itemId == 4){
                    mPresenter.deleteComment(mDocId,bean.getCommentId(),position);
                }
            }
        });
        fragment.show(getSupportFragmentManager(),"DynamicComment");
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvFrom.setVisibility(View.VISIBLE);
        mTvFrom.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvFrom.setText("评论");
        mTvFrom.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadCommentsSuccess(ArrayList<CommentV2Entity> commentV2Entities, boolean isPull) {
        mIsLoading = false;
        mRvList.setComplete();
        if(commentV2Entities.size() >= ApiService.LENGHT){
            mRvList.setLoadMoreEnabled(true);
        }else {
            mRvList.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(commentV2Entities);
        }else {
            mAdapter.addList(commentV2Entities);
        }
    }

    @Override
    public void onDeleteCommentSuccess(int position) {
        showToast("删除评论成功");
        mAdapter.getList().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void favoriteCommentSuccess(boolean isFavorite, int position) {
        if(isFavorite){
            showToast("点赞成功");
        }else {
            showToast("取消点赞成功");
        }
        mAdapter.getList().get(position).setLike(isFavorite);
        if(isFavorite){
            mAdapter.getList().get(position).setLikes( mAdapter.getList().get(position).getLikes() + 1);
        }else {
            mAdapter.getList().get(position).setLikes( mAdapter.getList().get(position).getLikes() - 1);
        }
        mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount());
    }
}
