package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerNewFolderEditComponent;
import com.moemoe.lalala.di.components.NewFolderEditComponent;
import com.moemoe.lalala.di.modules.BagModule;
import com.moemoe.lalala.di.modules.NewFolderEditModule;
import com.moemoe.lalala.model.entity.FolderRepEntity;
import com.moemoe.lalala.model.entity.NewFolderEntity;
import com.moemoe.lalala.presenter.NewFolderEditContract;
import com.moemoe.lalala.presenter.NewFolderEditPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.moemoe.lalala.utils.Constant.LIMIT_NICK_NAME;
import static com.moemoe.lalala.utils.Constant.LIMIT_TAG;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_CREATE_FOLDER;

/**
 * 文件夹操作界面
 * Created by yi on 2017/8/17.
 */

public class NewFolderEditActivity extends BaseAppCompatActivity implements NewFolderEditContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.iv_cover)
    ImageView mIvBg;
    @BindView(R.id.tv_coin)
    TextView mTvCoin;
    @BindView(R.id.tv_label_add_1)
    TextView mTvTag1;
    @BindView(R.id.tv_label_add_2)
    TextView mTvTag2;
    @BindView(R.id.tv_sort)
    TextView mTvSort;
    @Inject
    NewFolderEditPresenter mPresenter;

    private BottomMenuFragment bottomMenuFragment;
    private String type;
    private boolean mHasModified = false;
    private int mInputType = -1;
    private String mBgPath;
    private String mBgTemp;
    private String mSort;
    private String mFolderType;
    private String mFolderId;
    private int mCoin;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_folder_edit;
    }

    public static void startActivity(Context context,String type,String folderType,NewFolderEntity folder){
        Intent i = new Intent(context,NewFolderEditActivity.class);
        i.putExtra("type",type);
        i.putExtra("folderType",folderType);
        i.putExtra("folder",folder);
        context.startActivity(i);
    }


    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    public static void startActivityForResult(Context context,String type,String folderType,NewFolderEntity folder){
        Intent i = new Intent(context,NewFolderEditActivity.class);
        i.putExtra("type",type);
        i.putExtra("folderType",folderType);
        i.putExtra("folder",folder);
        ((BaseAppCompatActivity)context).startActivityForResult(i,REQ_CREATE_FOLDER);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerNewFolderEditComponent.builder()
                .newFolderEditModule(new NewFolderEditModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        type = getIntent().getStringExtra("type");
        mFolderType = getIntent().getStringExtra("folderType");
        mBgTemp = "";
        mSort = "NAME";
        mCoin = 0;
        if("create".equals(type)){
            mTvTitle.setText("新建");
            mTvCoin.setText("0节操");
        }else if("modify".equals(type)){
            mTvTitle.setText("修改");
            NewFolderEntity entity = getIntent().getParcelableExtra("folder");
            mTvName.setText(entity.getFolderName());
            Glide.with(NewFolderEditActivity.this)
                    .load(StringUtils.getUrl(this,entity.getCover(),DensityUtil.dip2px(NewFolderEditActivity.this,56), DensityUtil.dip2px(NewFolderEditActivity.this,56),false,true))
                    .override(DensityUtil.dip2px(NewFolderEditActivity.this,56), DensityUtil.dip2px(NewFolderEditActivity.this,56))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .bitmapTransform(new CropSquareTransformation(NewFolderEditActivity.this))
                    .into(mIvBg);
            mBgPath = entity.getCover();
            mBgTemp = entity.getCover();
            mFolderType = entity.getType();
            mFolderId = entity.getFolderId();
            mTvCoin.setText(entity.getCoin() + "节操");
            mCoin = entity.getCoin();
            for(int i = 0;i < entity.getTexts().size();i++){
                String tag = entity.getTexts().get(i);
                if(i == 0){
                    mTvTag1.setText(tag);
                    mTvTag1.setBackgroundResource(R.drawable.shape_rect_border_main_no_background_12);
                    mTvTag2.setVisibility(View.VISIBLE);
                }
                if(i == 1){
                    mTvTag2.setText(tag);
                    mTvTag2.setBackgroundResource(R.drawable.shape_rect_border_main_no_background_12);
                }
            }
        }
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.getPaint().setFakeBoldText(true);
        mTvSave.setEnabled(true);
        ViewUtils.setRightMargins(mTvSave, DensityUtil.dip2px(this,18));
        mTvSave.setText(getString(R.string.label_done));
        initPopupMenus();
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1, "名称排序");
        items.add(item);
        item = new MenuItem(2, "时间排序");
        items.add(item);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    mTvSort.setText("名称排序");
                    mSort = "NAME";
                }
                if(itemId == 2){
                    mTvSort.setText("时间排序");
                    mSort = "DATE";
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
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
    protected void initListeners() {
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
                    SoftKeyboardUtils.dismissSoftKeyboard(NewFolderEditActivity.this);
                    if(mInputType == 1){
                        if(content.length() > LIMIT_NICK_NAME){
                            mTvName.setSelected(true);
                        }else {
                            mTvName.setSelected(false);
                        }
                        mTvName.setText(content);
                    }else if(mInputType == 2){
                        if(!TextUtils.isEmpty(content)){
                            if(content.length() > LIMIT_TAG){
                                showToast("标签字数不能超过5个");
                                return;
                            }
                            mTvTag1.setText(content);
                            mTvTag1.setBackgroundResource(R.drawable.shape_rect_border_main_no_background_12);
                            if(mTvTag2.getVisibility() == View.GONE){
                                mTvTag2.setText("");
                                mTvTag2.setVisibility(View.VISIBLE);
                                mTvTag2.setBackgroundResource(R.drawable.ic_tag_add);
                            }
                        }else {
                            if(mTvTag2.getVisibility() == View.GONE){
                                mTvTag1.setText("");
                                mTvTag1.setBackgroundResource(R.drawable.ic_tag_add);
                            }else {
                                if(TextUtils.isEmpty(mTvTag2.getText())){
                                    mTvTag1.setText("");
                                    mTvTag1.setBackgroundResource(R.drawable.ic_tag_add);
                                    mTvTag2.setVisibility(View.GONE);
                                    mTvTag2.setText("");
                                    mTvTag2.setBackgroundResource(R.drawable.ic_tag_add);
                                }else {
                                    mTvTag1.setText(mTvTag2.getText());
                                    mTvTag1.setBackgroundResource(R.drawable.shape_rect_border_main_no_background_12);
                                    mTvTag2.setText("");
                                    mTvTag2.setBackgroundResource(R.drawable.ic_tag_add);
                                }
                            }
                        }
                    }else if(mInputType == 3){
                        if(!TextUtils.isEmpty(content)){
                            if(content.length() > LIMIT_TAG){
                                showToast("标签字数不能超过5个");
                                return;
                            }
                            mTvTag2.setText(content);
                            mTvTag2.setBackgroundResource(R.drawable.shape_rect_border_main_no_background_12);
                        }else {
                            mTvTag2.setText("");
                            mTvTag2.setBackgroundResource(R.drawable.ic_bag_add);
                        }
                    }
                    mHasModified = true;
                }else {
                    showToast(R.string.msg_doc_comment_not_empty);
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_menu,R.id.ll_name_root,R.id.ll_bg_root,R.id.ll_coin_root,R.id.tv_label_add_1,R.id.tv_label_add_2,R.id.ll_sort_root})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_menu:
                done();
                break;
            case R.id.ll_name_root:
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                mEdtCommentInput.setHint(getString(R.string.label_dir_name));
                mEdtCommentInput.requestFocus();
                mInputType = 1;
                SoftKeyboardUtils.showSoftKeyboard(NewFolderEditActivity.this, mEdtCommentInput);
                break;
            case R.id.ll_bg_root:
                try {
                    ArrayList<String> arrayList = new ArrayList<>();
                    DialogUtils.createImgChooseDlg(this, null,this, arrayList, 1).show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_coin_root:
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
                                mCoin = (int) coin;
                                mHasModified = true;
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
                break;
            case R.id.tv_label_add_1:
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                mEdtCommentInput.setHint("标签");
                mEdtCommentInput.requestFocus();
                mInputType = 2;
                SoftKeyboardUtils.showSoftKeyboard(NewFolderEditActivity.this, mEdtCommentInput);
                break;
            case R.id.tv_label_add_2:
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                mEdtCommentInput.setHint("标签");
                mEdtCommentInput.requestFocus();
                mInputType = 3;
                SoftKeyboardUtils.showSoftKeyboard(NewFolderEditActivity.this, mEdtCommentInput);
                break;
            case R.id.ll_sort_root:
                if(bottomMenuFragment != null)bottomMenuFragment.show(getSupportFragmentManager(),"FolderEdit");
                break;
        }
    }

    private void done(){
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast(R.string.msg_connection);
            return;
        }
        String name = mTvName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast(R.string.msg_name_cannot_null);
            return;
        }
        if(name.length() > LIMIT_NICK_NAME){
            showToast("名称不能超过10个字");
            return;
        }
        if(TextUtils.isEmpty(mBgPath)){
            showToast("封面不能为空");
            return;
        }
        createDialog();
        if (!mBgPath.equals(mBgTemp)){
            long size = new File(mBgPath).length();
            mPresenter.checkSize(size);
        }else {
            onCheckSize(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

            @Override
            public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                Glide.with(NewFolderEditActivity.this)
                        .load(photoPaths.get(0))
                        .override(DensityUtil.dip2px(NewFolderEditActivity.this,56), DensityUtil.dip2px(NewFolderEditActivity.this,56))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropSquareTransformation(NewFolderEditActivity.this))
                        .into(mIvBg);
                mBgPath = photoPaths.get(0);
                mHasModified = true;
            }
        });
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
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onSuccess() {
        finalizeDialog();
        showToast("操作成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCheckSize(boolean isOk) {
        if(isOk){
            FolderRepEntity entity = new FolderRepEntity();
            entity.coin = mCoin;
            entity.cover = mBgPath;
            entity.coverSize = mBgPath.equals(mBgTemp)? -1:0;
            entity.folderName = mTvName.getText().toString();
            entity.folderType = mFolderType;
            entity.orderbyType = mSort;
            ArrayList<String> tags = new ArrayList<>();
            if(!TextUtils.isEmpty(mTvTag1.getText())){
                tags.add(mTvTag1.getText().toString());
            }
            if(!TextUtils.isEmpty(mTvTag2.getText())){
                tags.add(mTvTag2.getText().toString());
            }
            entity.texts = tags;
            if("create".equals(type)){
                mPresenter.addFolder(entity);
            }else if("modify".equals(type)){
                mPresenter.updateFolder(mFolderId,entity);
            }
        }else {
            showToast("空间不足");
            finalizeDialog();
        }
    }
}
