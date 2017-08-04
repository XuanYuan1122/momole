package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBagComponent;
import com.moemoe.lalala.di.modules.BagModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.presenter.BagContract;
import com.moemoe.lalala.presenter.BagPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GridItemDecoration;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.BagAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/1/17.
 */

public class BagActivity extends BaseAppCompatActivity implements BagContract.View,AppBarLayout.OnOffsetChangedListener{

    private static final int REQ_GET_BAG = 30001;
    private static final int REQ_CREATE_FOLDER = 30002;
    private static final int REQ_TO_FOLDER = 30003;
    private static final int REQ_MODIFY_BAG = 30004;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.stub_open_bag)
    ViewStub mStubOpenBag;
    @BindView(R.id.tv_use_space)
    TextView mTvSpaceNum;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mTitleView;
    @BindView(R.id.iv_background)
    ImageView mIvBg;
    @BindView(R.id.rv_bag)
    PullAndLoadView mRv;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;

    @Inject
    BagPresenter mPresenter;

    private RelativeLayout mRlHaveRoot;
    private View mBack;
    private View mOpenBag;
    private BagAdapter mAdapter;
    private BottomMenuFragment bottomFragment;
    private String mUserId;
    private boolean mIsLoading = false;
    private String mBg;
    private String mBagName;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bag;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerBagComponent.builder()
                .bagModule(new BagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
//        ImmersionBar.with(this)
//                .titleBar(mToolbar)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(),null);
        mUserId = getIntent().getStringExtra(UUID);
        mTitleView.setCollapsedTitleTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTitleView.setExpandedTitleColor(ContextCompat.getColor(this,R.color.white));
        if(mUserId.equals(PreferenceUtils.getUUid())){
            if(PreferenceUtils.getAuthorInfo().isOpenBag()){
                mTvSpaceNum.setText("");
                mTitleView.setTitle("");
                init();
                initPopupMenus();
            }else {
                mRlHaveRoot = (RelativeLayout) mStubOpenBag.inflate();
                mBack = mRlHaveRoot.findViewById(R.id.iv_back);
                mOpenBag = mRlHaveRoot.findViewById(R.id.tv_get_bag);
            }
        }else {
            mTvSpaceNum.setText("");
            mTitleView.setTitle("");
            init();
            initPopupMenus();
        }
    }

    private void initPopupMenus() {
        bottomFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        if(mUserId.equals(PreferenceUtils.getUUid())){
            MenuItem item = new MenuItem(0, getString(R.string.label_setting));
            items.add(item);

            item = new MenuItem(1, getString(R.string.label_bag_buy_list));
            items.add(item);

            item = new MenuItem(2, getString(R.string.label_bag_follow_list));
            items.add(item);

            item = new MenuItem(3, getString(R.string.label_add_space));
            items.add(item);
        }else {
            // 举报
            MenuItem item = new MenuItem(4, getString(R.string.label_jubao));
            items.add(item);
        }

        bottomFragment.setShowTop(false);
        bottomFragment.setMenuItems(items);
        bottomFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent i = new Intent(BagActivity.this,BagEditActivity.class);
                    i.putExtra("bg",mBg);
                    i.putExtra("name",mBagName);
                    i.putExtra("read_type","");
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_BAG_MODIFY);
                    startActivityForResult(i,REQ_MODIFY_BAG);
                }else if(itemId == 1){
                    Intent i = new Intent(BagActivity.this,BagBuyActivity.class);
                    startActivity(i);
                }else if(itemId == 2){
                    Intent i = new Intent(BagActivity.this,BagFollowActivity.class);
                    startActivity(i);
                }else if (itemId == 3) {
                    String temp = "neta://com.moemoe.lalala/url_inner_1.0?http://www.moemoe.la/shubao/?token=" + PreferenceUtils.getToken();
                    Uri uri = Uri.parse(temp);
                    IntentUtils.toActivityFromUri(BagActivity.this, uri, null);
                }else if (itemId == 4) {
                    Intent intent = new Intent(BagActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, "");
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, "");
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE,3);
                    intent.putExtra(JuBaoActivity.UUID,mUserId);
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.BAG.toString());
                    startActivity(intent);
                }
            }
        });
    }

    private void init(){
        mRv.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new BagAdapter(this,mUserId.equals(PreferenceUtils.getUUid()),0);
        mRv.getRecyclerView().setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRv.setLayoutManager(layoutManager);
        mRv.getRecyclerView().addItemDecoration(new GridItemDecoration(DensityUtil.dip2px(this,10)));
        mRv.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BagDirEntity entity = (BagDirEntity) mAdapter.getItem(position);
                if(entity == null){
                    Intent i = new Intent(BagActivity.this,BagEditActivity.class);
                    i.putExtra("bg","");
                    i.putExtra("read_type","");
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_DIR_CREATE);
                    startActivityForResult(i,REQ_CREATE_FOLDER);
                }else {
                    Intent i = new Intent(BagActivity.this,FolderActivity.class);
                    i.putExtra("info",entity);
                    i.putExtra("position",position);
                    i.putExtra(UUID,mUserId);
                    startActivityForResult(i,REQ_TO_FOLDER);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRv.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing()) Glide.with(BagActivity.this).resumeRequests();
                } else {
                    if(!isFinishing())Glide.with(BagActivity.this).pauseRequests();
                }
            }
        });
        mRv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.getFolderList(mUserId,mUserId.equals(PreferenceUtils.getUUid())?mAdapter.getItemCount() - 1:mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.getFolderList(mUserId,0);
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
        mPresenter.getBagInfo(mUserId);
        mPresenter.getFolderList(mUserId,0);
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initListeners() {
        if(mBack != null){
            mBack.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    finish();
                }
            });
        }
        if(mOpenBag != null){
            mOpenBag.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(BagActivity.this,BagEditActivity.class);
                    i.putExtra("bg","");
                    i.putExtra("read_type","");
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_BAG_OPEN);
                    startActivityForResult(i,REQ_GET_BAG);
                }
            });
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_BAG && resultCode == RESULT_OK){
            if(mRlHaveRoot != null) mRlHaveRoot.setVisibility(View.GONE);
            PreferenceUtils.getAuthorInfo().setOpenBag(true);
            mTvSpaceNum.setText(getString(R.string.label_bag_space,0,500));
            mTitleView.setTitle(data.getStringExtra("name"));
            Glide.with(this)
                    .load(data.getStringExtra("bg"))
                    .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                    .error(R.drawable.btn_cardbg_defbg)
                    .placeholder(R.drawable.btn_cardbg_defbg)
                    .into(mIvBg);
            init();
            initPopupMenus();
        }else if(requestCode == REQ_CREATE_FOLDER && resultCode == RESULT_OK){
            mPresenter.getFolderList(mUserId,0);
        }else if(requestCode == REQ_MODIFY_BAG && resultCode == RESULT_OK){
            mTitleView.setTitle(data.getStringExtra("name"));
            Glide.with(this)
                    .load(data.getStringExtra("bg"))
                    .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                    .error(R.drawable.btn_cardbg_defbg)
                    .placeholder(R.drawable.btn_cardbg_defbg)
                    .into(mIvBg);
            mBg = data.getStringExtra("bg");
        }else if(requestCode == REQ_TO_FOLDER && resultCode == RESULT_OK){
            boolean change = data.getBooleanExtra("change",false);
            if(change){
                mPresenter.getFolderList(mUserId,0);
            }
        }
    }

    @OnClick({R.id.iv_menu_list})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_menu_list:
                if (bottomFragment != null)
                bottomFragment.show(getSupportFragmentManager(),"BagMenu");
                break;
        }
    }

    @Override
    public void openOrModifyBagSuccess() {

    }

    @Override
    public void loadBagInfoSuccess(BagEntity entity) {
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mTvSpaceNum.setText(getString(R.string.label_bag_space,getSize(entity.getUseSize()),getSize(entity.getMaxSize())));
        }else {
            mTvSpaceNum.setText(entity.getUpdateTime() + " 更新");
        }
        mTitleView.setTitle(entity.getName());
        mBg = entity.getBg();
        mBagName = entity.getName();
        Glide.with(this)
                .load(StringUtils.getUrl(this, ApiService.URL_QINIU + entity.getBg(), DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200),false,true))
                .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                .error(R.drawable.btn_cardbg_defbg)
                .placeholder(R.drawable.btn_cardbg_defbg)
                .into(mIvBg);
    }

    @Override
    public void loadFolderListSuccess(ArrayList<BagDirEntity> entities, boolean isPull) {
        mIsLoading = false;
        mRv.setComplete();
        if(entities.size() == 0){
            mRv.setLoadMoreEnabled(false);
        }else {
            mRv.setLoadMoreEnabled(true);
        }
        if(isPull){
            mAdapter.setData(entities);
        }else {
            mAdapter.addData(entities);
        }
    }

    @Override
    public void createFolderSuccess() {

    }

    @Override
    public void uploadFolderSuccess() {

    }

    @Override
    public void loadFolderItemListSuccess(ArrayList<FileEntity> entities, boolean isPull) {

    }

    @Override
    public void onCheckSize(boolean isOk) {

    }

    @Override
    public void onBuyFolderSuccess() {

    }

    @Override
    public void deleteFolderSuccess() {

    }

    @Override
    public void modifyFolderSuccess() {

    }

    @Override
    public void onFollowOrUnFollowFolderSuccess(boolean follow) {

    }

    @Override
    public void onLoadFolderSuccess(BagDirEntity entity) {

    }

    @Override
    public void onLoadFolderFail() {

    }

    private int getSize(long size){
        return (int) (size/1024/1024);
    }

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mRv.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    private boolean isChanged = false;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int temp = (int) (DensityUtil.dip2px(this,146) - getResources().getDimension(R.dimen.status_bar_height));
        float percent = (float)Math.abs(verticalOffset) / temp;

        if(percent > 0.4){
            if(!isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_blue_normal);
                mIvMenu.setImageResource(R.drawable.btn_menu_normal);
                isChanged = true;
            }
            mToolbar.setAlpha(percent);
            mIvMenu.setAlpha(percent);
        }else {
            if(isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_white_normal);
                mIvMenu.setImageResource(R.drawable.btn_menu_white_normal);
                isChanged = false;
            }
            mToolbar.setAlpha(1 - percent);
            mIvMenu.setAlpha(1 - percent);
        }
    }
}
