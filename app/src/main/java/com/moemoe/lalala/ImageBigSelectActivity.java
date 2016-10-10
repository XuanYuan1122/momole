package com.moemoe.lalala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.common.util.DensityUtil;
import com.app.common.util.IOUtil;
import com.app.common.util.MD5;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.longimage.LongImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.download.DownloadInfo;
import com.moemoe.lalala.download.DownloadManager;
import com.moemoe.lalala.download.DownloadService;
import com.moemoe.lalala.download.DownloadViewHolder;
import com.moemoe.lalala.utils.AnimationUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.ImagePreView;
import com.moemoe.lalala.view.MyViewPager;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.scaleimage.ScaleView;
import com.moemoe.lalala.view.scaleimage.ScaleViewAttacher;
;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Haru on 2016/4/30 0030.
 */
@ContentView(R.layout.ac_image_preview)
public class ImageBigSelectActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 传入FileBean
     */
    public static final String EXTRA_KEY_FILEBEAN = "filebean";
    public static final String EXTRA_FROM_MUL = "mul";

    public static final String EXTRAS_KEY_PREVIEW_PHOTO = "preview_image";
    public static final String EXTRAS_KEY_FIRST_PHTOT_INDEX = "first_image_index";
    public static final String EXTRAS_KEY_CAN_SELECT = "can_select";

    @FindView(R.id.tv_sava)
    private TextView mTvSave;
    @FindView(R.id.vp_image_container)
    private MyViewPager mViewPager;
    @FindView(R.id.tv_preview_count)
    private TextView mTvCount;
    @FindView(R.id.tv_save_to_gallery)
    private View mTvSaveToGallery;
    @FindView(R.id.tv_raw)
    private View mTvRaw;
    @FindView(R.id.pb_raw_downloading)
    private ProgressBar mPbDownloading;
    @FindView(R.id.tv_progress)
    private TextView mTvProgress;

    private ImagePagerAdapter mPagerAdapter;

    private ArrayList<Image> mImages = null;

    private int mFirstShowIndex = 0;
    private boolean mSelectMode = false; // 预留选取功能
    private int mTotalCount = 0;
    private boolean mFromMul = false;

    @Override
    protected void initView() {
        initValues();

        if(mSelectMode){
            mTvSave.setText((mFirstShowIndex + 1) + File.separator + mTotalCount);
            new AnimationUtil().alphaAnimation(1.0f,0.0f).setDuration(2000)
                    .setLinearInterpolator()
                    .setFillAfter(true)
                    .setOnAnimationEndLinstener(new AnimationUtil.OnAnimationEndListener() {

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(mTvSave!=null){
                                mTvSave.setVisibility(View.GONE);
                            }
                        }
                    }).startAnimation(mTvSave);
        }else{
            mTvCount.setText((mFirstShowIndex + 1) + File.separator + mTotalCount);
            new AnimationUtil().alphaAnimation(1.0f,0.0f).setDuration(2000)
                    .setLinearInterpolator()
                    .setFillAfter(true)
                    .setOnAnimationEndLinstener(new AnimationUtil.OnAnimationEndListener() {

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(mTvSave!=null){
                                mTvSave.setVisibility(View.GONE);
                            }
                        }
                    }).startAnimation(mTvCount);
        }
        mPagerAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(mPageListener);
        mViewPager.setCurrentItem(mFirstShowIndex);
        mPbDownloading.setMax(100);
        mFromMul = mIntent.getBooleanExtra(EXTRA_FROM_MUL,false);
        if (mFromMul){
            mTvSaveToGallery.setVisibility(View.GONE);
            mTvRaw.setVisibility(View.GONE);
        }else {
            mTvSaveToGallery.setVisibility(View.VISIBLE);
            mTvRaw.setVisibility(View.VISIBLE);
        }
        mTvSaveToGallery.setOnClickListener(this);
        mTvRaw.setOnClickListener(this);
        if(mSelectMode){
           // mTvTitle.setText(R.string.a_label_select_photo);
            //mTvMenu1.setVisibility(View.VISIBLE);
            //mTvMenu1.setOnClickListener(this); 预留选择
        }else{

        }
    }

    /**
     * 初始化intent数据
     */
    private void initValues() {
        mImages = new ArrayList<>();
        if (mIntent != null) {
            ArrayList<String> images = mIntent.getStringArrayListExtra(EXTRAS_KEY_PREVIEW_PHOTO);
            if (images != null && images.size() > 0) {
                for (int i = 0; i < images.size(); i++) {
                    Image fb = new Image();
                    fb.path = images.get(i);
                    mImages.add(fb);
                }
            }

            ArrayList<Image> fbs = mIntent.getParcelableArrayListExtra(EXTRA_KEY_FILEBEAN);
            if (fbs != null && fbs.size() > 0) {
                mImages.addAll(fbs);
            }

            mFirstShowIndex = mIntent.getIntExtra(EXTRAS_KEY_FIRST_PHTOT_INDEX, 0);
            mSelectMode = mIntent.getBooleanExtra(EXTRAS_KEY_CAN_SELECT, false);
        }
        mTotalCount = mImages.size();
        if (mTotalCount == 0) {
            finish();	// 没有图片需要查看了
        }
    }

    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            if(mSelectMode){
                mTvSave.setVisibility(View.VISIBLE);
                mTvSave.setText((mViewPager.getCurrentItem() + 1) + File.separator + mTotalCount);
                new AnimationUtil().alphaAnimation(1.0f,0.0f).setDuration(2000)
                        .setLinearInterpolator()
                        .setFillAfter(true)
                        .setOnAnimationEndLinstener(new AnimationUtil.OnAnimationEndListener() {

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mTvSave.setVisibility(View.GONE);
                            }
                        }).startAnimation(mTvSave);
            }else{
                mTvCount.setVisibility(View.VISIBLE);
                mTvCount.setText((mViewPager.getCurrentItem() + 1) + File.separator + mTotalCount);
                new AnimationUtil().alphaAnimation(1.0f,0.0f).setDuration(2000)
                        .setLinearInterpolator()
                        .setFillAfter(true)
                        .setOnAnimationEndLinstener(new AnimationUtil.OnAnimationEndListener() {

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if(mTvSave!=null){
                                    mTvSave.setVisibility(View.GONE);
                                }
                            }
                        }).startAnimation(mTvCount);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private class ImagePagerAdapter extends PagerAdapter{

        private HashMap<Integer, View> mViews = new HashMap<>();

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Image fb = mImages.get(position);
            View view = null;
            final String path = fb.path;
            View.OnClickListener clickListener = new NoDoubleClickListener() {

                @Override
                public void onNoDoubleClick(View v) {
                    finish();
                }
            };

            View.OnLongClickListener saveGalleryListener = new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ImageBigSelectActivity.this);
                    String[] items = new String[]{getString(R.string.label_save_to_gallery)};
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // FileUtil.saveToGallery(ImageBigSelectActivity.this, path);
                            downloadRaw();
                        }
                    };

                    builder.setItems(items, listener);

                    try {
                        builder.create().show();
                    } catch (Exception e) {
                    }

                    return true;
                }
            };

            if(fb.h > 4000){
                View viewTemp = View.inflate(ImageBigSelectActivity.this,R.layout.item_longimage,null);
                final LongImageView imageView = (LongImageView) viewTemp.findViewById(R.id.imageView);
                String temp = MD5.md5(fb.path) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                if(longImage.exists()){
                    imageView.setImage(longImage.getAbsolutePath());
                }else {
                    final DownloadInfo info = new DownloadInfo();
                    info.setUrl(fb.real_path);
                    info.setFileSavePath(longImage.getAbsolutePath());
                    info.setAutoRename(false);
                    info.setAutoResume(true);
                    com.moemoe.lalala.download.DownloadManager downloadManager = DownloadService.getDownloadManager();
                    downloadManager.startDownload(info, new DownloadViewHolder(null,info) {
                        @Override
                        public void onWaiting() {
                        }

                        @Override
                        public void onStarted() {
                        }

                        @Override
                        public void onLoading(long total, long current) {

                        }

                        @Override
                        public void onSuccess(File result) {
                            BitmapUtils.galleryAddPic(ImageBigSelectActivity.this, result.getAbsolutePath());
                            imageView.setImage(result.getAbsolutePath());
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                        }

                        @Override
                        public void onCancelled(Callback.CancelledException cex) {
                            IOUtil.deleteFileOrDir(new File(info.getFileSavePath()));
                        }
                    });
                }
                ((ViewPager) container).addView(viewTemp);
                view = viewTemp;
                view.setOnClickListener(clickListener);
            }else {
                if (FileUtil.isGif(fb.path)) {
                    ImageView imageView = new ImageView(ImageBigSelectActivity.this);
                    Glide.with(ImageBigSelectActivity.this)
                            .load(fb.real_path)
                            .placeholder(R.drawable.ic_default_club_l)
                            .error(R.drawable.ic_default_club_l)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageView);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ((ViewPager) container).addView(imageView, params);
                    view = imageView;
                    view.setOnClickListener(clickListener);
                    view.setOnLongClickListener(saveGalleryListener);
                } else {
                    ImagePreView viewPack = new ImagePreView(ImageBigSelectActivity.this);
                    ((ViewPager) container).addView(viewPack);
                    ScaleView scaleView = viewPack.getImageView();
                    if(fb.path.startsWith("file")){
                        Picasso.with(ImageBigSelectActivity.this)
                                .load(fb.path)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .into(scaleView);
                    }else {
                        Picasso.with(ImageBigSelectActivity.this)
                                .load(StringUtils.getUrl(ImageBigSelectActivity.this, fb.path, DensityUtil.getScreenWidth(), fb.h, true, true))
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .into(scaleView);
                    }

                    scaleView.setOnViewTapListener(new ScaleViewAttacher.OnViewTapListener() {

                        @Override
                        public void onViewTap(View view, float x, float y) {
                            finish();
                        }
                    });
                    scaleView.setOnLongClickListener(saveGalleryListener);
                    view = viewPack;
                }
            }
            mViews.put(position, view);
            return view;
        };

        public View getViewByPos(int position) {
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        };

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public int getCount() {
            return mImages == null ? 0 : mImages.size();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mSelectMode) {
            // 预留选择
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_raw) {
            //downloadRaw();
            showRaw();
        } else if (id == R.id.tv_save_to_gallery) {
            downloadRaw();
        }
    }

    public void showRaw(){
        Image mCurrFile = mImages.get(mViewPager.getCurrentItem());
        View view = mPagerAdapter.getViewByPos(mViewPager.getCurrentItem());
        if (view instanceof ImagePreView) {
            ImagePreView ipv = (ImagePreView) view;
            mTvRaw.setVisibility(View.GONE);
            Picasso.with(ImageBigSelectActivity.this)
                    .load(mCurrFile.real_path)
                    .placeholder(R.drawable.ic_default_club_l)
                    .error(R.drawable.ic_default_club_l)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(ipv.getImageView());
        }
    }

    public String createImageFile(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String imageFileName = "neta_" + timeStamp + ".jpg";
        File image = new File(StorageUtils.getGalleryDirPath(), imageFileName);
        return image.getAbsolutePath();
    }

    public void downloadRaw(){
        final Image image = mImages.get(mViewPager.getCurrentItem());
        final DownloadInfo info = new DownloadInfo();
        info.setUrl(image.real_path);
        info.setFileSavePath(createImageFile());
        info.setAutoRename(false);
        info.setAutoResume(true);
        DownloadManager downloadManager = DownloadService.getDownloadManager();
        downloadManager.startDownload(info, new DownloadViewHolder(null,info) {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                mPbDownloading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoading(long total, long current) {
                int progress = (int) (current / total * 100);
                mPbDownloading.setProgress(progress);
            }

            @Override
            public void onSuccess(File result) {
                image.local_path = info.getFileSavePath();
                BitmapUtils.galleryAddPic(ImageBigSelectActivity.this, image.local_path);
                ToastUtil.showToast(ImageBigSelectActivity.this,
                        getString(R.string.msg_register_to_gallery_success, image.local_path));
                mPbDownloading.setVisibility(View.GONE);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
               // IOUtil.deleteFileOrDir(new File(info.getFileSavePath()));
                //ToastUtil.showToast(ImageBigSelectActivity.this,i
                //        R.string.msg_register_to_gallery_fail);
                mPbDownloading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                IOUtil.deleteFileOrDir(new File(info.getFileSavePath()));
                mPbDownloading.setVisibility(View.GONE);
            }
        });
    }
}
