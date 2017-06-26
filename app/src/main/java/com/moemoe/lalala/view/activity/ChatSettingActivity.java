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
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/12/16.
 */

public class ChatSettingActivity extends BaseAppCompatActivity implements SettingContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_delete)
    TextView mTvDelete;

    private boolean mIsShield;
    private String mTalkId;

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
        mIsShield = getIntent().getBooleanExtra("shield",false);
        mTalkId = getIntent().getStringExtra("talk_id");
        mTvTitle.setText(getString(R.string.label_secret_setting));
        initStyleView(R.id.set_favorite);
        mTvDelete.setVisibility(View.VISIBLE);
        goneView(R.id.set_fans);
        goneView(R.id.set_follow);
    }

    private void goneView(int resId){
        View v = findViewById(resId);
        v.setVisibility(View.GONE);
    }

    private void initStyleView(int resId){
        View v = findViewById(resId);
        TextView name = getFunNameTv(v);
        ImageView indicate = getFunIndicateIv(v);
        if(resId == R.id.set_favorite){
            name.setText(R.string.label_shield);
            indicate.setImageResource(R.drawable.select_btn_3g);
            indicate.setSelected(mIsShield);
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

    @OnClick({R.id.set_favorite,R.id.tv_delete})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.set_favorite:
                mIsShield = !mIsShield;
                ImageView funIndicateIv = getFunIndicateIv(this.findViewById(R.id.set_favorite));
                funIndicateIv.setSelected(!funIndicateIv.isSelected());
                mPresenter.shieldUser(mIsShield,mTalkId);
                break;
            case R.id.tv_delete:
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createPromptNormalDialog(ChatSettingActivity.this,getString( R.string.label_delete_confirm));
                alertDialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        Intent i = new Intent();
                        i.putExtra("shield",mIsShield);
                        i.putExtra("delete",true);
                        setResult(RESULT_OK,i);
                        finish();
                        alertDialogUtil.dismissDialog();
                    }
                });
                alertDialogUtil.showDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("shield",mIsShield);
        i.putExtra("delete",false);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
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

    }

    @Override
    public void noUpdate() {

    }

    @Override
    public void shieldUserFail() {
        mIsShield = !mIsShield;
        ImageView funIndicateIv = getFunIndicateIv(this.findViewById(R.id.set_favorite));
        funIndicateIv.setSelected(!funIndicateIv.isSelected());
    }

    @Override
    public void onFailure(int code,String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

}
