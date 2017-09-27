package com.moemoe.lalala.view.activity;

import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.modules.CreateCommentModule;
import com.moemoe.lalala.model.entity.CommentSendV2Entity;
import com.moemoe.lalala.model.entity.tag.BaseTag;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.presenter.CreateCommentContract;
import com.moemoe.lalala.presenter.CreateCommentPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileItemDecoration;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.adapter.SelectItemAdapter;
import com.moemoe.lalala.di.components.DaggerCreateCommentComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_ALT_USER;

/**
 * 转发
 * Created by yi on 2017/9/21.
 */

public class CreateCommentActivity extends BaseAppCompatActivity implements CreateCommentContract.View{

    private static final int LIMIT_CONTENT = 150;

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
    @BindView(R.id.list)
    RecyclerView mRvImg;
    @BindView(R.id.ll_extra_root)
    LinearLayout mExtraRoot;
    @BindView(R.id.cb_comment)
    CheckBox mCbComment;
    @Inject
    CreateCommentPresenter mPresenter;

    private SelectItemAdapter mSelectAdapter;
    private ArrayList<String> mPaths;
    private String mId;
    private String mCommentTo;
    private boolean isSec;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_zhuan_or_comment;
    }

    public static void startActivity(Context context,String id,boolean isSec,String commentTo){
        Intent i = new Intent(context,CreateCommentActivity.class);
        i.putExtra(UUID,id);
        i.putExtra("commentTo",commentTo);
        i.putExtra("isSec",isSec);
        context.startActivity(i);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerCreateCommentComponent.builder()
                .createCommentModule(new CreateCommentModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        AndroidBug5497Workaround.assistActivity(this);
        mId = getIntent().getStringExtra(UUID);
        mCommentTo = getIntent().getStringExtra("commentTo");
        isSec = getIntent().getBooleanExtra("isSec",false);
        if(TextUtils.isEmpty(mId)){
            finish();
            return;
        }
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
        mSelectAdapter.setSelectSize(0);
        mRvImg.setLayoutManager(new GridLayoutManager(this,3));
        mRvImg.addItemDecoration(new FileItemDecoration());
        mRvImg.setAdapter(mSelectAdapter);
        mPaths = new ArrayList<>();
        mCbComment.setVisibility(View.VISIBLE);
        mCbComment.setText("评论并转发");
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
                finish();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText("评论");
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight,(int) getResources().getDimension(R.dimen.x36));
        mTvMenuRight.setText(getString(R.string.label_menu_publish_doc));
        mTvMenuRight.setTextColor(Color.WHITE);
        mTvMenuRight.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int) getResources().getDimension(R.dimen.x30));
        mTvMenuRight.setWidth((int) getResources().getDimension(R.dimen.x88));
        mTvMenuRight.setHeight((int) getResources().getDimension(R.dimen.y48));
        mTvMenuRight.setBackgroundResource(R.drawable.shape_rect_border_main_background_2);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_add_img,R.id.iv_alt_user,R.id.tv_menu})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_add_img:
                if (mPaths.size() < 1){
                    try {
                        DialogUtils.createImgChooseDlg(CreateCommentActivity.this, null, CreateCommentActivity.this, mPaths, 1).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    showToast("只能选择一张图片");
                }
                break;
            case R.id.iv_alt_user:
                Intent i3 = new Intent(CreateCommentActivity.this,SearchActivity.class);
                i3.putExtra("show_type",SearchActivity.SHOW_USER);
                startActivityForResult(i3,REQ_ALT_USER);
                break;
            case R.id.tv_menu:
                done();
                break;
        }
    }

    private void done(){
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
        createDialog();
        CommentSendV2Entity entity = new CommentSendV2Entity();
        entity.rt = mCbComment.isChecked();
        entity.content = TagControl.getInstance().paresToString(mEtContent.getText());
        entity.commentTo = mCommentTo;
        Set<HashMap<String,String>> attr = TagControl.getInstance().getAttr("at_user",mEtContent.getText());
        ArrayList<String> atUser = new ArrayList<>();
        for(HashMap<String,String> map : attr){
            for (String value : map.values()){
                atUser.add(value);
            }
        }
        entity.atUsers = atUser;
        mPresenter.createComment(isSec,mId,entity,mPaths);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
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
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ALT_USER && resultCode == RESULT_OK){
            if(data != null){
                String userId = data.getStringExtra("user_id");
                String userName = data.getStringExtra("user_name");
                insertTextInCurSelection("@" + userName,userId);
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    mPaths.addAll(photoPaths);
                    ArrayList<Object> res = new ArrayList<>();
                    res.addAll(mPaths);
                    mSelectAdapter.setData(res);
                }
            });
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onCreateCommentSuccess() {
        finalizeDialog();
        showToast("发表评论成功");
        finish();
    }
}
