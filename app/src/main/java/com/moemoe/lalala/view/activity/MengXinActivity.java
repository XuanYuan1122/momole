package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/1/6.
 */

public class MengXinActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_bg)
    ImageView mIvBg;

    private int mCurNum;
    private String schme;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_mengxin;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), null);
        schme = getIntent().getStringExtra("schema");
        mCurNum = 1;
        try {
            AssetManager assetManager = this.getAssets();
            InputStream in = assetManager.open(mCurNum + ".jpg");
            mIvBg.setImageDrawable(InputStream2Drawable(in));
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Drawable InputStream2Drawable(InputStream is){
        Drawable drawable = BitmapDrawable.createFromStream(is,"guiImg");
        return drawable;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    private void go2Login(){
        saveLaunch();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LoginActivity.EXTRA_KEY_FIRST_RUN, true);
        Intent i = new Intent(this,LoginActivity.class);
        i.putExtra(LoginActivity.EXTRA_KEY_SETTING, true);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    @Override
    protected void initListeners() {
        mIvBg.setOnClickListener(new NoDoubleClickListener(500) {
            @Override
            public void onNoDoubleClick(View v) {
                mCurNum++;
                if(mCurNum < 13){
                    try {
                        AssetManager assetManager = getAssets();
                        InputStream in = assetManager.open(mCurNum + ".jpg");
                        mIvBg.setImageDrawable(InputStream2Drawable(in));
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    if(PreferenceUtils.isLogin()){
                        goToMain();
                    }else {
                        go2Login();
                    }
                }
            }
        });
    }


    private void goToMain(){
        saveLaunch();
        Intent i = new Intent(this,MapActivity.class);
        if(!TextUtils.isEmpty(schme)){
            i.putExtra("schema",schme);
        }
        startActivity(i);
        finish();
    }

    private void saveLaunch(){
        PreferenceUtils.setAppFirstLaunch(this,false);
        PreferenceUtils.setVersion2FirstLaunch(this,false);
        PreferenceUtils.setVersionCode(this,getString(R.string.app_version_code));
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {

    }
}
