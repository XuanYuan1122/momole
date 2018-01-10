package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moemoe.lalala.BuildConfig;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerDetailComponent;
import com.moemoe.lalala.di.modules.DetailModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.GiveCoinEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.RichDocListEntity;
import com.moemoe.lalala.model.entity.RichEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.presenter.DocDetailContract;
import com.moemoe.lalala.presenter.DocDetailPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.DocRecyclerViewAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
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
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.reactivex.schedulers.Schedulers;

/**
 * 文章详情
 * Created by yi on 2016/12/2.
 */

public class NewDocDetailActivity extends BaseAppCompatActivity implements DocDetailContract.View{
    private final int MENU_FAVORITE = 102;
    private final int MENU_SHARE = 103;
    private final int MENU_REPORT = 104;
    private final int MENU_DELETE = 106;
    private final int TAG_DELETE = 107;
    private final int EDIT_DOC = 108;
    private final int MENU_EGG = 109;
    private final int MENU_FORWARD = 110;
    private final int MENU_SUBMISSION = 111;
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
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.tv_doc_content)
    TextView mTvComment;
    @BindView(R.id.rl_send_root)
    View mSendRoot;
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
    private BottomMenuFragment bottomMenuFragment;
    private int mCurType = 0;//0.对楼主 1.对某楼 2.对标签
    private ArrayList<Image> mImages ;
    private ArrayList<String> mIconPaths = new ArrayList<>();
    private boolean hasLoaded = false;
    private ArrayList<DocTagEntity> mDocTags;
    private int mPosition;
    private int mCommentNum;
    private boolean isLoading;
    private KeyboardListenerLayout mKlCommentBoard;
    private int mPrePosition;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_recycleview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        DaggerDetailComponent.builder()
                .detailModule(new DetailModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mDocId = getIntent().getStringExtra(UUID);
        mPosition = !TextUtils.isEmpty(getIntent().getStringExtra("position"))?Integer.valueOf(getIntent().getStringExtra("position")):-1;
        if(TextUtils.isEmpty(mDocId)){
            finish();
        }
        mUserName = "";
        mShareDesc = "";
        mShareTitle = "";
        mShareIcon = "";
        mIvMenu.setVisibility(View.VISIBLE);
        mList.setLoadMoreEnabled(true);
        isLoading = true;
        mList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new DocRecyclerViewAdapter(this);
        mList.getRecyclerView().setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);
        mImages = new ArrayList<>();
        mTvSendComment.setEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvFrom.setVisibility(View.VISIBLE);
        mTvFrom.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvFrom.setText("文章");
        mIvMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        onBackPressed("finish");
    }

    public void onBackPressed(String type) {
        Intent i = new Intent();
        i.putExtra("position",mPosition);
        i.putExtra("type",type);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed("finish");
            }
        });
        mTvFrom.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed("finish");
            }
        });
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"DocDetailMenu");
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
                    startActivity(intent);
                } else if (o instanceof DocDetailEntity.DocLink) {
                    DocDetailEntity.DocLink link = (DocDetailEntity.DocLink) o;
                    WebViewActivity.startActivity(NewDocDetailActivity.this, link.getUrl());
                }else if(o instanceof BagDirEntity){
                    BagDirEntity entity = (BagDirEntity) o;
                    if(entity.getFolderType().equals(FolderType.ZH.toString())){
                        NewFileCommonActivity.startActivity(NewDocDetailActivity.this,FolderType.ZH.toString(),entity.getFolderId(),entity.getUserId());
                    }else if(entity.getFolderType().equals(FolderType.TJ.toString())){
                        NewFileCommonActivity.startActivity(NewDocDetailActivity.this,FolderType.TJ.toString(),entity.getFolderId(),entity.getUserId());
                    }else if(entity.getFolderType().equals(FolderType.MH.toString())){
                        NewFileManHuaActivity.startActivity(NewDocDetailActivity.this,FolderType.MH.toString(),entity.getFolderId(),entity.getUserId());
                    }else if(entity.getFolderType().equals(FolderType.XS.toString())){
                        NewFileXiaoshuoActivity.startActivity(NewDocDetailActivity.this,FolderType.XS.toString(),entity.getFolderId(),entity.getUserId());
                    }
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
        mKlCommentBoard = findViewById(R.id.ll_comment_pannel);
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    String temp = mEdtCommentInput.getText().toString();
                    if(TextUtils.isEmpty(temp)){
                        mCurType = 0;
                        mEdtCommentInput.setHint(R.string.a_hint_input_comment);
                    }
                    mSendRoot.setVisibility(View.VISIBLE);
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
        mList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.requestTopComment(mDocId,mAdapter.getCommentType(),mAdapter.isSortTime(),mAdapter.getmComments().size());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.requestDoc(mDocId);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
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

    public void scrollToPosition(int position){
        mList.getRecyclerView().scrollToPosition(position);
    }

    public int getPosition(){
        return ((LinearLayoutManager)mList.getRecyclerView().getLayoutManager()).findFirstVisibleItemPosition();
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

    public void showShareToBuy() {
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle(mShareTitle);
        String url = "http://2333.moemoe.la/share/newDoc/" + mDocId;
        oks.setTitleUrl(url);
        oks.setText(mShareDesc + " " + url);
        oks.setImageUrl(ApiService.URL_QINIU + mShareIcon);
        oks.setUrl(url);
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl(url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        MoeMoeApplication.getInstance().getNetComponent().getApiService().shareKpi("doc")
                .subscribeOn(Schedulers.io())
                .subscribe();
        oks.show(this);
    }

    private void showShare() {
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
        if(mPresenter != null)mPresenter.release();
        if(mAdapter != null) mAdapter.releaseAdapter();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.iv_comment_send})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_comment_send:
                if(mCurType == 2){
                    sendLabel();
                }else {
                    sendComment(false,null);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CreateRichDocActivity.REQUEST_CODE_UPDATE_DOC && resultCode == CreateRichDocActivity.RESPONSE_CODE){
            autoSendComment();
        }else if(requestCode == 6666){
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

    public void replyNormal(){
//        mEdtCommentInput.setText("");
//        mEdtCommentInput.requestFocus();
//        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
        CreateCommentActivity.startActivity(NewDocDetailActivity.this,mDocId,false,"",true);
    }

    public void addDocLabelView(){
        mSendRoot.setVisibility(View.GONE);
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("添加标签吧~~");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
        mCurType = 2;
    }

    public void autoSendComment(){
        mPresenter.requestDoc(mDocId);
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        isLoading = false;
        mList.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(NewDocDetailActivity.this,code,msg);
    }

    @Override
    public void onPlusLabel(int position, boolean isLike) {
        finalizeDialog();
        mAdapter.plusSuccess(isLike,position);
    }

    @Override
    public void onDeleteCommentSuccess(int position) {
        showToast("删除评论成功");
        CommentV2Entity entity = (CommentV2Entity) mAdapter.getItem(position);
        mAdapter.getmComments().remove(entity);
        mAdapter.notifyDataSetChanged();
    }

    public void deleteComment(String id, String commentId, final int position){
        mPresenter.deleteComment(id,commentId,position);
    }

    public void giveCoin(int count){
        GiveCoinEntity bean = new GiveCoinEntity(count,mDocId);
        mPresenter.giveCoin(bean);
    }

    @Override
    public void onGiveCoin(int coins){
        showToast(R.string.label_give_coin_success);
        mAdapter.onGiveCoin(coins);
    }

    public void followUser(String userId,boolean follow){
        mPresenter.followUser(userId,follow);
    }

    @Override
    public void onFollowSuccess(boolean isFollow) {
        mAdapter.followUserSuccess(isFollow);
    }

    @Override
    public void favoriteCommentSuccess(boolean isFavorite, int position) {
        if(isFavorite){
            showToast("点赞成功");
        }else {
            showToast("取消点赞成功");
        }
        ((CommentV2Entity)mAdapter.getItem(position)).setLike(isFavorite);
        if(isFavorite){
            ((CommentV2Entity)mAdapter.getItem(position)).setLikes(((CommentV2Entity)mAdapter.getItem(position)).getLikes() + 1);
        }else {
            ((CommentV2Entity)mAdapter.getItem(position)).setLikes( ((CommentV2Entity)mAdapter.getItem(position)).getLikes() - 1);
        }
        mAdapter.notifyItemChanged(position);
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

    private void initPopupMenus(final DocDetailEntity entity) {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item;

        if(entity.getUserId().equals(PreferenceUtils.getUUid())){
            item = new MenuItem(TAG_DELETE,getString(R.string.label_tag_ctrl),R.drawable.btn_doc_option_tag);
            items.add(item);

            item = new MenuItem(EDIT_DOC,getString(R.string.label_update_post),R.drawable.btn_doc_option_edit);
            items.add(item);

            item = new MenuItem(MENU_SUBMISSION,"投稿",R.drawable.btn_doc_option_contribute);
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

            item = new MenuItem(MENU_REPORT, getString(R.string.label_jubao),R.drawable.btn_doc_option_report);
            items.add(item);
        }

        item = new MenuItem(MENU_FORWARD, "转发到动态",R.drawable.btn_doc_option_forward);
        items.add(item);

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
                }else if(itemId == MENU_FORWARD){
                    ShareArticleEntity entity1 = new ShareArticleEntity();
                    entity1.setDocId(mDoc.getId());
                    entity1.setTitle(mDoc.getShare().getTitle());
                    entity1.setContent(mDoc.getShare().getDesc());
                    entity1.setCover(mDoc.getCover());
                    entity1.setCreateTime(mDoc.getCreateTime());
                    UserTopEntity entity2 = new UserTopEntity();
                    if(mDoc.getBadgeList().size() > 0){
                        entity2.setBadge(mDoc.getBadgeList().get(0));
                    }else {
                        entity2.setBadge(null);
                    }
                    entity2.setHeadPath(mDoc.getUserIcon());
                    entity2.setLevel(mDoc.getUserLevel());
                    entity2.setLevelColor(mDoc.getUserLevelColor());
                    entity2.setSex(mDoc.getUserSex());
                    entity2.setUserId(mDoc.getUserId());
                    entity2.setUserName(mDoc.getUserName());
                    entity1.setDocCreateUser(entity2);
                    CreateForwardActivity.startActivity(NewDocDetailActivity.this,entity1);
                }else if(itemId == MENU_SUBMISSION){
                    Intent i = new Intent(NewDocDetailActivity.this,SubmissionActivity.class);
                    i.putExtra(UUID,mDocId);
                    i.putExtra("doc_name",entity.getTitle());
                    startActivity(i);
                }
            }
        });
    }

    private void gotoEditDoc(){
        Intent i = new Intent(NewDocDetailActivity.this,CreateRichDocActivity.class);
        i.putExtra("doc",createRichDocFromDoc());
        i.putExtra(UUID,mDocId);
        startActivityForResult(i,CreateRichDocActivity.REQUEST_CODE_UPDATE_DOC);
    }

    private RichDocListEntity createRichDocFromDoc(){
        RichDocListEntity entity = new RichDocListEntity();
        entity.setDocId(mDocId);
        entity.setTitle(mDoc.getTitle());
        entity.setFolderId(mDoc.getFolderInfo() == null ? "" : mDoc.getFolderInfo().getFolderId());
        entity.setTags(mDoc.getTags());
        entity.setBgCover(mDoc.getCover());
        entity.setHidType(mDoc.isCoinComment());
        for(DocDetailEntity.Detail detail : mDoc.getDetails()){
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
        if(mDoc.getCoinDetails() != null) {
            if (mDoc.getDetails() != null) {
                for (DocDetailEntity.Detail detail : mDoc.getCoinDetails()) {
                    RichEntity richEntity = new RichEntity();
                    if (detail.getType().equals("DOC_TEXT")) {
                        richEntity.setInputStr((String) detail.getTrueData());
                    } else if (detail.getType().equals("DOC_IMAGE")) {
                        Image image = (Image) detail.getTrueData();
                        richEntity.setImage(image);
                    }
                    entity.getHideList().add(richEntity);
                }
            }
        }
        return entity;
    }

    private DocDetailEntity mDoc;

    @Override
    public void onDocLoaded(DocDetailEntity entity) {
        mDoc = entity;
        mList.setComplete();
        isReplyShow = entity.isCoinComment() && (entity.getCoinDetails() == null || (entity.getCoinDetails() != null && entity.getCoinDetails().size() <= 0));
//        mList.setLoadMoreEnabled(true);
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
        isLoading = true;
        mPresenter.requestTopComment(mDocId,mAdapter.getCommentType(),mAdapter.isSortTime(),0);
       // mPresenter.requestTopComment(mDoc.getId());
    }

    public void requestComment(){
        mPresenter.requestTopComment(mDocId,mAdapter.getCommentType(),mAdapter.isSortTime(),0);
    }

    public void favoriteComment(String id,boolean isFavorite,int position){
        mPresenter.favoriteComment(mDoc.getId(),id,isFavorite,position);
    }

    @Override
    public void onLoadTopCommentSuccess(ArrayList<CommentV2Entity> commentV2Entities,boolean isPull) {
        finalizeDialog();
        isLoading = false;
        mList.setComplete();
        if(isPull){
            mAdapter.setComment(commentV2Entities);
        }else {
            mAdapter.addComment(commentV2Entities);
        }


//        if(commentV2Entities.size() > 1){
//            mTvComment.setGravity(Gravity.CENTER);
//            mTvComment.setPadding(0,0,0,0);
//            mTvComment.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
//            mTvComment.setText("显示全部"+StringUtils.getNumberInLengthLimit(mDoc.getComments(),3)+"条评论");
//            mTvComment.setOnClickListener(new NoDoubleClickListener() {
//                @Override
//                public void onNoDoubleClick(View v) {
//                    CommentListActivity.startActivityForResult(NewDocDetailActivity.this,mDocId,mDoc.getUserId());
//                }
//            });
//        }else {
            mTvComment.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            mTvComment.setPadding((int) getResources().getDimension(R.dimen.x24),0,0,0);
            mTvComment.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
            mTvComment.setText("输入评论...");
            mTvComment.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    CreateCommentActivity.startActivity(NewDocDetailActivity.this,mDocId,false,"",true);
                }
            });
//        }
    }

    @Override
    public void onDeleteDoc() {
        showToast("删除成功");
        onBackPressed("delete");
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

    private boolean isReplyShow = false;

    @Override
    public void onSendComment() {
        finalizeDialog();
        clearEdit();
        mIconPaths.clear();
        showToast(R.string.msg_send_comment_success);
        if(isReplyShow){
            autoSendComment();
        }
    }
}
