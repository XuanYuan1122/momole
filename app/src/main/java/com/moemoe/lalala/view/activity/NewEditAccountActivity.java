package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerEditAccountComponent;
import com.moemoe.lalala.di.modules.EditAccountModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.UserInfo;
import com.moemoe.lalala.presenter.EditAccountContract;
import com.moemoe.lalala.presenter.EditAccountPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.datetimepicker.date.DatePickerDialog;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2016/12/15.
 */

public class NewEditAccountActivity extends BaseAppCompatActivity implements EditAccountContract.View{
    public static final int REQ_EDIT = 6666;
    private final String DATEPICKER_TAG = "datepicker";
    private final int LIMIT_NICK_NAME = 10;
    private final int LIMIT_SIGN = 50;
    private final int REQ_TAKE_PHOTO = 1001;
    private final int REQ_GET_FROM_GALLERY = 1002;
    private final int REQ_CROP_AVATAR = 1003;
    private final int REQ_SECRET = 1004;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.tv_name)
    TextView mTvNickName;
    @BindView(R.id.tv_gender)
    TextView mTvGender;
    @BindView(R.id.tv_sign)
    TextView mTvSign;
    @BindView(R.id.tv_birthday)
    TextView mTvBirthday;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;

    @Inject
    EditAccountPresenter mPresenter;
    private DatePickerDialog mDatePickerDialog;
    private UserInfo mInfo;
    private String mBgPath;
    private Uri mTmpAvatar;
    private String mRawAvatarPath;
    private String mUploadPath;
    private boolean mIsNickname;
    private boolean mHasModified = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_new_edit;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerEditAccountComponent.builder()
                .editAccountModule(new EditAccountModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mInfo = getIntent().getParcelableExtra("info");
        mTvSave.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvSave,DensityUtil.dip2px(this,18));
        mTvSave.getPaint().setFakeBoldText(true);
        mTvSave.setText(getString(R.string.label_save_modify));
        mTvTitle.setText(getString(R.string.label_edit_personal_data));
        mDatePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                if(mInfo != null) mInfo.setBirthday(year + "-" + (month + 1) + "-" + day);
                mTvBirthday.setText(year + "-" + (month + 1) + "-" + day);
                mHasModified = true;
            }
        }, 1990, 6, 1, false);
        mDatePickerDialog.setYearRange(1949, 2016);
        mDatePickerDialog.setCloseOnSingleTapDay(true);
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mTvSendComment.setEnabled(false);
                } else {
                    mTvSendComment.setEnabled(true);
                }

            }
        });
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    mKlCommentBoard.setVisibility(View.GONE);
                }
            }
        });
        mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEdtCommentInput.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    SoftKeyboardUtils.dismissSoftKeyboard(NewEditAccountActivity.this);
                    if(mIsNickname){
                        if(content.length() > LIMIT_NICK_NAME){
                            mTvNickName.setSelected(true);
                        }else {
                            mTvNickName.setSelected(false);
                        }
                        mTvNickName.setText(content);
                    }else {
                        if(content.length() > LIMIT_SIGN){
                            mTvSign.setSelected(true);
                        }else {
                            mTvSign.setSelected(false);
                        }
                        mTvSign.setText(content);
                    }
                    mHasModified = true;
                }else {
                    showToast(R.string.msg_doc_comment_not_empty);
                }
            }
        });
        updateView();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @OnClick({R.id.tv_menu,R.id.ll_head_root,R.id.ll_bg_root,R.id.ll_birthday,R.id.ll_gender,R.id.ll_secret,R.id.ll_nickname,R.id.ll_sign})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_menu:
                modify();
                break;
            case R.id.ll_head_root:
                go2getAvatar();
                break;
            case R.id.ll_bg_root:
                try {
                    ArrayList<String> arrayList = new ArrayList<>();
                    DialogUtils.createImgChooseDlg(this, null,this, arrayList, 1).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_birthday:
                mDatePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                break;
            case R.id.ll_gender:
                showListDialog();
                break;
            case R.id.ll_secret:
                Intent i = new Intent(this,SecretSettingActivity.class);
                i.putExtra("show_fans",mInfo.isShowFans());
                i.putExtra("show_favorite",mInfo.isShowFavorite());
                i.putExtra("show_follow",mInfo.isShowFollow());
                startActivityForResult(i,REQ_SECRET);
                break;
            case R.id.ll_nickname:
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                mEdtCommentInput.setHint("昵称");
                mEdtCommentInput.requestFocus();
                mIsNickname = true;
                SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
                break;
            case R.id.ll_sign:
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                mEdtCommentInput.setHint("签名");
                mEdtCommentInput.requestFocus();
                mIsNickname = false;
                SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
                break;
        }
    }

    /**
     * 获取头像：
     */
    private void go2getAvatar() {
        com.moemoe.lalala.dialog.AlertDialog.Builder builder = new com.moemoe.lalala.dialog.AlertDialog.Builder(this);
        builder.setTitle(R.string.label_take_avatar);
        CharSequence[] items = new String[] { this.getString(R.string.label_take_photo),
                this.getString(R.string.label_get_picture) };

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mTmpAvatar == null) {
                    mTmpAvatar = Uri.fromFile(StorageUtils.getTempFile("ava" + System.currentTimeMillis() + ".jpg"));
                }

                if (which == 0) {
                    // 拍照
                    try {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, mTmpAvatar);
                        startActivityForResult(i, REQ_TAKE_PHOTO);// CAMERA_WITH_DATA
                    } catch (Exception e) {
                        Toast.makeText(NewEditAccountActivity.this, getString(R.string.msg_no_system_camera),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(NewEditAccountActivity.this, MultiImageChooseActivity.class);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, 1);
                    startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                }
            }
        });
        try {
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showListDialog() {
        final String[] items = { "男","女"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(this);
        listDialog.setTitle("选择性别");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    mTvGender.setText("男");
                    mInfo.setSex("F");
                }else {
                    mTvGender.setText("女");
                    mInfo.setSex("M");
                }
                mHasModified = true;
            }
        });
        listDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GET_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                // file
                if (data != null) {
                    ArrayList<String> paths = data
                            .getStringArrayListExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS);
                    if (paths != null && paths.size() == 1) {
                        mRawAvatarPath = paths.get(0);
                    }
                    go2CropAvatar(0,mRawAvatarPath);
                }
            }
        } else if (requestCode == REQ_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                mRawAvatarPath = mTmpAvatar.getPath();
                go2CropAvatar(0,mRawAvatarPath);
            }
        } else if (requestCode == REQ_CROP_AVATAR) {
            if (resultCode == Activity.RESULT_OK) {
                if(!NetworkUtils.isNetworkAvailable(this)){
                    showToast(R.string.msg_connection);
                    return;
                }
                createDialog();
                mPresenter.uploadAvatar(data.getStringExtra("path"),data.getIntExtra("type",0));
                mHasModified = true;
            }
        }else if(requestCode == REQ_SECRET){
            if(resultCode == RESULT_OK){
                mInfo.setShowFans(data.getBooleanExtra("show_fans",false));
                mInfo.setShowFavorite(data.getBooleanExtra("show_favorite",false));
                mInfo.setShowFollow(data.getBooleanExtra("show_follow",false));
            }
        } else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    mBgPath = photoPaths.get(0);
                    go2CropAvatar(1,mBgPath);
                }
            });
        }
    }

    private void go2CropAvatar(int type,String path) {
        Intent intent = new Intent(this, CropAvatarActivity.class);
        intent.putExtra(CropAvatarActivity.EXTRA_RAW_IMG_PATH, path);
        intent.putExtra("type",type);
        if(type == 1){
            intent.putExtra(CropAvatarActivity.EXTRA_W_RATIO,36);
            intent.putExtra(CropAvatarActivity.EXTRA_H_RATIO,23);
        }
        startActivityForResult(intent, REQ_CROP_AVATAR);
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
    public void uploadSuccess(String path,int type) {
        finalizeDialog();
        if(type == 0){
            Glide.with(this)
                    .load(StringUtils.getUrl(this,ApiService.URL_QINIU + path,DensityUtil.dip2px(this,50), DensityUtil.dip2px(this,50),false,true))
                    .override(DensityUtil.dip2px(this,50), DensityUtil.dip2px(this,50))
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .error(R.drawable.bg_default_circle)
                    .into(mIvAvatar);
            mUploadPath = path;
        }else {
            Glide.with(this)
                    .load(StringUtils.getUrl(this,ApiService.URL_QINIU + path,DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56),false,true))
                    .override(DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(mIvBg);
            mBgPath = path;
        }
    }

    @Override
    public void uploadFail(int type) {
        finalizeDialog();
        if(type == 0){
            mRawAvatarPath = "";
        }else {
            mBgPath = "";
        }
    }

    private void modify(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        mInfo.setUserName(mTvNickName.getText().toString());
        if (TextUtils.isEmpty(mInfo.getUserName())) {
            showToast(R.string.msg_nickname_cannot_null);
            return;
        }
        if (mInfo.getUserName().length() > LIMIT_NICK_NAME) {
            showToast(R.string.msg_nickname_too_long);
            return;
        }
        if (mTvSign.getText().toString().length() > LIMIT_SIGN) {
            showToast(R.string.msg_sign_too_long);
            return;
        }
        if (StringUtils.isLeagleNickName(mInfo.getUserName())) {
            showToast(R.string.msg_nickname_illegal);
            return;
        }
        if(!TextUtils.isEmpty(mUploadPath)){
            mInfo.setHeadPath(ApiService.URL_QINIU + mUploadPath);
        }
        if(!TextUtils.isEmpty(mBgPath)){
            mInfo.setBackground(mBgPath);
        }
        mInfo.setSignature(mTvSign.getText().toString());
        createDialog();
        mPresenter.modify(mInfo.getUserName(),mInfo.getSex(), mInfo.getBirthday(),mInfo.getBackground(),mInfo.getHeadPath(),mInfo.getSignature());
    }

    @Override
    public void modifySuccess() {
        finalizeDialog();
        showToast(R.string.msg_modify_my_data_success);
        PreferenceUtils.getAuthorInfo().setHeadPath(mInfo.getHeadPath());
        PreferenceUtils.getAuthorInfo().setUserName(mInfo.getUserName());
        Intent i = new Intent();
        i.putExtra("info",mInfo);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mHasModified) {
            DialogUtils.showAbandonModifyDlg(this);
        } else {
            super.onBackPressed();
        }
    }

    private void updateView(){
        Glide.with(this)
                .load(StringUtils.getUrl(this,mInfo.getHeadPath(), DensityUtil.dip2px(this,50),DensityUtil.dip2px(this,50),false,true))
                .override(DensityUtil.dip2px(this,50),DensityUtil.dip2px(this,50))
                .bitmapTransform(new CropCircleTransformation(this))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .into(mIvAvatar);
        Glide.with(this)
                .load(StringUtils.getUrl(this, ApiService.URL_QINIU + mInfo.getBackground(), DensityUtil.dip2px(this,56),DensityUtil.dip2px(this,56),false,true))
                .override(DensityUtil.dip2px(this,56),DensityUtil.dip2px(this,56))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into(mIvBg);
        mTvNickName.setText(mInfo.getUserName());
        mTvGender.setText(mInfo.getSex().equals("F") ? "男" : "女");
        mTvBirthday.setText(mInfo.getBirthday());
        mTvSign.setText(mInfo.getSignature());
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
