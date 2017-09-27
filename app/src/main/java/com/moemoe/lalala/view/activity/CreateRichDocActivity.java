package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerCreateRichDocComponent;
import com.moemoe.lalala.di.modules.CreateRichDocModule;
import com.moemoe.lalala.event.RichImgRemoveEvent;
import com.moemoe.lalala.model.entity.DocPut;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDocType;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.RichDocListEntity;
import com.moemoe.lalala.model.entity.RichEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.presenter.CreateRichDocContract;
import com.moemoe.lalala.presenter.CreateRichDocPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.richtext.NetaRichEditor;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2017/5/15.
 */

public class CreateRichDocActivity extends BaseAppCompatActivity implements CreateRichDocContract.View{

    /**
     * 9张图片上限
     */
    private final int ICON_NUM_LIMIT = 18;
    public static final String TYPE_TAG_NAME_DEFAULT = "tag_default";
    public static final String TYPE_QIU_MING_SHAN = "qiu_ming_shan";
    private final int REQ_GET_FROM_SELECT_MUSIC = 1003;
    private final int REQ_ADD_HIDE = 1004;
    private final int REQ_ADD_SEARCH = 1005;
    private static final int REQ_SELECT_FOLDER = 5001;
    public static final int RESPONSE_CODE = 10000;
    public static final int REQUEST_CODE_CREATE_DOC = 11000;
    public static final int REQUEST_CODE_UPDATE_DOC = 12000;

    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.rich_et)
    NetaRichEditor mRichEt;
    @BindView(R.id.rl_ope_root)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_add_bag)
    ImageView mIvAddBag;
    @BindView(R.id.iv_add_hide_doc)
    ImageView mIvAddHide;
    @BindView(R.id.iv_add_music)
    ImageView mIvAddMusic;
    @BindView(R.id.view_add_sep)
    View mViewAddSep;

    @Inject
    CreateRichDocPresenter mPresenter;

    private String mFromSchema;
    private String mFromName;
    private HashMap<String,String> mPathMap;
    private int mImageSize;
    private ArrayList<RichEntity> mHideList;
    private String mFolderId;
    private boolean mHideType;
    private MusicLoader.MusicInfo mMusicInfo;
    private Image mMusicCover;
    private int mDocType;
    private RichDocListEntity mDoc;
    private ArrayList<String> mUserIds;
    private BottomMenuFragment bottomFragment;
    private String mFolderType;
    private String mBgCover;
    private int coverSize;
    private boolean mIsCover = true;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_rich_doc;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerCreateRichDocComponent.builder()
                .createRichDocModule(new CreateRichDocModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        String mTagNameDef = i.getStringExtra(TYPE_TAG_NAME_DEFAULT);
        mFromSchema = i.getStringExtra("from_schema");
        mFromName = i.getStringExtra("from_name");
        mDocType = i.getIntExtra(TYPE_QIU_MING_SHAN,0);
        mDoc = i.getParcelableExtra("doc");
        mIvAddBag.setVisibility(View.VISIBLE);
        mIvAddHide.setVisibility(View.VISIBLE);
        mIvAddMusic.setVisibility(View.VISIBLE);
        mViewAddSep.setVisibility(View.VISIBLE);
        mHideList = new ArrayList<>();
        mUserIds = new ArrayList<>();
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft,DensityUtil.dip2px(this,18));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvTitle.setVisibility(View.VISIBLE);
        mPathMap = new HashMap<>();
        mImageSize = 0;
        if(mDoc != null){
            mTvTitle.setText(getString(R.string.label_update_post));
        }else {
            mTvTitle.setText(getString(R.string.label_create_post));
            mRichEt.setLabelAble();
            mRichEt.setmTagNameDef(mTagNameDef);
            mRichEt.setmKlCommentBoard(mKlCommentBoard);
        }
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight,DensityUtil.dip2px(this,18));
        mTvMenuRight.setText(getString(R.string.label_menu_publish_doc));
        mTvMenuRight.setTextColor(Color.WHITE);
        mTvMenuRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        mTvMenuRight.setWidth(DensityUtil.dip2px(this,44));
        mTvMenuRight.setHeight(DensityUtil.dip2px(this,24));
        mTvMenuRight.setBackgroundResource(R.drawable.shape_rect_border_main_background_2);
        initPopupMenus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxBus.getInstance().unSubscribe(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeChangedEvent();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void initData() {
        if(mDoc != null){
            mRichEt.setTop();
            if(!TextUtils.isEmpty(mDoc.getBgCover())){
                int w = (int) (DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x36) * 2);
                int h = (int) getResources().getDimension(R.dimen.y200);
                mRichEt.setCover(StringUtils.getUrl(this,mDoc.getBgCover(),w,h,false,true));
                mBgCover = mDoc.getBgCover();
                coverSize = -1;
            }
            mHideType = mDoc.isHidType();
            if(!TextUtils.isEmpty(mDoc.getTitle())){
                mRichEt.setTitle(mDoc.getTitle());
            }
            if(mDoc.getList().size() > 0){
                RichEntity entity = mDoc.getList().get(0);
                if(TextUtils.isEmpty(entity.getInputStr())){
                    mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),"");
                }
                Observable.fromIterable(mDoc.getList())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<RichEntity>() {

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                RichEntity entity1 = mDoc.getList().get(mDoc.getList().size() - 1);
                                if(TextUtils.isEmpty(entity1.getInputStr())){
                                    mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),"");
                                }
                            }

                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(RichEntity richEntity) {
                                    if(!TextUtils.isEmpty(richEntity.getInputStr())){
                                        mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),StringUtils.buildAtUserToEdit(CreateRichDocActivity.this,richEntity.getInputStr().toString()));
                                    }else if(richEntity.getImage() != null && !TextUtils.isEmpty(richEntity.getImage().getPath())){
                                        mRichEt.addImageViewAtIndex(mRichEt.getLastIndex(),richEntity.getImage().getPath(),richEntity.getImage().getW(),richEntity.getImage().getH(),richEntity.getImage().getSize());
                                        mPathMap.put(richEntity.getImage().getPath(),richEntity.getImage().getPath());
                                        mImageSize++;
                                    }
                            }
                        });
            }
            if(mDoc.getHideList().size() > 0){
                mHideList = mDoc.getHideList();
                mIvAddHide.setSelected(true);
            }
            if(!TextUtils.isEmpty(mDoc.getMusicPath())){
                mMusicInfo = new MusicLoader.MusicInfo();
                mMusicInfo.setUrl(mDoc.getMusicPath());
                mMusicInfo.setTitle(mDoc.getMusicTitle());
                mMusicInfo.setDuration(mDoc.getTime());
                mMusicCover = mDoc.getCover();
                mIvAddMusic.setSelected(true);
            }
            if(!TextUtils.isEmpty(mDoc.getFolderId())){
                mFolderId = mDoc.getFolderId();
                mIvAddBag.setSelected(true);
            }
        }else {
            mRichEt.createFirstEdit();
        }
    }

    private void subscribeChangedEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(RichImgRemoveEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<RichImgRemoveEvent>() {
                    @Override
                    public void accept(RichImgRemoveEvent richImgRemoveEvent) throws Exception {
                        mPathMap.remove(richImgRemoveEvent.getPath());
                        mImageSize--;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void initPopupMenus() {
        bottomFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem item = new MenuItem(0, "综合");
        items.add(item);

        item = new MenuItem(1, "图集");
        items.add(item);

        item = new MenuItem(2, "漫画");
        items.add(item);

        item = new MenuItem(3, "小说");
        items.add(item);


        bottomFragment.setShowTop(false);
        bottomFragment.setMenuItems(items);
        bottomFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    mFolderType = FolderType.ZH.toString();
                }else if(itemId == 1){
                    mFolderType = FolderType.TJ.toString();
                }else if (itemId == 2) {
                    mFolderType = FolderType.MH.toString();
                }else if (itemId == 3) {
                    mFolderType = FolderType.XS.toString();
                }
                Intent i1 = new Intent(CreateRichDocActivity.this,FolderSelectActivity.class);
                i1.putExtra("folderType",mFolderType);
                startActivityForResult(i1,REQ_SELECT_FOLDER);
            }
        });
    }

    private void createPost() {
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        if (!mRichEt.hasContent()) {
            showToast(R.string.msg_doc_content_cannot_null);
        } else if(mRichEt.getmTags() != null && mRichEt.getmTags().size() < 1){
            showToast(R.string.msg_need_one_tag);
        }else if(TextUtils.isEmpty(mBgCover)){
            showToast("封面不能为空");
        }else {
            createDialog();
            mTvMenuRight.setEnabled(false);
            DocPut mDocEntity = new DocPut();
            mDocEntity.docType = mFromName;
            mDocEntity.docTypeSchema = mFromSchema;
            mDocEntity.bagFolderId = mFolderId;
            mDocEntity.title = mRichEt.getTitle();
            if(mRichEt.getmTags() != null){
                for (int i = 0;i < mRichEt.getmTags().size();i++){
                    mDocEntity.tags.add(mRichEt.getmTags().get(i).getName());
                }
            }
            for (RichEntity entity : mRichEt.buildEditData() ){
                if(!TextUtils.isEmpty(entity.getInputStr())){
                    DocPut.DocPutText docPutText = new DocPut.DocPutText();
                    docPutText.text = StringUtils.buildDataAtUser(entity.getInputStr());
                    mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(), docPutText));
                    mDocEntity.userIds.addAll(StringUtils.getAtUserIds(entity.getInputStr()));
                }else if(entity.getImage() != null && !TextUtils.isEmpty(entity.getImage().getPath())){
                    DocPut.DocPutImage docPutImage = new DocPut.DocPutImage();
                    docPutImage.path = entity.getImage().getPath();
                    docPutImage.h = entity.getImage().getH();
                    docPutImage.w = entity.getImage().getW();
                    docPutImage.size = entity.getImage().getSize();
                    mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_IMAGE.toString(), docPutImage));
                }
            }
            for (RichEntity entity : mHideList){
                if(!TextUtils.isEmpty(entity.getInputStr())){
                    DocPut.DocPutText docPutText = new DocPut.DocPutText();
                    docPutText.text = entity.getInputStr().toString();
                    mDocEntity.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(), docPutText));
                }else if(entity.getImage() != null && !TextUtils.isEmpty(entity.getImage().getPath())){
                    DocPut.DocPutImage docPutImage = new DocPut.DocPutImage();
                    docPutImage.path = entity.getImage().getPath();
                    docPutImage.h = entity.getImage().getH();
                    docPutImage.w = entity.getImage().getW();
                    docPutImage.size = entity.getImage().getSize();
                    mDocEntity.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_IMAGE.toString(), docPutImage));
                }
            }
            mDocEntity.userIds.addAll(mUserIds);
            mDocEntity.coinComment = mHideType;
            if(mMusicInfo != null){
                DocPut.DocPutMusic docPutMusic = new DocPut.DocPutMusic();
                docPutMusic.name = mMusicInfo.getTitle();
                docPutMusic.timestamp = mMusicInfo.getDuration();
                docPutMusic.url = mMusicInfo.getUrl();
                docPutMusic.cover = mMusicCover;
                mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_MUSIC.toString(),docPutMusic));
            }
            if(mHideList.size() > 0){
                mDocEntity.coin.coin = 1;
            }else {
                mDocEntity.coin.coin = 0;
            }
            mDocEntity.cover = mBgCover;
            mPresenter.createDoc(mDocEntity,mDocType,mDoc == null ? "" : mDoc.getDocId(),coverSize);
        }
    }

    @OnClick({R.id.iv_add_img,R.id.iv_alt_user,R.id.iv_add_hide_doc,R.id.iv_add_bag,R.id.iv_add_music,R.id.tv_menu})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_add_img:
                choosePhoto();
                break;
            case R.id.iv_alt_user:
                Intent i3 = new Intent(CreateRichDocActivity.this,SearchActivity.class);
                i3.putExtra("show_type",SearchActivity.SHOW_USER);
                startActivityForResult(i3,REQ_ADD_SEARCH);
                break;
            case R.id.iv_add_hide_doc:
                if(mHideList.size() > 0){
                    Intent i = new Intent(CreateRichDocActivity.this,CreateRichDocHideActivity.class);
                    i.putParcelableArrayListExtra("hide_list",mHideList);
                    i.putExtra("hide_type",mHideType);
                    startActivityForResult(i,REQ_ADD_HIDE);
                }else {
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createNormalDialog(CreateRichDocActivity.this,"添加隐藏区\n需要消耗1节操");
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            Intent i = new Intent(CreateRichDocActivity.this,CreateRichDocHideActivity.class);
                            i.putParcelableArrayListExtra("hide_list",mHideList);
                            i.putExtra("hide_type",mHideType);
                            startActivityForResult(i,REQ_ADD_HIDE);
                            alertDialogUtil.dismissDialog();
                        }
                    });
                    alertDialogUtil.showDialog();
                }
                break;
            case R.id.iv_add_bag:
                if(bottomFragment != null) bottomFragment.show(getSupportFragmentManager(),"createDoc");
                break;
            case R.id.iv_add_music:
                Intent i2 = new Intent(CreateRichDocActivity.this,AddMusicActivity.class);
                i2.putExtra("music_info",mMusicInfo);
                i2.putExtra("music_cover", mMusicCover);
                startActivityForResult(i2,REQ_GET_FROM_SELECT_MUSIC);
                break;
            case R.id.tv_menu:
                createPost();
                break;
        }
    }

    private void choosePhoto() {
        if (mImageSize < ICON_NUM_LIMIT) {
            try {
                ArrayList<String> temp = new ArrayList<>();
                mIsCover = false;
                DialogUtils.createImgChooseDlg(this, null, this, temp, ICON_NUM_LIMIT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showToast(R.string.msg_create_doc_9_jpg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ADD_SEARCH && resultCode == RESULT_OK){
            if(data != null){
                String userId = data.getStringExtra("user_id");
                String userName = data.getStringExtra("user_name");
                mRichEt.insertTextInCurSelection("@" + userName,userId);
            }
        }else if(requestCode == REQ_GET_FROM_SELECT_MUSIC && resultCode == RESULT_OK){
            if(data != null){
                mMusicInfo = data.getParcelableExtra("music_info");
                mMusicCover = data.getParcelableExtra("music_cover");
                mIvAddMusic.setSelected(true);
            }else {
                mMusicInfo = null;
                mIvAddMusic.setSelected(false);
            }
        }else if(requestCode == REQ_ADD_HIDE && resultCode == RESULT_OK){
            if(data != null){
                mHideList = data.getParcelableArrayListExtra("hide_list");
                mHideType = data.getBooleanExtra("hide_type",false);
                mUserIds = data.getStringArrayListExtra("at_user");
                if(mHideList.size() > 0){
                    mIvAddHide.setSelected(true);
                }else {
                    mIvAddHide.setSelected(false);
                }
            }else {
                mHideList.clear();
                mHideType = false;
                mIvAddHide.setSelected(false);
            }
        }else if (requestCode == REQ_SELECT_FOLDER && resultCode == RESULT_OK){
            if (!TextUtils.isEmpty(data.getStringExtra("folderId"))){
                mIvAddBag.setSelected(true);
                mFolderId = data.getStringExtra("folderId");
            }else {
                mIvAddBag.setSelected(false);
                mFolderId = "";
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(final ArrayList<String> photoPaths, boolean override) {
                    if(mIsCover){
                        mRichEt.setCover(photoPaths.get(0));
                        mBgCover = photoPaths.get(0);
                        coverSize = 0;
                    }else {
                        Collections.reverse(photoPaths);
                        createDialog("图片插入中...");
                        onGetPhotos(photoPaths);
                        mIsCover = true;
                    }
                }
            });
        }
    }

    private void onGetPhotos(final ArrayList<String> paths) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> res) throws Exception {
                mRichEt.measure(0,0);
                try{
                    for (String s : paths){
                        res.onNext(s);
                    }
                    res.onComplete();
                }catch (Exception e){
                    res.onError(e);
                }
            }

        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {

            @Override
            public void onError(Throwable e) {
                finalizeDialog();
                showToast("图片插入失败");
            }

            @Override
            public void onComplete() {
                finalizeDialog();
                showToast("图片插入成功");
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(String s) {
                mRichEt.insertImage(s);
                mImageSize++;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mRichEt.hasContent()) {
            DialogUtils.showAbandonModifyDlg(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSendSuccess(final String id, final String path) {
        mTvMenuRight.setEnabled(true);
        finalizeDialog();
        if(mDoc != null){
            showToast(R.string.msg_update_doc_success);
        }else {
            showToast(R.string.msg_create_doc_success);
        }
        if(!TextUtils.isEmpty(id)){
            final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
            dialogUtil.createPromptNormalDialog(this,"是否分享到动态");
            dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    Intent i = new Intent();
                    setResult(RESPONSE_CODE, i);
                    finish();
                    dialogUtil.dismissDialog();
                }

                @Override
                public void ConfirmOnClick() {
                    ShareArticleEntity entity = new ShareArticleEntity();
                    entity.setDocId(id);
                    entity.setTitle(mRichEt.getTitle());
                    entity.setCover(path);
                    String content = "";
                    for (RichEntity tmp : mRichEt.buildEditData() ){
                        if(!TextUtils.isEmpty(tmp.getInputStr())){
                            content = TagControl.getInstance().paresToString(tmp.getInputStr());
                            break;
                        }
                    }
                    entity.setContent(content);
                    UserTopEntity entity1 = new UserTopEntity();
                    entity1.setUserId(PreferenceUtils.getUUid());
                    entity1.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
                    entity1.setLevel(PreferenceUtils.getAuthorInfo().getLevel());
                    entity1.setHeadPath(PreferenceUtils.getAuthorInfo().getHeadPath());
                    entity.setDocCreateUser(entity1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String mTime = sdf.format(new Date());
                    entity.setCreateTime(mTime);
                    CreateForwardActivity.startActivity(CreateRichDocActivity.this,entity);
                    dialogUtil.dismissDialog();
                    finish();
                }
            });
            dialogUtil.showDialog();
        }else {
            Intent i = new Intent();
            setResult(RESPONSE_CODE, i);
            finish();
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        mTvMenuRight.setEnabled(true);
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(CreateRichDocActivity.this,code,msg);
    }
}
