package com.moemoe.lalala;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.cropper.CropImageView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    @ViewInject(R.id.crop_iv_avatar)
    private CropImageView mCropAvatar;

    private String mCropedPath;


    @Override
    protected void initView() {
        String mRawPath = mIntent.getStringExtra(EXTRA_RAW_IMG_PATH);
        float mWhRatio = mIntent.getFloatExtra(EXTRA_WH_RATIO, 1.0f);
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
