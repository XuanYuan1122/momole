package com.moemoe.lalala;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.app.AlertDialog;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.datetimepicker.date.DatePickerDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

;

/**
 * Created by Haru on 2016/4/30 0030.
 */
@ContentView(R.layout.ac_edit_personal)
public class EditAccountActivity extends BaseActivity {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final int LIMIT_NICK_NAME = 10;
    private static final int REQ_TAKE_PHOTO = 1001;
    private static final int REQ_GET_FROM_GALLERY = 1002;
    private static final int REQ_CROP_AVATAR = 1003;
    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_sava)
    private TextView mSava;
    @FindView(R.id.rb_group_sex)
    private RadioGroup mRg;
    @FindView(R.id.rb_female)
    private RadioButton mRbFemale;
    @FindView(R.id.rb_male)
    private RadioButton mRbMale;
    @FindView(R.id.edt_person_nickname)
    private EditText mEdtNickName;
    @FindView(R.id.tv_content_birthday)
    private TextView mTvBirthday;
    @FindView(R.id.iv_avatar)
    private ImageView mIvAvatar;
    private AuthorInfo mAuthorInfo;
    private DatePickerDialog mDatePickerDialog;

    private boolean mSetSex = false;
    private Uri mTmpAvatar;
    private String mRawAvatarPath;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.main_title_cyan);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAuthorInfo = mPreferMng.getThirdPartyLoginMsg();
        mSava.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                modify();
            }
        });
        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mSetSex = true;
                if (checkedId == R.id.rb_female) {
                    mAuthorInfo.setmGender(PersonBean.SEX_FEMALE);
                } else if (checkedId == R.id.rb_male) {
                    mAuthorInfo.setmGender(PersonBean.SEX_MALE);
                }
            }
        });
        mIvAvatar.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                go2getAvatar();
            }
        });
        mDatePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                mAuthorInfo.setBirthday(new Date(year - 1900, month, day).getTime());
                mTvBirthday.setText(year + "-" + (month + 1) + "-" + day);
            }
        }, 1990, 6, 1, false);
        mDatePickerDialog.setYearRange(1949, 2016);
        mDatePickerDialog.setCloseOnSingleTapDay(true);
        mTvBirthday.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                mDatePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });
        mEdtNickName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > LIMIT_NICK_NAME || !StringUtils.isLeagleNickName(s.toString())) {
                    mEdtNickName.setSelected(true);
                } else {
                    mEdtNickName.setSelected(false);
                }

            }
        });

        if(mAuthorInfo != null){
//            Utils.image().bind(mIvAvatar, mAuthorInfo.getmHeadPath(), new ImageOptions.Builder()
//                    .setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
//                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                    .setLoadingDrawableId(R.drawable.ic_default_avatar_l)
//                    .setFailureDrawableId(R.drawable.ic_default_avatar_l)
//                    .build());
            Picasso.with(this)
                    .load(mAuthorInfo.getmHeadPath())
                    .resize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
                    .placeholder(R.drawable.ic_default_avatar_l)
                    .error(R.drawable.ic_default_avatar_l)
                    .config(Bitmap.Config.RGB_565)
                    .into(mIvAvatar);
            mEdtNickName.setText(mAuthorInfo.getmUserName());
            mTvBirthday.setText(StringUtils.getNormalDate(mAuthorInfo.getBirthday()));
            if (PersonBean.SEX_FEMALE.equals(mAuthorInfo.getmGender())) {
                mRbFemale.setChecked(true);
                mRbMale.setChecked(false);
            } else if (PersonBean.SEX_MALE.equals(mAuthorInfo.getmGender())) {
                mRbFemale.setChecked(false);
                mRbMale.setChecked(true);
            } else {
                mRbFemale.setChecked(false);
                mRbMale.setChecked(false);
            }
        }
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
                    go2CropAvatar();
                }

            }
        } else if (requestCode == REQ_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                mRawAvatarPath = mTmpAvatar.getPath();
                go2CropAvatar();
            }
        } else if (requestCode == REQ_CROP_AVATAR) {
            if (resultCode == Activity.RESULT_OK) {
                uploadAvatar(data.getData());
            }
        }
    }


    /**
     * 上传头像
     *
     * @param uri
     */
    private void uploadAvatar(Uri uri) {
        if(!NetworkUtils.isNetworkAvailable(this)){
            ToastUtil.showCenterToast(this,R.string.a_server_msg_connection);
            return;
        }
        final Image fb = new Image();
        fb.local_path = uri.getPath();
        createDialog();
        Otaku.getAccountV2().requestQnFileKey(mAuthorInfo.getmToken(), -1, fb.local_path, new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                fb.path = s;
                if (fb.path != null) {
                    Otaku.getCommonV2().modifyMyIcon(mAuthorInfo.getmToken(), fb.path).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            finalizeDialog();
                            fb.path = StringUtils.getUrl(EditAccountActivity.this,fb.path,fb.w,fb.h);
                            if (mIvAvatar.getWidth() > 0) {
                                Picasso.with(EditAccountActivity.this)
                                        .load(fb.path)
                                        .resize(mIvAvatar.getWidth(), mIvAvatar.getWidth())
                                        .placeholder(R.drawable.ic_default_avatar_l)
                                        .error(R.drawable.ic_default_avatar_l)
                                        .config(Bitmap.Config.RGB_565)
                                        .into(mIvAvatar);
                            } else {
                                Picasso.with(EditAccountActivity.this)
                                        .load(fb.path)
                                        .resize(400, 400)
                                        .placeholder(R.drawable.ic_default_avatar_l)
                                        .config(Bitmap.Config.RGB_565)
                                        .error(R.drawable.ic_default_avatar_l)
                                        .into(mIvAvatar);
                            }
                            ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_avatar_success);
                            mAuthorInfo.setmHeadPath(fb.path);
                        }

                        @Override
                        public void failure(String e) {
                            finalizeDialog();
                            ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_avatar_fail);
                        }
                    }));
                }
            }

            @Override
            public void failure(String e) {
                finalizeDialog();
            }
        });
//        Otaku.getAccount().requestQnFileKey(mAuthorInfo.getmToken(),-1, fb.local_path, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                fb.path = result;
//                if (fb.path != null) {
//                    Otaku.getCommon().modifyMyIcon(mAuthorInfo.getmToken(), fb.path, new InterceptCallback<String>() {
//                        @Override
//                        public void beforeRequest(UriRequest request) throws Throwable {
//
//                        }
//
//                        @Override
//                        public void afterRequest(UriRequest request) throws Throwable {
//
//                        }
//
//                        @Override
//                        public void onSuccess(String result) {
//                            finalizeDialog();
//                            try{
//                                JSONObject json = new JSONObject(result);
//                                if(json.optInt("ok") == Otaku.SERVER_OK){
//                                   // fb.path =  Otaku.URL_QINIU +  fb.path;
//                                    fb.path = StringUtils.getUrl(EditAccountActivity.this,fb.path,fb.w,fb.h);
//                                    if (mIvAvatar.getWidth() > 0) {
//                                        Utils.image().bind(mIvAvatar, fb.path, new ImageOptions.Builder()
//                                                .setSize(mIvAvatar.getWidth(), mIvAvatar.getWidth())
//                                                .setLoadingDrawableId(R.drawable.ic_default_avatar_l)
//                                                .setFailureDrawableId(R.drawable.ic_default_avatar_l)
//                                                .build());
//                                    } else {
//                                        Utils.image().bind(mIvAvatar, fb.path, new ImageOptions.Builder()
//                                                .setSize(400, 400)
//                                                .setLoadingDrawableId(R.drawable.ic_default_avatar_l)
//                                                .setFailureDrawableId(R.drawable.ic_default_avatar_l)
//                                                .build());
//                                    }
//                                    ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_avatar_success);
//                                    mAuthorInfo.setmHeadPath(fb.path);
//                                }else {
//                                    String err = json.optString("error_code");
//                                    if(TextUtils.isEmpty(err)){
//                                        err = json.optString("data");
//                                    }
//                                    if(!TextUtils.isEmpty(err) && err.contains("TOKEN")){
//                                        String uuid = mPreferMng.getUUid();
//                                        if(!TextUtils.isEmpty(uuid)){
//                                            tryLoginFirst(null);
//                                        }
//                                    }
//                                    ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_avatar_fail);
//                                }
//                            }catch (Exception e){
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable ex, boolean isOnCallback) {
//                            finalizeDialog();
//                            ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_avatar_fail);
//                        }
//
//                        @Override
//                        public void onCancelled(CancelledException cex) {
//                            finalizeDialog();
//                        }
//
//                        @Override
//                        public void onFinished() {
//                            finalizeDialog();
//                        }
//                    },EditAccountActivity.this);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                finalizeDialog();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//                finalizeDialog();
//            }
//        });
    }

    /**
     * 获取头像：
     */
    private void go2getAvatar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        Toast.makeText(EditAccountActivity.this, getString(R.string.msg_no_system_camera),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(EditAccountActivity.this, MultiImageChooseActivity.class);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, 1);
                    // intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS, selected);
                    startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                }
            }
        });
        try {
            builder.create().show();
        } catch (Exception e) {
        }
    }

    private void go2CropAvatar() {
        Intent intent = new Intent(this, CropAvatarActivity.class);
        intent.putExtra(CropAvatarActivity.EXTRA_RAW_IMG_PATH, mRawAvatarPath);
        startActivityForResult(intent, REQ_CROP_AVATAR);
    }

    private void modify() {
        if(!NetworkUtils.isNetworkAvailable(this)){
            ToastUtil.showToast(this,R.string.msg_server_connection);
            return;
        }
        mAuthorInfo.setmUserName(mEdtNickName.getText().toString());
        if (TextUtils.isEmpty(mAuthorInfo.getmUserName())) {
            ToastUtil.showToast(this, R.string.msg_nickname_cannot_null);
            return;
        }
        if (mAuthorInfo.getmUserName().length() > LIMIT_NICK_NAME) {
            ToastUtil.showToast(this, R.string.msg_nickname_too_long);
            return;
        }
        if (!StringUtils.isLeagleNickName(mAuthorInfo.getmUserName())) {
            ToastUtil.showToast(this, R.string.msg_nickname_illegal);
            return;
        }
        createDialog();
        Otaku.getCommonV2().modifyAll(mAuthorInfo.getmToken(), mAuthorInfo.getmUserName(), mAuthorInfo.getmGender(), StringUtils.toServerDateString(mAuthorInfo.getBirthday())).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_my_data_success);
                mPreferMng.saveThirdPartyLoginMsg(mAuthorInfo);
                finish();
            }

            @Override
            public void failure(String e) {
                finalizeDialog();
                ToastUtil.showToast(EditAccountActivity.this, R.string.msg_modify_my_data_fail);
            }
        }));
    }
}
