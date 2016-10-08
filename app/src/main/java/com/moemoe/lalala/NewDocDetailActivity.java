package com.moemoe.lalala;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.common.util.DensityUtil;
import com.app.ex.DbException;
import com.app.http.request.UriRequest;
import com.app.image.ImageOptions;
import com.app.view.DbManager;
import com.moemoe.lalala.adapter.SelectImgAdapter;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.adapter.DocRecyclerViewAdapter;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.DocPut;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.MusicInfo;
import com.moemoe.lalala.data.NewCommentBean;
import com.moemoe.lalala.data.NewDocBean;
import com.moemoe.lalala.data.NewDocType;
import com.moemoe.lalala.data.REPORT;
import com.moemoe.lalala.music.MusicServiceManager;
import com.moemoe.lalala.music.MusicTimer;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.onekeyshare.OnekeyShare;
import com.moemoe.lalala.view.KeyboardListenerLayout;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.menu.MenuItem;
import com.moemoe.lalala.view.menu.PopupListMenu;
import com.moemoe.lalala.view.menu.PopupMenuItems;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haru on 2016/5/6 0006.
 */
@ContentView(R.layout.ac_one_recycleview)
public class NewDocDetailActivity extends BaseActivity implements IConstants,View.OnClickListener {
    public static final String TAG="NewDocDetailActivity";
    private static final int ICON_NUM_LIMIT = 9;
    private static final int MENU_SHARE = 103;
    private static final int MENU_REPORT = 104;
    private static final int REQ_GET_EDIT_VERSION_IMG = 2333;
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
    private DocRecyclerViewAdapter mAdapter;
    private MusicServiceManager mServiceManager;
    private MusicPlayBroadCast mReceiver;
    private MusicTimer mMusicTimer;
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
        mServiceManager = MapActivity.sMusicServiceManager;
        mReceiver = new MusicPlayBroadCast();
        IntentFilter filter = new IntentFilter(BROADCAST_NAME);
        filter.addAction(BROADCAST_NAME);
        registerReceiver(mReceiver, filter);
        mSwipeRefreshWidget = mPullAndLoadView.getSwipeRefreshLayout();
        mSwipeRefreshWidget.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mDocRv = mPullAndLoadView.getRecyclerView();
        mAdapter = new DocRecyclerViewAdapter(this,mServiceManager);
        mDocRv.setAdapter(mAdapter);
        mMusicTimer = new MusicTimer(mAdapter.mHandler);
        mAdapter.setMusicTimer(mMusicTimer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPullAndLoadView.setLayoutManager(layoutManager);
        mDocRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(NewDocDetailActivity.this).resumeTag(TAG);
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
        mIvAddImg.setOnClickListener(this);
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
        //mTvSendComment.setTag(TAG_SEND_COMMEND);
        mIsLabel = false;
        mIvSendGift = findViewById(R.id.iv_comment_smiles);
        // 默认不能点赞，后续变成可以
        mIvSendGift.setSelected(false);
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

    private void initPopupMenus() {
        PopupMenuItems items = new PopupMenuItems(this);

        boolean isMyDoc = false;
        MenuItem item;
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
                if (itemId == MENU_REPORT) {
                    reportDoc();
                } else if (itemId == MENU_SHARE) {
                    showShare();
                }
            }
        });
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
                        requestComments(mComments.size());
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
                                requestComments(mComments.size());
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

    private void requestDoc(){
        Otaku.getDocV2().requestNewDoc(mPreferMng.getToken(), mDocId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mDocBean = new NewDocBean();
                mDocBean.readFromJsonContent(NewDocDetailActivity.this, s);
                if (TextUtils.isEmpty(mDocBean.id)) {
                    // 帖子不存在
                    ToastUtil.showToast(NewDocDetailActivity.this, R.string.msg_doc_has_deleted);
                    finish();
                    return;
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
                requestComments(0);
                allFinishCallback.onSuccess();
                upDataView();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
                NetworkUtils.checkNetworkAndShowError(NewDocDetailActivity.this);
            }
        }));
    }

    public void autoSendComment(){
       // sendComment(true,getString(R.string.label_auto_comment));
        requestDoc();
    }

    private void requestComments(final int index){
        if(mDocBean != null){
            Otaku.getDocV2().requestNewComment(mPreferMng.getToken(), mDocBean.id, index).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    ArrayList<NewCommentBean> beans = NewCommentBean.readFromJsonList(NewDocDetailActivity.this, s);
                    if(beans.size() < Otaku.LENGTH && index != 0){
                        mIsHasLoadedAll = true;
                        mPullAndLoadView.isLoadMoreEnabled(false);
                    }else {
                        mIsHasLoadedAll = false;
                        mPullAndLoadView.isLoadMoreEnabled(true);
                    }
                    if(index == 0){
                        mComments.clear();
                    }
                    mComments.addAll(beans);
                    mAdapter.addComment(beans);
                    allFinishCallback.onSuccess();
                }

                @Override
                public void failure(String e) {
                    allFinishCallback.onSuccess();
                    NetworkUtils.checkNetworkAndShowError(NewDocDetailActivity.this);
                }
            }));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicTimer.stopTimer();
        Picasso.with(NewDocDetailActivity.this)
                .cancelTag(TAG);
        unregisterReceiver(mReceiver);
    }

    class MusicPlayBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BROADCAST_NAME)) {
                MusicInfo musicInfo = new MusicInfo();
                int curPlayIndex = intent.getIntExtra(PLAY_MUSIC_INDEX, -1);
                int playState = intent.getIntExtra(PLAY_STATE_NAME, MPS_NOFILE);
                int prePlayPosition = intent.getIntExtra(PLAY_PRE_MUSIC_POSITION,-1);
                Bundle bundle = intent.getBundleExtra(MusicInfo.KEY_MUSIC);
                if (bundle != null) {
                    musicInfo = bundle.getParcelable(MusicInfo.KEY_MUSIC);
                }
                switch (playState) {
                    case MPS_INVALID:
                        mMusicTimer.stopTimer();
                        mAdapter.notifyItemChanged(mAdapter.getMusicPosition());
                        break;
                    case MPS_RESET:
                        break;
                    case MPS_PAUSE:
                        mMusicTimer.stopTimer();
                        break;
                    case MPS_PLAYING:
                        mMusicTimer.startTimer();
                        break;
                    case MPS_PREPARE:
                        mMusicTimer.stopTimer();
                        break;
                    case MPS_NOFILE:
                        mMusicTimer.stopTimer();
                        mAdapter.notifyItemChanged(mAdapter.getMusicPosition());
                        break;
                }
            }
        }
    }

    private class UpdateTask extends AsyncTask<Void,Void,Void> {

        public UpdateTask(boolean IsPullDown){
            mIsPullDown = IsPullDown;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mIsLoading = true;
            if (mIsPullDown) {
                requestDoc();
            }else {
                requestComments(mComments.size());
            }
            return null;
        }
    }

}
