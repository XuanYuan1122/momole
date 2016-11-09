package com.moemoe.lalala;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.moemoe.lalala.adapter.DocRecyclerViewAdapter;
import com.moemoe.lalala.adapter.SelectImgAdapter;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.DocTag;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.NetaDownloadInfo;
import com.moemoe.lalala.data.NewCommentBean;
import com.moemoe.lalala.data.NewDocBean;
import com.moemoe.lalala.data.REPORT;
import com.moemoe.lalala.db.GameDbHelper;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.filedownloader.DownloadFileInfo;
import com.moemoe.lalala.utils.filedownloader.FileDownloader;
import com.moemoe.lalala.utils.filedownloader.base.Status;
import com.moemoe.lalala.utils.filedownloader.listener.OnDeleteDownloadFileListener;
import com.moemoe.lalala.utils.filedownloader.listener.OnDetectBigUrlFileListener;
import com.moemoe.lalala.utils.filedownloader.listener.OnFileDownloadStatusListener;
import com.moemoe.lalala.utils.filedownloader.util.MathUtil;
import com.moemoe.lalala.utils.onekeyshare.OnekeyShare;
import com.moemoe.lalala.view.FreshDownloadView;
import com.moemoe.lalala.view.KeyboardListenerLayout;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.menu.MenuItem;
import com.moemoe.lalala.view.menu.PopupListMenu;
import com.moemoe.lalala.view.menu.PopupMenuItems;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.moemoe.lalala.view.recycler.RecyclerViewPositionHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haru on 2016/5/6 0006.
 */
@ContentView(R.layout.ac_one_recycleview)
public class NewDocDetailActivity extends BaseActivity implements IConstants,View.OnClickListener,OnFileDownloadStatusListener{
    public static final String TAG="NewDocDetailActivity";
    private static final int ICON_NUM_LIMIT = 9;
    private static final int MENU_FAVORITE = 102;
    private static final int MENU_SHARE = 103;
    private static final int MENU_REPORT = 104;
    private static final int MENU_GOTO_FLOOR = 105;
    private static final int REQ_GET_EDIT_VERSION_IMG = 2333;
    public static final String specialId = "8b6cbeca-9029-11e6-b6ea-525400761152";
    public static final String gameUrl = "http://cdn1.media.moemoe.la/apk/game/bh3_neta.apk";
    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.tv_title)
    private TextView mTvTitle;
    private RecyclerView mDocRv;
    @FindView(R.id.rv_list)
    private PullAndLoadView mPullAndLoadView;
    @FindView(R.id.iv_follow_event)
    private ImageView mRss;
    @FindView(R.id.iv_menu_list)
    private ImageView mIvMenu;
    @FindView(R.id.rv_img)
    private RecyclerView mRvComment;
    @FindView(R.id.iv_add_img)
    private ImageView mIvAddImg;
    @FindView(R.id.fv_download)
    private FreshDownloadView mFvDownload;
    @FindView(R.id.tv_only_host)
    private TextView mTvOnlyHost;
    @FindView(R.id.iv_cancel_jump)
    private ImageView mIvCancelJump;
    @FindView(R.id.tv_jump_to)
    private TextView mTvJumpTo;
    @FindView(R.id.ll_jump_root)
    private View mLlJumpRoot;
    private DocRecyclerViewAdapter mAdapter;
   // private MusicServiceManager mServiceManager;
   // private MusicPlayBroadCast mReceiver;
   // private MusicTimer mMusicTimer;
    private NewDocBean mDocBean;
    private ArrayList<NewCommentBean> mComments;
    private SelectImgAdapter mSelectAdapter;
    // 回复评论
    private KeyboardListenerLayout mKlCommentBoard;
    private EditText mEdtCommentInput;
    private View mIvSendGift, mTvSendComment;
    private boolean mIsLabel;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private String mDocId;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private ArrayList<Image> mImages ;
    private ArrayList<String> mIconPaths = new ArrayList<>();
    private NetaDownloadInfo mInfo;
    private String mTargetId;
    /**
     * 当前回复给谁
     */
    private String mToUserId;
    private DbManager db;
    /**
     * more 菜单
     */
    private PopupListMenu mMenu;
    private MoeMoeCallback allFinishCallback;
    private boolean mIsPullDown;
    private int mCurFloor = 0;
    private RecyclerViewPositionHelper mRecyclerViewHelper;

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText("");
        mDocId = mIntent.getStringExtra(BaseActivity.EXTRA_KEY_UUID);
        Log.d("docId",mDocId);
        if(TextUtils.isEmpty(mDocId)){
            finish();
        }
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mMenu.showMenu(mIvMenu);
            }
        });
        mImages = new ArrayList<>();
        mComments = new ArrayList<>();
       // mServiceManager = MapActivity.sMusicServiceManager;
       // mReceiver = new MusicPlayBroadCast();
        IntentFilter filter = new IntentFilter(BROADCAST_NAME);
        filter.addAction(BROADCAST_NAME);
        registerReceiver(mReceiver, filter);
        mSwipeRefreshWidget = mPullAndLoadView.getSwipeRefreshLayout();
        mSwipeRefreshWidget.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mDocRv = mPullAndLoadView.getRecyclerView();
        mAdapter = new DocRecyclerViewAdapter(this);
        mDocRv.setAdapter(mAdapter);
       // mMusicTimer = new MusicTimer(mAdapter.mHandler);
       // mAdapter.setMusicTimer(mMusicTimer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPullAndLoadView.setLayoutManager(layoutManager);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mDocRv);
        mDocRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mLlJumpRoot.getVisibility() == View.VISIBLE){
                    int pos = mRecyclerViewHelper.findFirstVisibleItemPosition();
                    if(pos >= 0){
                        Object o = mAdapter.getItem(pos);
                        if(o instanceof NewCommentBean){
                            NewCommentBean bean = (NewCommentBean) o;
                            int curFloor = bean.idx;
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
                    Picasso.with(NewDocDetailActivity.this).resumeTag(TAG);
                    int pos = mRecyclerViewHelper.findFirstVisibleItemPosition();
                    if(pos >= 0){
                        Object o = mAdapter.getItem(pos);
                        if(o instanceof NewCommentBean){
                            NewCommentBean bean = (NewCommentBean) o;
                            if(TextUtils.isEmpty(mTargetId)) mCurFloor = bean.idx;

                        }else {
                            mCurFloor = 0;
                        }
                    }
                } else {
                    Picasso.with(NewDocDetailActivity.this).pauseTag(TAG);
                }
            }
        });
        mAdapter.setOnItemClickListener(new DocRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if (o instanceof NewDocBean.DocText) {

                } else if (o instanceof NewDocBean.DocImage) {
                    NewDocBean.DocImage img = (NewDocBean.DocImage) o;
                    Intent intent = new Intent(NewDocDetailActivity.this, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, mImages);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                            mImages.indexOf(img.image));
                    // 以后可选择 有返回数据
                    startActivity(intent);
                } else if (o instanceof NewDocBean.DocLink) {
                    NewDocBean.DocLink link = (NewDocBean.DocLink) o;
                    WebViewActivity.startActivity(NewDocDetailActivity.this, link.url);
                } else if (o instanceof NewCommentBean) {

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPullAndLoadView.isLoadMoreEnabled(true);
        mPullAndLoadView.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
                mPullAndLoadView.isLoadMoreEnabled(true);
                mIsHasLoadedAll = false;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return mIsHasLoadedAll;
            }
        });

        mKlCommentBoard = (KeyboardListenerLayout) findViewById(R.id.ll_comment_pannel);
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    if(mIsLabel){
                        mEdtCommentInput.setText("");
                        mEdtCommentInput.setHint(R.string.a_hint_input_comment);
                    }
                    //mTvSendComment.setTag(TAG_SEND_COMMEND);
                    mIsLabel = false;
                }
            }
        });
        mEdtCommentInput = (EditText) findViewById(R.id.edt_comment_input);
        mEdtCommentInput.setOnClickListener(this);
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsLabel) {
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
        mCurFloor = mPreferMng.getDocCurFloor(mDocId);
        if (mCurFloor > 20){
            mTvJumpTo.setText(getString(R.string.label_jump_to,mCurFloor));
            mLlJumpRoot.setVisibility(View.VISIBLE);
        }else {
            mPreferMng.removeData(mDocId);
            mLlJumpRoot.setVisibility(View.GONE);
        }
        mTvOnlyHost.setOnClickListener(this);
        mIvAddImg.setOnClickListener(this);
        mIvCancelJump.setOnClickListener(this);
        mTvJumpTo.setOnClickListener(this);
        mSelectAdapter = new SelectImgAdapter(this);
        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvComment.setLayoutManager(selectRvL);
        mRvComment.setAdapter(mSelectAdapter);
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
        mRvComment.setVisibility(View.GONE);
        mTvSendComment = findViewById(R.id.iv_comment_send);
        mTvSendComment.setOnClickListener(this);
        mTvSendComment.setEnabled(false);
        mRss.setOnClickListener(this);
        mFvDownload.setOnClickListener(this);
        //mTvSendComment.setTag(TAG_SEND_COMMEND);
        mIsLabel = false;
        mIvSendGift = findViewById(R.id.iv_comment_smiles);
        // 默认不能点赞，后续变成可以
        mIvSendGift.setSelected(false);

        FileDownloader.registerDownloadStatusListener(this);
        if(mDocId.equals(specialId)){
            mFvDownload.setVisibility(View.VISIBLE);
            initDownloadGameInfo();
        }else {
            mFvDownload.setVisibility(View.GONE);
        }
        loadDataFromDb();
        initPopupMenus();
        mPullAndLoadView.initLoad();
        allFinishCallback = new MoeMoeCallback() {
            @Override
            public void onSuccess() {
                mPullAndLoadView.setComplete();
                mIsLoading = false;
            }

            @Override
            public void onFailure() {

            }
        };
    }

    private void initDownloadGameInfo(){
        Intent i = new Intent(this,NetaDownloadService.class);
        startService(i);
        mInfo = new NetaDownloadInfo(specialId,gameUrl,"《崩坏3rd》官服公测Neta版.apk", GameDbHelper.getInstance(this));
    }

    private void initPopupMenus() {
        PopupMenuItems items = new PopupMenuItems(this);

        boolean isMyDoc = false;
        MenuItem item;
        item = new MenuItem(MENU_FAVORITE, getString(R.string.label_favorite));
        items.addMenuItem(item);

        item = new MenuItem(MENU_GOTO_FLOOR, getString(R.string.label_goto_floor));
        items.addMenuItem(item);

        item = new MenuItem(MENU_SHARE, getString(R.string.label_share));
        items.addMenuItem(item);
        //}//       }

        // 举报
        item = new MenuItem(MENU_REPORT, getString(R.string.label_jubao));
        items.addMenuItem(item);


        mMenu = new PopupListMenu(this, items);
        mMenu.setMenuItemClickListener(new PopupListMenu.MenuItemClickListener() {

            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == MENU_FAVORITE){
                    favoriteDoc();
                }else if(itemId == MENU_GOTO_FLOOR){
                    if(TextUtils.isEmpty(mTargetId)) {
                        jumpToFloor();
                    }else {
                        ToastUtil.showCenterToast(NewDocDetailActivity.this,"只看楼主时无法跳转楼层",Toast.LENGTH_SHORT);
                    }
                }else if (itemId == MENU_REPORT) {
                    reportDoc();
                }else if (itemId == MENU_SHARE) {
                    showShare();
                }
            }
        });
    }

    private void jumpToFloor(){
        if(mDocBean != null){
            if(mDocBean.comments > 0){
                final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                dialogUtil.createEditDialog(this,mDocBean.comments,1);
                dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        dialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        requestCommentsByFloor(dialogUtil.getEditTextContent(),mTargetId,true,false);
                        dialogUtil.dismissDialog();
                    }
                });
                dialogUtil.showDialog();
            }else {
                ToastUtil.showCenterToast(this,R.string.msg_have_no_comments);
            }
        }

    }

    private void favoriteDoc(){
        if(mDocBean != null){
            if (mDocBean.favoriteFlag){
                Otaku.getDocV2().cancelfavoriteDoc(mPreferMng.getToken(),mDocId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        mMenu.changeItemTextById(MENU_FAVORITE,getString(R.string.label_favorite));
                        mDocBean.favoriteFlag = false;
                        ToastUtil.showCenterToast(NewDocDetailActivity.this,R.string.label_cancel_favorite_success);
                    }

                    @Override
                    public void failure(String e) {
                        ToastUtil.showCenterToast(NewDocDetailActivity.this,R.string.label_cancel_favorite_failure);
                    }
                }));
            }else {
                Otaku.getDocV2().favoriteDoc(mPreferMng.getToken(),mDocId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        mDocBean.favoriteFlag = true;
                        mMenu.changeItemTextById(MENU_FAVORITE,getString(R.string.label_cancel_favorite));
                        ToastUtil.showCenterToast(NewDocDetailActivity.this,R.string.label_favorite_success);
                    }

                    @Override
                    public void failure(String e) {
                        ToastUtil.showCenterToast(NewDocDetailActivity.this,R.string.label_favorite_failure);
                    }
                }));
            }
        }
    }

    private void reportDoc(){
        Intent intent = new Intent(this, JuBaoActivity.class);
        intent.putExtra(JuBaoActivity.EXTRA_NAME, mDocBean.userName);
        intent.putExtra(JuBaoActivity.EXTRA_CONTENT, mDocBean.shareInfo.desc);
        intent.putExtra(JuBaoActivity.EXTRA_KEY_UUID,mDocBean.id);
        intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC.toString());
        startActivity(intent);
    }

    private void showShare() {
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mDocBean.shareInfo.title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        String url = "";
        if(AppSetting.isDebug){
            url = Otaku.SHARE_BASE_DEBUG + mDocId;
        }else {
            url = Otaku.SHARE_BASE + mDocId;
        }
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mDocBean.shareInfo.desc + " " + url);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(mDocBean.shareInfo.icon);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mAdapter != null) mAdapter.dismissPopupWindow();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_comment_smiles) {
            // 送节操
            //sendGift();
        } else if (id == R.id.iv_comment_send) {
            if(mIsLabel){
                sendLabel();
            }else {
                sendComment(false,null);
            }
        }else if(id == R.id.edt_comment_input){
            //mToUserId = mDocBean.userId;
        }else if(id == R.id.iv_follow_event){
            if (!NetworkUtils.checkNetworkAndShowError(this)){
                return;
            }
            if(DialogUtils.checkLoginAndShowDlg(this)){
                if(mDocBean.rssFlag){
                    Otaku.getDocV2().cancelRss(mPreferMng.getToken(), mDocBean.rssId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            mDocBean.rssFlag = false;
                            upDataView();
                        }

                        @Override
                        public void failure(String e) {

                        }
                    }));
                }else {
                    Otaku.getDocV2().createRss(mPreferMng.getToken(), mDocBean.rssId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            mDocBean.rssFlag = true;
                            upDataView();
                        }

                        @Override
                        public void failure(String e) {

                        }
                    }));

                }
            }
        }else if(id == R.id.iv_add_img){
            choosePhoto();
        }else if(id == R.id.fv_download){
            if(mInfo != null){
                if(NetworkUtils.checkNetworkAndShowError(this)){
                    if(!NetworkUtils.isWifi(this) && !mFvDownload.using()){
                        final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                        dialogUtil.createPromptDialog(this,"",getString(R.string.label_not_wifi_waring));
                        dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                            @Override
                            public void CancelOnClick() {
                                dialogUtil.dismissDialog();
                            }

                            @Override
                            public void ConfirmOnClick() {
                                downloadGame();
                                dialogUtil.dismissDialog();
                            }
                        });
                        dialogUtil.showDialog();
                    }else {
                        downloadGame();
                    }
                }
            }
        }else if(id == R.id.tv_only_host){
            if(TextUtils.isEmpty(mTargetId)){
                if(mDocBean != null){
                    mTargetId = mDocBean.userId;
                    mTvOnlyHost.setSelected(true);
                    requestCommentsByFloor(1,mTargetId,true,false);
                }
            }else {
                mTargetId = null;
                mTvOnlyHost.setSelected(false);
                requestCommentsByFloor(1,mTargetId,true,false);
            }
        }else if(id == R.id.iv_cancel_jump){
            mLlJumpRoot.setVisibility(View.GONE);
        }else if(id == R.id.tv_jump_to){
            mLlJumpRoot.setVisibility(View.GONE);
            mPreferMng.removeData(mDocId);
            requestCommentsByFloor(mCurFloor,mTargetId,true,false);
        }
    }

    private void download(){
        FileDownloader.detect(mInfo.getGameUrl(), new OnDetectBigUrlFileListener() {
            @Override
            public void onDetectNewDownloadFile(String url, String fileName, String saveDir, long fileSize) {
                FileDownloader.createAndStart(url,StorageUtils.getTempRootPath(),"《崩坏3rd》官服公测Neta版.apk");
            }

            @Override
            public void onDetectUrlFileExist(String url) {
                FileDownloader.start(url);
            }

            @Override
            public void onDetectUrlFileFailed(String url, DetectBigUrlFileFailReason failReason) {
            }
        });
    }

    private void downloadGame(){
        if(!mFvDownload.using()){
            download();
        }else {
            DownloadFileInfo downloadFileInfo = mInfo.getDownloadFileInfo();
            if(downloadFileInfo != null){
                switch (downloadFileInfo.getStatus()){
                    case Status.DOWNLOAD_STATUS_UNKNOWN:
                        ToastUtil.showCenterToast(this,"文件出现未知错误,已重新开始下载",Toast.LENGTH_SHORT);
                        FileUtil.deleteFile(downloadFileInfo.getTempFilePath());
                        FileUtil.deleteFile(downloadFileInfo.getFilePath());
                        download();
                        break;
                    case Status.DOWNLOAD_STATUS_ERROR:
                    case Status.DOWNLOAD_STATUS_PAUSED:
                        // start
                        FileDownloader.start(mInfo.getGameUrl());
                        break;
                    case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
                        ToastUtil.showCenterToast(this,"下载文件已被删除，请重新下载", Toast.LENGTH_SHORT);
                        FileDownloader.delete(mInfo.getGameUrl(), true, new OnDeleteDownloadFileListener() {
                            @Override
                            public void onDeleteDownloadFilePrepared(DownloadFileInfo downloadFileNeedDelete) {

                            }

                            @Override
                            public void onDeleteDownloadFileSuccess(DownloadFileInfo downloadFileDeleted) {
                            }

                            @Override
                            public void onDeleteDownloadFileFailed(DownloadFileInfo downloadFileInfo, DeleteDownloadFileFailReason failReason) {

                            }
                        });
                        mFvDownload.showDownloadError();
                        mFvDownload.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mFvDownload.reset();
                            }
                        },1000);
                        //FileDownloader.reStart(mInfo.getGameUrl());
                        break;
                    case Status.DOWNLOAD_STATUS_WAITING:
                    case Status.DOWNLOAD_STATUS_PREPARING:
                    case Status.DOWNLOAD_STATUS_PREPARED:
                    case Status.DOWNLOAD_STATUS_DOWNLOADING:
                        // pause
                        FileDownloader.pause(mInfo.getGameUrl());
                        break;
                    case Status.DOWNLOAD_STATUS_COMPLETED:
                        mFvDownload.showDownloadOk();
                        mFvDownload.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mFvDownload.reset();
                            }
                        },1000);
                        break;
                }
            }else {
                mFvDownload.showDownloadOk();
                mFvDownload.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFvDownload.reset();
                    }
                },1000);
            }
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
                }
            } else {
                try {
                    DialogUtils.createImgChooseDlg(this, null, this, mIconPaths, ICON_NUM_LIMIT).show();
                } catch (Exception e) {
                }
            }
        } else {
            ToastUtil.showToast(this, R.string.msg_create_doc_9_jpg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_EDIT_VERSION_IMG) {
            if (resultCode == RESULT_OK && data != null) {
                String photoPath = null;
                Uri u = data.getData();
                if (u != null) {
                    String schema = u.getScheme();
                    if ("file".equals(schema)) {
                        photoPath = u.getPath();
                    }else if ("content".equals(schema)) {
                        photoPath = StorageUtils.getTempFile(System.currentTimeMillis() + ".jpg").getAbsolutePath();
                        InputStream is = null;
                        FileOutputStream fos = null;
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
                    mIconPaths.add(photoPath);
                    onGetPhotos();

                }
            }
        }else {
            boolean res = DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

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

    private void sendLabel(){
        if (mDocBean == null) {
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            String content = mEdtCommentInput.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                SoftKeyboardUtils.dismissSoftKeyboard(this);
                mAdapter.createLabel(content);
            }else {
                ToastUtil.showCenterToast(this, R.string.msg_doc_comment_not_empty);
            }
        }
    }

    private void sendComment(final boolean auto,String str) {
        if (mDocBean == null) {
            return;
        }
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            String content = mEdtCommentInput.getText().toString();
            if(auto) content = str;
            if(TextUtils.isEmpty(content)){
                ToastUtil.showCenterToast(this, R.string.msg_doc_comment_not_empty);
                return;
            }
            if(TextUtils.isEmpty(content) && mIconPaths.size() == 0){
                ToastUtil.showCenterToast(this, R.string.msg_doc_comment_not_empty);
                return;
            }
            final ArrayList<Image> images = BitmapUtils.handleUploadImage(mIconPaths);
            ArrayList<String> paths = new ArrayList<String>();
            if(images != null && images.size() > 0){
                for(int i = 0; i < images.size(); i++){
                    paths.add(images.get(i).path);
                }
            }
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            if(paths.size() == 0) {
                createDialog();
                Otaku.getDocV2().sendNewComment(mPreferMng.getToken(), mDocBean.id, content, mToUserId,"").enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        finalizeDialog();
                        //requestComments(mComments.size());
                        mEdtCommentInput.setText("");
                        if(!auto)ToastUtil.showCenterToast(getApplicationContext(), R.string.msg_send_comment_success);
                    }

                    @Override
                    public void failure(String e) {
                        finalizeDialog();
                        if(!auto)ToastUtil.showCenterToast(getApplicationContext(), R.string.msg_send_comment_fail);
                    }
                }));
            }else {
                createDialog();
                final String finalContent = content;
                Otaku.getAccountV2().uploadFilesToQiniu(mPreferMng.getToken(), paths, new OnNetWorkCallback<String, ArrayList<String>>() {
                    @Override
                    public void success(String token, ArrayList<String> result) {
                        String imgs = listToString(result, ',');
                        Otaku.getDocV2().sendNewComment(mPreferMng.getToken(), mDocBean.id, finalContent, mToUserId,imgs).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                finalizeDialog();
                                //requestComments(mComments.size());
                                mEdtCommentInput.setText("");
                                mIconPaths.clear();
                                mSelectAdapter.notifyDataSetChanged();
                                mRvComment.setVisibility(View.GONE);
                                ToastUtil.showCenterToast(getApplicationContext(), R.string.msg_send_comment_success);
                            }

                            @Override
                            public void failure(String e) {
                                finalizeDialog();
                                ToastUtil.showCenterToast(getApplicationContext(), R.string.msg_send_comment_fail);
                            }
                        }));
                    }

                    @Override
                    public void failure(String e) {
                        finalizeDialog();
                    }
                });
            }
        }
    }

    public void removeComment(NewCommentBean bean){
        mComments.remove(bean);
    }

    public String listToString(List list, char separator){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0,sb.toString().length()-1);
    }

    public void reply(NewCommentBean bean){
        mToUserId = bean.fromUserId;
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("回复 " + bean.fromUserName + ": ");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
    }

    public void addDocLabelView(){
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("添加标签吧~~");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
        mIsLabel = true;
    }

    private void upDataView(){
        if(!TextUtils.isEmpty(mDocBean.rssId)){
            mRss.setVisibility(View.VISIBLE);
            if(mDocBean.rssFlag){
                mRss.setImageResource(R.drawable.btn_event_inside_done);
                mRss.setVisibility(View.GONE);
            }else {
                mRss.setImageResource(R.drawable.btn_event_inside_follow);
            }
        }
    }

    private void loadDataFromDb(){
        try {
            NewDocBean newDocBean = db.selector(NewDocBean.class)
                    .where("id","=",mDocId)
                    .findFirst();
            if(newDocBean != null && newDocBean.json != null){
                mDocBean = new NewDocBean();
                mDocBean.readFromJsonContent(NewDocDetailActivity.this, newDocBean.json);
                for(NewDocBean.DocDetail detail : mDocBean.details){
                    if(detail.data instanceof NewDocBean.DocImage){
                        NewDocBean.DocImage img = (NewDocBean.DocImage) detail.data;
                        mImages.add(img.image);
                    }
                }
                for(NewDocBean.DocDetail detail : mDocBean.coinDetails){
                    if(detail.data instanceof NewDocBean.DocImage){
                        NewDocBean.DocImage img = (NewDocBean.DocImage) detail.data;
                        mImages.add(img.image);
                    }
                }
                mTvTitle.setText(mDocBean.title);
                mAdapter.setData(mDocBean);
            }else {

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void requestDocContent(){
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        Otaku.getDocV2().requestNewDocContent(mPreferMng.getToken(), mDocId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mDocBean = new NewDocBean();
                mDocBean.readFromJsonContent(NewDocDetailActivity.this, s);
                if (TextUtils.isEmpty(mDocBean.id) || TextUtils.isEmpty(mDocBean.userId)) {
                    // 帖子不存在
                    ToastUtil.showToast(NewDocDetailActivity.this, R.string.msg_doc_has_deleted);
                    finish();
                    return;
                }
                if(mDocBean.favoriteFlag){
                    mMenu.changeItemTextById(MENU_FAVORITE,getString(R.string.label_cancel_favorite));
                }else {
                    mMenu.changeItemTextById(MENU_FAVORITE,getString(R.string.label_favorite));
                }

                NewDocBean docBean = new NewDocBean();
                docBean.id = mDocId;
                docBean.json = s;
                try {
                    db.saveOrUpdate(docBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mImages.clear();
                for(NewDocBean.DocDetail detail : mDocBean.details){
                    if(detail.data instanceof NewDocBean.DocImage){
                        NewDocBean.DocImage img = (NewDocBean.DocImage) detail.data;
                        mImages.add(img.image);
                    }
                }
                for (NewDocBean.DocDetail detail : mDocBean.coinDetails){
                    if(detail.data instanceof NewDocBean.DocImage){
                        NewDocBean.DocImage img = (NewDocBean.DocImage) detail.data;
                        mImages.add(img.image);
                    }
                }
                mTvTitle.setText(mDocBean.title);
                mAdapter.setData(mDocBean);
                requestTags();
                requestCommentsByFloor(1,mTargetId,false,false);
                allFinishCallback.onSuccess();
                upDataView();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
               // NetworkUtils.checkNetworkAndShowError(NewDocDetailActivity.this);
            }
        }));
    }

    private void requestTags(){
        Otaku.getDocV2().requestNewDocTag(mPreferMng.getToken(),mDocId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
               // mTags = DocTag.readFromJsonList(NewDocDetailActivity.this,s);
                mDocBean.tags.clear();
                mDocBean.tags.addAll(DocTag.readFromJsonList(NewDocDetailActivity.this,s));
                mAdapter.notifyTags();
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    public void autoSendComment(){
       // sendComment(true,getString(R.string.label_auto_comment));
        //requestDoc();
        requestDocContent();
    }

    public void requestCommentsByFloor(final int floor, final String targetId, final boolean isJump, final boolean addBefore, int length, final boolean clear){
        if(mDocBean != null){
            if (floor < 1){
                ToastUtil.showCenterToast(this,R.string.label_floor_limit);
                return;
            }
            Otaku.getDocV2().requestCommentsByFloor(mPreferMng.getToken(),mDocId,floor,length,targetId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    ArrayList<NewCommentBean> beans = NewCommentBean.readFromJsonList(NewDocDetailActivity.this, s);
                    if(floor != 1){
                        if(beans.size() == 0){
                            ToastUtil.showCenterToast(NewDocDetailActivity.this,R.string.msg_all_load_down);
                        }else {
                            mIsHasLoadedAll = false;
                            mPullAndLoadView.isLoadMoreEnabled(true);
                        }
                    }
                    int bfSize = mComments.size();
                    if((floor == 1 || isJump )&& clear){
                        mComments.clear();
                    }
                    if(addBefore){
                        mComments.addAll(0,beans);
                    }else {
                        mComments.addAll(beans);
                    }
                    int afSize = mComments.size();
                    mAdapter.addComment(bfSize,afSize,mComments,targetId,isJump,addBefore);
                    if(isJump) {
                        mDocRv.scrollToPosition(mAdapter.getTagsPosition() + 2);
                    }
                    allFinishCallback.onSuccess();
                }

                @Override
                public void failure(String e) {
                    finalizeDialog();
                    allFinishCallback.onSuccess();
                }
            }));
        }
    }

    public void requestCommentsByFloor(final int floor, final String targetId, final boolean isJump, final boolean addBefore){
        requestCommentsByFloor(floor,targetId,isJump,addBefore,Otaku.LENGTH,true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelTag(TAG);
        FileDownloader.unregisterDownloadStatusListener(this);
       // mMusicTimer.stopTimer();
        mAdapter.releaseAdapter();
        mPreferMng.saveDocCurFloor(mDocId,mCurFloor);
       // unregisterReceiver(mReceiver);
    }

//    class MusicPlayBroadCast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(BROADCAST_NAME)) {
//                MusicInfo musicInfo = new MusicInfo();
//                int curPlayIndex = intent.getIntExtra(PLAY_MUSIC_INDEX, -1);
//                int playState = intent.getIntExtra(PLAY_STATE_NAME, MPS_NOFILE);
//                int prePlayPosition = intent.getIntExtra(PLAY_PRE_MUSIC_POSITION,-1);
//                Bundle bundle = intent.getBundleExtra(MusicInfo.KEY_MUSIC);
//                if (bundle != null) {
//                    musicInfo = bundle.getParcelable(MusicInfo.KEY_MUSIC);
//                }
//                switch (playState) {
//                    case MPS_INVALID:
//                        mMusicTimer.stopTimer();
//                        mAdapter.notifyItemChanged(mAdapter.getMusicPosition());
//                        break;
//                    case MPS_RESET:
//                        break;
//                    case MPS_PAUSE:
//                        mMusicTimer.stopTimer();
//                        break;
//                    case MPS_PLAYING:
//                        mMusicTimer.startTimer();
//                        break;
//                    case MPS_PREPARE:
//                        mMusicTimer.stopTimer();
//                        break;
//                    case MPS_NOFILE:
//                        mMusicTimer.stopTimer();
//                        mAdapter.notifyItemChanged(mAdapter.getMusicPosition());
//                        break;
//                }
//            }
//        }
//    }

    private class UpdateTask extends AsyncTask<Void,Void,Void> {

        public UpdateTask(boolean IsPullDown){
            mIsPullDown = IsPullDown;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mIsLoading = true;
            if (mIsPullDown) {
                //requestDoc();
                requestDocContent();
            }else {
               // requestComments(mComments.size());
                int floor = 1;
                if(mComments.size() > 0){
                    floor = mComments.get(mComments.size() - 1).idx + 1;
                }
                requestCommentsByFloor(floor,mTargetId,false,false);
            }
            return null;
        }
    }


    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())) {
            mInfo.setDownloadFileInfo(downloadFileInfo);
        }
    }

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())) {
            mInfo.setDownloadFileInfo(downloadFileInfo);
        }
    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())) {
            mInfo.setDownloadFileInfo(downloadFileInfo);
        }
    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())){
            mInfo.setDownloadFileInfo(downloadFileInfo);
            long totalSize =  downloadFileInfo.getFileSizeLong();
            long downloaded = downloadFileInfo.getDownloadedSizeLong();
            int progress1 = (int) (downloaded * 100 / totalSize);
            double downloadSize = downloaded / 1024f / 1024;
            double fileSize = totalSize / 1024f / 1024;
            downloadSize = MathUtil.formatNumber(downloadSize);
            fileSize = MathUtil.formatNumber(fileSize);
            int progress = (int) (downloadSize * 100 / fileSize);
            //Log.i("NewDoc:progress1",progress1 + "");
            //Log.i("NewDoc:progress",progress + "");
            mFvDownload.upDateProgress(progress);
        }
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())){
            mInfo.setDownloadFileInfo(downloadFileInfo);
        }
    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())) {
            mInfo.setDownloadFileInfo(downloadFileInfo);
        }
        if(mFvDownload != null){
            mFvDownload.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFvDownload.showDownloadOk();
                    mFvDownload.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFvDownload.reset();
                        }
                    },1000);
                }
            },1000);
        }
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        if(mFvDownload != null && mInfo != null && mInfo.getGameUrl().equals(downloadFileInfo.getUrl())) {
            mInfo.setDownloadFileInfo(null);
        }
    }
}
