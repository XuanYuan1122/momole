package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneEditGroupComponent;
import com.moemoe.lalala.di.modules.PhoneEditGroupModule;
import com.moemoe.lalala.model.entity.GroupEditEntity;
import com.moemoe.lalala.presenter.PhoneGroupEditContract;
import com.moemoe.lalala.presenter.PhoneGroupEditPresenter;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

/**
 * 手机群组界面
 * Created by yi on 2017/9/4.
 */

public class PhoneEditGroupV2Fragment extends BaseFragment implements IPhoneFragment,PhoneGroupEditContract.View{

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_desc)
    EditText mEtDesc;

    @Inject
    PhoneGroupEditPresenter mPresent;

    private String cover;
    private String type;
    private String id;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public static PhoneEditGroupV2Fragment newInstance(String type, String id){
        PhoneEditGroupV2Fragment fragment = new PhoneEditGroupV2Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        if("create".equals(type)){
            fragment.setTitle("申请创建群聊");
        }else {
            fragment.setTitle("修改群信息");
        }
        bundle.putString("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PhoneEditGroupV2Fragment newInstance(String type){
        PhoneEditGroupV2Fragment fragment = new PhoneEditGroupV2Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        if("create".equals(type)){
            fragment.setTitle("申请创建群聊");
        }else {
            fragment.setTitle("修改群信息");
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_group_edit;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneEditGroupComponent.builder()
                .phoneEditGroupModule(new PhoneEditGroupModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        type = getArguments().getString("type");
        id = getArguments().getString("id");
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtName.getText();
                int len = editable.length();
                if (len > 10) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtName.setText(editable.subSequence(0, 10));
                    editable = mEtName.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtDesc.getText();
                int len = editable.length();
                if (len > 20) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtDesc.setText(editable.subSequence(0, 20));
                    editable = mEtDesc.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.ll_cover_root})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_cover_root:
                try {
                    ArrayList<String> arrayList = new ArrayList<>();
                    DialogUtils.createImgChooseDlg(getActivity(), this,getContext(), arrayList, 1).show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void release(){
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DialogUtils.handleImgChooseResult(getContext(), requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

            @Override
            public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                int size = (int) getResources().getDimension(R.dimen.y112);
                Glide.with(PhoneEditGroupV2Fragment.this)
                        .load(photoPaths.get(0))
                        .override(size, size)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropSquareTransformation(getContext()))
                        .into(mIvCover);
                cover = photoPaths.get(0);
            }
        });
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return R.drawable.btn_alarm_save;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onMenuClick() {
        String name = mEtName.getText().toString();
        String desc = mEtDesc.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)){
            ToastUtils.showShortToast(getContext(),"名称或简介不能为空");
            return;
        }
        GroupEditEntity editEntity = new GroupEditEntity();
        editEntity.cover = cover;
        editEntity.groupName = name;
        editEntity.desc = desc;
        if("create".equals(type)){
            mPresent.createGroup(editEntity);
        }else {
            mPresent.updateGroup(id,editEntity);
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    @Override
    public void onEditSuccess() {
        ToastUtils.showShortToast(getContext(),"操作成功");
        ((PhoneMainV2Activity)getContext()).onBackPressed();
    }
}
