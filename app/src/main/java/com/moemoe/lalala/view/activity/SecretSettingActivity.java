package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSettingComponent;
import com.moemoe.lalala.di.modules.SettingModule;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.presenter.SettingContract;
import com.moemoe.lalala.presenter.SettingPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/12/16.
 */

public class SecretSettingActivity extends BaseAppCompatActivity implements SettingContract.View{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;

    private boolean mIsShowFans;
    private boolean mIsShowFavorite;
    private boolean mIsShowFollow;
    @Inject
    SettingPresenter mPresenter;
    @Override
    protected int getLayoutId() {
        return R.layout.ac_secret_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(getIntent() == null){
            finish();
            return;
        }
        DaggerSettingComponent.builder()
                .settingModule(new SettingModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mIsShowFans = getIntent().getBooleanExtra("show_fans",false);
        mIsShowFavorite = getIntent().getBooleanExtra("show_favorite",false);
        mIsShowFollow = getIntent().getBooleanExtra("show_follow",false);
        mTvTitle.setText(getString(R.string.label_secret_setting));
        initStyleView(R.id.set_favorite);
        initStyleView(R.id.set_fans);
        initStyleView(R.id.set_follow);
    }

    private void initStyleView(int resId){
        View v = findViewById(resId);
        TextView name = getFunNameTv(v);
        ImageView indicate = getFunIndicateIv(v);
        if(resId == R.id.set_favorite){
            name.setText(R.string.label_show_favorite);
            indicate.setImageResource(R.drawable.select_btn_3g);
            indicate.setSelected(mIsShowFavorite);
        }else if(resId == R.id.set_follow){
            name.setText(R.string.label_show_follow);
            indicate.setImageResource(R.drawable.select_btn_3g);
            indicate.setSelected(mIsShowFollow);
        }else if(resId == R.id.set_fans){
            name.setText(R.string.label_show_fans);
            indicate.setImageResource(R.drawable.select_btn_3g);
            indicate.setSelected(mIsShowFans);
        }
    }

    private TextView getFunNameTv(View v){
        return (TextView) v.findViewById(R.id.tv_function_name);
    }

    private TextView getFunDetailTv(View v){
        return (TextView) v.findViewById(R.id.tv_function_detail);
    }

    private ImageView getFunIndicateIv(View v){
        return (ImageView) v.findViewById(R.id.iv_indicate_img);
    }

    @OnClick({R.id.set_favorite,R.id.set_follow,R.id.set_fans})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.set_favorite:
                mIsShowFavorite = !mIsShowFavorite;
                ImageView funIndicateIv = getFunIndicateIv(this.findViewById(R.id.set_favorite));
                funIndicateIv.setSelected(!funIndicateIv.isSelected());
                mPresenter.modifySecret(mIsShowFavorite,0);
                break;

            case R.id.set_follow:
                mIsShowFollow = !mIsShowFollow;
                ImageView funIndicateIv1 = getFunIndicateIv(this.findViewById(R.id.set_follow));
                funIndicateIv1.setSelected(!funIndicateIv1.isSelected());
                mPresenter.modifySecret(mIsShowFollow,1);
                break;

            case R.id.set_fans:
                mIsShowFans = !mIsShowFans;
                ImageView funIndicateIv2 = getFunIndicateIv(this.findViewById(R.id.set_fans));
                funIndicateIv2.setSelected(!funIndicateIv2.isSelected());
                mPresenter.modifySecret(mIsShowFans,2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("show_fans",mIsShowFans);
        i.putExtra("show_favorite",mIsShowFavorite);
        i.putExtra("show_follow",mIsShowFollow);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void showUpdateDialog(AppUpdateEntity entity) {

    }

    @Override
    public void logoutSuccess() {

    }

    @Override
    public void modifySecretFail(int type) {
        if(type == 0){
            mIsShowFavorite = !mIsShowFavorite;
            ImageView funIndicateIv = getFunIndicateIv(this.findViewById(R.id.set_favorite));
            funIndicateIv.setSelected(!funIndicateIv.isSelected());
        }else if (type == 1){
            mIsShowFollow = !mIsShowFollow;
            ImageView funIndicateIv1 = getFunIndicateIv(this.findViewById(R.id.set_follow));
            funIndicateIv1.setSelected(!funIndicateIv1.isSelected());
        }else if(type == 2){
            mIsShowFans = !mIsShowFans;
            ImageView funIndicateIv1 = getFunIndicateIv(this.findViewById(R.id.set_fans));
            funIndicateIv1.setSelected(!funIndicateIv1.isSelected());
        }
    }

    @Override
    public void noUpdate() {

    }

    @Override
    public void shieldUserFail() {

    }

    @Override
    public void onFailure(int code,String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
