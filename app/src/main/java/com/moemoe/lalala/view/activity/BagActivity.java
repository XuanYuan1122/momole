package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
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
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.adapter.BagAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.menu.MenuItem;
import com.moemoe.lalala.view.widget.menu.PopupListMenu;
import com.moemoe.lalala.view.widget.menu.PopupMenuItems;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/1/17.
 */

public class BagActivity extends BaseAppCompatActivity implements BagContract.View{

    private static final int REQ_GET_BAG = 30001;
    private static final int REQ_CREATE_FOLDER = 30002;
    private static final int REQ_TO_FOLDER = 30003;
    private static final int REQ_MODIFY_BAG = 30004;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_is_bag)
    RelativeLayout mRlHaveRoot;
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
    private BagAdapter mAdapter;
    private PopupListMenu mMenu;
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
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mUserId = getIntent().getStringExtra(UUID);
        if(mUserId.equals(PreferenceUtils.getUUid())){
            if(PreferenceUtils.getAuthorInfo().isOpenBag()){
                mRlHaveRoot.setVisibility(View.GONE);
                mTvSpaceNum.setText("");
                mTitleView.setTitle("");
                init();
                initPopupMenus();
            }else {
                mRlHaveRoot.setVisibility(View.VISIBLE);
            }
        }else {
            mRlHaveRoot.setVisibility(View.GONE);
            mTvSpaceNum.setText("");
            mTitleView.setTitle("");
            init();
            initPopupMenus();
        }
    }

    private void initPopupMenus() {
        PopupMenuItems items = new PopupMenuItems(this);
        MenuItem item;
        if(mUserId.equals(PreferenceUtils.getUUid())){
            item = new MenuItem(0, getString(R.string.label_setting));
            items.addMenuItem(item);

            item = new MenuItem(1, getString(R.string.label_records));
            items.addMenuItem(item);

            item = new MenuItem(2, getString(R.string.label_add_space));
            items.addMenuItem(item);
        }else {
            // 举报
            item = new MenuItem(3, getString(R.string.label_jubao));
            items.addMenuItem(item);
        }

        mMenu = new PopupListMenu(this, items);
        mMenu.setMenuItemClickListener(new PopupListMenu.MenuItemClickListener() {

            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent i = new Intent(BagActivity.this,BagEditActivity.class);
                    i.putExtra("bg",mBg);
                    i.putExtra("name",mBagName);
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_BAG_MODIFY);
                    startActivityForResult(i,REQ_MODIFY_BAG);
                }else if(itemId == 1){
                    Intent i = new Intent(BagActivity.this,BagFavoriteActivity.class);
                    startActivity(i);
                }else if (itemId == 2) {
                    String temp = "neta://com.moemoe.lalala/url_inner_1.0?http://www.moemoe.la/shubao/?token=" + PreferenceUtils.getToken();
                    Uri uri = Uri.parse(temp);
                    IntentUtils.toActivityFromUri(BagActivity.this, uri, null);
                }else if (itemId == 3) {
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
        mRv.isLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BagDirEntity entity = (BagDirEntity) mAdapter.getItem(position);
                if(entity == null){
                    Intent i = new Intent(BagActivity.this,BagEditActivity.class);
                    i.putExtra("bg","");
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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_BAG && resultCode == RESULT_OK){
            mRlHaveRoot.setVisibility(View.GONE);
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
           // int position = data.getIntExtra("position",-1);
          //  BagDirEntity entity = data.getParcelableExtra("info");
            boolean change = data.getBooleanExtra("change",false);
            if(change){
                mPresenter.getFolderList(mUserId,0);
            }
//            if(position != -1){
//                if(entity == null){
//                    mAdapter.getList().remove(position);
//                    mAdapter.notifyItemRemoved(position);
//                }else {
////                    BagDirEntity bagDirEntity = (BagDirEntity) mAdapter.getList().get(position);
////                    bagDirEntity = entity;
//                    mAdapter.getList().add(position,entity);
//                    mAdapter.notifyItemInserted(position);
//                   // mAdapter.notifyItemChanged(position);
//                }
//            }

        }
    }

    @OnClick({R.id.iv_back,R.id.tv_get_bag,R.id.iv_menu_list})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_bag:
                Intent i = new Intent(BagActivity.this,BagEditActivity.class);
                i.putExtra("bg","");
                i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_BAG_OPEN);
                startActivityForResult(i,REQ_GET_BAG);
                break;
            case R.id.iv_menu_list:
                mMenu.showMenu(mIvMenu);
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
            mRv.isLoadMoreEnabled(false);
        }else {
            mRv.isLoadMoreEnabled(true);
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

    private int getSize(long size){
        return (int) (size/1024/1024);
    }

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mRv.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
