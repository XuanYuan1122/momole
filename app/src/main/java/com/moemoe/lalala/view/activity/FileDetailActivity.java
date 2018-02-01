package com.moemoe.lalala.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFileComponent;
import com.moemoe.lalala.di.modules.FileModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.DownloadEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.MoveFileEntity;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.presenter.FilesContract;
import com.moemoe.lalala.presenter.FilesPresenter;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TasksManager;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.scaleimage.ScaleView;
import com.moemoe.lalala.view.widget.scaleimage.ScaleViewAttacher;
import com.moemoe.lalala.view.widget.view.ImagePreView;
import com.moemoe.lalala.view.widget.view.MyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/11/30.
 */

public class FileDetailActivity extends BaseAppCompatActivity implements FilesContract.View,IPlayBack.Callback{

    public static final String EXTRAS_KEY_FIRST_PHTOT_INDEX = "first_image_index";
    private static final int REQ_SELECT_FOLDER = 5001;

    @BindView(R.id.rl_bar)
    View mBarRoot;
    @BindView(R.id.ll_bottom_bar)
    View mBottomBarRoot;
    @BindView(R.id.vp_image_container)
    MyViewPager mViewPager;
    @BindView(R.id.tv_preview_count)
    TextView mTvCount;
    @BindView(R.id.tv_save_to_gallery)
    View mTvSaveToGallery;
    @BindView(R.id.tv_raw)
    View mTvRaw;
    @BindView(R.id.iv_back)
    View mBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_move)
    TextView mTvMove;
    @BindView(R.id.tv_down)
    TextView mTvDown;
    @BindView(R.id.fl_delete_root)
    View mDelRoot;
    @BindView(R.id.fl_edit_root)
    View mEditRoot;

    @Inject
    FilesPresenter mPresenter;

    private ImagePagerAdapter mPagerAdapter;
    private ArrayList<CommonFileEntity> mItems = null;
    private int mFirstShowIndex = 0;
  // private RxDownload downloadSub;
    private String mFolderType;
    private String mFolderId;
    private boolean change;
    private int changeNum = 0;
    private Player mPlayer;
    private String mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_image_preview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), null);
        DaggerFileComponent.builder()
                .fileModule(new FileModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTvSaveToGallery.setVisibility(View.GONE);
        mTvRaw.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        mBarRoot.setVisibility(View.VISIBLE);
        mBarRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.alpha_80));
        mBottomBarRoot.setVisibility(View.VISIBLE);
        mItems = getIntent().getParcelableArrayListExtra("list");
        mFolderId = getIntent().getStringExtra("folderId");
        mUserId = getIntent().getStringExtra("userId");
        mFolderType = getIntent().getStringExtra("folderType");
        if(mItems == null){
            finish();
            return;
        }
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mTvMove.setText("移动");
            mTvMove.setVisibility(View.GONE);
        }else {
            if(mFolderType.equals(FolderType.TJ.toString())){
                mTvMove.setVisibility(View.GONE);
            }else {
                mTvMove.setText("存到我的书包");
                mTvMove.setVisibility(View.VISIBLE);
            }
            mDelRoot.setVisibility(View.GONE);
            mEditRoot.setVisibility(View.GONE);
        }
        mPlayer = Player.getInstance(this);
        mPlayer.registerCallback(this);
        mFirstShowIndex = getIntent().getIntExtra(EXTRAS_KEY_FIRST_PHTOT_INDEX, 0);
        mPagerAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mFirstShowIndex);
        mTvTitle.setText(mItems.get(mFirstShowIndex).getFileName());
        mTvName.setText("上传者: " + mItems.get(mFirstShowIndex).getUserName());
//        downloadSub = RxDownload.getInstance(this)
//                    .maxThread(1)
//                    .maxRetryCount(3)
//                    .defaultSavePath(StorageUtils.getGalleryDirPath())
//                    .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    mHandler.removeCallbacks(mProgressCallback);
                }
                CommonFileEntity entity = mItems.get(mViewPager.getCurrentItem());
                if(entity.getType().equals("music")){
                    View v =  mPagerAdapter.getViewByPos(mViewPager.getCurrentItem());
                    ImageView ivControl = (ImageView) v.findViewById(R.id.iv_music_control);
                    ivControl.setImageResource(R.drawable.ic_bag_music_play);
                }
                mTvTitle.setText(mItems.get(position).getFileName());
                mTvName.setText("上传者: " + mItems.get(position).getUserName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void deleteFilesSuccess() {
        finalizeDialog();
        change = true;
        changeNum ++;
        showToast("删除文件成功");
        onBackPressed();
    }

    @Override
    public void moveFilesSuccess() {
        finalizeDialog();
        change = true;
        changeNum ++;
        showToast("移动文件成功");
        onBackPressed();
    }

    @Override
    public void modifyFileSuccess(String name) {
        finalizeDialog();
        change = true;
        mTvTitle.setText(name);
        mItems.get(mViewPager.getCurrentItem()).setFileName(name);
        showToast("修改文件成功");
    }

    @Override
    public void copyFileSuccess() {
        finalizeDialog();
        change = true;
        showToast("转存文件成功");
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {
        View v =  mPagerAdapter.getViewByPos(mViewPager.getCurrentItem());
        ImageView ivControl = (ImageView) v.findViewById(R.id.iv_music_control);
        ivControl.setImageResource(R.drawable.btn_doc_video_play);
        updateProgressTextWithDuration(0);
        mPlayer.seekTo(0);
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        CommonFileEntity entity = mItems.get(mViewPager.getCurrentItem());
        if(entity.getType().equals("music")){
            View v =  mPagerAdapter.getViewByPos(mViewPager.getCurrentItem());
            TextView tvTime = (TextView) v.findViewById(R.id.tv_music_time);
            tvTime.setText("00:00 / "+getMinute(entity.getAttr().get("timestamp").getAsInt()));
            ImageView ivControl = (ImageView) v.findViewById(R.id.iv_music_control);
            if (isPlaying){
                ivControl.setImageResource(R.drawable.ic_bag_music_stop);
                mHandler.removeCallbacks(mProgressCallback);
                mHandler.post(mProgressCallback);
            }else {
                ivControl.setImageResource(R.drawable.ic_bag_music_play);
                mHandler.removeCallbacks(mProgressCallback);
            }
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (mPlayer.isPlaying()) {
                updateProgressTextWithDuration(mPlayer.getProgress());
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    private void updateProgressTextWithDuration(int duration) {
        View v =  mPagerAdapter.getViewByPos(mViewPager.getCurrentItem());
        TextView tvTime = (TextView) v.findViewById(R.id.tv_music_time);
        tvTime.setText(getMinute(duration) + " / " + getMinute(mPlayer.getPlayingSong().getDuration()));
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private HashMap<Integer, View> mViews = new HashMap<>();

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final CommonFileEntity fb = mItems.get(position);
            View view;
            View.OnClickListener clickListener = new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    finish();
                }
            };
            if(fb.getType().equals("image")){
                int h = fb.getAttr().get("h").getAsInt();
                int w = fb.getAttr().get("w").getAsInt();
                if(((float)h / w) > 16.0f / 9.0f){
                    View viewTemp = View.inflate(FileDetailActivity.this,R.layout.item_longimage,null);
                    final LongImageView imageView = viewTemp.findViewById(R.id.imageView);
                    String temp = EncoderUtils.MD5(ApiService.URL_QINIU + fb.getPath()) + ".jpg";
                    final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                    if(longImage.exists()){
                        imageView.setImage(longImage.getAbsolutePath());
                    }else {
                        FileDownloader.getImpl().create(ApiService.URL_QINIU + fb.getPath())
                                .setPath(StorageUtils.getGalleryDirPath() + temp)
                                .setForceReDownload(true)
                                .setCallbackProgressTimes(1)
                                .setListener(new FileDownloadListener() {
                                    @Override
                                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                    }

                                    @Override
                                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                    }

                                    @Override
                                    protected void completed(BaseDownloadTask task) {
                                        BitmapUtils.galleryAddPic(FileDetailActivity.this, longImage.getAbsolutePath());
                                        imageView.setImage(longImage.getAbsolutePath());
                                    }

                                    @Override
                                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                    }

                                    @Override
                                    protected void error(BaseDownloadTask task, Throwable e) {

                                    }

                                    @Override
                                    protected void warn(BaseDownloadTask task) {

                                    }
                                }).start();
//                        downloadSub.download(ApiService.URL_QINIU + fb.getPath(),temp,null)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new NetTResultSubscriber<DownloadStatus>() {
//                                    @Override
//                                    public void onSuccess() {
//                                        BitmapUtils.galleryAddPic(FileDetailActivity.this, longImage.getAbsolutePath());
//                                        imageView.setImage(longImage.getAbsolutePath());
//                                        downloadSub.deleteServiceDownload(ApiService.URL_QINIU + fb.getPath(),false).subscribe();
//                                    }
//
//                                    @Override
//                                    public void onLoading(DownloadStatus res) {
//
//                                    }
//
//                                    @Override
//                                    public void onFail(Throwable e) {
//                                        downloadSub.deleteServiceDownload(ApiService.URL_QINIU + fb.getPath(),true).subscribe();
//                                    }
//                                });
                    }
                    container.addView(viewTemp);
                    view = viewTemp;
                    view.setOnClickListener(clickListener);
                }else {
                    if (FileUtil.isGif(fb.getPath())) {
                        ImageView imageView = new ImageView(FileDetailActivity.this);
                        Glide.with(FileDetailActivity.this)
                                .load(ApiService.URL_QINIU + fb.getPath())
                                .asGif()
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(imageView);
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        container.addView(imageView, params);
                        view = imageView;
                        view.setOnClickListener(clickListener);
                    } else {
                        ImagePreView viewPack = new ImagePreView(FileDetailActivity.this);
                        container.addView(viewPack);
                        final ScaleView scaleView = viewPack.getImageView();
                        if(fb.getPath().startsWith("file")){
                            Glide.with(FileDetailActivity.this)
                                    .load(fb.getPath())
                                    .placeholder(R.drawable.bg_default_square)
                                    .error(R.drawable.bg_default_square)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(new SimpleTarget<GlideDrawable>() {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                            scaleView.setImageDrawable(resource);
                                        }
                                    });
                        }else {
                            Glide.with(FileDetailActivity.this)
                                    .load(StringUtils.getUrl(FileDetailActivity.this,ApiService.URL_QINIU + fb.getPath(), DensityUtil.getScreenWidth(FileDetailActivity.this), h, true, true))
                                    .placeholder(R.drawable.bg_default_square)
                                    .error(R.drawable.bg_default_square)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(new SimpleTarget<GlideDrawable>() {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                            scaleView.setImageDrawable(resource);
                                        }
                                    });
                        }
                        scaleView.setOnViewTapListener(new ScaleViewAttacher.OnViewTapListener() {
                            @Override
                            public void onViewTap(View view, float x, float y) {
                                finish();
                            }
                        });
                        view = viewPack;
                    }
                }
            }else if(fb.getType().equals("music")){
                int time = 0;
                if(fb.getAttr().has("timestamp")){
                    time = fb.getAttr().get("timestamp").getAsInt();
                }
                View viewTemp = View.inflate(FileDetailActivity.this,R.layout.item_music_detail,null);
                ImageView bg = (ImageView) viewTemp.findViewById(R.id.iv_bg);
                TextView tvTime = (TextView) viewTemp.findViewById(R.id.tv_music_time);
                tvTime.setText("00:00 / "+getMinute(time));
                final ImageView ivControl = (ImageView) viewTemp.findViewById(R.id.iv_music_control);
                final int finalTime = time;
                ivControl.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(NetworkUtils.isNetworkAvailable(FileDetailActivity.this)){
                            Song musicInfo = mPlayer.getPlayingSong();
                            Song mMusicInfo = new Song();
                            mMusicInfo.setPath(ApiService.URL_QINIU + fb.getPath());
                            mMusicInfo.setDisplayName(fb.getFileName());
                            mMusicInfo.setDuration(finalTime);
                            if(mPlayer.isPlaying() && musicInfo.getPath().equals(ApiService.URL_QINIU + fb.getPath())){
                                mPlayer.pause();
                                ivControl.setImageResource(R.drawable.btn_doc_video_play);
                            }else if(musicInfo != null && musicInfo.getPath().equals(ApiService.URL_QINIU + fb.getPath())){
                                mPlayer.play();
                                ivControl.setImageResource(R.drawable.btn_doc_video_stop);
                            }else {
                                PlayList playList = new PlayList(mMusicInfo);
                                mPlayer.play(playList,0);
                                ivControl.setImageResource(R.drawable.btn_doc_video_stop);
                            }
                        }else {
                            ToastUtils.showShortToast(FileDetailActivity.this, getString(R.string.msg_connection));
                        }
                    }
                });
                container.addView(viewTemp);
                view = viewTemp;
                view.setOnClickListener(clickListener);
            }else if(fb.getType().equals("txt")){
                ImagePreView viewPack = new ImagePreView(FileDetailActivity.this);
                container.addView(viewPack);
                final ScaleView scaleView = viewPack.getImageView();
                Glide.with(FileDetailActivity.this)
                        .load(R.drawable.bg_bag_word)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                scaleView.setImageDrawable(resource);
                            }
                        });
                scaleView.setOnViewTapListener(new ScaleViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                    }
                });
                view = viewPack;
            }else {
                ImagePreView viewPack = new ImagePreView(FileDetailActivity.this);
                container.addView(viewPack);
                final ScaleView scaleView = viewPack.getImageView();
                Glide.with(FileDetailActivity.this)
                        .load(R.drawable.ic_bag_unknow)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                scaleView.setImageDrawable(resource);
                            }
                        });
                scaleView.setOnViewTapListener(new ScaleViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                    }
                });
                view = viewPack;
            }
            mViews.put(position, view);
            return view;
        }

        View getViewByPos(int position) {
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        mPlayer.pause();
        mPlayer.unregisterCallback(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(change){
            Intent i = new Intent();
            i.putExtra("change",change);
            i.putExtra("number",changeNum);
            setResult(RESULT_OK,i);
        }
        finish();
    }

    private String getMinute(int time) {
        int h = time / (1000 * 60 * 60);
        String minute;
        int sec = (time % (1000 * 60)) / 1000;
        int min = time % (1000 * 60 * 60) / (1000 * 60);
        String hS = h < 10 ? "0" + h : "" + h;
        String secS = sec < 10 ? "0" + sec : "" + sec;
        String minS = min < 10 ? "0" + min : "" + min;
        if (h == 0) {
            minute = minS + ":" + secS;
        } else {
            minute = hS + ":" + minS + ":" + secS;
        }
        return minute;
    }

    @OnClick({R.id.fl_down_root,R.id.fl_delete_root,R.id.fl_move_root,R.id.fl_edit_root})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fl_down_root:
                downloadRaw();
                break;
            case R.id.fl_delete_root:
                ArrayList<String> ids = new ArrayList<>();
                ids.add(mItems.get(mViewPager.getCurrentItem()).getFileId());
                createDialog();
                mPresenter.deleteFiles(mFolderId,mFolderType,ids);
                break;
            case R.id.fl_move_root:
                Intent i = new Intent(FileDetailActivity.this,FolderSelectActivity.class);
                i.putExtra("folderType",mFolderType);
                i.putExtra("folderId",mFolderId);
                startActivityForResult(i,REQ_SELECT_FOLDER);
                break;
            case R.id.fl_edit_root:
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle("文件名称")
                        .setView(editText)
                        .setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(!TextUtils.isEmpty(editText.getText().toString())){
                                    createDialog();
                                    mPresenter.modifyFile(mFolderType,mFolderId,mItems.get(mViewPager.getCurrentItem()).getFileId(),editText.getText().toString());
                                    dialogInterface.dismiss();
                                }else {
                                    showToast("文件名不能为空");
                                }
                            }
                        })
                        .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_SELECT_FOLDER && resultCode == RESULT_OK){
            ArrayList<String> ids = new ArrayList<>();
            ids.add(mItems.get(mViewPager.getCurrentItem()).getFileId());
            MoveFileEntity entity = new MoveFileEntity(ids,mFolderId);
            if(mUserId.equals(PreferenceUtils.getUUid())){
                createDialog();
                mPresenter.moveFiles(data.getStringExtra("folderId"),entity);
            }else {
                createDialog();
                mPresenter.copyFile(mFolderId,mItems.get(mViewPager.getCurrentItem()).getFileId(),data.getStringExtra("folderId"));
            }
        }
    }

    public void downloadRaw(){
        final CommonFileEntity entity = mItems.get(mViewPager.getCurrentItem());
        final File file;
        if(entity.getType().equals("image")){
            file = new File(StorageUtils.getGalleryDirPath(),entity.getFileName());
        }else {
            file = new File(StorageUtils.getGalleryDirPath(),entity.getFileName());
        }
//        DownloadBean downloadBean = new DownloadBean
//                .Builder(ApiService.URL_QINIU + entity.getPath())
//                .setSaveName(entity.getFileName())
//                .setSavePath(path)
//                .setExtra1(entity.getPath())
//                .setExtra2(entity.getFileName())
//                .setExtra3(entity.getType())
//                .setExtra4(file.getAbsolutePath())
//                .build();
        if(!FileUtil.isExists(file.getAbsolutePath())){
          //  FileUtil.deleteFile(file.getAbsolutePath());
            DownloadEntity entity1 = TasksManager.getImpl().getById(FileDownloadUtils.generateId(entity.getPath(), StorageUtils.getGalleryDirPath() + entity.getFileName()));
            if(entity1 != null){
                showToast("已存在相同文件");
                return;
            }
            if(entity.getType().equals("image")){
                createDialog();
            }

            BaseDownloadTask task = FileDownloader.getImpl().create(ApiService.URL_QINIU + entity.getPath())
                    .setPath(StorageUtils.getGalleryDirPath() + entity.getFileName())
                    .setCallbackProgressTimes(100)
                    .setListener(new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            int i = 0;
                        }

                        @Override
                        protected void started(BaseDownloadTask task) {
                            showToast("开始下载");
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            if(entity.getType().equals("image")){
                                finalizeDialog();
                                BitmapUtils.galleryAddPic(FileDetailActivity.this, file.getAbsolutePath());
                                showToast(getString(R.string.msg_register_to_gallery_success, file.getAbsolutePath()));
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            if(entity.getType().equals("image")){
                                finalizeDialog();
                                showToast("保存失败，请重试");
                            }
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {

                        }
                    });
            int taskId = task.getId();
            DownloadEntity downloadEntity = new DownloadEntity();
            downloadEntity.setUrl(entity.getPath());
            downloadEntity.setDirPath(StorageUtils.getGalleryDirPath());
            downloadEntity.setFileName(entity.getFileName());
            downloadEntity.setPath(StorageUtils.getGalleryDirPath() + entity.getFileName());
            downloadEntity.setType(entity.getType());
            downloadEntity.setId((long) taskId);
            GreenDaoManager.getInstance().getSession().getDownloadEntityDao().insertOrReplace(downloadEntity);
            task.start();
        }else {
            showToast(getString(R.string.msg_register_to_gallery_success, file.getAbsolutePath()));
        }
//            downloadSub.download(downloadBean)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new NetTResultSubscriber<DownloadStatus>() {
//                        @Override
//                        public void onSuccess() {
//                            finalizeDialog();
//                            BitmapUtils.galleryAddPic(FileDetailActivity.this, file.getAbsolutePath());
//                            showToast(getString(R.string.msg_register_to_gallery_success, file.getAbsolutePath()));
//                            downloadSub.deleteServiceDownload(ApiService.URL_QINIU + entity.getPath(),false).subscribe();
//                        }
//
//                        @Override
//                        public void onLoading(DownloadStatus res) {
//
//                        }
//
//                        @Override
//                        public void onFail(Throwable e) {
//                            finalizeDialog();
//                            showToast("保存失败，请重试");
//                            downloadSub.deleteServiceDownload(ApiService.URL_QINIU + entity.getPath(),true).subscribe();
//                        }
//                    });
 //       }
//        else {
//            downloadSub.serviceDownload(downloadBean)
//                    .subscribe(new Consumer<Object>() {
//                        @Override
//                        public void accept(Object o) throws Exception {
//                            showToast("下载开始");
//                        }
//                    });
//        }
    }
}
