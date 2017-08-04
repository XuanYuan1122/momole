package com.moemoe.lalala.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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
import com.moemoe.lalala.presenter.BagContract;
import com.moemoe.lalala.presenter.BagPresenter;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 * Created by yi on 2017/4/12.
 */

public class MangaActivity extends BaseAppCompatActivity implements BagContract.View{

    @BindView(R.id.list)
    PullAndLoadView mRvList;
    @BindView(R.id.stub_read_top)
    ViewStub mStubReadTop;
    @BindView(R.id.stub_bottom_progress)
    ViewStub mStubReadBottomProgress;
    @BindView(R.id.stub_read_time_bottom)
    ViewStub mStubReadTimeBottom;
    @Inject
    BagPresenter mPresenter;

    private View mTopView;
    private View mBottomProgress;
    private View mBottomTime;
    private View mBack;
    private TextView mTitle;
    private View mAll;
    private TextView mTvBarProgress;
    private SeekBar mBarProgress;
    private ProgressBar mBattery;
    private TextView mTvTime;
    private TextView mTvBottomProgress;
    private int mCurIndex;
    private MangaAdapter mAdapter;
    private RxDownload downloadSub;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Receiver receiver = new Receiver();
    private IntentFilter intentFilter = new IntentFilter();
    private boolean mIsLoading = false;
    private String mFolderId;
    private View decodeView;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_manga;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), null);
        DaggerBagComponent.builder()
                .bagModule(new BagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ArrayList<FileEntity> fileEntities = getIntent().getParcelableArrayListExtra("file");
        String title = getIntent().getStringExtra("title");
        mFolderId = getIntent().getStringExtra("folderId");
        if(fileEntities == null || fileEntities.size() <= 0 || TextUtils.isEmpty(mFolderId)){
            finish();
        }
        mCurIndex = fileEntities.size();
        decodeView = getWindow().getDecorView();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //top
        mTopView = mStubReadTop.inflate();
        mBack = mTopView.findViewById(R.id.iv_back);
        mTitle = (TextView) mTopView.findViewById(R.id.tv_title);
        mAll = mTopView.findViewById(R.id.tv_all);
        //progress bottom
        mBottomProgress = mStubReadBottomProgress.inflate();
        mTvBarProgress = (TextView) mBottomProgress.findViewById(R.id.tv_progress_content);
        mBarProgress = (SeekBar) mBottomProgress.findViewById(R.id.seekbar_reading_progress);
        mBarProgress.setOnSeekBarChangeListener(new SeekBarChangeListener());
        //time bottom
        mBottomTime = mStubReadTimeBottom.inflate();
        mBattery = (ProgressBar) mBottomTime.findViewById(R.id.progreebar_battery);
        mTvTime = (TextView) mBottomTime.findViewById(R.id.tv_time);
        mTvBottomProgress = (TextView) mBottomTime.findViewById(R.id.tv_progress);

        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new MangaAdapter();
        mRvList.getRecyclerView().setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        checkData(fileEntities);
        mAdapter.setData(fileEntities);
        mRvList.getSwipeRefreshLayout().setEnabled(false);
        if(mCurIndex < 20){
            mRvList.setLoadMoreEnabled(false);
        }
        String time = dateFormat.format(new Date());
        mTvTime.setText(time);
        mTvBottomProgress.setText(getString(R.string.label_read_progress,1,mAdapter.getItemCount()));
        mTvBarProgress.setText(getString(R.string.label_read_progress,1,mAdapter.getItemCount()));
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
        mBarProgress.setMax(mAdapter.getItemCount());
        mBarProgress.setProgress(0);
        mTitle.setText(title);
        downloadSub = RxDownload.getInstance()
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getGalleryDirPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());

    }

    public void checkData(ArrayList<FileEntity> entities){
        ArrayList<FileEntity> tmp = new ArrayList<>();
        for(FileEntity entity : entities){
            if(!entity.getType().equals("image")){
                tmp.add(entity);
            }
        }
        entities.removeAll(tmp);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAll.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mTopView.getVisibility() == View.VISIBLE){
                    mTopView.setVisibility(View.GONE);
                    mBottomProgress.setVisibility(View.GONE);
                    mBottomTime.setVisibility(View.VISIBLE);
                    decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                }else {
                    mTopView.setVisibility(View.VISIBLE);
                    mBottomProgress.setVisibility(View.VISIBLE);
                    mBottomTime.setVisibility(View.GONE);
                    decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing()) Glide.with(MangaActivity.this).resumeRequests();
                    String time = dateFormat.format(new Date());
                    mTvTime.setText(time);
                    int last = ((LinearLayoutManager)mRvList.getRecyclerView().getLayoutManager()).findLastVisibleItemPosition() + 1;
                    mTvBarProgress.setText(getString(R.string.label_read_progress,last,mAdapter.getItemCount()));
                    mTvBottomProgress.setText(getString(R.string.label_read_progress,last,mAdapter.getItemCount()));
                    if(last > 0){
                        mBarProgress.setProgress(last);
                    }else {
                        mBarProgress.setProgress(0);
                    }
                } else {
                    if(!isFinishing())Glide.with(MangaActivity.this).pauseRequests();
                    int last = ((LinearLayoutManager)mRvList.getRecyclerView().getLayoutManager()).findLastVisibleItemPosition() + 1;
                    mTvBottomProgress.setText(getString(R.string.label_read_progress,last,mAdapter.getItemCount()));
                }
            }
        });
        mRvList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.getFolderItemList(mFolderId,mCurIndex);
            }

            @Override
            public void onRefresh() {
                mIsLoading = false;
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
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onDestroy() {
        mPresenter.release();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(mBarProgress != null){
                if (seekBar.getId() == mBarProgress.getId() && fromUser) {
                    if(progress <= mAdapter.getItemCount()){
                        mRvList.getRecyclerView().scrollToPosition(progress);
                    }
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

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
        mRvList.setComplete();
        if(entities.size() == 0){
            mRvList.setLoadMoreEnabled(false);
        }else {
            mRvList.setLoadMoreEnabled(true);
        }
        if(isPull){
            mCurIndex = entities.size();
            checkData(entities);
            mAdapter.setData(entities);
        }else {
            mCurIndex += entities.size();
            checkData(entities);
            mAdapter.addData(entities);
        }
        String time = dateFormat.format(new Date());
        mTvTime.setText(time);
        int last = ((LinearLayoutManager)mRvList.getRecyclerView().getLayoutManager()).findLastVisibleItemPosition() + 1;
        mTvBottomProgress.setText(getString(R.string.label_read_progress,last,mAdapter.getItemCount()));
        mTvBarProgress.setText(getString(R.string.label_read_progress,last,mAdapter.getItemCount()));
        mBarProgress.setMax(mAdapter.getItemCount());
        if(last > 0){
            mBarProgress.setProgress(last);
        }else {
            mBarProgress.setProgress(0);
        }
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

    @Override
    public void onFailure(int code, String msg) {

    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBattery != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);
                    mBattery.setProgress(100 - level);
                }
            }
        }
    }

    class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.ImageHolder>{

        ArrayList<FileEntity> list;
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
        }

        public MangaAdapter(){
            list = new ArrayList<>();
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImageHolder(LayoutInflater.from(MangaActivity.this).inflate(R.layout.item_new_doc_image,parent,false));
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, final int position) {
            createImage(holder,position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(view,position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ImageHolder extends RecyclerView.ViewHolder{
            ImageView mIvImage;
            LongImageView mIvLongImage;

            ImageHolder(View itemView) {
                super(itemView);
                mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
                mIvLongImage = (LongImageView) itemView.findViewById(R.id.iv_doc_long_image);
            }
        }

        public void setData(ArrayList<FileEntity> entities){
            this.list.clear();
            this.list.addAll(entities);
            notifyDataSetChanged();
        }

        public void addData(ArrayList<FileEntity> entities){
            int bgSize = getItemCount();
            this.list.addAll(entities);
            int afSize = getItemCount();
            notifyItemRangeInserted(bgSize,afSize - bgSize);
        }

        public FileEntity getItem(int pos){
            return list.get(pos);
        }

        private void createImage(final ImageHolder holder, final int position){
            final FileEntity image  = getItem(position);
            final int[] wh = getDocIconSize(image.getAttr().get("w").getAsInt(), image.getAttr().get("h").getAsInt(), DensityUtil.getScreenWidth(MangaActivity.this));
            if(wh[1] > 2048){
                holder.mIvImage.setVisibility(View.GONE);
                holder.mIvLongImage.setVisibility(View.VISIBLE);
                String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                ViewGroup.LayoutParams layoutParams = holder.mIvLongImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvLongImage.setLayoutParams(layoutParams);
                holder.mIvLongImage.requestLayout();
                ViewGroup.LayoutParams layoutParams1 = holder.itemView.getLayoutParams();
                layoutParams1.width = wh[0];
                layoutParams1.height = wh[1];
                holder.itemView.setLayoutParams(layoutParams1);
                holder.itemView.requestLayout();
                if(longImage.exists()){
                    holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                }else {
                    downloadSub.download(ApiService.URL_QINIU + image.getPath(),temp,null)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<DownloadStatus>() {
                                @Override
                                public void onCompleted() {
                                    BitmapUtils.galleryAddPic(MangaActivity.this, longImage.getAbsolutePath());
                                    holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                    notifyItemChanged(position);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(DownloadStatus downloadStatus) {

                                }
                            });
                }
            }else {
                holder.mIvImage.setVisibility(View.VISIBLE);
                holder.mIvLongImage.setVisibility(View.GONE);
                if(FileUtil.isGif(image.getPath())){
                    ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    holder.mIvImage.setLayoutParams(layoutParams);
                    holder.mIvImage.requestLayout();
                    ViewGroup.LayoutParams layoutParams1 = holder.itemView.getLayoutParams();
                    layoutParams1.width = wh[0];
                    layoutParams1.height = wh[1];
                    holder.itemView.setLayoutParams(layoutParams1);
                    holder.itemView.requestLayout();
                    Glide.with(MangaActivity.this)
                            .load(ApiService.URL_QINIU + image.getPath())
                            .asGif()
                            .override(wh[0], wh[1])
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .into(holder.mIvImage);
                }else {
                    ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    holder.mIvImage.setLayoutParams(layoutParams);
                    holder.mIvImage.requestLayout();
                    ViewGroup.LayoutParams layoutParams1 = holder.itemView.getLayoutParams();
                    layoutParams1.width = wh[0];
                    layoutParams1.height = wh[1];
                    holder.itemView.setLayoutParams(layoutParams1);
                    holder.itemView.requestLayout();
                    Glide.with(MangaActivity.this)
                            .load(StringUtils.getUrl(MangaActivity.this,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                            .override(wh[0], wh[1])
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .into(holder.mIvImage);
                }
            }
        }
    }
    public static int[] getDocIconSize(int width, int height, int widthLimit){
        int[] res = new int[2];
        res[0] = widthLimit;
        res[1] = height * widthLimit / width;
        return res;
    }
}
