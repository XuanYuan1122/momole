package com.moemoe.lalala;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.moemoe.lalala.adapter.SelectImgAdapter;
import com.moemoe.lalala.app.AlertDialog;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.DocPut;
import com.moemoe.lalala.data.DocTag;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.NewDocType;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.network.SimpleCallback;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.KeyboardListenerLayout;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Haru on 2016/5/2 0002.
 */
@ContentView(R.layout.ac_create_doc_normal)
public class CreateNormalDocActivity extends BaseActivity{

    public static final String TYPE_CREATE = "type";
    public static final String TYPE_TAG_NAME_DEFAULT = "tag_default";
    public static final String TYPE_QIU_MING_SHAN = "qiu_ming_shan";
    public static final int TYPE_IMG_DOC = 0;
    public static final int TYPE_MUSIC_DOC = 1;
    private static final int REQ_GET_FROM_GALLERY = 1002;
    private static final int REQ_GET_FROM_SELECT_MUSIC = 1003;
    public static final int RESPONSE_CODE = 10000;
    public static final int REQ_ADD_HIDE = 1004;

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
    private static final int REQ_GET_EDIT_VERSION_IMG = 2333;
    private static final int REQ_GET_EDIT_VERSION_IMG_2 = 233;
    private static final int REQ_GET_EDIT_VERSION_MUSIC = 6666;

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    @ViewInject(R.id.tv_menu)
    private TextView mTvSend;
    @ViewInject(R.id.edt_title)
    private EditText mEdtTitle;
    @ViewInject(R.id.edt_content)
    private EditText mEdtContent;
    @ViewInject(R.id.tv_content_rm)
    private TextView mTvContentNumRemain;
    @ViewInject(R.id.dv_doc_label_root)
    private DocLabelView mDocLabel;
    @ViewInject(R.id.edt_comment_input)
    private EditText mEdtCommentInput;
    @ViewInject(R.id.ll_comment_pannel)
    private KeyboardListenerLayout mKlCommentBoard;
    @ViewInject(R.id.iv_comment_send)
    private View mTvSendComment;
    @ViewInject(R.id.rl_add_img_root)
    private View mRlAddRoot;
    @ViewInject(R.id.ll_add_music_root)
    private View mLlAddRoot;
    @ViewInject(R.id.ll_select_music)
    private View mLlSelectMusic;
    @ViewInject(R.id.ll_select_img)
    private View mLlSelectImg;
    @ViewInject(R.id.tv_select_music)
    private TextView mTvMusic;
    @ViewInject(R.id.tv_select_img)
    private TextView mTvImg;
    @ViewInject(R.id.iv_select_music)
    private ImageView mIvMusic;
    @ViewInject(R.id.iv_select_img)
    private ImageView mIvImg;
    @ViewInject(R.id.rv_img)
    private RecyclerView mRvSelectImg;
    @ViewInject(R.id.ll_jiecao)
    private View mRlHideRoot;
    @ViewInject(R.id.tv_add)
    private TextView mTvAddHide;
    private DocPut mDocBean;

    private ArrayList<String> mIconPaths = new ArrayList<>();
    private ArrayList<String> mCoinPaths = new ArrayList<>();

    private String mTitle;
    private String mContent;
    private ArrayList<DocTag> mTags;
    private int mContentRemain;
    private SelectImgAdapter mSelectAdapter;

    private boolean mHasModified = false;
    private int mType;
    private MusicLoader.MusicInfo mMusicInfo;
    private String mTagNameDef;
    private String mCoinContent;
    private boolean mIsQiu;

    @Override
    protected void initView() {
        mTags = new ArrayList<>();
        if (mIntent != null){
            mType = mIntent.getIntExtra(TYPE_CREATE, TYPE_IMG_DOC);
            mTagNameDef = mIntent.getStringExtra(TYPE_TAG_NAME_DEFAULT);
            if(!TextUtils.isEmpty(mTagNameDef)){
                DocTag DocTag = new DocTag();
                DocTag.setLikes(1);
                DocTag.setName(mTagNameDef);
                DocTag.setLiked(true);
                mTags.add(DocTag);
            }
            if(mType == TYPE_IMG_DOC){
                mLlAddRoot.setVisibility(View.GONE);
                mRlAddRoot.setVisibility(View.VISIBLE);
                mRlHideRoot.setVisibility(View.VISIBLE);
            }else if(mType == TYPE_MUSIC_DOC){
                mLlAddRoot.setVisibility(View.VISIBLE);
                mRlAddRoot.setVisibility(View.GONE);
                mRlHideRoot.setVisibility(View.GONE);
            }else {
                finish();
                return;
            }
            mIsQiu = mIntent.getBooleanExtra(TYPE_QIU_MING_SHAN,false);
        }else {
            finish();
            return;
        }
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
        mTvTitle.setText(R.string.label_create_post);
        mTvSend.setVisibility(View.VISIBLE);
        mTvSend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                createPost();
            }
        });
        mTvSend.setText(R.string.label_menu_publish_doc);
        mTvAddHide.setVisibility(View.INVISIBLE);
        mContentRemain = CONTENT_LIMIT;
        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvSelectImg.setLayoutManager(selectRvL);
        mSelectAdapter = new SelectImgAdapter(this);
        mRvSelectImg.setAdapter(mSelectAdapter);
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
        //TODO
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
                        ToastUtil.showCenterToast(CreateNormalDocActivity.this, R.string.msg_doc_title_limit);
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
        mDocLabel.setContentAndNumList(true,mTags);
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
        for(DocTag tagBean : mTags){
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
                DocTag DocTag = new DocTag();
                DocTag.setLikes(1);
                DocTag.setName(content);
                DocTag.setLiked(true);
                mTags.add(DocTag);
                mDocLabel.notifyAdapter();
            }else {
                ToastUtil.showCenterToast(this, R.string.msg_tag_already_exit);
            }
        }else {
            ToastUtil.showCenterToast(this, R.string.msg_doc_comment_not_empty);
        }
    }

    private void deleteLabel(final int position){
        if(!TextUtils.isEmpty(mTagNameDef) && mTagNameDef.equals(mTags.get(position).getName())){
            final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
            alertDialogUtil.createPromptDialog(this, getString(R.string.a_dlg_delete),getString( R.string.a_dlg_content_tag_del));
            alertDialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.a_dlg_cancel),0);
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
                    ToastUtil.showCenterToast(CreateNormalDocActivity.this, R.string.msg_need_one_cover);
                    return;
                }
                paths.add(images.get(0).getPath());
            }
            if (mType == TYPE_IMG_DOC){
                if(paths.size() == 0) {
                    createDialog();
                    if(mIsQiu){
                        Otaku.getDocV2().createQiuMingShanDoc(mDocBean, new SimpleCallback() {
                            @Override
                            public void action() {
                                mTvSend.setEnabled(true);
                                finalizeDialog();
                                ToastUtil.showToast(CreateNormalDocActivity.this, R.string.msg_create_doc_success);
                                Intent i = new Intent();
                                setResult(RESPONSE_CODE, i);
                                finish();
                            }
                        }, new OneParameterCallback<Integer>() {
                            @Override
                            public void action(Integer integer) {
                                mTvSend.setEnabled(true);
                                finalizeDialog();
                                ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                            }
                        });
                    }else {
                        Otaku.getDocV2().createNormalDoc(mDocBean, new SimpleCallback() {
                            @Override
                            public void action() {
                                mTvSend.setEnabled(true);
                                finalizeDialog();
                                ToastUtil.showToast(CreateNormalDocActivity.this, R.string.msg_create_doc_success);
                                Intent i = new Intent();
                                setResult(RESPONSE_CODE, i);
                                finish();
                            }
                        }, new OneParameterCallback<Integer>() {
                            @Override
                            public void action(Integer integer) {
                                mTvSend.setEnabled(true);
                                finalizeDialog();
                                ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                            }
                        });
                    }
                }else {
                    createDialog();
                    Otaku.getAccountV2().uploadFilesToQiniu(paths, new OneParameterCallback<ArrayList<Image>>() {
                        @Override
                        public void action(ArrayList<Image> result) {
                            for (int i = 0; i < images.size(); i++) {
                                DocPut.DocPutImage docPutImage = new DocPut.DocPutImage();
                                docPutImage.path = result.get(i).getPath();
                                docPutImage.h = result.get(i).getH();
                                docPutImage.w = result.get(i).getW();
                                if(i < visibleSize){
                                    mDocBean.details.add(new DocPut.DocDetail(NewDocType.DOC_IMAGE.toString(), docPutImage));
                                }else {
                                    mDocBean.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_IMAGE.toString(), docPutImage));
                                }
                            }
                            if(mIsQiu){
                                Otaku.getDocV2().createQiuMingShanDoc(mDocBean, new SimpleCallback() {
                                    @Override
                                    public void action() {
                                        mTvSend.setEnabled(true);
                                        finalizeDialog();
                                        for (Image image : images){
                                            FileUtil.deleteFile(image.getPath());
                                        }
                                        ToastUtil.showToast(CreateNormalDocActivity.this, R.string.msg_create_doc_success);
                                        Intent i = new Intent();
                                        setResult(RESPONSE_CODE, i);
                                        finish();
                                    }
                                }, new OneParameterCallback<Integer>() {
                                    @Override
                                    public void action(Integer integer) {
                                        mTvSend.setEnabled(true);
                                        finalizeDialog();
                                        for (Image image : images){
                                            FileUtil.deleteFile(image.getPath());
                                        }
                                        ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                                    }
                                });
                            }else {
                                Otaku.getDocV2().createNormalDoc(mDocBean, new SimpleCallback() {
                                    @Override
                                    public void action() {
                                        mTvSend.setEnabled(true);
                                        finalizeDialog();
                                        for (Image image : images){
                                            FileUtil.deleteFile(image.getPath());
                                        }
                                        ToastUtil.showToast(CreateNormalDocActivity.this, R.string.msg_create_doc_success);
                                        Intent i = new Intent();
                                        setResult(RESPONSE_CODE, i);
                                        finish();
                                    }
                                }, new OneParameterCallback<Integer>() {
                                    @Override
                                    public void action(Integer integer) {
                                        mTvSend.setEnabled(true);
                                        finalizeDialog();
                                        for (Image image : images){
                                            FileUtil.deleteFile(image.getPath());
                                        }
                                        ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                                    }
                                });
                            }
                        }
                    }, new OneParameterCallback<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            mTvSend.setEnabled(true);
                            finalizeDialog();
                            ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                        }
                    });
                }
            }else if(mType == TYPE_MUSIC_DOC){
                createDialog();
                Otaku.getAccountV2().uploadFilesToQiniu(paths, new OneParameterCallback<ArrayList<Image>>() {
                    @Override
                    public void action(ArrayList<Image> result) {
                        DocPut.DocPutMusic docPutMusic = new DocPut.DocPutMusic();
                        docPutMusic.name = mMusicInfo.getTitle();
                        docPutMusic.timestamp = mMusicInfo.getDuration();
                        for(int i = 0;i < result.size();i++){
                            if(FileUtil.isImageFileBySuffix(result.get(i).getPath())){
                                docPutMusic.cover.setPath(result.get(i).getPath());
                                docPutMusic.cover.setH(result.get(i).getH());
                                docPutMusic.cover.setW(result.get(i).getW());
                            }else {
                                docPutMusic.url = Otaku.URL_QINIU + result.get(i).getPath();
                            }
                        }
                        mDocBean.details.add(new DocPut.DocDetail(NewDocType.DOC_MUSIC.toString(),docPutMusic));
                        if(mIsQiu){
                            Otaku.getDocV2().createQiuMingShanDoc(mDocBean, new SimpleCallback() {
                                @Override
                                public void action() {
                                    mTvSend.setEnabled(true);
                                    finalizeDialog();
                                    for (Image image : images){
                                        FileUtil.deleteFile(image.getPath());
                                    }
                                    ToastUtil.showToast(CreateNormalDocActivity.this, R.string.msg_create_doc_success);
                                    Intent i = new Intent();
                                    setResult(RESPONSE_CODE, i);
                                    finish();
                                }
                            }, new OneParameterCallback<Integer>() {
                                @Override
                                public void action(Integer integer) {
                                    mTvSend.setEnabled(true);
                                    finalizeDialog();
                                    for (Image image : images){
                                        FileUtil.deleteFile(image.getPath());
                                    }
                                    ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                                }
                            });
                        }else {
                            Otaku.getDocV2().createNormalDoc(mDocBean, new SimpleCallback() {
                                @Override
                                public void action() {
                                    mTvSend.setEnabled(true);
                                    finalizeDialog();
                                    for (Image image : images){
                                        FileUtil.deleteFile(image.getPath());
                                    }
                                    ToastUtil.showToast(CreateNormalDocActivity.this, R.string.msg_create_doc_success);
                                    Intent i = new Intent();
                                    setResult(RESPONSE_CODE, i);
                                    finish();
                                }
                            }, new OneParameterCallback<Integer>() {
                                @Override
                                public void action(Integer integer) {
                                    mTvSend.setEnabled(true);
                                    finalizeDialog();
                                    for (Image image : images){
                                        FileUtil.deleteFile(image.getPath());
                                    }
                                    ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                                }
                            });
                        }
                    }
                }, new OneParameterCallback<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        mTvSend.setEnabled(true);
                        finalizeDialog();
                        ErrorCodeUtils.showErrorMsgByCode(CreateNormalDocActivity.this,integer);
                    }
                });
            }
        }
    };

    private void createPost() {
        if(!NetworkUtils.isNetworkAvailable(this)){
            ToastUtil.showCenterToast(this,R.string.a_server_msg_connection);
            return;
        }
        if (mTitle != null && mTitle.length() > TITLE_LIMIT) {
            ToastUtil.showCenterToast(CreateNormalDocActivity.this, R.string.msg_doc_title_limit);
        } else if (TextUtils.isEmpty(mContent)) {
            ToastUtil.showCenterToast(CreateNormalDocActivity.this, R.string.msg_doc_content_cannot_null);
        } else if (mContent.length() > CONTENT_LIMIT) {
            ToastUtil.showCenterToast(CreateNormalDocActivity.this,R.string.label_more_doc_content);
        } else if(mTags.size() < 1){
            ToastUtil.showCenterToast(CreateNormalDocActivity.this, R.string.msg_need_one_tag);
        } else if(mType == TYPE_MUSIC_DOC && mMusicInfo == null){
            ToastUtil.showCenterToast(CreateNormalDocActivity.this, R.string.msg_need_one_music);
        }else {
            mDocBean = new DocPut();
            mDocBean.title = mTitle;
            for (int i = 0;i < mTags.size();i++){
                mDocBean.tags.add(mTags.get(i).getName());
            }
            DocPut.DocPutText docPutText = new DocPut.DocPutText();
            docPutText.text = mContent;
            mDocBean.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(), docPutText));
            if(!TextUtils.isEmpty(mCoinContent)){
                DocPut.DocPutText docPutText1 = new DocPut.DocPutText();
                docPutText1.text = mCoinContent;
                mDocBean.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(),docPutText1));
            }
            if(!TextUtils.isEmpty(mCoinContent) || mCoinPaths.size() > 0){
                mDocBean.coin.coin = 1;
            }else {
                mDocBean.coin.coin = 0;
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
            ToastUtil.showToast(this, R.string.msg_create_doc_9_jpg);
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
                        x.image().bind(mIvImg, "file://" + mIconPaths.get(0), new ImageOptions.Builder()
                                .setSize(DensityUtil.dip2px(115), DensityUtil.dip2px(115))
                                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                                .setFailureDrawableId(R.drawable.ic_default_club_l)
                                .setLoadingDrawableId(R.drawable.ic_default_club_l)
                                .build());
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
                        x.image().bind(mIvImg, "file://" + mIconPaths.get(0), new ImageOptions.Builder()
                                .setSize(DensityUtil.dip2px(115), DensityUtil.dip2px(115))
                                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                                .setFailureDrawableId(R.drawable.ic_default_club_l)
                                .setLoadingDrawableId(R.drawable.ic_default_club_l)
                                .build());
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
        }else {
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
            tv.setTextColor(ContextCompat.getColor(this,R.color.red_error));
        }
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
