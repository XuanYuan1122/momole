package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerDepartComponent;
import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.CalendarDayType;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.presenter.DepartContract;
import com.moemoe.lalala.presenter.DepartPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SpacesItemDecoration;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
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

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @BindView(R.id.rl_tv_menu_root)
    View mTvMenuRoot;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;

    @Inject
    DepartPresenter mPresenter;
    private DepartmentListAdapter mListAdapter;
    private String mRoomId;
    private boolean mIsLoading = false;
    private int mIsFollow;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
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
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        DaggerDepartComponent.builder()
                .departModule(new DepartModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mIsFollow = -1;
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListAdapter = new DepartmentListAdapter(this);
        mListDocs.getRecyclerView().setAdapter(mListAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mListDocs.setLayoutManager(layoutManager);
        mListDocs.getRecyclerView().addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(this,9)));
        mListDocs.setLoadMoreEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(this,60),DensityUtil.dip2px(this,26));
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.rightMargin = DensityUtil.dip2px(this,18);
        mTvMenuRoot.setLayoutParams(lp);
        mTvMenuRoot.setBackgroundResource(R.drawable.shape_rect_border_main_no_background_3);
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.gravity = Gravity.CENTER;
        mTvMenu.setLayoutParams(lp1);
        mTvMenu.setVisibility(View.VISIBLE);
        mTvMenu.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvMenu.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mTvMenu.setCompoundDrawablesWithIntrinsicBounds (null,null,ContextCompat.getDrawable(this,R.drawable.ic_club_blue_follow),null);
        mTvMenu.setCompoundDrawablePadding(DensityUtil.dip2px(this,3));
        mTvMenu.setText("关注");
        mTvMenuRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(DialogUtils.checkLoginAndShowDlg(DepartmentActivity.this) && mIsFollow != -1){
                    mPresenter.followDepartment(mRoomId,mIsFollow == 0);
                }
            }
        });
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
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mListAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof DepartmentEntity.DepartmentDoc) {
                        DepartmentEntity.DepartmentDoc bean = (DepartmentEntity.DepartmentDoc) object;
                        if (!TextUtils.isEmpty(bean.getSchema())) {
                            String mSchema = bean.getSchema();
                            if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                                String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                                String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                                mSchema = begin + "uuid=" + uuid + "&from_name=" + mTitle.getText().toString();
                            }
                            Uri uri = Uri.parse(mSchema);
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

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing()) Glide.with(DepartmentActivity.this).resumeRequests();
                } else {
                    if(!isFinishing()) Glide.with(DepartmentActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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
        mPresenter.loadIsFollow(mRoomId);
        mPresenter.requestBannerData(mRoomId);
        mPresenter.requestFeatured(mRoomId);
        mPresenter.requestDocList(0,mRoomId,0);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        if(mListAdapter !=null )mListAdapter.onDestroy();
        super.onDestroy();

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
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(this,getString(R.string.msg_all_load_down));
        }else {
            mListDocs.setLoadMoreEnabled(true);
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
    public void onFollowDepartmentSuccess(boolean follow) {
        mIsFollow = follow ? 0 : 1;
        mTvMenu.setText(follow?"已关注":"关注");
        mTvMenu.setCompoundDrawablesWithIntrinsicBounds (null,
                null,
                ContextCompat.getDrawable(DepartmentActivity.this,follow?R.drawable.ic_club_blue_followed:R.drawable.ic_club_blue_follow),
                null);
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(DepartmentActivity.this,code,msg);
    }
}
