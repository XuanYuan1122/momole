package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerDepartComponent;
import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.CalendarDayType;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.presenter.DepartContract;
import com.moemoe.lalala.presenter.DepartPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SpacesItemDecoration;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.adapter.DepartmentListAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class DepartmentActivity extends BaseAppCompatActivity implements DepartContract.View {

    private final String EXTRA_NAME = "name";

    @BindView(R.id.rl_bar)
    View mRlRoot;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    DepartPresenter mPresenter;
    private DepartmentListAdapter mListAdapter;
    private String mRoomId;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_pulltorefresh_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        mRoomId = "";
        if(i != null){
            String roomId = i.getStringExtra(UUID);
            if(!TextUtils.isEmpty(roomId)){
                mRoomId = roomId;
            }
            String title = i.getStringExtra(EXTRA_NAME);
            if(!TextUtils.isEmpty(title)){
                mTitle.setText(title);
                mTitle.setVisibility(View.VISIBLE);
            }else {
                mTitle.setVisibility(View.GONE);
            }
        }
        DaggerDepartComponent.builder()
                .departModule(new DepartModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mRlRoot.setVisibility(View.VISIBLE);
        mRlRoot.getBackground().mutate().setAlpha(0);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListAdapter = new DepartmentListAdapter(this);
        mListDocs.getRecyclerView().setAdapter(mListAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mListDocs.setLayoutManager(layoutManager);
        mListDocs.getRecyclerView().addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(this,9)));
        mListDocs.isLoadMoreEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mListAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof DepartmentEntity.DepartmentDoc) {
                        DepartmentEntity.DepartmentDoc bean = (DepartmentEntity.DepartmentDoc) object;
                        if (!TextUtils.isEmpty(bean.getSchema())) {
                            Uri uri = Uri.parse(bean.getSchema());
                            IntentUtils.toActivityFromUri(DepartmentActivity.this, uri,view);
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        ((GridLayoutManager)mListDocs.getRecyclerView().getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position > 1) {
                    String[] uis = ((DepartmentEntity.DepartmentDoc)mListAdapter.getItem(position)).getUi().split("#");
                    int type = CalendarDayType.getType(uis[0]);
                    if (type == CalendarDayType.valueOf(CalendarDayType.DOC_G_2)) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return 2;
                }
            }
        });
        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            int curY = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing()) Glide.with(DepartmentActivity.this).resumeRequests();
                } else {
                    if(!isFinishing())Glide.with(DepartmentActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                curY += dy;
                toolBarAlpha(curY);
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.requestDocList(mListAdapter.getItemCount() - 2,mRoomId,0);
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.requestBannerData(mRoomId);
                mPresenter.requestFeatured(mRoomId);
                mPresenter.requestDocList(0,mRoomId,0);
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
        mPresenter.requestBannerData(mRoomId);
        mPresenter.requestFeatured(mRoomId);
        mPresenter.requestDocList(0,mRoomId,0);
    }

    public void toolBarAlpha(int curY) {
        int startOffset = 0;
        int endOffset = mRlRoot.getHeight();
        if (Math.abs(curY) <= startOffset) {
            mRlRoot.getBackground().mutate().setAlpha(0);
            mTitle.setTextColor(Color.argb(0, 255, 255, 255));
        } else if (Math.abs(curY) > startOffset && Math.abs(curY) < endOffset) {
            float precent = (float) (Math.abs(curY) - startOffset) / endOffset;
            int alpha = Math.round(precent * 255);
            mRlRoot.getBackground().mutate().setAlpha(alpha);
            mTitle.setTextColor(Color.argb(alpha, 255, 255, 255));
        } else if (Math.abs(curY) >= endOffset) {
            mRlRoot.getBackground().mutate().setAlpha(255);
            mTitle.setTextColor(Color.argb(255, 255, 255, 255));
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListAdapter !=null )mListAdapter.onDestroy();
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
    }

    @Override
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {
        mListAdapter.setBanner(bannerEntities);
    }

    @Override
    public void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities) {
        mListAdapter.setFeaturedBeans(featuredEntities);
    }

    @Override
    public void onDocLoadSuccess(Object entity,boolean pull) {
        mIsLoading = false;
        if(((DepartmentEntity)entity).getList().size() == 0){
            mListDocs.isLoadMoreEnabled(false);
            ToastUtils.showCenter(this,getString(R.string.msg_all_load_down));
        }else {
            mListDocs.isLoadMoreEnabled(true);
        }
        mListDocs.setComplete();
        if(pull){
            mListAdapter.setDocList(((DepartmentEntity)entity).getList());
        }else {
            mListAdapter.addDocList(((DepartmentEntity)entity).getList());
        }
    }

    @Override
    public void onChangeSuccess(Object entity) {

    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(DepartmentActivity.this,code,msg);
    }
}