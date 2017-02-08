package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerCreateDocComponent;
import com.moemoe.lalala.di.modules.CreateDocModule;
import com.moemoe.lalala.dialog.AlertDialog;
import com.moemoe.lalala.model.entity.DocPut;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDocType;
import com.moemoe.lalala.presenter.CreateDocContract;
import com.moemoe.lalala.presenter.CreateDocPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.adapter.SelectImgAdapter;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class CreateNormalDocActivity extends BaseAppCompatActivity implements CreateDocContract.View{

    public static final String TYPE_CREATE = "type";
    public static final String TYPE_TAG_NAME_DEFAULT = "tag_default";
    public static final String TYPE_QIU_MING_SHAN = "qiu_ming_shan";
    public static final int TYPE_IMG_DOC = 0;
    public static final int TYPE_MUSIC_DOC = 1;
    public static final int REQUEST_CODE_CREATE_DOC = 9000;
    public static final int RESPONSE_CODE = 10000;

    private final int REQ_GET_FROM_GALLERY = 1002;
    private final int REQ_GET_FROM_SELECT_MUSIC = 1003;
    private final int REQ_ADD_HIDE = 1004;

    /**
     * 标题限制长度
     */
    private final int TITLE_LIMIT = 40;
    /**
     * 内容限制长度
     */
    private final int CONTENT_LIMIT = 3000;
    /**
     * 9张图片上限
     */
    private final int ICON_NUM_LIMIT = 9;

    /**
     * 编辑版本，图库选图
     */

    private static final int REQ_SELECT_FOLDER = 5001;
    private final int REQ_GET_EDIT_VERSION_IMG = 2333;
    private final int REQ_GET_EDIT_VERSION_IMG_2 = 233;
    private final int REQ_GET_EDIT_VERSION_MUSIC = 6666;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSend;
    @BindView(R.id.edt_title)
    EditText mEdtTitle;
    @BindView(R.id.edt_content)
    EditText mEdtContent;
    @BindView(R.id.tv_content_rm)
    TextView mTvContentNumRemain;
    @BindView(R.id.dv_doc_label_root)
    DocLabelView mDocLabel;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.rl_add_img_root)
    View mRlAddRoot;
    @BindView(R.id.ll_add_music_root)
    View mLlAddRoot;
    @BindView(R.id.ll_select_music)
    View mLlSelectMusic;
    @BindView(R.id.ll_select_img)
    View mLlSelectImg;
    @BindView(R.id.tv_select_music)
    TextView mTvMusic;
    @BindView(R.id.tv_select_img)
    TextView mTvImg;
    @BindView(R.id.iv_select_music)
    ImageView mIvMusic;
    @BindView(R.id.iv_select_img)
    ImageView mIvImg;
    @BindView(R.id.rv_img)
    RecyclerView mRvSelectImg;
    @BindView(R.id.ll_jiecao)
    View mRlHideRoot;
    @BindView(R.id.tv_add)
    TextView mTvAddHide;
    @BindView(R.id.ll_bag)
    View mRlBagRoot;
    @BindView(R.id.tv_bag_add)
    TextView mTvAddBag;

    @Inject
    CreateDocPresenter mPresenter;
    private int mType;
    private String mTagNameDef;
    private int mDocType;
    private int mContentRemain;
    private SelectImgAdapter mSelectAdapter;
    private ArrayList<DocTagEntity> mTags;
    private String mTitle;
    private String mContent;
    private boolean mHasModified = false;
    private MusicLoader.MusicInfo mMusicInfo;
    private DocPut mDocEntity;
    private String mFolderId;
    private String mCoinContent;
    private ArrayList<String> mIconPaths = new ArrayList<>();
    private ArrayList<String> mCoinPaths = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_doc_normal;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        DaggerCreateDocComponent.builder()
                .createDocModule(new CreateDocModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTags = new ArrayList<>();
        mType = i.getIntExtra(TYPE_CREATE, TYPE_IMG_DOC);
        mTagNameDef = i.getStringExtra(TYPE_TAG_NAME_DEFAULT);
        if(!TextUtils.isEmpty(mTagNameDef)){
            DocTagEntity DocTag = new DocTagEntity();
            DocTag.setLikes(1);
            DocTag.setName(mTagNameDef);
            DocTag.setLiked(true);
            mTags.add(DocTag);
        }
        if(mType == TYPE_IMG_DOC){
            mLlAddRoot.setVisibility(View.GONE);
            mRlAddRoot.setVisibility(View.VISIBLE);
            mRlHideRoot.setVisibility(View.VISIBLE);
            mRlBagRoot.setVisibility(View.VISIBLE);
        }else if(mType == TYPE_MUSIC_DOC){
            mLlAddRoot.setVisibility(View.VISIBLE);
            mRlAddRoot.setVisibility(View.GONE);
            mRlHideRoot.setVisibility(View.GONE);
            mRlBagRoot.setVisibility(View.GONE);
        }else {
            finish();
            return;
        }
        mDocType = i.getIntExtra(TYPE_QIU_MING_SHAN,0);
        mTvTitle.setText(R.string.label_create_post);
        mTvSend.setVisibility(View.VISIBLE);
        mTvSend.setText(R.string.label_menu_publish_doc);
        mTvAddHide.setVisibility(View.INVISIBLE);
        mTvAddBag.setVisibility(View.INVISIBLE);
        mContentRemain = CONTENT_LIMIT;
        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvSelectImg.setLayoutManager(selectRvL);
        mSelectAdapter = new SelectImgAdapter(this);
        mRvSelectImg.setAdapter(mSelectAdapter);
        mDocLabel.setContentAndNumList(true,mTags);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mRlHideRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(CreateNormalDocActivity.this,DocHideAddActivity.class);
                startActivityForResult(i,REQ_ADD_HIDE);
            }
        });
        mRlBagRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(CreateNormalDocActivity.this,FolderSelectActivity.class);
                startActivityForResult(i,REQ_SELECT_FOLDER);
            }
        });
        mTvSend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                createPost();
            }
        });
        mSelectAdapter.setOnItemClickListener(new SelectImgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == mIconPaths.size()){
                    choosePhoto();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onAllDelete() {

            }
        });
        mEdtTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > TITLE_LIMIT) {
                    if (!mEdtTitle.isSelected()) {
                        mEdtTitle.setSelected(true);
                        showToast(R.string.msg_doc_title_limit);
                    }
                } else {
                    mEdtTitle.setSelected(false);
                }
                mTitle = s.toString();
                mHasModified = true;
            }
        });
        mEdtContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentRemain = CONTENT_LIMIT - s.length();
                if (mContentRemain <= -1000) { // 无聊...免得显示4位数不好看
                    mEdtContent.setText(mEdtContent.getText().subSequence(0, 999 + CONTENT_LIMIT));
                }
                mContent = s.toString();
                updateTextNumRemain(mTvContentNumRemain, mContentRemain);
                mHasModified = true;
            }
        });
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEdtCommentInput.getText();
                int len = editable.length();
                if (len > 15) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0, 15);
                    mEdtCommentInput.setText(newStr);
                    editable = mEdtCommentInput.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
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
        mDocLabel.setItemClickListener(new DocLabelView.LabelItemClickListener() {

            @Override
            public void itemClick(int position) {
                if (position < mTags.size()) {
                    //plusLabel(position);
                    deleteLabel(position);
                } else {
                    mKlCommentBoard.setVisibility(View.VISIBLE);
                    mEdtCommentInput.setText("");
                    mEdtCommentInput.setHint("添加标签吧~~");
                    mEdtCommentInput.requestFocus();
                    SoftKeyboardUtils.showSoftKeyboard(CreateNormalDocActivity.this, mEdtCommentInput);
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
                createLabel();
            }
        });
        mLlSelectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSetting.IS_EDITOR_VERSION){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    try {
                        startActivityForResult(intent,REQ_GET_EDIT_VERSION_MUSIC);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Intent intent = new Intent(CreateNormalDocActivity.this, SelectMusicActivity.class);
                    startActivityForResult(intent,REQ_GET_FROM_SELECT_MUSIC);
                }
            }
        });
        mLlSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSetting.IS_EDITOR_VERSION){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    try {
                        startActivityForResult(intent, REQ_GET_EDIT_VERSION_IMG_2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Intent intent = new Intent(CreateNormalDocActivity.this, MultiImageChooseActivity.class);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, 1);
                    startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                }
            }
        });
    }

    private boolean checkLabel(String content){
        for(DocTagEntity tagBean : mTags){
            if(tagBean.getName().equals(content)){
                return false;
            }
        }
        return true;
    }

    private void createLabel(){
        String content = mEdtCommentInput.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            if(checkLabel(content)){
                DocTagEntity DocTag = new DocTagEntity();
                DocTag.setLikes(1);
                DocTag.setName(content);
                DocTag.setLiked(true);
                mTags.add(DocTag);
                mDocLabel.notifyAdapter();
            }else {
                showToast(R.string.msg_tag_already_exit);
            }
        }else {
            showToast(R.string.msg_doc_comment_not_empty);
        }
    }

    private void deleteLabel(final int position){
        if(!TextUtils.isEmpty(mTagNameDef) && mTagNameDef.equals(mTags.get(position).getName())){
            final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
            alertDialogUtil.createPromptDialog(this, getString(R.string.label_delete),getString( R.string.label_content_tag_del));
            alertDialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
            alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    alertDialogUtil.dismissDialog();
                }

                @Override
                public void ConfirmOnClick() {
                    alertDialogUtil.dismissDialog();
                    mTags.remove(position);
                    mDocLabel.notifyAdapter();
                }
            });
            alertDialogUtil.showDialog();
        }else {
            mTags.remove(position);
            mDocLabel.notifyAdapter();
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final int visibleSize = msg.arg1;
            final ArrayList<Image> images = (ArrayList<Image>) msg.obj;
            // 获取上传图片列表
            ArrayList<String> paths = new ArrayList<String>();
            if(images != null && images.size() > 0){
                for(int i = 0; i < images.size(); i++){
                    paths.add(images.get(i).getPath());
                }
            }
            mTvSend.setEnabled(false);
            if(mMusicInfo != null && images.size() == 1 && mType == TYPE_MUSIC_DOC){
                paths.clear();
                paths.add(mMusicInfo.getUrl());
                if(images.size() < 1){
                    showToast(R.string.msg_need_one_cover);
                    return;
                }
                paths.add(images.get(0).getPath());
            }
            if (mType == TYPE_IMG_DOC){
                if(paths.size() == 0) {
                    createDialog();
                    mPresenter.createDoc(mDocEntity, mDocType);
                }else {
                    createDialog();
                    mPresenter.createUploadDoc(mDocEntity,paths,0, mDocType,visibleSize,null);
                }
            }else if(mType == TYPE_MUSIC_DOC){
                DocPut.DocPutMusic docPutMusic = new DocPut.DocPutMusic();
                docPutMusic.name = mMusicInfo.getTitle();
                docPutMusic.timestamp = mMusicInfo.getDuration();
                mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_MUSIC.toString(),docPutMusic));
                mPresenter.createUploadDoc(mDocEntity,paths,1, mDocType,visibleSize,docPutMusic);
            }
        }
    };

    private void createPost() {
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        if (mTitle != null && mTitle.length() > TITLE_LIMIT) {
            showToast(R.string.msg_doc_title_limit);
        } else if (TextUtils.isEmpty(mContent)) {
            showToast(R.string.msg_doc_content_cannot_null);
        } else if (mContent.length() > CONTENT_LIMIT) {
            showToast(R.string.label_more_doc_content);
        } else if(mTags.size() < 1){
            showToast(R.string.msg_need_one_tag);
        } else if(mType == TYPE_MUSIC_DOC && mMusicInfo == null){
            showToast(R.string.msg_need_one_music);
        }else {
            mDocEntity = new DocPut();
            mDocEntity.bagFolderId = mFolderId;
            mDocEntity.title = mTitle;
            for (int i = 0;i < mTags.size();i++){
                mDocEntity.tags.add(mTags.get(i).getName());
            }
            DocPut.DocPutText docPutText = new DocPut.DocPutText();
            docPutText.text = mContent;
            mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(), docPutText));
            if(!TextUtils.isEmpty(mCoinContent)){
                DocPut.DocPutText docPutText1 = new DocPut.DocPutText();
                docPutText1.text = mCoinContent;
                mDocEntity.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(),docPutText1));
            }
            if(!TextUtils.isEmpty(mCoinContent) || mCoinPaths.size() > 0){
                mDocEntity.coin.coin = 1;
            }else {
                mDocEntity.coin.coin = 0;
            }
            final int visibleSize = mIconPaths.size();
            mIconPaths.addAll(mCoinPaths);
            final ArrayList<Image> images = BitmapUtils.handleUploadImage(mIconPaths);
            Message msg = mHandler.obtainMessage();
            msg.arg1 = visibleSize;
            msg.obj = images;
            mHandler.sendMessage(msg);
        }
    }

    private void choosePhoto() {
        if (mIconPaths.size() < ICON_NUM_LIMIT) {
            if (AppSetting.IS_EDITOR_VERSION) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                try {
                    startActivityForResult(intent, REQ_GET_EDIT_VERSION_IMG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DialogUtils.createImgChooseDlg(this, null, this, mIconPaths, ICON_NUM_LIMIT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            showToast(R.string.msg_create_doc_9_jpg);
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
                        mIconPaths = paths;
                        mTvImg.setText(R.string.label_select_img_finish);
                        Glide.with(this)
                                .load( "file://" + mIconPaths.get(0))
                                .override(DensityUtil.dip2px(this,115), DensityUtil.dip2px(this,115))
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .centerCrop()
                                .into(mIvImg);
                    }
                }
            }
        } else if (requestCode == REQ_GET_FROM_SELECT_MUSIC) {
            if (data != null) {
                mMusicInfo = data.getParcelableExtra(SelectMusicActivity.EXTRA_SELECT_MUSIC);
                mTvMusic.setText(mMusicInfo.getTitle());
                mIvMusic.setBackgroundResource(R.drawable.btn_select_music_finish);
            }
        }else if(requestCode == REQ_GET_EDIT_VERSION_IMG || requestCode == REQ_GET_EDIT_VERSION_IMG_2) {
            if (resultCode == RESULT_OK && data != null) {
                String photoPath = null;
                Uri u = data.getData();
                if (u != null) {
                    String schema = u.getScheme();
                    if ("file".equals(schema)) {
                        photoPath = u.getPath();
                    }else if ("content".equals(schema)) {
                        photoPath = StorageUtils.getTempFile(System.currentTimeMillis() + ".jpg").getAbsolutePath();
                        InputStream is ;
                        FileOutputStream fos ;
                        try {
                            is = getContentResolver().openInputStream(u);
                            fos = new FileOutputStream(new File(photoPath));
                            FileUtil.copyFile(is, fos);
                        } catch (Exception e) {
                        }
                        if (FileUtil.isValidGifFile(photoPath)) {
                            String newFile = StorageUtils.getTempFile(System.currentTimeMillis() + ".gif").getAbsolutePath();
                            FileUtil.copyFile(photoPath, newFile);
                            FileUtil.deleteOneFile(photoPath);
                            photoPath = newFile;
                        }
                    }
                    if(requestCode == REQ_GET_EDIT_VERSION_IMG_2){
                        mIconPaths.clear();
                    }
                    mIconPaths.add(photoPath);
                    if(requestCode == REQ_GET_EDIT_VERSION_IMG){
                        onGetPhotos();
                    }else {
                        mTvImg.setText(R.string.label_select_img_finish);
                        Glide.with(this)
                                .load( "file://" + mIconPaths.get(0))
                                .override(DensityUtil.dip2px(this,115), DensityUtil.dip2px(this,115))
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .centerCrop()
                                .into(mIvImg);
                    }
                }
            }
        }else if(requestCode == REQ_GET_EDIT_VERSION_MUSIC) {
            if (resultCode == RESULT_OK && data != null) {
                String musicPath = null;
                Uri u = data.getData();
                if (u != null) {
                    String schema = u.getScheme();
                    if ("file".equals(schema)) {
                        musicPath = u.getPath();
                    }else if ("content".equals(schema)) {
                        musicPath = StorageUtils.getTempFile(System.currentTimeMillis() + ".mp3").getAbsolutePath();
                        InputStream is = null;
                        FileOutputStream fos = null;
                        try {
                            is = getContentResolver().openInputStream(u);
                            fos = new FileOutputStream(new File(musicPath));
                            FileUtil.copyFile(is, fos);
                        } catch (Exception e) {
                        }
                    }
                    mMusicInfo = new MusicLoader.MusicInfo();
                    mMusicInfo.setUrl(musicPath);
                    mMusicInfo.setDuration(0);
                    final EditText ed = new EditText(this);
                    new AlertDialog.Builder(this)
                            .setTitle("请输入歌名")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(ed)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String title = ed.getText().toString();
                                    mMusicInfo.setTitle(title);
                                    mTvMusic.setText(title);
                                    mIvMusic.setBackgroundResource(R.drawable.btn_select_music_finish);
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        }else if(requestCode == REQ_ADD_HIDE){
            if(data != null){
                mCoinContent = data.getStringExtra("content");
                mCoinPaths = data.getStringArrayListExtra("paths");
                if(!TextUtils.isEmpty(mCoinContent) || mCoinPaths.size() > 0){
                    mTvAddHide.setVisibility(View.VISIBLE);
                }else {
                    mTvAddHide.setVisibility(View.INVISIBLE);
                }
            }
        }else if (requestCode == REQ_SELECT_FOLDER && resultCode == RESULT_OK){
            if (!TextUtils.isEmpty(data.getStringExtra("folderId"))){
                mTvAddBag.setVisibility(View.VISIBLE);
                mFolderId = data.getStringExtra("folderId");
            }else {
                mTvAddBag.setVisibility(View.INVISIBLE);
            }
        } else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    if (override) {
                        mIconPaths = photoPaths;
                    } else {
                        mIconPaths.addAll(photoPaths);
                    }
                    onGetPhotos();
                }
            });
        }
    }

    private void onGetPhotos() {
        mSelectAdapter.setData(mIconPaths);
    }

    private void updateTextNumRemain(TextView tv, int charRemain) {
        tv.setText(String.valueOf(charRemain));
        if (charRemain >= 0) {
            tv.setTextColor(Color.DKGRAY);
        } else {
            tv.setTextColor(ContextCompat.getColor(this,R.color.red_cc6666));
        }
    }


    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code,String msg) {
        mTvSend.setEnabled(true);
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,code,msg);
    }

    @Override
    public void onSendSuccess() {
        mTvSend.setEnabled(true);
        finalizeDialog();
        showToast(R.string.msg_create_doc_success);
        Intent i = new Intent();
        setResult(RESPONSE_CODE, i);
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
}
