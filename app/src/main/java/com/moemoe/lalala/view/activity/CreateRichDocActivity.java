package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Color;
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
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDocType;
import com.moemoe.lalala.model.entity.RichEntity;
import com.moemoe.lalala.presenter.CreateRichDocContract;
import com.moemoe.lalala.presenter.CreateRichDocPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.compress.NetaImgCompress;
import com.moemoe.lalala.view.widget.richtext.NetaRichEditor;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2017/5/15.
 */

public class CreateRichDocActivity extends BaseAppCompatActivity implements CreateRichDocContract.View{

    /**
     * 标题限制长度
     */
    private final int TITLE_LIMIT = 30;
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

    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.rich_et)
    NetaRichEditor mRichEt;
    @BindView(R.id.et_title)
    EditText mEtTitle;
    @BindView(R.id.ev_title_count)
    TextView mTvTitleCount;
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
    @BindView(R.id.rl_title_root)
    View mTitleRoot;
    @Inject
    CreateRichDocPresenter mPresenter;

    private String mFromSchema;
    private String mFromName;
    private HashMap<String,String> mPathMap;
    private ArrayList<RichEntity> mHideList;
    private String mFolderId;
    private boolean mHideType;
    private MusicLoader.MusicInfo mMusicInfo;
    private String mMusicCover;
    private int mDocType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_rich_doc;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AndroidBug5497Workaround.assistActivity(this);
        DaggerCreateRichDocComponent.builder()
                .createRichDocModule(new CreateRichDocModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        String mTagNameDef = i.getStringExtra(TYPE_TAG_NAME_DEFAULT);
        mFromSchema = i.getStringExtra("from_schema");
        mFromName = i.getStringExtra("from_name");
        mDocType = i.getIntExtra(TYPE_QIU_MING_SHAN,0);
        mIvAddBag.setVisibility(View.VISIBLE);
        mIvAddHide.setVisibility(View.VISIBLE);
        mIvAddMusic.setVisibility(View.VISIBLE);
        mViewAddSep.setVisibility(View.VISIBLE);
        mTitleRoot.setVisibility(View.VISIBLE);

        mHideList = new ArrayList<>();
        mPathMap = new HashMap<>();
        mRichEt.setLabelAble();
        mRichEt.setmTagNameDef(mTagNameDef);
        mRichEt.setmKlCommentBoard(mKlCommentBoard);
        mTvMenuLeft.setVisibility(View.VISIBLE);
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.label_create_post));
        mTvMenuRight.setVisibility(View.VISIBLE);
        mTvMenuRight.setText(getString(R.string.label_menu_publish_doc));
        mTvMenuRight.setTextColor(Color.WHITE);
        mTvMenuRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        mTvMenuRight.setWidth(DensityUtil.dip2px(this,44));
        mTvMenuRight.setHeight(DensityUtil.dip2px(this,24));
        mTvMenuRight.setBackgroundResource(R.drawable.shape_rect_border_main_background_2);
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
        mPresenter.release();
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
        mEtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtTitle.getText();
                int len = editable.length();
                if (len > TITLE_LIMIT) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0, TITLE_LIMIT);
                    mEtTitle.setText(newStr);
                    editable = mEtTitle.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
                mTvTitleCount.setText((TITLE_LIMIT - mEtTitle.getText().length()) + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    private void subscribeChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(RichImgRemoveEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<RichImgRemoveEvent>() {
                    @Override
                    public void call(RichImgRemoveEvent event) {
                        mPathMap.remove(event.getPath());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void createPost() {
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        if (!mRichEt.hasContent()) {
            showToast(R.string.msg_doc_content_cannot_null);
        }  else if(mRichEt.getmTags().size() < 1){
            showToast(R.string.msg_need_one_tag);
        }else {
            finalizeDialog();
            mTvMenuRight.setEnabled(false);
            DocPut mDocEntity = new DocPut();
            mDocEntity.docType = mFromName;
            mDocEntity.docTypeSchema = mFromSchema;
            mDocEntity.bagFolderId = mFolderId;
            mDocEntity.title = mEtTitle.getText().toString();
            for (int i = 0;i < mRichEt.getmTags().size();i++){
                mDocEntity.tags.add(mRichEt.getmTags().get(i).getName());
            }
            for (RichEntity entity : mRichEt.buildEditData() ){
                if(!TextUtils.isEmpty(entity.getInputStr())){
                    DocPut.DocPutText docPutText = new DocPut.DocPutText();
                    docPutText.text = StringUtils.buildDataAtUser(entity.getInputStr());
                    mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(), docPutText));
                }else {
                    DocPut.DocPutImage docPutImage = new DocPut.DocPutImage();
                    docPutImage.path = entity.getImagePath();
                    docPutImage.size = new File(entity.getImagePath()).length();
                    mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_IMAGE.toString(), docPutImage));
                }
            }
            for (RichEntity entity : mHideList){
                if(!TextUtils.isEmpty(entity.getInputStr())){
                    DocPut.DocPutText docPutText = new DocPut.DocPutText();
                    docPutText.text = entity.getInputStr().toString();
                    mDocEntity.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_TEXT.toString(), docPutText));
                }else {
                    DocPut.DocPutImage docPutImage = new DocPut.DocPutImage();
                    docPutImage.path = entity.getImagePath();
                    docPutImage.size = new File(entity.getImagePath()).length();
                    mDocEntity.coin.details.add(new DocPut.DocDetail(NewDocType.DOC_IMAGE.toString(), docPutImage));
                }
            }
            mDocEntity.coinComment = mHideType;
            if(mMusicInfo != null){
                DocPut.DocPutMusic docPutMusic = new DocPut.DocPutMusic();
                docPutMusic.name = mMusicInfo.getTitle();
                docPutMusic.timestamp = mMusicInfo.getDuration();
                docPutMusic.url = mMusicInfo.getUrl();
                Image image = new Image();
                image.setPath(mMusicCover);
                docPutMusic.cover = image;
                mDocEntity.details.add(new DocPut.DocDetail(NewDocType.DOC_MUSIC.toString(),docPutMusic));
            }
            if(mHideList.size() > 0){
                mDocEntity.coin.coin = 1;
            }else {
                mDocEntity.coin.coin = 0;
            }
            mPresenter.createDoc(mDocEntity,mDocType);
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
                Intent i = new Intent(CreateRichDocActivity.this,CreateRichDocHideActivity.class);
                i.putParcelableArrayListExtra("hide_list",mHideList);
                startActivityForResult(i,REQ_ADD_HIDE);
                break;
            case R.id.iv_add_bag:
                Intent i1 = new Intent(CreateRichDocActivity.this,FolderSelectActivity.class);
                startActivityForResult(i1,REQ_SELECT_FOLDER);
                break;
            case R.id.iv_add_music:
                Intent i2 = new Intent(CreateRichDocActivity.this,AddMusicActivity.class);
                i2.putExtra("music_info",mMusicInfo);
                i2.putExtra("music_cover",mMusicCover);
                startActivityForResult(i2,REQ_GET_FROM_SELECT_MUSIC);
                break;
            case R.id.tv_menu:
                createPost();
                break;
        }
    }

    private void choosePhoto() {
        if (mPathMap.size() < ICON_NUM_LIMIT) {
            try {
                ArrayList<String> temp = new ArrayList<>();
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
                mMusicCover = data.getStringExtra("music_cover");
                mIvAddMusic.setSelected(true);
            }
        }else if(requestCode == REQ_ADD_HIDE && resultCode == RESULT_OK){
            if(data != null){
                mHideList = data.getParcelableArrayListExtra("hide_list");
                mHideType = data.getBooleanExtra("hide_type",false);
                if(mHideList.size() > 0){
                    mIvAddHide.setSelected(true);
                }else {
                    mIvAddHide.setSelected(false);
                }
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
                    final ArrayList<String> res = new ArrayList<>();
                    NetaImgCompress.get(CreateRichDocActivity.this)
                            .load(photoPaths)
                            .asPath()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            })
                            .onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
                                @Override
                                public Observable<? extends String> call(Throwable throwable) {
                                    return Observable.empty();
                                }
                            })
                            .subscribe(new Subscriber<String>() {
                                @Override
                                public void onCompleted() {
                                    for (int i = 0;i < photoPaths.size();i++){
                                        mPathMap.put(res.get(i),photoPaths.get(i));
                                    }
                                    onGetPhotos(res);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(String s) {
                                    res.add(s);
                                }
                            });
                }
            });
        }
    }

    private void onGetPhotos(final ArrayList<String> paths) {
        createDialog("图片插入中...");
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mRichEt.measure(0,0);
                try{
                    for (String s : paths){
                        subscriber.onNext(s);
                    }
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                finalizeDialog();
                showToast("图片插入成功");
            }

            @Override
            public void onError(Throwable e) {
                finalizeDialog();
            }

            @Override
            public void onNext(String s) {
                mRichEt.insertImage(s);
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
    public void onSendSuccess() {
        mTvMenuRight.setEnabled(true);
        for (RichEntity entity : mRichEt.buildEditData() ){
            if(TextUtils.isEmpty(entity.getInputStr())){
                if(!FileUtil.isGif(entity.getImagePath())) FileUtil.deleteFile(entity.getImagePath());
            }
        }
        for (RichEntity entity : mHideList){
            if(TextUtils.isEmpty(entity.getInputStr())){
                if(!FileUtil.isGif(entity.getImagePath())) FileUtil.deleteFile(entity.getImagePath());
            }
        }
        finalizeDialog();
        showToast(R.string.msg_create_doc_success);
        Intent i = new Intent();
        setResult(RESPONSE_CODE, i);

        finish();
    }

    @Override
    public void onFailure(int code, String msg) {
        mTvMenuRight.setEnabled(true);
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(CreateRichDocActivity.this,code,msg);
    }
}
