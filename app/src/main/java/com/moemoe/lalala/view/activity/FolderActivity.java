package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.moemoe.lalala.presenter.BagContract;
import com.moemoe.lalala.presenter.BagPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GlideCircleTransform;
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

/**
 * Created by yi on 2017/1/19.
 */

public class FolderActivity extends BaseAppCompatActivity implements BagContract.View{

    private static final int REQ_ADD_ITEM = 40000;
    private static final int REQ_MODIFY_DIE = 40001;
    private static final int REQ_MODIFY_FILES = 40002;
    private static final int REQ_DETAIL_FILES = 40003;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fl_add_item_root)
    FrameLayout mFlAddRoot;
    @BindView(R.id.rl_user_root)
    RelativeLayout mRlUserRoot;
    @BindView(R.id.rl_buy_root)
    RelativeLayout mRlBuyRoot;
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
    @BindView(R.id.tv_select)
    TextView mTvSelect;
    @BindView(R.id.tv_num)
    TextView mTvNum;
    @BindView(R.id.tv_buy)
    TextView mTvBuy;
    @BindView(R.id.tv_buy_desc)
    TextView mTvBuyDesc;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_user_name)
    TextView mTvBagName;
    @BindView(R.id.tv_more)
    TextView mTvMore;

    @Inject
    BagPresenter mPresenter;
    private PopupListMenu mMenu;
    private BagAdapter mAdapter;
    private String mUserId;
    private BagDirEntity mDir;
    private boolean mChange = false;
    private int position;
    private boolean mIsLoading = false;
    private ArrayList<FileEntity> mCurList = new ArrayList<>();

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
        mRlHaveRoot.setVisibility(View.GONE);
        mUserId = getIntent().getStringExtra(UUID);
        mDir = getIntent().getParcelableExtra("info");
        position = getIntent().getIntExtra("position",-1);
        mTvNum.setText(mDir.getNumber() + "项");
        mTitleView.setTitle(mDir.getName());
        mTvSpaceNum.setText(mDir.getUpdateTime() + " 更新");
        Glide.with(this)
                .load(StringUtils.getUrl(this, ApiService.URL_QINIU +  mDir.getCover(), DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,200), false, false))
                .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                .error(R.drawable.btn_cardbg_defbg)
                .placeholder(R.drawable.btn_cardbg_defbg)
                .into(mIvBg);
        mIvMenu.setVisibility(View.GONE);
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mFlAddRoot.setVisibility(View.VISIBLE);
            mRlUserRoot.setVisibility(View.GONE);
            mRlBuyRoot.setVisibility(View.GONE);
          //  mTvSelect.setVisibility(View.GONE);
            mIvMenu.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = mTitleView.getLayoutParams();
            layoutParams.height = DensityUtil.dip2px(this,200);
            mTitleView.setLayoutParams(layoutParams);
            mTitleView.setExpandedTitleMarginBottom(DensityUtil.dip2px(this,25));
            mIvMenu.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mMenu.showMenu(mIvMenu);
                }
            });
            initPopupMenus();
        }else {
            mFlAddRoot.setVisibility(View.GONE);
         //   mTvSelect.setVisibility(View.GONE);
            mRlBuyRoot.setVisibility(View.VISIBLE);
            mRlUserRoot.setVisibility(View.VISIBLE);
            mTvBagName.setText(mDir.getBagName());
            ViewGroup.LayoutParams layoutParams = mTitleView.getLayoutParams();
            layoutParams.height = DensityUtil.dip2px(this,250);
            mTitleView.setLayoutParams(layoutParams);
            mTitleView.setExpandedTitleMarginBottom(DensityUtil.dip2px(this,75));
            Glide.with(this)
                    .load(StringUtils.getUrl(this, ApiService.URL_QINIU +  mDir.getCover(), DensityUtil.dip2px(this,40), DensityUtil.dip2px(this,40), false, false))
                    .override(DensityUtil.dip2px(this,40),DensityUtil.dip2px(this,40))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .transform(new GlideCircleTransform(this))
                    .into(mIvAvatar);
            mTvMore.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i2 = new Intent(FolderActivity.this,BagActivity.class);
                    i2.putExtra(UUID,mDir.getUserId());
                    startActivity(i2);
                }
            });
            if (mDir.isBuy()){
                mRlBuyRoot.setVisibility(View.GONE);
            }else {
                if(mDir.getCoin() > 0){
                    mTvBuy.setText(mDir.getCoin() + "节操购买");
                    mTvBuyDesc.setVisibility(View.VISIBLE);
                    mRlBuyRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.pink_fb7ba2));
                }else {
                    mTvBuy.setText("添加到收藏夹");
                    mTvBuyDesc.setVisibility(View.GONE);
                    mRlBuyRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.green_7ebf40));
                }
            }
        }
        mRv.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mRv.getRecyclerView().setHasFixedSize(true);
        mAdapter = new BagAdapter(this,false,1);
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mAdapter.setIsBuy(true);
        }else {
            mAdapter.setIsBuy(mDir.getCoin() <= 0 || mDir.isBuy());
        }
        mRv.getRecyclerView().setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRv.setLayoutManager(layoutManager);
        mRv.isLoadMoreEnabled(false);
    }

    private void initPopupMenus() {
        PopupMenuItems items = new PopupMenuItems(this);
        MenuItem item;
        if(mUserId.equals(PreferenceUtils.getUUid())){
            item = new MenuItem(0, getString(R.string.label_modify));
            items.addMenuItem(item);
        }
        item = new MenuItem(1,"选择");
        items.addMenuItem(item);

        mMenu = new PopupListMenu(this, items);
        mMenu.setMenuItemClickListener(new PopupListMenu.MenuItemClickListener() {

            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent i = new Intent(FolderActivity.this,BagEditActivity.class);
                    i.putExtra("bg",mDir.getCover());
                    i.putExtra("name",mDir.getName());
                    i.putExtra("coin",mDir.getCoin());
                    i.putExtra("folderId",mDir.getFolderId());
                    i.putExtra("size",mDir.getSize());
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_DIR_MODIFY);
                    startActivityForResult(i,REQ_MODIFY_DIE);
                }else if(itemId == 1){
                    Intent i = new Intent(FolderActivity.this,FilesSelectActivity.class);
                    i.putExtra("folderId",mDir.getFolderId());
                    i.putParcelableArrayListExtra("list",mCurList);
                    startActivityForResult(i,REQ_MODIFY_FILES);
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initListeners() {
//        mTvSelect.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                Intent i = new Intent(FolderActivity.this,FilesSelectActivity.class);
//                i.putExtra("folderId",mDir.getFolderId());
//                i.putParcelableArrayListExtra("list",mCurList);
//                startActivityForResult(i,REQ_MODIFY_FILES);
//            }
//        });
        mIvBg.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(FolderActivity.this,BagEditActivity.class);
                i.putExtra("bg",mDir.getCover());
                i.putExtra("name",mDir.getName());
                i.putExtra("coin",mDir.getCoin());
                i.putExtra("folderId",mDir.getFolderId());
                i.putExtra("size",mDir.getSize());
                i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_DIR_MODIFY);
                startActivityForResult(i,REQ_MODIFY_DIE);
            }
        });
        mFlAddRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(FolderActivity.this,BagEditActivity.class);
                i.putExtra("bg",mDir.getCover());
                i.putExtra("name",mDir.getName());
                i.putExtra("isBuy",mDir.getCoin() > 0);
                i.putExtra("folderId",mDir.getFolderId());
                i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_DIR_ITEM_ADD);
                startActivityForResult(i,REQ_ADD_ITEM);
            }
        });
        mRlBuyRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                String title;
                if(mDir.getCoin() > 0){
                    title = "确认购买？";
                }else {
                    title = "确认收藏？";
                }
                alertDialogUtil.createPromptNormalDialog(FolderActivity.this,title);
                alertDialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        mPresenter.buyFolder(mDir.getFolderId());
                        alertDialogUtil.dismissDialog();
                    }
                });
                alertDialogUtil.showDialog();

            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mDir.isBuy() || mDir.getCoin() <= 0){
                    Intent i = new Intent(FolderActivity.this,FileDetailActivity.class);
                    i.putParcelableArrayListExtra("list",mCurList);
                    i.putExtra("folderId",mDir.getFolderId());
                    i.putExtra("userId",mDir.getUserId());
                    i.putExtra(FileDetailActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,position);
                    startActivityForResult(i,REQ_DETAIL_FILES);
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
                    if(!isFinishing()) Glide.with(FolderActivity.this).resumeRequests();
                } else {
                    if(!isFinishing())Glide.with(FolderActivity.this).pauseRequests();
                }
            }
        });
        mRv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.getFolderItemList(mDir.getFolderId(),mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.getFolderItemList(mDir.getFolderId(),0);
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
        mPresenter.getFolderItemList(mDir.getFolderId(),0);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("info",mDir);
        i.putExtra("change",mChange);
        i.putExtra("position",position);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ADD_ITEM && resultCode == RESULT_OK){
            mPresenter.getFolderItemList(mDir.getFolderId(),0);
            mDir.setNumber(mDir.getNumber() + data.getIntExtra("number",0));
            mTvNum.setText(mDir.getNumber() + "项");
            mChange = true;
        }else if(requestCode == REQ_MODIFY_DIE){
            if(resultCode == RESULT_OK){
                mDir.setName(data.getStringExtra("name"));
                mDir.setCover(data.getStringExtra("bg"));
                mChange = true;
                mTitleView.setTitle(mDir.getName());
                Glide.with(this)
                        .load(data.getStringExtra("bg"))
                        .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                        .error(R.drawable.btn_cardbg_defbg)
                        .placeholder(R.drawable.btn_cardbg_defbg)
                        .into(mIvBg);
            }else if(resultCode == BagEditActivity.RES_DELETE){
                Intent i = new Intent();
                mDir = null;
                mChange = true;
                i.putExtra("info",mDir);
                i.putExtra("change",mChange);
                i.putExtra("position",position);
                setResult(RESULT_OK,i);
                finish();
            }
        }else if(requestCode == REQ_MODIFY_FILES && resultCode == RESULT_OK){
            mPresenter.getFolderItemList(mDir.getFolderId(),0);
            mDir.setNumber(mDir.getNumber() - data.getIntExtra("number",0));
            mTvNum.setText(mDir.getNumber() + "项");
            mChange = true;
        }else if(requestCode == REQ_DETAIL_FILES && resultCode == RESULT_OK){
            mPresenter.getFolderItemList(mDir.getFolderId(),0);
            mDir.setNumber(mDir.getNumber() - data.getIntExtra("number",0));
            mTvNum.setText(mDir.getNumber() + "项");
            mChange = true;
        }
    }

    @Override
    public void openOrModifyBagSuccess() {

    }

    @Override
    public void loadBagInfoSuccess(BagEntity entity) {

    }

    @Override
    public void loadFolderListSuccess(ArrayList<BagDirEntity> entities, boolean isPull) {

    }

    @Override
    public void createFolderSuccess() {

    }

    @Override
    public void uploadFolderSuccess() {

    }

    @Override
    public void loadFolderItemListSuccess(ArrayList<FileEntity> entities, boolean isPull) {
        mIsLoading = false;
        mRv.setComplete();
        if(entities.size() == 0){
            mRv.isLoadMoreEnabled(false);
        }else {
            mRv.isLoadMoreEnabled(true);
        }
        if(isPull){
            mCurList.clear();
            mCurList.addAll(entities);
            mAdapter.setData(entities);
        }else {
            mCurList.addAll(entities);
            mAdapter.addData(entities);
        }
    }

    @Override
    public void onCheckSize(boolean isOk) {

    }

    @Override
    public void onBuyFolderSuccess() {
        mAdapter.setIsBuy(true);
        mDir.setBuy(true);
        mChange = true;
        mAdapter.notifyDataSetChanged();
        if(mDir.getCoin() > 0){
            showToast("购买成功");
        }else {
            showToast("收藏成功");
        }
        mRlBuyRoot.setVisibility(View.GONE);
    }

    @Override
    public void deleteFolderSuccess() {

    }

    @Override
    public void modifyFolderSuccess() {

    }

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mRv.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
