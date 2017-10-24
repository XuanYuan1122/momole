package com.moemoe.lalala.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.utils.AnimationUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.scaleimage.ScaleView;
import com.moemoe.lalala.view.widget.scaleimage.ScaleViewAttacher;
import com.moemoe.lalala.view.widget.view.ImagePreView;
import com.moemoe.lalala.view.widget.view.MyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadBean;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by yi on 2016/11/30.
 */

public class ImageBigSelectActivity extends BaseAppCompatActivity {

    public static final String EXTRA_KEY_FILEBEAN = "filebean";
    public static final String EXTRA_FROM_MUL = "mul";

    public static final String EXTRAS_KEY_PREVIEW_PHOTO = "preview_image";
    public static final String EXTRAS_KEY_FIRST_PHTOT_INDEX = "first_image_index";
    public static final String EXTRAS_KEY_CAN_SELECT = "can_select";

    @BindView(R.id.tv_sava)
    TextView mTvSave;
    @BindView(R.id.vp_image_container)
    MyViewPager mViewPager;
    @BindView(R.id.tv_preview_count)
    TextView mTvCount;
    @BindView(R.id.tv_save_to_gallery)
    View mTvSaveToGallery;
    @BindView(R.id.tv_raw)
    View mTvRaw;
    @BindView(R.id.tv_add_to_bag)
    TextView mTvAddToBag;

    private ImagePagerAdapter mPagerAdapter;
    private ArrayList<Image> mImages = null;
    private int mFirstShowIndex = 0;
    private boolean mSelectMode = false; // 预留选取功能
    private int mTotalCount = 0;
    private RxDownload downloadSub;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_image_preview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), null);
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
        boolean mFromMul = getIntent().getBooleanExtra(EXTRA_FROM_MUL,false);
        if (mFromMul){
            mTvSaveToGallery.setVisibility(View.GONE);
            mTvRaw.setVisibility(View.GONE);
        }else {
            mTvSaveToGallery.setVisibility(View.VISIBLE);
            mTvRaw.setVisibility(View.VISIBLE);
        }
        downloadSub = RxDownload.getInstance(this)
                    .maxThread(1)
                    .maxRetryCount(3)
                    .defaultSavePath(StorageUtils.getGalleryDirPath())
                    .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
    }

    /**
     * 初始化intent数据
     */
    private void initValues() {
        mImages = new ArrayList<>();
        if (getIntent() != null) {
            ArrayList<String> images = getIntent().getStringArrayListExtra(EXTRAS_KEY_PREVIEW_PHOTO);
            if (images != null && images.size() > 0) {
                for (int i = 0; i < images.size(); i++) {
                    Image fb = new Image();
                    fb.setPath(images.get(i));
                    mImages.add(fb);
                }
            }
            ArrayList<Image> fbs = getIntent().getParcelableArrayListExtra(EXTRA_KEY_FILEBEAN);
            if (fbs != null && fbs.size() > 0) {
                mImages.addAll(fbs);
            }
            mFirstShowIndex = getIntent().getIntExtra(EXTRAS_KEY_FIRST_PHTOT_INDEX, 0);
            mSelectMode = getIntent().getBooleanExtra(EXTRAS_KEY_CAN_SELECT, false);
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

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    private class ImagePagerAdapter extends PagerAdapter {

        private HashMap<Integer, View> mViews = new HashMap<>();

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final Image fb = mImages.get(position);
            View view;
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
                            downloadRaw();
                        }
                    };
                    builder.setItems(items, listener);
                    try {
                        builder.create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            };
            if(fb.getH() != 0 && fb.getW() != 0 && ((float)(fb.getH()/fb.getW())) > 16.0f / 9.0f){
                View viewTemp = View.inflate(ImageBigSelectActivity.this,R.layout.item_longimage,null);
                final LongImageView imageView = viewTemp.findViewById(R.id.imageView);
                imageView.setOnClickListener(clickListener);
                String temp = EncoderUtils.MD5(ApiService.URL_QINIU + fb.getPath()) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                if(longImage.exists()){
                    imageView.setImage(longImage.getAbsolutePath());
                }else {
                    downloadSub.download(ApiService.URL_QINIU + fb.getPath(),temp,null)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<DownloadStatus>() {

                                @Override
                                public void onError(Throwable e) {
                                    downloadSub.deleteServiceDownload(ApiService.URL_QINIU + fb.getPath(),true).subscribe();
                                }

                                @Override
                                public void onComplete() {
                                    BitmapUtils.galleryAddPic(ImageBigSelectActivity.this, longImage.getAbsolutePath());
                                    imageView.setImage(longImage.getAbsolutePath());
                                    downloadSub.deleteServiceDownload(ApiService.URL_QINIU + fb.getPath(),false).subscribe();
                                }

                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(DownloadStatus downloadStatus) {

                                }
                            });
                }
                container.addView(viewTemp);
                view = viewTemp;
                view.setOnClickListener(clickListener);
            }else {
                if (FileUtil.isGif(fb.getPath())) {
                    ImageView imageView = new ImageView(ImageBigSelectActivity.this);
                    Glide.with(ImageBigSelectActivity.this)
                            .load(StringUtils.getUrl(ApiService.URL_QINIU + fb.getPath()))
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
                    view.setOnLongClickListener(saveGalleryListener);
                } else {
                    ImagePreView viewPack = new ImagePreView(ImageBigSelectActivity.this);
                    container.addView(viewPack);
                    final ScaleView scaleView = viewPack.getImageView();
                    if(fb.getPath().startsWith("file")){
                        Glide.with(ImageBigSelectActivity.this)
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
                        final int[] wh = BitmapUtils.getDocIconSizeFromW(fb.getW() * 2, fb.getH() * 2, DensityUtil.getScreenWidth(ImageBigSelectActivity.this));
                        Glide.with(ImageBigSelectActivity.this)
                                .load(StringUtils.getUrl(ImageBigSelectActivity.this,fb.getPath(), wh[0], wh[1], true, true))
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
                    scaleView.setOnLongClickListener(saveGalleryListener);
                    view = viewPack;
                }
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
            return mImages == null ? 0 : mImages.size();
        }
    }

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

    @OnClick({R.id.tv_raw,R.id.tv_save_to_gallery})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_raw:
                showRaw();
                break;
            case R.id.tv_save_to_gallery:
                downloadRaw();
                break;
        }
    }

    public void showRaw(){
        Image mCurrFile = mImages.get(mViewPager.getCurrentItem());
        View view = mPagerAdapter.getViewByPos(mViewPager.getCurrentItem());
        if (view instanceof ImagePreView) {
            final ImagePreView ipv = (ImagePreView) view;
            mTvRaw.setVisibility(View.GONE);
            Glide.with(ImageBigSelectActivity.this)
                    .load(ApiService.URL_QINIU + mCurrFile.getPath())
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            ipv.getImageView().setImageDrawable(resource);
                        }
                    });
        }
    }

    public void downloadRaw(){
        final Image image = mImages.get(mViewPager.getCurrentItem());
        String temp = StringUtils.createImageFile(FileUtil.isGif(ApiService.URL_QINIU + image.getPath()));
        final File longImage = new File(StorageUtils.getDownloadRootPath(), temp);
        DownloadBean downloadBean = new DownloadBean
                .Builder(ApiService.URL_QINIU + image.getPath())
                .setSaveName(temp)
                .setSavePath(StorageUtils.getDownloadRootPath())
                .setExtra1(image.getPath())
                .setExtra2(longImage.getName())
                .setExtra3("image")
                .setExtra4(longImage.getAbsolutePath())
                .build();
        createDialog();
        downloadSub.download(downloadBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadStatus>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull DownloadStatus downloadStatus) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        finalizeDialog();
                        showToast("保存失败，请重试");
                        downloadSub.deleteServiceDownload(ApiService.URL_QINIU + image.getPath(),true).subscribe();
                    }

                    @Override
                    public void onComplete() {
                        finalizeDialog();
                        BitmapUtils.galleryAddPic(ImageBigSelectActivity.this, longImage.getAbsolutePath());
                        showToast(getString(R.string.msg_register_to_gallery_success, longImage.getAbsolutePath()));
                    }
                });
    }
}
