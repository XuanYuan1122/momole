package com.moemoe.lalala.view.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerCameraComponent;
import com.moemoe.lalala.di.modules.CameraModule;
import com.moemoe.lalala.event.StickChangeEvent;
import com.moemoe.lalala.model.entity.StickEntity;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.presenter.CameraContract;
import com.moemoe.lalala.presenter.CameraPresenter;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.StickFragment;
import com.moemoe.lalala.view.widget.camera.StickerView;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 * Created by yi on 2017/11/16.
 */

public class CameraPreview2Activity extends BaseAppCompatActivity implements CameraContract.View {

    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.iv_show)
    ImageView mIvShow;
    @BindView(R.id.iv_take_picture)
    ImageView mIvTakePicture;
    @BindView(R.id.sticker)
    StickerView mSticker;
    @BindView(R.id.rl_root)
    RelativeLayout mRoot;
    @BindView(R.id.tv_switch_camera)
    TextView mTvSwitchCamera;
    @BindView(R.id.tv_switch_face)
    TextView mTvSwitchFace;
    @BindView(R.id.ll_face_root)
    View mSticksRoot;
    @BindView(R.id.indicator_person_data)
    CommonTabLayout mTabLayout;
    @BindView(R.id.pager)
    ViewPager mPager;

    @Inject
    CameraPresenter mPresenter;

    private TabFragmentPagerAdapter mAdapter;

    String imagUrl = "";
    String tmpUrl = "";
    private int mW;
    private int mH;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_camera;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        DaggerCameraComponent.builder()
                .cameraModule(new CameraModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);

        mRoot.setDrawingCacheEnabled(true);
        mRoot.buildDrawingCache();

        mW = DensityUtil.getScreenWidth(this) / 2;
        mH = DensityUtil.getScreenHeight(this) / 2;

        mTvSwitchCamera.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                switchCamera();
            }
        });
        mIvTakePicture.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                takePhoto();
            }
        });
        camera.setFacing(CameraKit.Constants.FACING_FRONT);
        mTvSwitchCamera.setSelected(false);
        mPresenter.loadStickList();
    }

    private Bitmap getScaleBitmap(Bitmap source,int mWidth,int mHeight){
        Bitmap.Config config =
                source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, config);
        float scaleX = (float) mWidth / source.getWidth();
        float scaleY = (float) mHeight / source.getHeight();
        float scale = Math.max(scaleX, scaleY);

        float scaledWidth = scale * source.getWidth();
        float scaledHeight = scale * source.getHeight();
        float left = (mWidth - scaledWidth) / 2;
        float top = (mHeight - scaledHeight) / 2;
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, null, targetRect, null);

        return bitmap;
    }

    private void switchCamera(){
        if (camera.isFacingFront()) {
            camera.setFacing(CameraKit.Constants.FACING_BACK);
            mTvSwitchCamera.setSelected(true);
        } else {
            camera.setFacing(CameraKit.Constants.FACING_FRONT);
            mTvSwitchCamera.setSelected(false);
        }
    }

    private void hideAll(){
        mTvSwitchCamera.setVisibility(View.GONE);
        mTvSwitchFace.setVisibility(View.GONE);
        mIvTakePicture.setVisibility(View.GONE);
        mSticker.setShowDrawController(false);
    }

    private void showAll(){
        mTvSwitchCamera.setVisibility(View.VISIBLE);
        mTvSwitchFace.setVisibility(View.VISIBLE);
        mIvTakePicture.setVisibility(View.VISIBLE);
        mIvShow.setImageBitmap(null);
        camera.start();
        mIvShow.setVisibility(View.GONE);
        mSticker.setShowDrawController(true);
    }

    private void takePhoto() {
        createDialog();
        camera.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(final CameraKitImage event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camera.stop();
                        mIvShow.setImageBitmap(event.getBitmap());
                        mIvShow.setVisibility(View.VISIBLE);
                        hideAll();
                        Bitmap bitmap = mRoot.getDrawingCache();
                        mIvShow.setImageBitmap(bitmap);
                        saveImageToGallery(bitmap);
                        showAll();
                        finalizeDialog();
                    }
                });
            }
        });
    }

    @OnClick({R.id.iv_close_stick,R.id.tv_switch_face})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_close_stick:
                if(mSticksRoot.getVisibility() == View.VISIBLE){
                    mSticksRoot.setVisibility(View.GONE);
                    mTvSwitchFace.setVisibility(View.VISIBLE);
                    mTvSwitchCamera.setVisibility(View.VISIBLE);
                    mIvTakePicture.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_switch_face:
                if(mAdapter != null){
                    if(mSticksRoot.getVisibility() == View.GONE){
                        mSticksRoot.setVisibility(View.VISIBLE);
                        mTvSwitchFace.setVisibility(View.GONE);
                        mTvSwitchCamera.setVisibility(View.GONE);
                        mIvTakePicture.setVisibility(View.GONE);
                    }
                }else {
                    showToast("没有贴纸可选");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 保存合成后的图片
     * @param bmp
     */
    public void saveImageToGallery(Bitmap bmp) {
        // 首先保存图片
        String fileName = StorageUtils.getGalleryDirPath() + System.currentTimeMillis() + ".jpg";
        File file = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapUtils.galleryAddPic(this,file.getAbsolutePath());

        imagUrl = file.getAbsolutePath();
    }

    public void buyStick(String id,String roleId,int position){
        createDialog();
        mPresenter.buyStick(id,roleId,position);
    }

    public void changeStick(String path){
        Glide.with(this)
                .load(StringUtils.getUrl(path))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mSticker.setWaterMark(getScaleBitmap(resource,mW,mH));
                    }
                });

    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
    }

    @Override
    public void onLoadStickListSuccess(ArrayList<StickEntity> entities) {
        List<BaseFragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for(StickEntity entity : entities){
            fragmentList.add(StickFragment.newInstance(entity.getSticks(),entity.getRoleId()));
            titles.add(entity.getRoleName());
            mTabEntities.add(new TabEntity(entity.getRoleName(), R.drawable.ic_personal_bag, R.drawable.ic_personal_bag));
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(2);
        mTabLayout.setTabData(mTabEntities);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBuyStickSuccess(String roleId,int position) {
        finalizeDialog();
        RxBus.getInstance().post(new StickChangeEvent(roleId,position));
    }
}
