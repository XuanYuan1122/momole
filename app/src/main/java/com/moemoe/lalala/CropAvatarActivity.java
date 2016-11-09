package com.moemoe.lalala;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.image.ImageOptions;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.cropper.CropImageView;

import java.io.File;

/**
 * Created by Haru on 2016/5/1 0001.
 */
@ContentView(R.layout.ac_crop_avatar)
public class CropAvatarActivity extends BaseActivity implements View.OnClickListener{

    public static final String EXTRA_RAW_IMG_PATH = "img_raw_path";

    /**
     * 裁剪宽高比
     */
    public static final String EXTRA_WH_RATIO = "img_ration";

    //views
    @FindView(R.id.crop_iv_avatar)
    private CropImageView mCropAvatar;

    // fields
    private String mRawPath;

    private String mCropedPath;
    /**
     * 裁剪宽高比
     */
    private float mWhRatio;

    @Override
    protected void initView() {
        mRawPath = mIntent.getStringExtra(EXTRA_RAW_IMG_PATH);
        mWhRatio = mIntent.getFloatExtra(EXTRA_WH_RATIO, 1.0f);
        mCropAvatar.setFixedAspectRatio(true);
        mCropAvatar.setAspectRatio((int)(mWhRatio * 100), 100);
        mCropAvatar.setGuidelines(0);
        int[] ids = new int[]{
                R.id.iv_crop_back, R.id.iv_crop_done, R.id.iv_crop_turn_left, R.id.iv_crop_turn_right
        };
        for(int id : ids){
            findViewById(id).setOnClickListener(this);
        }


        if(!TextUtils.isEmpty(mRawPath)){
            Bitmap raw = BitmapUtils.loadThumb(mRawPath, 1600, 1600);
//            Utils.image().loadDrawable(mRawPath, new ImageOptions.Builder()
//                    .setSize(1600, 1600)
//                    .build(), new Callback.CommonCallback<Drawable>() {
//                @Override
//                public void onSuccess(Drawable result) {
//                    mCropAvatar.setImageBitmap(((BitmapDrawable)result).getBitmap());
//                }
//
//                @Override
//                public void onError(Throwable ex, boolean isOnCallback) {
//
//                }
//
//                @Override
//                public void onCancelled(CancelledException cex) {
//
//                }
//
//                @Override
//                public void onFinished() {
//
//                }
//            });
            mCropAvatar.setImageBitmap(raw);
            mCropedPath = StorageUtils.getIconByFileName(new File(mRawPath).getName());
        }
    }

    private void doCropAndFinish(){
        Bitmap croppedImage = BitmapUtils.getServerSizeBitmap(mCropAvatar.getCroppedImage());
        BitmapUtils.saveAsJpg(croppedImage, mCropedPath);
        Intent result = new Intent();
        result.setData(Uri.fromFile(new File(mCropedPath)));
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
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
}
