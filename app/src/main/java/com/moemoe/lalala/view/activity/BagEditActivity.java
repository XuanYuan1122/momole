package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBagComponent;
import com.moemoe.lalala.di.modules.BagModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.BookEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.presenter.BagContract;
import com.moemoe.lalala.presenter.BagPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.adapter.SelectItemAdapter;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/1/18.
 */

public class BagEditActivity extends BaseAppCompatActivity implements BagContract.View{

    public static final int RES_DELETE = 3001;
    private final int REQ_CROP_AVATAR = 1003;
    public static final String EXTRA_TYPE = "extra_type";
    private final int REQ_GET_FROM_SELECT_MUSIC = 1004;
    private final int REQ_GET_FROM_SELECT_BOOK = 1005;
    public static final int TYPE_BAG_OPEN = 2000;
    public static final int TYPE_BAG_MODIFY = 2001;
    public static final int TYPE_DIR_CREATE = 2002;
    public static final int TYPE_DIR_ITEM_ADD = 2003;
    public static final int TYPE_DIR_MODIFY = 2004;
    public static final int TYPE_READ_CHANGE = 2005;

    private final int LIMIT_NICK_NAME = 10;
    private final int ICON_NUM_LIMIT = 9;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.stub_read_type)
    ViewStub mStubReadType;
    @BindView(R.id.tv_folder_name)
    TextView mTvFolderName;
    @BindView(R.id.tv_have_coin)
    TextView mTvHaveCoin;
    @BindView(R.id.tv_delete)
    TextView mTvDelete;
    @BindView(R.id.iv_cover)
    ImageView mIvBg;
    @BindView(R.id.stub_name)
    ViewStub mStubNameRoot;
    @BindView(R.id.ll_bg_root)
    LinearLayout mLlBgRoot;
    @BindView(R.id.stub_coin)
    ViewStub mStubCoinRoot;
    @BindView(R.id.ll_images)
    LinearLayout mLlImages;
    @BindView(R.id.ll_add_root)
    LinearLayout mLlAddRoot;
    @BindView(R.id.rv_img)
    RecyclerView mRvImg;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.iv_add_cover)
    ImageView mIvAddCover;

    @Inject
    BagPresenter mPresenter;

    private TextView mTvNameLabel;
    private TextView mTvName;
    private TextView mTvCoin;
    private TextView mModeName;

    private boolean mIsName;
    private boolean mIsCover;
    private boolean mHasModified = false;
    private int mType;
    private String mBgPath;
    private String mName;
    private String folderId;
    private String readMode;
    private long size;
    private boolean isBuy;
    private int mCoin;
    private SelectItemAdapter mSelectAdapter;
    private ArrayList<Object> mItemPaths = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Object> items = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bag_edit;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AndroidBug5497Workaround.assistActivity(this);
        DaggerBagComponent.builder()
                .bagModule(new BagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = getIntent().getIntExtra(EXTRA_TYPE,-1);
        mBgPath = getIntent().getStringExtra("bg");
        mName = getIntent().getStringExtra("name");
        isBuy = getIntent().getBooleanExtra("isBuy",false);
        folderId = getIntent().getStringExtra("folderId");
        mCoin = getIntent().getIntExtra("coin",0);
        size = getIntent().getLongExtra("size",0);
        readMode = "";
        readMode = getIntent().getStringExtra("read_type");
        if(mType == -1) {
            finish();
            return;
        }
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getString(R.string.label_done));
        if(mType != TYPE_DIR_ITEM_ADD){
            initNameRoot();
        }
        if(mType == TYPE_DIR_CREATE || mType == TYPE_DIR_MODIFY){
            initCoinRoot();
            initReadTypeRoot();
        }

        if(mType == TYPE_BAG_OPEN || mType == TYPE_BAG_MODIFY){
            mTvNameLabel.setText(getString(R.string.label_bag_name));
            mTvTitle.setText(getString(R.string.label_modify));
            if(mType == TYPE_BAG_MODIFY){
                mTvName.setText(mName);
                if(mBgPath.startsWith("/")){
                    Glide.with(this)
                            .load(mBgPath)
                            .override(DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56))
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .into(mIvBg);
                }else {
                    Glide.with(this)
                            .load(StringUtils.getUrl(this, ApiService.URL_QINIU + mBgPath, DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56),false,true))
                            .override(DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56))
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .into(mIvBg);
                }
            }
        }else if(mType == TYPE_DIR_CREATE){
            mTvNameLabel.setText(getString(R.string.label_dir_name));
            mTvTitle.setText(getString(R.string.label_create));
            mLlImages.setVisibility(View.VISIBLE);
            mSelectAdapter = new SelectItemAdapter(this);
            LinearLayoutManager selectRvL = new LinearLayoutManager(this);
            selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRvImg.setLayoutManager(selectRvL);
            mRvImg.setAdapter(mSelectAdapter);
            mTvCoin.setText("0节操");
        }else if(mType == TYPE_DIR_ITEM_ADD){
            mTvTitle.setText(getString(R.string.label_add));
            mLlBgRoot.setVisibility(View.GONE);
            mLlAddRoot.setVisibility(View.VISIBLE);
            mLlImages.setVisibility(View.VISIBLE);
            mSelectAdapter = new SelectItemAdapter(this);
            LinearLayoutManager selectRvL = new LinearLayoutManager(this);
            selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRvImg.setLayoutManager(selectRvL);
            mRvImg.setAdapter(mSelectAdapter);
            mTvFolderName.setText(mName);
            mTvHaveCoin.setVisibility(isBuy ? View.VISIBLE : View.INVISIBLE);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,ApiService.URL_QINIU + mBgPath, DensityUtil.dip2px(this,40), DensityUtil.dip2px(this,40),false,true))
                    .override(DensityUtil.dip2px(this,40), DensityUtil.dip2px(this,40))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(mIvAddCover);
        }else if(mType == TYPE_DIR_MODIFY){
            mTvNameLabel.setText(getString(R.string.label_dir_name));
            mTvTitle.setText(getString(R.string.label_modify));
            mTvDelete.setVisibility(View.VISIBLE);
            mTvName.setText(mName);
            mTvCoin.setText(mCoin + "节操");
            Glide.with(this)
                    .load(StringUtils.getUrl(this,ApiService.URL_QINIU + mBgPath, DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56),false,true))
                    .override(DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(mIvBg);
        }

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
                    SoftKeyboardUtils.dismissSoftKeyboard(BagEditActivity.this);
                    if(mIsName){
                        if(content.length() > LIMIT_NICK_NAME){
                            mTvName.setSelected(true);
                        }else {
                            mTvName.setSelected(false);
                        }
                        mTvName.setText(content);
                    }
                    mHasModified = true;
                }else {
                    showToast(R.string.msg_doc_comment_not_empty);
                }
            }
        });
    }

    private void initReadTypeRoot(){
        View typeRoot = mStubReadType.inflate();
        TextView tvNameLabel = (TextView) typeRoot.findViewById(R.id.tv_name_label);
        typeRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(BagEditActivity.this,ReadTypeSettingActivity.class);
                i.putExtra("read_type",readMode);
                startActivityForResult(i,TYPE_READ_CHANGE);
            }
        });
        tvNameLabel.setText("默认浏览模式");
        mModeName = (TextView) typeRoot.findViewById(R.id.tv_name);
        mModeName.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        if(readMode.equalsIgnoreCase("IMAGE")){
            mModeName.setText("看图模式");
        }else if(readMode.equalsIgnoreCase("TEXT")){
            mModeName.setText("阅读模式");
        }else {
            mModeName.setText("正常模式");
        }
    }

    private void initCoinRoot() {
         View mLlCoin = mStubCoinRoot.inflate();
        TextView tvNameLabel = (TextView) mLlCoin.findViewById(R.id.tv_name_label);
        tvNameLabel.setText(getString(R.string.label_coin_set));
        mTvCoin = (TextView) mLlCoin.findViewById(R.id.tv_name);
        mTvCoin.setTextColor(ContextCompat.getColor(this,R.color.pink_fb7ba2));
        mLlCoin.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showDialog();
            }
        });
    }

    private void initNameRoot() {
        View mLlNameRoot =  mStubNameRoot.inflate();
        mTvNameLabel = (TextView) mLlNameRoot.findViewById(R.id.tv_name_label);
        mTvName = (TextView) mLlNameRoot.findViewById(R.id.tv_name);
        mTvName.setTextColor(ContextCompat.getColor(this,R.color.txt_gray_929292_red));
        mLlNameRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                if(mType == TYPE_BAG_OPEN || mType == TYPE_BAG_MODIFY){
                    mEdtCommentInput.setHint(getString(R.string.label_bag_name));
                }
                mEdtCommentInput.requestFocus();
                mIsName = true;
                SoftKeyboardUtils.showSoftKeyboard(BagEditActivity.this, mEdtCommentInput);
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick({R.id.tv_menu,R.id.ll_bg_root,R.id.tv_delete})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_menu:
                mTvSave.setEnabled(false);
                modify();
                break;
            case R.id.ll_bg_root:
                try {
                    mIsCover = true;
                    ArrayList<String> arrayList = new ArrayList<>();
                    DialogUtils.createImgChooseDlg(this, null,this, arrayList, 1).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_delete:
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createPromptNormalDialog(this, getString( R.string.label_delete_confirm));
                alertDialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        ArrayList<String> ids = new ArrayList<>();
                        ids.add(folderId);
                        mPresenter.deleteFolder(ids);
                        alertDialogUtil.dismissDialog();
                    }
                });
                alertDialogUtil.showDialog();
                break;
        }
    }

    private void showDialog(){
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        new AlertDialog.Builder(this).setTitle("节操价格")
                .setView(editText)
                .setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        long coin;
                        String coinStr = editText.getText().toString();
                        if(!TextUtils.isEmpty(coinStr)){
                            coin = Long.valueOf(coinStr);
                        }else {
                            coin = 0;
                        }
                        if(coin < 0){
                            showToast("请输入正确的价格");
                            return;
                        }
                        mTvCoin.setText(coin + "节操");
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void modify() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast(R.string.msg_connection);
            mTvSave.setEnabled(true);
            return;
        }
        if(mType == TYPE_BAG_OPEN || mType == TYPE_BAG_MODIFY){
            String name = mTvName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showToast(R.string.msg_name_cannot_null);
                return;
            }
            if(TextUtils.isEmpty(mBgPath)){
                showToast("封面不能为空");
                return;
            }
            createDialog();
            if (mBgPath.startsWith("/")){
                ArrayList<String> paths = new ArrayList<>();
                paths.add(mBgPath);
                images = BitmapUtils.handleUploadImage(paths);
            }else {
                images = new ArrayList<>();
                Image image = new Image();
                image.setPath(mBgPath);
                images.add(image);
            }
            onCheckSize(true);
        }else if(mType == TYPE_DIR_CREATE || mType == TYPE_DIR_ITEM_ADD){
            if(mType == TYPE_DIR_CREATE){
                String name = mTvName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    showToast(R.string.msg_name_cannot_null);
                    return;
                }
                if(TextUtils.isEmpty(mBgPath)){
                    showToast("封面不能为空");
                    return;
                }
                ArrayList<String> paths = new ArrayList<>();
                paths.add(mBgPath);
                images = BitmapUtils.handleUploadImage(paths);
            }
            createDialog();
            items = BitmapUtils.handleUploadItem(mItemPaths);
            long size = 0;
            if(mType == TYPE_DIR_CREATE){
                size = new File(images.get(0).getPath()).length();
            }else if(mType == TYPE_DIR_ITEM_ADD) {
                size = 0;
            }
            for (Object o : items){
                if(o instanceof Image){
                    size += new File(((Image) o).getPath()).length();
                }else if(o instanceof MusicLoader.MusicInfo){
                    size += new File(((MusicLoader.MusicInfo) o).getUrl()).length();
                }else if(o instanceof BookEntity){
                    size += new File(((BookEntity) o).getPath()).length();
                }
            }
            mPresenter.checkSize(size);
        }else if(mType == TYPE_DIR_MODIFY){
            String name = mTvName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showToast(R.string.msg_name_cannot_null);
                return;
            }
            if(TextUtils.isEmpty(mBgPath)){
                showToast("封面不能为空");
                return;
            }
            createDialog();
            if (mBgPath.startsWith("/")){
                ArrayList<String> paths = new ArrayList<>();
                paths.add(mBgPath);
                images = BitmapUtils.handleUploadImage(paths);
                long size = new File(images.get(0).getPath()).length();
                mPresenter.checkSize(size);
            }else {
                images = new ArrayList<>();
                Image image = new Image();
                image.setPath(mBgPath);
                images.add(image);
                onCheckSize(true);
            }
        }
    }

    @Override
    protected void initListeners() {
        if(mSelectAdapter != null){
            mSelectAdapter.setOnItemClickListener(new SelectItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(position == mItemPaths.size()){
                        choose();
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }

                @Override
                public void onAllDelete() {

                }
            });
        }
    }

    private void choose(){
        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
        alertDialogUtil.createSelectDialog(this);
        alertDialogUtil.setOnItemClickListener(new AlertDialogUtil.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (position){
                    case 0:
                        chooseItem(0);
                        alertDialogUtil.dismissDialog();
                        break;
                    case 1:
                        chooseItem(1);
                        alertDialogUtil.dismissDialog();
                        break;
                    case 2:
                        chooseItem(2);
                        alertDialogUtil.dismissDialog();
                        break;
                    case 3:
                        alertDialogUtil.dismissDialog();
                        break;
                }
            }
        });
        alertDialogUtil.showDialog();
    }

    private void chooseItem(int type){
        if (mItemPaths.size() < ICON_NUM_LIMIT) {
                if(type == 0){
                    try {
                        ArrayList<String> selected = new ArrayList<>();
                        for (Object o : mItemPaths){
                            if(o instanceof String){
                                selected.add((String) o);
                            }
                            if(o instanceof MusicLoader.MusicInfo){
                                selected.add("music" + ((MusicLoader.MusicInfo) o).getUrl());
                            }
                        }
                        mIsCover = false;
                        DialogUtils.createImgChooseDlg(this, null, this, selected, ICON_NUM_LIMIT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(type == 1){
                    Intent intent = new Intent(BagEditActivity.this, SelectMusicActivity.class);
                    startActivityForResult(intent,REQ_GET_FROM_SELECT_MUSIC);
                }else if(type == 2){
                    Intent intent = new Intent(BagEditActivity.this, SelectBookActivity.class);
                    startActivityForResult(intent,REQ_GET_FROM_SELECT_BOOK);
                }
        } else {
            showToast(R.string.msg_select_9_item);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CROP_AVATAR) {
            if (resultCode == Activity.RESULT_OK) {
                if(!NetworkUtils.isNetworkAvailable(this)){
                    showToast(R.string.msg_connection);
                    return;
                }
                mBgPath = data.getStringExtra("path");
                Glide.with(this)
                        .load(mBgPath)
                        .override(DensityUtil.dip2px(this,56), DensityUtil.dip2px(this,56))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(mIvBg);
                mHasModified = true;
            }
        }else if (requestCode == REQ_GET_FROM_SELECT_MUSIC) {
            if (data != null) {
                MusicLoader.MusicInfo mMusicInfo = data.getParcelableExtra(SelectMusicActivity.EXTRA_SELECT_MUSIC);
                if(!checkInfo(mMusicInfo.getUrl())){
                    mItemPaths.add(mMusicInfo);
                }
                mSelectAdapter.setData(mItemPaths);
            }
        }else if(requestCode == REQ_GET_FROM_SELECT_BOOK && resultCode == RESULT_OK){
            if(data != null){
                BookEntity entity = data.getParcelableExtra(SelectBookActivity.EXTRA_SELECT_BOOK);
                if(!checkInfo(entity.getPath())){
                    mItemPaths.add(entity);
                }
                mSelectAdapter.setData(mItemPaths);
            }
        }else if(requestCode == TYPE_READ_CHANGE && resultCode == RESULT_OK){
            if(data != null){
                readMode = data.getStringExtra("read_type");
                if(readMode.equalsIgnoreCase("IMAGE")){
                    mModeName.setText("看图模式");
                }else if(readMode.equalsIgnoreCase("TEXT")){
                    mModeName.setText("阅读模式");
                }else {
                    mModeName.setText("正常模式");
                }
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    if(mIsCover){
                        go2CropAvatar(photoPaths.get(0));
                    }else {
                        removePath(photoPaths);
                        mItemPaths.addAll(photoPaths);
                        mSelectAdapter.setData(mItemPaths);
                    }
                }
            });
        }
    }

    private void removePath(ArrayList<String> paths){
        ArrayList<String>  temp = new ArrayList<>();
        for(Object o : mItemPaths){
            if(o instanceof String){
                for(String path : paths){
                    if(path.equals(o)){
                        temp.add(path);
                    }
                }
            }
            if(o instanceof MusicLoader.MusicInfo){
                for(String path : paths){
                    if(path.equals("music" + ((MusicLoader.MusicInfo) o).getUrl())){
                        temp.add(path);
                    }
                }
            }
        }
        paths.removeAll(temp);
    }

    private boolean checkInfo(String path){
        for (Object o : mItemPaths){
            if(o instanceof MusicLoader.MusicInfo){
                MusicLoader.MusicInfo info = (MusicLoader.MusicInfo) o;
                if(info.getUrl().equals(path)){
                    return true;
                }
            }else if(o instanceof BookEntity){
                BookEntity entity = (BookEntity) o;
                if(entity.getPath().equals(path)){
                    return true;
                }
            }
        }
        return false;
    }

    private void go2CropAvatar(String path) {
        Intent intent = new Intent(this, CropAvatarActivity.class);
        intent.putExtra(CropAvatarActivity.EXTRA_RAW_IMG_PATH, path);
        if(mIsCover){
            intent.putExtra(CropAvatarActivity.EXTRA_W_RATIO,9);
            intent.putExtra(CropAvatarActivity.EXTRA_H_RATIO,5);
        }
        startActivityForResult(intent, REQ_CROP_AVATAR);
    }

    @Override
    public void onBackPressed() {
        if (mHasModified) {
            DialogUtils.showAbandonModifyDlg(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        mTvSave.setEnabled(true);
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void openOrModifyBagSuccess() {
        mTvSave.setEnabled(true);
        finalizeDialog();
        Intent i = new Intent();
        if(mType == TYPE_BAG_OPEN || mType == TYPE_BAG_MODIFY){
            i.putExtra("bg",mBgPath);
            i.putExtra("name",mTvName.getText().toString());
            setResult(RESULT_OK,i);
        }
        finish();
    }

    @Override
    public void loadBagInfoSuccess(BagEntity entity) {

    }

    @Override
    public void loadFolderListSuccess(ArrayList<BagDirEntity> entities, boolean isPull) {

    }

    @Override
    public void createFolderSuccess() {
        mTvSave.setEnabled(true);
        finalizeDialog();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void uploadFolderSuccess() {
        mTvSave.setEnabled(true);
        finalizeDialog();
        Intent i = new Intent();
        i.putExtra("number",mItemPaths.size());
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void loadFolderItemListSuccess(ArrayList<FileEntity> entities, boolean isPull) {

    }

    @Override
    public void onCheckSize(boolean isOk) {
        if(isOk){
            if(mType == TYPE_BAG_OPEN || mType == TYPE_BAG_MODIFY){
                String name = mTvName.getText().toString();
                if(mType == TYPE_BAG_MODIFY){
                    mPresenter.openBag(name,images.get(0),1);
                }else {
                    mPresenter.openBag(name,images.get(0),0);
                }
            }else if(mType == TYPE_DIR_CREATE){
                String name = mTvName.getText().toString();
                int coin = Integer.valueOf(mTvCoin.getText().toString().replace("节操",""));
                mPresenter.createFolder(name,coin,images.get(0),items,readMode);
            }else if(mType == TYPE_DIR_ITEM_ADD){
                mPresenter.uploadFilesToFolder(folderId,items);
            }else if(mType == TYPE_DIR_MODIFY){
                String name = mTvName.getText().toString();
                int coin = Integer.valueOf(mTvCoin.getText().toString().replace("节操",""));
                mPresenter.modifyFolder(folderId,name,coin,images.get(0),size,readMode);
            }
        }else {
            showToast("空间不足");
        }
    }

    @Override
    public void onBuyFolderSuccess() {

    }

    @Override
    public void deleteFolderSuccess() {
        mTvSave.setEnabled(true);
        setResult(RES_DELETE);
        finish();
    }

    @Override
    public void modifyFolderSuccess() {
        mTvSave.setEnabled(true);
        finalizeDialog();
        Intent i = new Intent();
        i.putExtra("name",mTvName.getText().toString());
        i.putExtra("bg",mBgPath);
        i.putExtra("read_type",readMode);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void onFollowOrUnFollowFolderSuccess(boolean follow) {

    }

    @Override
    public void onLoadFolderSuccess(BagDirEntity entity) {

    }

    @Override
    public void onLoadFolderFail() {

    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }
}
