package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerBagComponent;
import com.moemoe.lalala.di.modules.BagModule;
import com.moemoe.lalala.event.DirBuyEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.presenter.BagContract;
import com.moemoe.lalala.presenter.BagPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GridItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.BagAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 *
 * Created by yi on 2017/1/19.
 */

public class FolderActivity extends BaseAppCompatActivity implements BagContract.View,AppBarLayout.OnOffsetChangedListener{

    private static final int REQ_ADD_ITEM = 40000;
    private static final int REQ_MODIFY_DIE = 40001;
    private static final int REQ_MODIFY_FILES = 40002;
    private static final int REQ_DETAIL_FILES = 40003;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fl_add_item_root)
    FrameLayout mFlAddRoot;
    @BindView(R.id.stub_folder_user_root)
    ViewStub mStubUserRoot;
    @BindView(R.id.stub_buy_bottom_root)
    ViewStub mStubBuyRoot;
    FrameLayout mRlBuyRoot;
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
    @BindView(R.id.tv_num)
    TextView mTvNum;

    private View mBuyRoot;
    private TextView mTvMode;
    private TextView mTvFavorite;
    private TextView mTvBuyNum;

    @Inject
    BagPresenter mPresenter;

    private BottomMenuFragment bottomMenuFragment;
    private BagAdapter mAdapter;
    private String mUserId;
    private BagDirEntity mDir;
    private boolean mChange = false;
    private int position;
    private boolean mIsLoading = false;
    private ArrayList<FileEntity> mCurList = new ArrayList<>();
    private RxDownload downloadSub;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bag;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .titleBar(mToolbar)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), null);
        DaggerBagComponent.builder()
                .bagModule(new BagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mUserId = getIntent().getStringExtra(UUID);
        mDir = getIntent().getParcelableExtra("info");
        position = getIntent().getIntExtra("position",-1);
        if(mDir != null){
            boolean mIsShowMore = getIntent().getBooleanExtra("show_more",false);
            init(mIsShowMore);
        }else {
            String folderId = getIntent().getStringExtra("folder_id");
            mPresenter.getFolder(mUserId,folderId);
        }
    }

    private void init(boolean showMore){
        mTvNum.setText(mDir.getNumber() + "项");
        mTitleView.setTitle(mDir.getName());
        mTvSpaceNum.setText(mDir.getUpdateTime() + " 更新");
        Glide.with(this)
                .load(StringUtils.getUrl(this, ApiService.URL_QINIU +  mDir.getCover(), DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,200), false, true))
                .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                .error(R.drawable.btn_cardbg_defbg)
                .placeholder(R.drawable.btn_cardbg_defbg)
                .into(mIvBg);
        mIvMenu.setVisibility(View.GONE);
        mRlBuyRoot = (FrameLayout) mStubBuyRoot.inflate();
        TextView mTvBuy = (TextView) mRlBuyRoot.findViewById(R.id.tv_buy);
        mTvMode = (TextView) mRlBuyRoot.findViewById(R.id.tv_mode);
        mBuyRoot =  mRlBuyRoot.findViewById(R.id.ll_buy_root);
        mTvBuyNum = (TextView) mRlBuyRoot.findViewById(R.id.tv_buy_desc);
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mFlAddRoot.setVisibility(View.VISIBLE);
            mIvMenu.setVisibility(View.VISIBLE);
            mIvMenu.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                   if(bottomMenuFragment!=null) bottomMenuFragment.show(getSupportFragmentManager(),"FolderMenu");
                }
            });
            initPopupMenus();
            showMode();
        }else {
            RelativeLayout mRlUserRoot = (RelativeLayout) mStubUserRoot.inflate();
            ImageView mIvAvatar = (ImageView) mRlUserRoot.findViewById(R.id.iv_avatar);
            TextView mTvBagName = (TextView) mRlUserRoot.findViewById(R.id.tv_user_name);
            mTvFavorite = (TextView) mRlUserRoot.findViewById(R.id.tv_favorite);
            TextView mTvMore = (TextView) mRlUserRoot.findViewById(R.id.tv_more);
            if(showMore){
                mTvMore.setVisibility(View.VISIBLE);
                mTvMore.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        Intent i2 = new Intent(FolderActivity.this,BagActivity.class);
                        i2.putExtra(UUID,mDir.getUserId());
                        startActivity(i2);
                    }
                });
            }else {
                mTvMore.setVisibility(View.GONE);
            }
            ViewGroup.LayoutParams layoutParams = mTitleView.getLayoutParams();
            layoutParams.height = DensityUtil.dip2px(this,250);
            mTitleView.setLayoutParams(layoutParams);
            mTitleView.setExpandedTitleMarginBottom(DensityUtil.dip2px(this,75));
            mFlAddRoot.setVisibility(View.GONE);

            mTvBagName.setText(mDir.getBagName());
            Glide.with(this)
                    .load(StringUtils.getUrl(this,ApiService.URL_QINIU + mDir.getIcon(), DensityUtil.dip2px(this,40), DensityUtil.dip2px(this,40), false, false))
                    .override(DensityUtil.dip2px(this,40),DensityUtil.dip2px(this,40))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(mIvAvatar);
            mIvAvatar.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(FolderActivity.this,NewPersonalActivity.class);
                    i.putExtra(BaseAppCompatActivity.UUID,mUserId);
                    startActivity(i);
                }
            });
            mTvFavorite.setSelected(mDir.isFollow());
            mTvFavorite.setText(mDir.isFollow()?"已收藏":"收藏");
            mTvFavorite.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(mDir.isFollow()){
                        mPresenter.unFollowFolder(mDir.getFolderId());
                    }else {
                        mPresenter.followFolder(mDir.getFolderId());
                    }

                }
            });
            if (mDir.isBuy()){
                showMode();
            }else {
                if(mDir.getCoin() > 0){
                    mTvMode.setVisibility(View.GONE);
                    mBuyRoot.setVisibility(View.VISIBLE);
                    mTvBuy.setText(mDir.getCoin() + "节操购买");
                    mTvBuyNum.setText(mDir.getBuyUserNum() + "人也购买了");
                    mRlBuyRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.pink_fb7ba2));
                }else {
                    showMode();
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
        mRv.getRecyclerView().addItemDecoration(new GridItemDecoration(DensityUtil.dip2px(this,10)));
        mRv.setLoadMoreEnabled(false);
        downloadSub = RxDownload.getInstance()
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getNovRootPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());

        if(mUserId.equals(PreferenceUtils.getUUid())){
            mIvBg.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(FolderActivity.this,BagEditActivity.class);
                    i.putExtra("bg",mDir.getCover());
                    i.putExtra("name",mDir.getName());
                    i.putExtra("coin",mDir.getCoin());
                    i.putExtra("folderId",mDir.getFolderId());
                    i.putExtra("size",mDir.getSize());
                    i.putExtra("read_type",mDir.getReadType());
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_DIR_MODIFY);
                    startActivityForResult(i,REQ_MODIFY_DIE);
                }
            });
        }
        mFlAddRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(FolderActivity.this,BagEditActivity.class);
                i.putExtra("bg",mDir.getCover());
                i.putExtra("name",mDir.getName());
                i.putExtra("isBuy",mDir.getCoin() > 0);
                i.putExtra("folderId",mDir.getFolderId());
                i.putExtra("read_type",mDir.getReadType());
                i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_DIR_ITEM_ADD);
                startActivityForResult(i,REQ_ADD_ITEM);
            }
        });
        if(mRlBuyRoot != null){
            mRlBuyRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!mDir.isBuy() && mDir.getCoin() > 0){
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
                    }else {
                        if(mDir.getReadType().equalsIgnoreCase("IMAGE")){
                            boolean res = false;
                            for (FileEntity entity : mCurList){
                                if(entity.getType().equals("image")){
                                    res = true;
                                    break;
                                }
                            }
                            if(res){
                                Intent i = new Intent(FolderActivity.this,MangaActivity.class);
                                i.putParcelableArrayListExtra("file",mCurList);
                                i.putExtra("title",mDir.getName());
                                i.putExtra("folderId",mDir.getFolderId());
                                startActivity(i);
                            }else {
                                showToast("当前没有图片哦~");
                            }
                        }else if(mDir.getReadType().equalsIgnoreCase("TEXT")){
                            int res = -1;
                            for (int i = 0;i < mCurList.size();i++){
                                if(mCurList.get(i).getType().equals("txt")){
                                    res = i;
                                    break;
                                }
                            }
                            if(res >= 0){
                                goToRead(mCurList.get(res));
                            }

                        }else {
                            showToast("看看有没有新版本再来点吧~");
                        }
                    }
                }
            });
        }
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(mDir.isBuy() || mDir.getCoin() <= 0){
                    if(mCurList.get(position).getType().equals("txt")){
                        if(FileUtil.isExists(StorageUtils.getNovRootPath() + mCurList.get(position).getFileId() + File.separator + "1.txt")){
                            goToRead(mCurList.get(position));
                        }else {
                            String temp = "1.txt";
                            File file = new File(StorageUtils.getNovRootPath() + mCurList.get(position).getFileId());
                            if(file.mkdir()){
                                final ProgressDialog dialog = new ProgressDialog(FolderActivity.this);
                                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                                dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                                dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                                dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                                dialog.setTitle("下载中");
                                downloadSub.download(ApiService.URL_QINIU +  mCurList.get(position).getPath(),temp,StorageUtils.getNovRootPath() + mCurList.get(position).getFileId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<DownloadStatus>() {
                                            @Override
                                            public void onCompleted() {
                                                dialog.dismiss();
                                                goToRead(mCurList.get(position));
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                dialog.dismiss();
                                                FileUtil.deleteFile(StorageUtils.getNovRootPath() + mCurList.get(position).getFileId());
                                                showToast("下载失败");
                                            }

                                            @Override
                                            public void onNext(DownloadStatus downloadStatus) {
                                                dialog.setMax((int) downloadStatus.getTotalSize());
                                                dialog.setProgress((int) downloadStatus.getDownloadSize());
                                            }
                                        });
                                dialog.show();
                            }
                        }
                    }else {
                        Intent i = new Intent(FolderActivity.this,FileDetailActivity.class);
                        i.putParcelableArrayListExtra("list",mCurList);
                        i.putExtra("folderId",mDir.getFolderId());
                        i.putExtra("userId",mDir.getUserId());
                        i.putExtra(FileDetailActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,position);
                        startActivityForResult(i,REQ_DETAIL_FILES);
                    }
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

    private void showMode(){
        mBuyRoot.setVisibility(View.GONE);
        mRlBuyRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_4b5052));
        String mode = mDir.getReadType();
        mTvMode.setVisibility(View.VISIBLE);
        if(mode.equalsIgnoreCase("IMAGE")){
            mRlBuyRoot.setVisibility(View.VISIBLE);
            mTvMode.setText("进入看图模式");
        }else if(mode.equalsIgnoreCase("TEXT")){
            mRlBuyRoot.setVisibility(View.VISIBLE);
            mTvMode.setText("进入阅读模式");
        }else {
            mRlBuyRoot.setVisibility(View.GONE);
        }
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        MenuItem item;
        ArrayList<MenuItem> items = new ArrayList<>();
        if(mUserId.equals(PreferenceUtils.getUUid())){
            item = new MenuItem(0, getString(R.string.label_modify));
            items.add(item);
        }
        item = new MenuItem(1,"选择");
        items.add(item);

        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent i = new Intent(FolderActivity.this,BagEditActivity.class);
                    i.putExtra("bg",mDir.getCover());
                    i.putExtra("name",mDir.getName());
                    i.putExtra("coin",mDir.getCoin());
                    i.putExtra("folderId",mDir.getFolderId());
                    i.putExtra("size",mDir.getSize());
                    i.putExtra("read_type",mDir.getReadType());
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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onLoadFolderSuccess(BagDirEntity entity) {
        mDir = entity;
        init(true);
    }

    @Override
    public void onLoadFolderFail() {
        finish();
    }

    private void goToRead(FileEntity entity){
        Intent i = new Intent(this,ReadActivity.class);
        ArrayList<BookInfo> bookList = new ArrayList<>();
        int position = mCurList.indexOf(entity);
        for (FileEntity entity1 : mCurList){
            if(mCurList.indexOf(entity1) <= position)
                continue;
            if(entity1.getType().equals("txt")){
                BookInfo book = new BookInfo();
                book.setTitle(entity1.getFileName());
                book.setFromSD(true);
                book.setId(entity1.getFileId());
                book.setPath(entity1.getPath());
                bookList.add(book);
            }
        }
        BookInfo mBook = new BookInfo();
        mBook.setTitle(entity.getFileName());
        mBook.setFromSD(true);
        mBook.setId(entity.getFileId());
        mBook.setPath(entity.getPath());
        i.putExtra("book",mBook);
        i.putExtra("books",bookList);
        i.putExtra("userId",mDir.getUserId());
        i.putExtra("folderId",mDir.getFolderId());
        startActivityForResult(i,REQ_DETAIL_FILES);
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
                mDir.setReadType(data.getStringExtra("read_type"));
                mChange = true;
                mTitleView.setTitle(mDir.getName());
                String url = mDir.getCover();
                if(mDir.getCover().startsWith("image")){
                    url = StringUtils.getUrl(this, ApiService.URL_QINIU +  mDir.getCover(), DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,200), false, true);
                }
                Glide.with(this)
                        .load(url)
                        .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,200))
                        .error(R.drawable.btn_cardbg_defbg)
                        .placeholder(R.drawable.btn_cardbg_defbg)
                        .into(mIvBg);
                showMode();
            }else if(resultCode == BagEditActivity.RES_DELETE){
                Intent i = new Intent();
                mDir = null;
                mChange = true;
                i.putExtra("change",true);
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
            mRv.setLoadMoreEnabled(false);
        }else {
            mRv.setLoadMoreEnabled(true);
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
        if(mRlBuyRoot != null){
            showMode();
        }
        RxBus.getInstance().post(new DirBuyEvent(position,true));
    }

    @Override
    public void deleteFolderSuccess() {

    }

    @Override
    public void modifyFolderSuccess() {

    }

    @Override
    public void onFollowOrUnFollowFolderSuccess(boolean follow) {
        if(follow){
            mDir.setFollow(true);
            mChange = true;
            showToast("收藏成功");
        }else {
            mDir.setFollow(false);
            mChange = true;
            showToast("取消收藏成功");
        }
        mTvFavorite.setSelected(mDir.isFollow());
        mTvFavorite.setText(mDir.isFollow()?"已收藏":"收藏");
    }

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mRv.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
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
