package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.moemoe.lalala.BuildConfig;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerDetailComponent;
import com.moemoe.lalala.di.modules.DetailModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.GiveCoinEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.RichDocListEntity;
import com.moemoe.lalala.model.entity.RichEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.presenter.DocDetailContract;
import com.moemoe.lalala.presenter.DocDetailPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.adapter.DocRecyclerViewAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.SelectImgAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.recycler.RecyclerViewPositionHelper;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by yi on 2016/12/2.
 */

public class NewDocDetailActivity extends BaseAppCompatActivity implements DocDetailContract.View{
    private final int MENU_FAVORITE = 102;
    private final int MENU_SHARE = 103;
    private final int MENU_REPORT = 104;
    private final int MENU_GOTO_FLOOR = 105;
    private final int MENU_DELETE = 106;
    private final int TAG_DELETE = 107;
    private final int EDIT_DOC = 108;
    private final int ICON_NUM_LIMIT = 9;
    private final int REQ_GET_EDIT_VERSION_IMG = 2333;
    private final int REQ_TO_FOLDER = 30003;
    private final int REQ_DELETE_TAG = 30004;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvFrom;
    @BindView(R.id.rv_list)
    PullAndLoadView mList;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.rv_img)
    RecyclerView mRvComment;
    @BindView(R.id.tv_menu)
    TextView mTvOnlyHost;
    @BindView(R.id.tv_jump_to)
    TextView mTvJumpTo;
    @BindView(R.id.ll_jump_root)
    View mLlJumpRoot;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @Inject
    DocDetailPresenter mPresenter;
    /**
     * 当前回复给谁
     */
    private String mToUserId;
    private String mDocId;
    private String mUserName;
    private String mShareDesc;
    private String mShareTitle;
    private String mShareIcon;
    private DocRecyclerViewAdapter mAdapter;
    private SelectImgAdapter mSelectAdapter;
    private BottomMenuFragment bottomMenuFragment;
    private boolean mTargetId;
    private int mCurFloor = 0;
    private int mCurType = 0;//0.对楼主 1.对某楼 2.对标签
    private ArrayList<Image> mImages ;
    private ArrayList<String> mIconPaths = new ArrayList<>();
    private boolean hasLoaded = false;
    private boolean mIsLoading = false;
    private int mCommentNum = 0;
    private ArrayList<DocTagEntity> mDocTags;
    
    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_recycleview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerDetailComponent.builder()
                .detailModule(new DetailModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mDocId = getIntent().getStringExtra(UUID);
        if(TextUtils.isEmpty(mDocId)){
            finish();
        }
        mUserName = "";
        mShareDesc = "";
        mShareTitle = "";
        mShareIcon = "";
        mIvMenu.setVisibility(View.VISIBLE);
        mList.setLoadMoreEnabled(false);
        mList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new DocRecyclerViewAdapter(this);
        mList.getRecyclerView().setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);
        mImages = new ArrayList<>();
        mCurFloor = PreferenceUtils.getDocCurFloor(this,mDocId);
        if (mCurFloor > 20){
            mTvJumpTo.setText(getString(R.string.label_jump_to,mCurFloor));
            mLlJumpRoot.setVisibility(View.VISIBLE);
        }else {
            PreferenceUtils.removeData(this,mDocId);
            mLlJumpRoot.setVisibility(View.GONE);
        }
        mSelectAdapter = new SelectImgAdapter(this);
        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvComment.setLayoutManager(selectRvL);
        mRvComment.setAdapter(mSelectAdapter);
        mRvComment.setVisibility(View.GONE);
        mTvSendComment.setEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvFrom.setVisibility(View.VISIBLE);
        mTvFrom.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        String from = getIntent().getStringExtra("from_name");
        if(!TextUtils.isEmpty(from)) mTvFrom.setText(from);
        mIvMenu.setVisibility(View.VISIBLE);
        mTvOnlyHost.setVisibility(View.VISIBLE);
        mTvOnlyHost.setWidth(DensityUtil.dip2px(this,50));
        mTvOnlyHost.setHeight(DensityUtil.dip2px(this,20));
        mTvOnlyHost.setTextSize(TypedValue.COMPLEX_UNIT_DIP,9);
        mTvOnlyHost.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvOnlyHost.setText(getString(R.string.label_only_host));
        mTvOnlyHost.setBackgroundResource(R.drawable.btn_only_host_new);
    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"DocDetailMenu");
            }
        });
        final RecyclerViewPositionHelper recyclerViewHelper = RecyclerViewPositionHelper.createHelper(mList.getRecyclerView());
        mList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mLlJumpRoot.getVisibility() == View.VISIBLE){
                    int pos = recyclerViewHelper.findFirstVisibleItemPosition();
                    if(pos >= 0){
                        Object o = mAdapter.getItem(pos);
                        if(o instanceof NewCommentEntity){
                            NewCommentEntity bean = (NewCommentEntity) o;
                            int curFloor = bean.getIdx();
                            if(curFloor > 10){
                                mLlJumpRoot.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                   // if(!isFinishing()) Glide.with(NewDocDetailActivity.this).resumeRequests();
                    int pos = recyclerViewHelper.findFirstVisibleItemPosition();
                    if(pos >= 0){
                        Object o = mAdapter.getItem(pos);
                        if(o instanceof NewCommentEntity){
                            NewCommentEntity bean = (NewCommentEntity) o;
                            if(!mTargetId) mCurFloor = bean.getIdx();

                        }else {
                            mCurFloor = 0;
                        }
                    }
                } else {
                  //  if(!isFinishing()) Glide.with(NewDocDetailActivity.this).pauseRequests();
                }
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if (o instanceof Image) {
                    Image img = (Image) o;
                    Intent intent = new Intent(NewDocDetailActivity.this, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, mImages);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                            mImages.indexOf(img));
                    // 以后可选择 有返回数据
                    startActivity(intent);
                } else if (o instanceof DocDetailEntity.DocLink) {
                    DocDetailEntity.DocLink link = (DocDetailEntity.DocLink) o;
                    WebViewActivity.startActivity(NewDocDetailActivity.this, link.getUrl());
                }else if(o instanceof BagDirEntity){
                    BagDirEntity entity = (BagDirEntity) o;
                    Intent i = new Intent(NewDocDetailActivity.this,FolderActivity.class);
                    i.putExtra("info",entity);
                    i.putExtra("position",position);
                    i.putExtra("show_more",true);
                    i.putExtra(UUID, entity.getUserId());
                    startActivityForResult(i,REQ_TO_FOLDER);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof String){
                    ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("绅士内容", (String) o);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(NewDocDetailActivity.this, "复制成功");
                }
            }
        });
        KeyboardListenerLayout mKlCommentBoard = (KeyboardListenerLayout) findViewById(R.id.ll_comment_pannel);
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    String temp = mEdtCommentInput.getText().toString();
                    if(TextUtils.isEmpty(temp)){
                        mCurType = 0;
                        mEdtCommentInput.setHint(R.string.a_hint_input_comment);
                    }
                }
            }
        });
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCurType == 2) {
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
                mRvComment.setVisibility(View.GONE);
                mTvSendComment.setEnabled(false);
            }
        });
        mList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                int floor = 1;
                Object o = mAdapter.getItem(mAdapter.getItemCount() - 1);
                if(o instanceof NewCommentEntity){
                    floor = ((NewCommentEntity)o).getIdx() + 1;
                }
                mIsLoading = true;
                requestCommentsByFloor(floor,mTargetId,false,false);
            }

            @Override
            public void onRefresh() {
                if(NetworkUtils.checkNetworkAndShowError(NewDocDetailActivity.this)){
                    mIsLoading = true;
                    mPresenter.requestDoc(mDocId);
                }
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.requestDoc(mDocId);
    }

    @Override
    protected void initData() {

    }

    private void deleteDoc(){
        if(NetworkUtils.checkNetworkAndShowError(this)){
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
                    mPresenter.deleteDoc(mDocId);
                    alertDialogUtil.dismissDialog();
                }
            });
            alertDialogUtil.showDialog();

        }
    }

    private void jumpToFloor(){
        if(hasLoaded){
            if(mCommentNum > 0){
                final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                dialogUtil.createEditDialog(this, mCommentNum,1);
                dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        dialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        String content = dialogUtil.getEditTextContent();
                        if(!TextUtils.isEmpty(content)){
                            requestCommentsByFloor(Long.valueOf(content),mTargetId,true,false);
                            dialogUtil.dismissDialog();
                        }else {
                            showToast(R.string.msg_can_not_empty);
                        }
                    }
                });
                dialogUtil.showDialog();
            }else {
                showToast(R.string.msg_have_no_comments);
            }
        }

    }

    private void favoriteDoc(){
        if(hasLoaded){
            mPresenter.favoriteDoc(mDocId);
        }
    }

    private void reportDoc(){
        Intent intent = new Intent(this, JuBaoActivity.class);
        intent.putExtra(JuBaoActivity.EXTRA_NAME, mUserName);
        intent.putExtra(JuBaoActivity.EXTRA_CONTENT, mShareDesc);
        intent.putExtra(JuBaoActivity.UUID,mDocId);
        intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC.toString());
        startActivity(intent);
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mShareTitle);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        String url;
        if(BuildConfig.DEBUG){
            url = ApiService.SHARE_BASE_DEBUG + mDocId;
        }else {
            url = ApiService.SHARE_BASE + mDocId;
        }
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mShareDesc + " " + url);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(ApiService.URL_QINIU + mShareIcon);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        // 启动分享GUI
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                mPresenter.shareDoc();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        mAdapter.releaseAdapter();
        PreferenceUtils.saveDocCurFloor(this,mDocId,mCurFloor);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
    }

    @OnClick({R.id.iv_comment_send,R.id.iv_add_img,R.id.tv_menu,R.id.iv_cancel_jump,R.id.tv_jump_to})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_comment_send:
                if(mCurType == 2){
                    sendLabel();
                }else {
                    sendComment(false,null);
                }
                break;
            case R.id.iv_add_img:
                choosePhoto();
                break;
            case R.id.tv_menu:
                if(!mTargetId){
                    if(hasLoaded){
                        mTargetId = true;
                        mTvOnlyHost.setSelected(true);
                        mTvOnlyHost.setTextColor(ContextCompat.getColor(this,R.color.white));
                        requestCommentsByFloor(1,mTargetId,true,false);
                    }
                }else {
                    mTargetId = false;
                    mTvOnlyHost.setSelected(false);
                    mTvOnlyHost.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
                    requestCommentsByFloor(1,mTargetId,true,false);
                }
                break;
            case R.id.iv_cancel_jump:
                mLlJumpRoot.setVisibility(View.GONE);
                break;
            case R.id.tv_jump_to:
                mLlJumpRoot.setVisibility(View.GONE);
                PreferenceUtils.removeData(NewDocDetailActivity.this,mDocId);
                requestCommentsByFloor(mCurFloor,mTargetId,true,false);
                break;
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
        if(requestCode == 6666){
            if(resultCode == RESULT_OK && data != null){
                int position = data.getIntExtra(JuBaoActivity.EXTRA_POSITION,-1);
                if(position != -1 && mAdapter != null){
                    mAdapter.ownerDelSuccess(position);
                }
            }
        }else if(requestCode == REQ_GET_EDIT_VERSION_IMG) {
            if (resultCode == RESULT_OK && data != null) {
                String photoPath = null;
                Uri u = data.getData();
                if (u != null) {
                    String schema = u.getScheme();
                    if ("file".equals(schema)) {
                        photoPath = u.getPath();
                    }else if ("content".equals(schema)) {
                        photoPath = StorageUtils.getTempFile(System.currentTimeMillis() + ".jpg").getAbsolutePath();
                        InputStream is;
                        FileOutputStream fos;
                        try {
                            is = getContentResolver().openInputStream(u);
                            fos = new FileOutputStream(new File(photoPath));
                            FileUtil.copyFile(is, fos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (FileUtil.isValidGifFile(photoPath)) {
                            String newFile = StorageUtils.getTempFile(System.currentTimeMillis() + ".gif").getAbsolutePath();
                            FileUtil.copyFile(photoPath, newFile);
                            FileUtil.deleteOneFile(photoPath);
                            photoPath = newFile;
                        }
                    }
                    mIconPaths.add(photoPath);
                    onGetPhotos();

                }
            }
        }else if(requestCode == REQ_TO_FOLDER && resultCode == RESULT_OK){
            boolean change = data.getBooleanExtra("change",false);
            int position = data.getIntExtra("position",-1);
            BagDirEntity entity = data.getParcelableExtra("info");
            if(entity != null && change){
                if(position != -1){
                    Object o = mAdapter.getItem(position);
                    ((BagDirEntity)o).setBuy(entity.isBuy());
                }
            }
        }else if(requestCode == REQ_DELETE_TAG && resultCode == RESULT_OK){
            ArrayList<DocTagEntity> entities = data.getParcelableArrayListExtra("tags");
            if(entities != null){
                mDocTags = entities;
                mAdapter.setTags(entities);
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
        if (mIconPaths.size() == 0) {
            // 取消选择了所有图
            mRvComment.setVisibility(View.GONE);
            String content = mEdtCommentInput.getText().toString();
            if(TextUtils.isEmpty(content)){
                mTvSendComment.setEnabled(false);
            }
        }else if(mIconPaths.size() <= ICON_NUM_LIMIT){
            mTvSendComment.setEnabled(true);
            mRvComment.setVisibility(View.VISIBLE);
            mSelectAdapter.setData(mIconPaths);
        }
    }

    public void clearEdit(){
        mToUserId = "";
        mEdtCommentInput.setText("");
        mCurType = 0;
        mEdtCommentInput.setHint(R.string.a_hint_input_comment);
    }

    private void sendLabel(){
        if (!hasLoaded) {
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            String content = mEdtCommentInput.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                SoftKeyboardUtils.dismissSoftKeyboard(this);
                mAdapter.createLabel(content);
            }else {
                showToast(R.string.msg_doc_comment_not_empty);
            }
        }
    }

    private void sendComment(final boolean auto,String str) {
        if (!hasLoaded) {
            return;
        }
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            String content = mEdtCommentInput.getText().toString();
            if(auto) content = str;
            if(TextUtils.isEmpty(content)){
                showToast(R.string.msg_doc_comment_not_empty);
                return;
            }
            if(TextUtils.isEmpty(content) && mIconPaths.size() == 0){
                showToast(R.string.msg_doc_comment_not_empty);
                return;
            }
            final ArrayList<Image> images = BitmapUtils.handleUploadImage(mIconPaths);
            ArrayList<String> paths = new ArrayList<>();
            if(images != null && images.size() > 0){
                for(int i = 0; i < images.size(); i++){
                    paths.add(images.get(i).getPath());
                }
            }
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            createDialog();
            CommentSendEntity bean = new CommentSendEntity(content,mDocId,null,mToUserId);
            mPresenter.sendComment(paths,bean);

        }
    }

    public void likeTag(boolean isLike,int position, TagLikeEntity entity){
        mPresenter.likeTag(isLike,position,entity);
    }

    public void reply(NewCommentEntity bean){
        mToUserId = bean.getFromUserId();
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("回复 " + bean.getFromUserName() + ": ");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
    }

    public void addDocLabelView(){
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("添加标签吧~~");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
        mCurType = 2;
        //  mIsLabel = true;
    }

    public void autoSendComment(){
        mPresenter.requestDoc(mDocId);
    }

    public void requestCommentsByFloor(final long floor, final boolean targetId, final boolean isJump, final boolean addBefore, int length, final boolean clear){
        if(hasLoaded){
            if (floor < 1){
                showToast(R.string.label_floor_limit);
                return;
            }
            mPresenter.requestCommentFloor(mDocId,floor,length,targetId,isJump,clear,addBefore);
        }
    }

    public void requestCommentsByFloor(final long floor, final boolean targetId, final boolean isJump, final boolean addBefore){
        requestCommentsByFloor(floor,targetId,isJump,addBefore, ApiService.LENGHT,true);
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        mIsLoading = false;
        mList.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(NewDocDetailActivity.this,code,msg);
    }

    @Override
    public void onPlusLabel(int position, boolean isLike) {
        finalizeDialog();
        mAdapter.plusSuccess(isLike,position);
    }

    public void deleteComment(NewCommentEntity entity, int position){
        mPresenter.deleteComment(entity,position);
    }

    @Override
    public void onDeleteComment(NewCommentEntity entity, int position) {
        showToast(R.string.msg_comment_delete_success);
        mAdapter.deleteCommentSuccess(entity,position);
    }

    public void giveCoin(){
        GiveCoinEntity bean = new GiveCoinEntity(1,mDocId);
        mPresenter.giveCoin(bean);
    }

    @Override
    public void onGiveCoin(){
        showToast(R.string.label_give_coin_success);
        mAdapter.onGiveCoin();
    }

    public void followUser(String userId,boolean follow){
        mPresenter.followUser(userId,follow);
    }

    @Override
    public void onFollowSuccess(boolean isFollow) {
        mAdapter.followUserSuccess(isFollow);
    }

    public void getCoinContent(){
        mPresenter.getCoinContent(mDocId);
    }

    @Override
    public void onGetCoinContent() {
        autoSendComment();
        showToast(R.string.label_use_coin_success);
    }

    public void createLabel(TagSendEntity entity){
        createDialog();
        mPresenter.createLabel(entity);
    }

    @Override
    public void onCreateLabel(String s,String name){
        finalizeDialog();
        clearEdit();
        mAdapter.onCreateLabel(s,name);
    }

    private void initPopupMenus(DocDetailEntity entity) {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item;

        if(entity.getUserId().equals(PreferenceUtils.getUUid())){
            item = new MenuItem(MENU_GOTO_FLOOR, getString(R.string.label_goto_floor),R.drawable.btn_doc_option_jump);
            items.add(item);

            item = new MenuItem(TAG_DELETE,getString(R.string.label_tag_ctrl),R.drawable.btn_doc_option_tag);
            items.add(item);

            item = new MenuItem(EDIT_DOC,getString(R.string.label_update_post),R.drawable.btn_doc_option_edit);
            items.add(item);

            item = new MenuItem(MENU_DELETE,getString(R.string.label_delete),R.drawable.btn_doc_option_delete);
            items.add(item);

        }else {

            if(entity.isFavoriteFlag()){
                item = new MenuItem(MENU_FAVORITE, getString(R.string.label_cancel_favorite),R.drawable.btn_doc_option_collected);
            }else {
                item = new MenuItem(MENU_FAVORITE, getString(R.string.label_favorite),R.drawable.btn_doc_option_collect);
            }
            items.add(item);

            item = new MenuItem(MENU_GOTO_FLOOR, getString(R.string.label_goto_floor),R.drawable.btn_doc_option_jump);
            items.add(item);

            item = new MenuItem(MENU_REPORT, getString(R.string.label_jubao),R.drawable.btn_doc_option_report);
            items.add(item);

        }

        item = new MenuItem(MENU_SHARE, getString(R.string.label_share),R.drawable.btn_doc_option_share);
        items.add(item);

        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_HORIZONTAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == MENU_FAVORITE){
                    favoriteDoc();
                }else if(itemId == MENU_GOTO_FLOOR){
                    if(!mTargetId) {
                        jumpToFloor();
                    }else {
                        showToast("只看楼主时无法跳转楼层");
                    }
                }else if (itemId == MENU_REPORT) {
                    reportDoc();
                }else if (itemId == MENU_SHARE) {
                    if (hasLoaded) {
                        showShare();
                    }else {
                        showToast(R.string.label_doc_not_loaded);
                    }
                }else if(itemId == MENU_DELETE){
                    deleteDoc();
                }else if(itemId == TAG_DELETE){
                    if(mDocTags != null){
                        Intent i = new Intent(NewDocDetailActivity.this,TagControlActivity.class);
                        i.putParcelableArrayListExtra("tags",mDocTags);
                        i.putExtra(UUID,mDocId);
                        startActivityForResult(i,REQ_DELETE_TAG);
                    }
                }else if(itemId == EDIT_DOC){
                   gotoEditDoc();
                }
            }
        });
    }

    private void gotoEditDoc(){
        Intent i = new Intent(NewDocDetailActivity.this,CreateRichDocActivity.class);
        i.putExtra("doc",createRichDocFromDoc());
        i.putExtra(UUID,mDocId);
        startActivityForResult(i,REQ_DELETE_TAG);
    }

    private RichDocListEntity createRichDocFromDoc(){
        RichDocListEntity entity = new RichDocListEntity();
        entity.setDocId(mDocId);
        entity.setFolderId(mDoc.getFolderInfo().getFolderId());
        entity.setTags(mDoc.getTags());
        if(mDoc.getCoinDetails() != null){
            for(DocDetailEntity.Detail detail : mDoc.getCoinDetails()){
                RichEntity richEntity = new RichEntity();
                if(detail.getType().equals("DOC_TEXT")){
                    richEntity.setInputStr((String) detail.getTrueData());
                }else if(detail.getType().equals("DOC_IMAGE")){
                    Image image = (Image) detail.getTrueData();
                    richEntity.setImage(image);
                }else if(detail.getType().equals("DOC_MUSIC")){
                    DocDetailEntity.DocMusic music = (DocDetailEntity.DocMusic) detail.getTrueData();
                    entity.setMusicPath(music.getUrl());
                    entity.setMusicTitle(music.getName());
                    entity.setTime(music.getTimestamp());
                    entity.setCover(music.getCover());
                }
                entity.getList().add(richEntity);
            }
        }
        if(mDoc.getDetails() != null){
            for(DocDetailEntity.Detail detail : mDoc.getDetails()){
                RichEntity richEntity = new RichEntity();
                if(detail.getType().equals("DOC_TEXT")){
                    richEntity.setInputStr((String) detail.getTrueData());
                }else if(detail.getType().equals("DOC_IMAGE")){
                    Image image = (Image) detail.getTrueData();
                    richEntity.setImage(image);
                }
                entity.getHideList().add(richEntity);
            }
        }
        return entity;
    }

    private DocDetailEntity mDoc;

    @Override
    public void onDocLoaded(DocDetailEntity entity) {
        mDoc = entity;
        mIsLoading = false;
        mList.setComplete();
        mList.setLoadMoreEnabled(true);
        mCommentNum = entity.getComments();
        hasLoaded = true;
        mUserName = entity.getUserName();
        mShareDesc = entity.getShare().getDesc();
        mShareTitle = entity.getShare().getTitle();
        mShareIcon = entity.getShare().getIcon();
        mDocTags = entity.getTags();
        if(bottomMenuFragment == null) initPopupMenus(entity);
        mImages.clear();
        Gson gson = new Gson();
        if(entity.getCoinDetails() != null){
            for(DocDetailEntity.Detail detail : entity.getCoinDetails()){
                if(detail.getType().equals("DOC_TEXT")){
                    detail.setTrueData(detail.getData().get("text").getAsString());
                }else if(detail.getType().equals("DOC_IMAGE")){
                    Image image = gson.fromJson(detail.getData(),Image.class);
                    detail.setTrueData(image);
                    mImages.add(image);
                }else if(detail.getType().equals("DOC_MUSIC")){
                    detail.setTrueData(gson.fromJson(detail.getData(),DocDetailEntity.DocMusic.class));
                }else if(detail.getType().equals("DOC_LINK")){
                    detail.setTrueData(gson.fromJson(detail.getData(),DocDetailEntity.DocLink.class));
                }else if(detail.getType().equals("DOC_GROUP_LINK")){
                    detail.setTrueData(gson.fromJson(detail.getData(),DocDetailEntity.DocGroupLink.class));
                }
            }
        }
        if(entity.getDetails() != null){
            for(DocDetailEntity.Detail detail : entity.getDetails()){
                if(detail.getType().equals("DOC_TEXT")){
                    detail.setTrueData(detail.getData().get("text").getAsString());
                }else if(detail.getType().equals("DOC_IMAGE")){
                    Image image = gson.fromJson(detail.getData(),Image.class);
                    detail.setTrueData(image);
                    mImages.add(image);
                }else if(detail.getType().equals("DOC_MUSIC")){
                    detail.setTrueData(gson.fromJson(detail.getData(),DocDetailEntity.DocMusic.class));
                }else if(detail.getType().equals("DOC_LINK")){
                    detail.setTrueData(gson.fromJson(detail.getData(),DocDetailEntity.DocLink.class));
                }else if(detail.getType().equals("DOC_GROUP_LINK")){
                    detail.setTrueData(gson.fromJson(detail.getData(),DocDetailEntity.DocGroupLink.class));
                }
            }
        }
        mAdapter.setData(entity);
        requestCommentsByFloor(1,mTargetId,false,false);
    }

    @Override
    public void onCommentsLoaded(ArrayList<NewCommentEntity> entities,boolean pull,boolean isJump,boolean clear,boolean addBefore) {
        finalizeDialog();
        mIsLoading = false;
        mList.setComplete();
        if(entities.size() > 0){
            if((pull || isJump) && clear){
                mAdapter.setComment(entities,mTargetId);
            }else {
                mAdapter.addComment(entities,mTargetId,addBefore);
            }
            if(isJump) {
                mList.getRecyclerView().scrollToPosition(mAdapter.getTagsPosition() + 2);
            }
        }
        if(entities.size() == 0 && isJump){
            mAdapter.setComment(entities,mTargetId);
        }
    }

    @Override
    public void onDeleteDoc() {
        showToast("删除成功");
        finish();
    }

    @Override
    public void onFavoriteDoc(boolean favorite) {
        if (favorite){
            bottomMenuFragment.changeItemTextById(MENU_FAVORITE,getString(R.string.label_cancel_favorite),R.drawable.btn_doc_option_collected);
            showToast(R.string.label_favorite_success);
        }else {
            bottomMenuFragment.changeItemTextById(MENU_FAVORITE,getString(R.string.label_favorite),R.drawable.btn_doc_option_collect);
            showToast(R.string.label_cancel_favorite_success);
        }
    }

    @Override
    public void onSendComment() {
        finalizeDialog();
        clearEdit();
        mIconPaths.clear();
        mSelectAdapter.notifyDataSetChanged();
        mRvComment.setVisibility(View.GONE);
        showToast(R.string.msg_send_comment_success);
    }
}
