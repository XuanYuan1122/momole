package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCreateTrashComponent;
import com.moemoe.lalala.di.modules.CreateTrashModule;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.TrashPut;
import com.moemoe.lalala.presenter.CreateTrashContract;
import com.moemoe.lalala.presenter.CreateTrashPresenter;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/14.
 */

public class CreateTrashActivity extends BaseAppCompatActivity implements CreateTrashContract.View{
    public static final int TYPE_IMG_TRASH = 0;
    public static final int TYPE_TEXT_TRASH = 1;
    public static final String TYPE_CREATE = "type";

    /**
     * 标题限制长度
     */
    private final int TITLE_LIMIT = 40;
    /**
     * 内容限制长度
     */
    private final int CONTENT_LIMIT = 3000;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSend;
    @BindView(R.id.edt_title)
    EditText mEdtTitle;
    @BindView(R.id.edt_content)
    EditText mEdtContent;
    @BindView(R.id.dv_doc_label_root)
    DocLabelView mDocLabel;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.iv_img)
    ImageView mLlSelectImg;

    @Inject
    CreateTrashPresenter mPresenter;
    private int mType;
    private ArrayList<DocTagEntity> mTags;
    private boolean mHasModified = false;
    private String mTitle;
    private String mContent;
    private String mIconPath;
    private int mContentRemain;
    private TrashPut mTrashEntity;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_trash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        DaggerCreateTrashComponent.builder()
                .createTrashModule(new CreateTrashModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = i.getIntExtra(TYPE_CREATE, TYPE_TEXT_TRASH);
        if(mType == TYPE_TEXT_TRASH){
            mEdtContent.setVisibility(View.VISIBLE);
            mLlSelectImg.setVisibility(View.GONE);
        }else if(mType == TYPE_IMG_TRASH){
            mEdtContent.setVisibility(View.GONE);
            mLlSelectImg.setVisibility(View.VISIBLE);
        }else {
            finish();
            return;
        }
        mTags = new ArrayList<>();
        mTvTitle.setText(R.string.label_create_trash);
        mTvSend.setVisibility(View.VISIBLE);
        mTvSend.getPaint().setFakeBoldText(true);
        ViewUtils.setRightMargins(mTvSend,DensityUtil.dip2px(this,18));
        mTvSend.setText(R.string.label_done);
        mDocLabel.setContentAndNumList(true,mTags);
        mContentRemain = CONTENT_LIMIT;
        mTitle = "";
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
                finish();
            }
        });
        mTvSend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                createTrash();
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
        mEdtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
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
                    deleteLabel(position);
                } else {
                    mKlCommentBoard.setVisibility(View.VISIBLE);
                    mEdtCommentInput.setText("");
                    mEdtCommentInput.setHint("添加标签吧~~");
                    mEdtCommentInput.requestFocus();
                    SoftKeyboardUtils.showSoftKeyboard(CreateTrashActivity.this, mEdtCommentInput);
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
        mLlSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> arrayList = new ArrayList<>();
                    DialogUtils.createImgChooseDlg(CreateTrashActivity.this, null, CreateTrashActivity.this, arrayList, 1).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void deleteLabel(final int position){
            mTags.remove(position);
            mDocLabel.notifyAdapter();
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

    private boolean checkLabel(String content){
        for(DocTagEntity tagBean : mTags){
            if(tagBean.getName().equals(content)){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

            @Override
            public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                mIconPath = photoPaths.get(0);
                Glide.with(CreateTrashActivity.this)
                        .load(mIconPath)
                        .override(DensityUtil.dip2px(CreateTrashActivity.this,115), DensityUtil.dip2px(CreateTrashActivity.this,115))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .into(mLlSelectImg);
            }
        });
    }

    private void createTrash() {
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        if (mTitle.length() > TITLE_LIMIT) {
            showToast(R.string.msg_doc_title_limit);
        } else if (mType == TYPE_TEXT_TRASH && TextUtils.isEmpty(mContent)) {
            showToast(R.string.msg_doc_content_cannot_null);
        } else if (mType == TYPE_TEXT_TRASH && mContent.length() > CONTENT_LIMIT) {
            showToast(R.string.label_more_doc_content);
        } else if(mType == TYPE_IMG_TRASH && TextUtils.isEmpty(mIconPath)){
            showToast(R.string.msg_doc_img_cannot_null);
        } else if(mTags.size() < 1){
            showToast(R.string.msg_need_one_tag);
        }else {
            mTrashEntity = new TrashPut();
            mTrashEntity.title = mTitle;
            for (int i = 0;i < mTags.size();i++){
                mTrashEntity.tags.add(mTags.get(i).getName());
            }
            ArrayList<String> arrayList = new ArrayList<>();
            if(mType == TYPE_TEXT_TRASH ){
                mTrashEntity.content = mContent;
            }else if(mType == TYPE_IMG_TRASH){
                arrayList.add(mIconPath);
            }
            final ArrayList<Image> images = BitmapUtils.handleUploadImage(arrayList);
            Message msg = mHandler.obtainMessage();
            msg.obj = images;
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ArrayList<Image> images = (ArrayList<Image>) msg.obj;
            // 获取上传图片列表
            ArrayList<String> paths = new ArrayList<String>();
            if(images != null && images.size() > 0){
                for(int i = 0; i < images.size(); i++){
                    paths.add(images.get(i).getPath());
                }
            }
            mTvSend.setEnabled(false);
            createDialog();
            if(mType == TYPE_TEXT_TRASH){
                mPresenter.createTrash(mTrashEntity);
            }else if(mType == TYPE_IMG_TRASH){
                mPresenter.createUploadTrash(mTrashEntity,mIconPath);
            }
        }
    };

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onCreateSuccess() {
        mTvSend.setEnabled(true);
        finalizeDialog();
        showToast(R.string.msg_create_trash_success);
        finish();
    }

    @Override
    public void onFailure(int code,String msg) {
        mTvSend.setEnabled(true);
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
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
