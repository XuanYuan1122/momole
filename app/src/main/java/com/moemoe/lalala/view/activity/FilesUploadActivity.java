package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBadgeComponent;
import com.moemoe.lalala.di.components.DaggerFileUploadComponent;
import com.moemoe.lalala.di.modules.BadgeModule;
import com.moemoe.lalala.di.modules.FileUploadModule;
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.model.entity.ZipInfo;
import com.moemoe.lalala.presenter.FileUploadContract;
import com.moemoe.lalala.presenter.FileUploadPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.SelectItemAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropTransformation;
import retrofit2.http.Body;

import static com.moemoe.lalala.utils.Constant.LIMIT_NICK_NAME;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_FILE_UPLOAD;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GET_FROM_SELECT_BOOK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GET_FROM_SELECT_MUSIC;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GET_FROM_SELECT_ZIP;

/**
 *
 * Created by yi on 2017/8/21.
 */

public class FilesUploadActivity extends BaseAppCompatActivity implements FileUploadContract.View{

    private final int ICON_NUM_LIMIT = 50;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.tv_upload_zip)
    TextView mTvZip;
    @BindView(R.id.ll_bg_root)
    View mBgRoot;
    @BindView(R.id.ll_name_root)
    View mNameRoot;
    @BindView(R.id.rv_img)
    RecyclerView mRvImg;
    @BindView(R.id.iv_cover)
    ImageView mIvBg;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.rl_zip_root)
    View mZipRoot;
    @BindView(R.id.tv_zip_name)
    TextView mTvZipName;
    @BindView(R.id.iv_del_img)
    ImageView mIvDelZip;
    @BindView(R.id.tv_num)
    TextView mTvNum;
    @Inject
    FileUploadPresenter mPresenter;

    private BottomMenuFragment bottomMenuFragment;
    private SelectItemAdapter mSelectAdapter;
    private String mFolderType;
    private String mFolderId;
    private String mParentId;
    private String mBgPath = "";
    private String mBgTmp = "";
    private boolean mIsCover;
    private ArrayList<Object> mItemPaths = new ArrayList<>();
    private boolean mHasModified = false;
    private ZipInfo mZipInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_file_upload;
    }

    public static void startActivityForResult(Context context, String folderType, String folderId, String parentId){
        Intent i = new Intent(context,FilesUploadActivity.class);
        i.putExtra("folderType",folderType);
        i.putExtra("folderId",folderId);
        i.putExtra("parentId",parentId);
        ((BaseAppCompatActivity)context).startActivityForResult(i,REQ_FILE_UPLOAD);
    }

    public static void startActivityForResult(Context context, String folderType, String folderId, String parentId, ManHua2Entity entity){
        Intent i = new Intent(context,FilesUploadActivity.class);
        i.putExtra("folderType",folderType);
        i.putExtra("folderId",folderId);
        i.putExtra("parentId",parentId);
        i.putExtra("manhua",entity);
        ((BaseAppCompatActivity)context).startActivityForResult(i,REQ_FILE_UPLOAD);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFileUploadComponent.builder()
                .fileUploadModule(new FileUploadModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        mFolderType = getIntent().getStringExtra("folderType");
        mFolderId = getIntent().getStringExtra("folderId");
        mParentId = getIntent().getStringExtra("parentId");
        mSelectAdapter = new SelectItemAdapter(this);
        mRvImg.setVisibility(View.VISIBLE);
        if(mFolderType.equals(FolderType.MH.toString()) || mFolderType.equals(FolderType.MHD.toString())){
            mTvZip.setVisibility(View.VISIBLE);
            mBgRoot.setVisibility(View.VISIBLE);
            mNameRoot.setVisibility(View.VISIBLE);
            if(mFolderType.equals(FolderType.MHD.toString())){
                ManHua2Entity entity = getIntent().getParcelableExtra("manhua");
                mTvName.setText(entity.getFolderName());
                mBgPath = entity.getCover();
                mBgTmp = entity.getCover();
                Glide.with(FilesUploadActivity.this)
                        .load(StringUtils.getUrl(FilesUploadActivity.this,mBgPath,(int)getResources().getDimension(R.dimen.y112),(int)getResources().getDimension(R.dimen.y112),false,true))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(FilesUploadActivity.this,(int)getResources().getDimension(R.dimen.y112),(int)getResources().getDimension(R.dimen.y112)))
                        .into(mIvBg);
            }
        }else if(mFolderType.equals(FolderType.XS.toString())){
            mBgRoot.setVisibility(View.VISIBLE);
            mNameRoot.setVisibility(View.VISIBLE);
            mSelectAdapter.setSelectSize(1);
        }

        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvImg.setLayoutManager(selectRvL);
        mRvImg.setAdapter(mSelectAdapter);
        mSelectAdapter.setOnItemClickListener(new SelectItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == mItemPaths.size()){
                    if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"upload");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onAllDelete() {

            }
        });
        mNameRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mKlCommentBoard.setVisibility(View.VISIBLE);
                mEdtCommentInput.setText("");
                mEdtCommentInput.setHint("名称");
                mEdtCommentInput.requestFocus();
                SoftKeyboardUtils.showSoftKeyboard(FilesUploadActivity.this, mEdtCommentInput);
            }
        });
        mBgRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                try {
                    mIsCover = true;
                    ArrayList<String> arrayList = new ArrayList<>();
                    DialogUtils.createImgChooseDlg(FilesUploadActivity.this, null,FilesUploadActivity.this, arrayList, 1).show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                    SoftKeyboardUtils.dismissSoftKeyboard(FilesUploadActivity.this);
                    if(content.length() > LIMIT_NICK_NAME){
                        mTvName.setSelected(true);
                    }else {
                        mTvName.setSelected(false);
                    }
                    mTvName.setText(content);
                    mHasModified = true;
                }else {
                    showToast(R.string.msg_doc_comment_not_empty);
                }
            }
        });
        mTvZip.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mItemPaths.clear();
                mSelectAdapter.setData(mItemPaths);
                mSelectAdapter.notifyDataSetChanged();
                mRvImg.setVisibility(View.GONE);
                Intent intent = new Intent(FilesUploadActivity.this, SelectZipActivity.class);
                startActivityForResult(intent,REQ_GET_FROM_SELECT_ZIP);
            }
        });
        mIvDelZip.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mItemPaths.clear();
                mSelectAdapter.setData(mItemPaths);
                mSelectAdapter.notifyDataSetChanged();
                mRvImg.setVisibility(View.VISIBLE);
                mZipRoot.setVisibility(View.GONE);
                mZipInfo = null;
            }
        });
        mTvMenuRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                done();
            }
        });
        initPopupMenus();
    }

    private void done(){
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast(R.string.msg_connection);
            return;
        }
        if(mFolderType.equals(FolderType.MH.toString()) || mFolderType.equals(FolderType.XS.toString()) || mFolderType.equals(FolderType.MHD.toString()) ){
            String name = mTvName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showToast(R.string.msg_name_cannot_null);
                return;
            }
            if(TextUtils.isEmpty(mBgPath)){
                showToast("封面不能为空");
                return;
            }
        }
        if (mZipInfo == null){
            if(mItemPaths.size() <= 0){
                showToast("什么都没选呢");
                return;
            }
            long size = 0;
            if (!TextUtils.isEmpty(mBgPath)){
                size = new File(mBgPath).length();
            }
            for (Object o : mItemPaths){
                if(o instanceof String){
                    size += new File((String) o).length();
                }else if(o instanceof MusicLoader.MusicInfo){
                    size += new File(((MusicLoader.MusicInfo) o).getUrl()).length();
                }else if(o instanceof BookInfo){
                    size += new File(((BookInfo) o).getPath()).length();
                }
            }
            createDialog();
            mPresenter.checkSize(size);
        }else {
            if(FileUtil.unzip(mZipInfo.getPath(), StorageUtils.getTempRootPath() + mZipInfo.getTitle() + File.separator)){
                ArrayList<String> temp =  FileUtil.getUnZipFileList(StorageUtils.getTempRootPath() + File.separator + mZipInfo.getTitle());
                if(temp.size() > 0){
                    long size = 0;
                    for(String o : temp){
                        size += new File(o).length();
                    }
                    mItemPaths.addAll(temp);
                    createDialog();
                    mPresenter.checkSize(size);
                }else {
                    showToast("没有任何文件");
                }
            }else {
                showToast("解压失败，压缩文件有密码或者格式不支持,压缩包只能含有图片文件，不能存在文件夹");
            }
        }
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1, "图片");
        if(mFolderType.equals(FolderType.ZH.toString()) || mFolderType.equals(FolderType.TJ.toString()) || mFolderType.equals(FolderType.MH.toString()) || mFolderType.equals(FolderType.MHD.toString())){
            items.add(item);
        }
        item = new MenuItem(2, "文本");
        if(mFolderType.equals(FolderType.XS.toString()) || mFolderType.equals(FolderType.ZH.toString())){
            items.add(item);
        }
        item = new MenuItem(3, "音乐");
        if(mFolderType.equals(FolderType.ZH.toString())){
            items.add(item);
        }
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    chooseItem(0);
                }else if(itemId == 2){
                    chooseItem(1);
                }else if(itemId == 3){
                    chooseItem(2);
                }
            }
        });
    }


    private void chooseItem(int type){
        if (mItemPaths.size() < ICON_NUM_LIMIT) {
            if(type == 0){
                try {
                    ArrayList<String> selected = new ArrayList<>();
                    for (Object o : mItemPaths){
                        if(o instanceof String){
                            selected.add((String) o);
                        }
                        if(o instanceof MusicLoader.MusicInfo){
                            selected.add("music" + ((MusicLoader.MusicInfo) o).getUrl());
                        }
                    }
                    mIsCover = false;
                    DialogUtils.createImgChooseDlg(this, null, this, selected, ICON_NUM_LIMIT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(type == 1){
                Intent intent = new Intent(FilesUploadActivity.this, SelectBookActivity.class);
                startActivityForResult(intent,REQ_GET_FROM_SELECT_BOOK);
            }else if(type == 2){
                Intent intent = new Intent(FilesUploadActivity.this, SelectMusicActivity.class);
                startActivityForResult(intent,REQ_GET_FROM_SELECT_MUSIC);
            }
        } else {
            showToast(R.string.msg_select_9_item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_FROM_SELECT_ZIP ){
            if(data != null && resultCode == RESULT_OK){
                mRvImg.setVisibility(View.GONE);
                mZipRoot.setVisibility(View.VISIBLE);
                mZipInfo = data.getParcelableExtra(SelectZipActivity.EXTRA_SELECT_ZIP);
                mTvZipName.setText(mZipInfo.getTitle());
                mHasModified = true;
                mTvNum.setText("已选1项");
            }else {
                mRvImg.setVisibility(View.VISIBLE);
                mZipRoot.setVisibility(View.GONE);
                mZipInfo = null;
            }
        }else if (requestCode == REQ_GET_FROM_SELECT_MUSIC && resultCode == RESULT_OK) {
            if (data != null) {
                MusicLoader.MusicInfo mMusicInfo = data.getParcelableExtra(SelectMusicActivity.EXTRA_SELECT_MUSIC);
                if(!checkInfo(mMusicInfo.getUrl())){
                    mItemPaths.add(mMusicInfo);
                }
                mSelectAdapter.setData(mItemPaths);
                mTvNum.setText("已选" + mItemPaths.size() + "项");
            }
        }else if(requestCode == REQ_GET_FROM_SELECT_BOOK && resultCode == RESULT_OK){
            if(data != null){
                BookInfo entity = data.getParcelableExtra(SelectBookActivity.EXTRA_SELECT_BOOK);
                if(!checkInfo(entity.getPath())){
                    mItemPaths.add(entity);
                }
                mSelectAdapter.setData(mItemPaths);
                mTvNum.setText("已选" + mItemPaths.size() + "项");
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    if(mIsCover){
                        mBgPath = photoPaths.get(0);
                        Glide.with(FilesUploadActivity.this)
                                .load(mBgPath)
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .bitmapTransform(new CropTransformation(FilesUploadActivity.this,(int)getResources().getDimension(R.dimen.y112),(int)getResources().getDimension(R.dimen.y112)))
                                .into(mIvBg);
                        mHasModified = true;
                    }else {
                        removePath(photoPaths);
                        mItemPaths.addAll(photoPaths);
                        mSelectAdapter.setData(mItemPaths);
                        mTvNum.setText("已选" + mItemPaths.size() + "项");
                    }
                }
            });
        }
    }

    private void removePath(ArrayList<String> paths){
        ArrayList<String>  temp = new ArrayList<>();
        for(Object o : mItemPaths){
            if(o instanceof String){
                for(String path : paths){
                    if(path.equals(o)){
                        temp.add(path);
                    }
                }
            }
            if(o instanceof MusicLoader.MusicInfo){
                for(String path : paths){
                    if(path.equals("music" + ((MusicLoader.MusicInfo) o).getUrl())){
                        temp.add(path);
                    }
                }
            }
        }
        paths.removeAll(temp);
    }

    private boolean checkInfo(String path){
        for (Object o : mItemPaths){
            if(o instanceof MusicLoader.MusicInfo){
                MusicLoader.MusicInfo info = (MusicLoader.MusicInfo) o;
                if(info.getUrl().equals(path)){
                    return true;
                }
            }else if(o instanceof BookInfo){
                BookInfo entity = (BookInfo) o;
                if(entity.getPath().equals(path)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft, (int)getResources().getDimension(R.dimen.x36));
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.label_add));
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight, (int)getResources().getDimension(R.dimen.x36));
        mTvMenuRight.setText(getString(R.string.label_done));
        mTvMenuRight.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onCheckSize(boolean isOk) {
        if(isOk){
            mPresenter.uploadFiles(mFolderType,mFolderId,mParentId,mTvName.getText().toString(),mItemPaths,mBgPath,mBgPath.equals(mBgTmp)?-1:0);
        }else {
            showToast("空间不足");
            finalizeDialog();
        }
    }

    @Override
    public void onUploadFilesSuccess() {
        finalizeDialog();
        showToast("上传成功");
        setResult(RESULT_OK);
        finish();
    }
}
