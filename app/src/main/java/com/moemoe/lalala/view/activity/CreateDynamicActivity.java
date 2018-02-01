package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCreateDynamicComponent;
import com.moemoe.lalala.di.modules.CreateDynamicModule;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.DynamicSendEntity;
import com.moemoe.lalala.model.entity.tag.BaseTag;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.presenter.CreateDynamicContract;
import com.moemoe.lalala.presenter.CreateDynamicPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileItemDecoration;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.adapter.SelectItemAdapter;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.moemoe.lalala.utils.Constant.ICON_NUM_LIMIT;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_ALT_USER;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_CREATE_HONGBAO;

/**
 * 发布动态
 * Created by yi on 2017/9/21.
 */

public class CreateDynamicActivity extends BaseAppCompatActivity implements CreateDynamicContract.View{

    private static final int LIMIT_CONTENT = 2000;

    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.tv_content_count)
    TextView mTvContentCount;
    @BindView(R.id.rl_ope_root)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.list)
    RecyclerView mRvImg;
    @BindView(R.id.dv_doc_label_root)
    DocLabelView docLabelView;
    @BindView(R.id.iv_add_hongbao)
    ImageView mIvAddHongbao;

    @Inject
    CreateDynamicPresenter mPresenter;

    private SelectItemAdapter mSelectAdapter;
    private ArrayList<Object> mPaths;
    private ArrayList<DocTagEntity> mTags;
    private boolean tagFlag;
    private String mTagNameDef;
    private int mCoin;
    private int mHongBaoNum;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_dynamic;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        DaggerCreateDynamicComponent.builder()
                .createDynamicModule(new CreateDynamicModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtContent.getText();
                int len = editable.length();
                if (len > LIMIT_CONTENT) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtContent.setText(editable.subSequence(0, LIMIT_CONTENT));
                    editable = mEtContent.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
                mTvContentCount.setText(mEtContent.getText().length() + "/" + LIMIT_CONTENT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSelectAdapter = new SelectItemAdapter(this);
        mSelectAdapter.setSelectSize(9);
        mRvImg.setLayoutManager(new GridLayoutManager(this,3));
        mRvImg.addItemDecoration(new FileItemDecoration());
        mRvImg.setAdapter(mSelectAdapter);
        mPaths = new ArrayList<>();
        mSelectAdapter.setOnItemClickListener(new SelectItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == mPaths.size()){
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
        mTags = new ArrayList<>();
        mTagNameDef = getIntent().getStringExtra("default_tag");
        if(!TextUtils.isEmpty(mTagNameDef)){
            DocTagEntity DocTag = new DocTagEntity();
            DocTag.setLikes(1);
            DocTag.setName(mTagNameDef);
            DocTag.setLiked(true);
            mTags.add(DocTag);
        }
        docLabelView.setContentAndNumList(true,mTags);
        docLabelView.setItemClickListener(new DocLabelView.LabelItemClickListener() {
            @Override
            public void itemClick(int position) {
                if(!tagFlag){
                    if (position < mTags.size()) {
                        deleteLabel(position);
                    } else {
                        SoftKeyboardUtils.dismissSoftKeyboard(CreateDynamicActivity.this);
                        DocTagEntity docTag = new DocTagEntity();
                        docTag.setLikes(1);
                        docTag.setName("");
                        docTag.setLiked(true);
                        docTag.setEdit(true);
                        mTags.add(docTag);
                        docLabelView.notifyAdapter();
                        tagFlag = true;
                    }
                }
            }
        });
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    tagFlag = false;
                    if(mTags.size() > 0){
                        DocTagEntity entity = mTags.get(mTags.size() - 1);
                        if(!TextUtils.isEmpty(entity.getName())){
                            if(checkLabel(entity.getName())){
                                mTags.get(mTags.size() - 1).setEdit(false);
                            }else {
                                entity.setName("");
                                ToastUtils.showShortToast(CreateDynamicActivity.this,R.string.msg_tag_already_exit);
                            }
                        }else {
                            mTags.remove(entity);
                        }
                        docLabelView.notifyAdapter();
                    }
                }
            }
        });
    }

    private void deleteLabel(final int position){
        if(!TextUtils.isEmpty(mTagNameDef) && mTagNameDef.equals(mTags.get(position).getName())){
            final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
            alertDialogUtil.createPromptNormalDialog(this, getString( R.string.label_content_tag_del));
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
                    docLabelView.notifyAdapter();
                }
            });
            alertDialogUtil.showDialog();
        }else {
            mTags.remove(position);
            docLabelView.notifyAdapter();
        }
    }

    private boolean checkLabel(String content){
        ArrayList<DocTagEntity> tmp = new ArrayList<>();
        tmp.addAll(mTags);
        if(mTags.size() > 0){
            tmp.remove(tmp.size() - 1);
        }
        for(DocTagEntity tagBean : tmp){
            if(tagBean.getName().equals(content)){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft, (int) getResources().getDimension(R.dimen.x36));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                SoftKeyboardUtils.dismissSoftKeyboard(CreateDynamicActivity.this);
                finish();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText("发动态");
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight,(int) getResources().getDimension(R.dimen.x36));
        mTvMenuRight.setText(getString(R.string.label_menu_publish_doc));
        mTvMenuRight.setTextColor(Color.WHITE);
        mTvMenuRight.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int) getResources().getDimension(R.dimen.x30));
        mTvMenuRight.setWidth((int) getResources().getDimension(R.dimen.x88));
        mTvMenuRight.setHeight((int) getResources().getDimension(R.dimen.y48));
        mTvMenuRight.setBackgroundResource(R.drawable.shape_main_background_2);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ALT_USER && resultCode == RESULT_OK){
            if(data != null){
                String userId = data.getStringExtra("user_id");
                String userName = data.getStringExtra("user_name");
                insertTextInCurSelection("@" + userName,userId);
            }
        }else if(requestCode == REQ_CREATE_HONGBAO && resultCode == RESULT_OK){
            if(data != null){
                mCoin = data.getIntExtra("coin",0);
                mHongBaoNum = data.getIntExtra("num",0);
                if(mCoin > 0 && mHongBaoNum > 0){
                    mIvAddHongbao.setSelected(true);
                }else {
                    mIvAddHongbao.setSelected(false);
                }
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    mPaths.clear();
                    mPaths.addAll(photoPaths);
                    mSelectAdapter.setData(mPaths);
                }
            });
        }
    }

    private void insertTextInCurSelection(String str,String id){
        SpannableStringBuilder lastEditStr = new SpannableStringBuilder(mEtContent.getText());
        int cursorIndex = mEtContent.getSelectionStart();
        BaseTag tag = new BaseTag();
        tag.setTag("at_user");
        tag.setSpan(new UserUrlSpan(this,tag));
        HashMap<String,String> attrs = new HashMap<>();
        attrs.put("user_id",id);
        tag.setAttrs(attrs);
        if(cursorIndex < 0){
            lastEditStr.insert(lastEditStr.length(),str + " ");
            lastEditStr.setSpan(tag.getSpan(),mEtContent.getText().length(),mEtContent.getText().length() + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else {
            lastEditStr.insert(cursorIndex,str + " ");
            lastEditStr.setSpan(tag.getSpan(),cursorIndex,cursorIndex + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mEtContent.setText(lastEditStr);
        mEtContent.setSelection(mEtContent.getText().length());
    }

    @OnClick({R.id.iv_add_img,R.id.iv_alt_user,R.id.tv_menu,R.id.iv_add_hongbao})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_add_img:
                choosePhoto();
                break;
            case R.id.iv_alt_user:
                Intent i3 = new Intent(CreateDynamicActivity.this,SearchActivity.class);
                i3.putExtra("show_type",SearchActivity.SHOW_USER);
                startActivityForResult(i3,REQ_ALT_USER);
                break;
            case R.id.tv_menu:
                createDynamic();
                break;
            case R.id.iv_add_hongbao:
                Intent i4 = new Intent(CreateDynamicActivity.this,CreateHongbaoActivity.class);
                startActivityForResult(i4,REQ_CREATE_HONGBAO);
                break;
        }
    }

    private void choosePhoto(){
        if (mPaths.size() < ICON_NUM_LIMIT){
            try {
                ArrayList<String> res = new ArrayList<>();
                for(Object tmp : mPaths){
                    res.add((String) tmp);
                }
                DialogUtils.createImgChooseDlg(CreateDynamicActivity.this, null, CreateDynamicActivity.this, res, ICON_NUM_LIMIT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            showToast(R.string.msg_select_9_item);
        }
    }

    private void createDynamic(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        if(TextUtils.isEmpty(mEtContent.getText())){
            showToast(R.string.msg_doc_content_cannot_null);
            return;
        }
        if(mEtContent.getText().length() > LIMIT_CONTENT){
            showToast("超过字数限制");
            return;
        }
        SoftKeyboardUtils.dismissSoftKeyboard(this);
        createDialog();
        DynamicSendEntity entity = new DynamicSendEntity();
        Set<HashMap<String,String>> attr = TagControl.getInstance().getAttr("at_user",mEtContent.getText());
        ArrayList<String> atUser = new ArrayList<>();
        for(HashMap<String,String> map : attr){
            atUser.addAll(map.values());
        }
        entity.atUsers = atUser;
        entity.content = TagControl.getInstance().paresToString(mEtContent.getText());
        ArrayList<String> tags = new ArrayList<>();
        for(DocTagEntity tag : mTags){
            tags.add(tag.getName());
        }
        entity.tags = tags;
        entity.images = new ArrayList<>();
        entity.coins = mCoin;
        entity.users = mHongBaoNum;
        ArrayList<String> path = new ArrayList<>();
        for(Object tmp : mPaths){
            path.add((String) tmp);
        }
        mPresenter.createDynamic(entity,path);
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onCreateDynamicSuccess() {
        finalizeDialog();
        finish();
    }
}
