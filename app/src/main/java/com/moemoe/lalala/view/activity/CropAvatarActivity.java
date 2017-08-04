package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.cropper.CropImageView;

import java.io.File;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class CropAvatarActivity extends BaseAppCompatActivity implements View.OnClickListener{
    public static final String EXTRA_RAW_IMG_PATH = "img_raw_path";

    /**
     * 裁剪宽高比
     */
    public static final String EXTRA_W_RATIO = "img_w_ration";
    public static final String EXTRA_H_RATIO = "img_h_ration";

    //views
    @BindView(R.id.crop_iv_avatar)
    CropImageView mCropAvatar;

    private String mCropedPath;
    private int mType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_crop_avatar;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), null);
        String mRawPath = getIntent().getStringExtra(EXTRA_RAW_IMG_PATH);
        int mWRatio = getIntent().getIntExtra(EXTRA_W_RATIO, 1);
        int mHRatio = getIntent().getIntExtra(EXTRA_H_RATIO,1);
        mType = getIntent().getIntExtra("type",0);
        mCropAvatar.setFixedAspectRatio(true);
        mCropAvatar.setAspectRatio(mWRatio, mHRatio);
        mCropAvatar.setGuidelines(0);
        int[] ids = new int[]{
                R.id.iv_crop_back, R.id.iv_crop_done, R.id.iv_crop_turn_left, R.id.iv_crop_turn_right
        };
        for(int id : ids){
            findViewById(id).setOnClickListener(this);
        }


        if(!TextUtils.isEmpty(mRawPath)){
            Bitmap raw = BitmapUtils.loadThumb(mRawPath, 1600, 1600);
//            Glide.with(this)
//                    .load(mRawPath)
//                    .asBitmap()
//                    .override(1600,1600)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            mCropAvatar.setImageBitmap(resource);
//                        }
//                    });
            mCropAvatar.setImageBitmap(raw);
            mCropedPath = StorageUtils.getIconByFileName(System.currentTimeMillis() + new File(mRawPath).getName());
           // mCropedPath = StorageUtils.getIconByFileName(System.currentTimeMillis() + "");
        }
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.iv_crop_back){
            finish();
        }else if(id == R.id.iv_crop_done){
            doCropAndFinish();
        }else if(id == R.id.iv_crop_turn_left){
            mCropAvatar.rotateImage(90);
        }else if(id == R.id.iv_crop_turn_right){
            mCropAvatar.rotateImage(270);
        }
    }

    private void doCropAndFinish(){
        Bitmap croppedImage = BitmapUtils.getServerSizeBitmap(mCropAvatar.getCroppedImage());
        BitmapUtils.saveAsJpg(croppedImage, mCropedPath);
        Intent result = new Intent();
       // result.setData(Uri.fromFile(new File(mCropedPath)));
        result.putExtra("path",new File(mCropedPath).getAbsolutePath());
        result.putExtra("type",mType);
        setResult(RESULT_OK, result);
        finish();
    }
}
