package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerAddAddressComponent;
import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.AddressPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/7/17.
 */

public class AddAddressActivity extends BaseAppCompatActivity implements AddressContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_menu)
    TextView mMenu;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_address)
    EditText mEtAddress;

    @Inject
    AddressPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_add_address;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerAddAddressComponent.builder()
                .addAddressModule(new AddAddressModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        AddressEntity entity = getIntent().getParcelableExtra("address");
        if(entity != null && !TextUtils.isEmpty(entity.getAddress())){
            mEtName.setText(entity.getUserName());
            mEtPhone.setText(entity.getPhone());
            mEtAddress.setText(entity.getAddress());
        }else {
            createDialog();
            mPresenter.loadUserAddress();
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText(getString(R.string.label_order_address));
        mMenu.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mMenu, (int)getResources().getDimension(R.dimen.x36));
        mMenu.setText(getString(R.string.label_save));
        mMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String name = mEtName.getText().toString();
                String phone = mEtPhone.getText().toString();
                String address = mEtAddress.getText().toString();
                if(!TextUtils.isEmpty(name)
                        && !TextUtils.isEmpty(phone)
                        && !TextUtils.isEmpty(address)){
                    AddressEntity entity =  new AddressEntity(address, phone, name);
                    createDialog();
                    mPresenter.saveUserAddress(entity);
                }else {
                    showToast(getString(R.string.msg_input_not_empty));
                }
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }

    @Override
    public void onLoadAddressSuccess(AddressEntity entity) {
        finalizeDialog();
        mEtName.setText(entity.getUserName());
        mEtPhone.setText(entity.getPhone());
        mEtAddress.setText(entity.getAddress());
    }

    @Override
    public void onSaveAddressSuccess() {
        finalizeDialog();
        Intent i = new Intent();
        String name = mEtName.getText().toString();
        String phone = mEtPhone.getText().toString();
        String address = mEtAddress.getText().toString();
        AddressEntity entity =  new AddressEntity(address, phone, name);
        i.putExtra("address",entity);
        setResult(RESULT_OK, i);
        finish();
    }
}
