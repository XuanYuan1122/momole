package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.RichImgRemoveEvent;
import com.moemoe.lalala.model.entity.RichEntity;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.richtext.NetaRichEditor;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
 * Created by yi on 2017/5/15.
 */

public class CreateRichDocHideActivity extends BaseAppCompatActivity {

    /**
     * 9张图片上限
     */
    private final int ICON_NUM_LIMIT = 18;
    private final int REQ_ADD_SEARCH = 1005;

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
    @BindView(R.id.rl_type_root)
    View mTypeRoot;
    @BindView(R.id.tv_coin)
    TextView mTvCoin;
    @BindView(R.id.tv_report)
    TextView mTvComment;
    @BindView(R.id.tv_type_info)
    TextView mTvTypeInfo;

    private HashMap<String,String> mPathMap;
    private int mImageSize;
    private ArrayList<RichEntity> mHideList;
    private boolean coinComment;//false coin  true comment

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_rich_doc;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        mHideList = i.getParcelableArrayListExtra("hide_list");
        coinComment = i.getBooleanExtra("hide_type",false);
        if(coinComment){
            mTvCoin.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
            mTvComment.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
            mTvTypeInfo.setText(getString(R.string.label_report_watch_info));
        }else {
            mTvCoin.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
            mTvComment.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
            mTvTypeInfo.setText(getString(R.string.label_coin_watch_info));
        }
        mTypeRoot.setVisibility(View.VISIBLE);
        mPathMap = new HashMap<>();
        mImageSize = 0;
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft,DensityUtil.dip2px(this,18));
        mTvMenuLeft.setText(getString(R.string.label_cancel));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.label_hide_area));
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight, DensityUtil.dip2px(this,18));
        mTvMenuRight.setText(getString(R.string.label_done));
        mTvMenuRight.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        subscribeChangedEvent();
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
        if(mHideList.size() > 0){
            RichEntity entity = mHideList.get(0);
            if(TextUtils.isEmpty(entity.getInputStr())){
                mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),"");
            }
            Observable.fromIterable(mHideList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RichEntity>() {

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            RichEntity entity1 = mHideList.get(mHideList.size() - 1);
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
                                mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),StringUtils.buildAtUserToEdit(CreateRichDocHideActivity.this,richEntity.getInputStr().toString()));
                            }else if(richEntity.getImage() != null && !TextUtils.isEmpty(richEntity.getImage().getPath())){
                                mRichEt.addImageViewAtIndex(mRichEt.getLastIndex(),richEntity.getImage().getPath(),richEntity.getImage().getW(),richEntity.getImage().getH(),richEntity.getImage().getSize());
                                mPathMap.put(richEntity.getImage().getPath(),richEntity.getImage().getPath());
                                mImageSize++;
                            }
                        }
                    });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }

    @OnClick({R.id.iv_add_img,R.id.iv_alt_user,R.id.tv_menu,R.id.tv_change_type})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_add_img:
                choosePhoto();
                break;
            case R.id.iv_alt_user:
                Intent i3 = new Intent(CreateRichDocHideActivity.this,SearchActivity.class);
                i3.putExtra("show_type",SearchActivity.SHOW_USER);
                startActivityForResult(i3,REQ_ADD_SEARCH);
                break;
            case R.id.tv_menu:
                done();
                break;
            case R.id.tv_change_type:
                coinComment = !coinComment;
                if(coinComment){
                    mTvCoin.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
                    mTvComment.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
                    mTvTypeInfo.setText(getString(R.string.label_report_watch_info));
                }else {
                    mTvCoin.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
                    mTvComment.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
                    mTvTypeInfo.setText(getString(R.string.label_coin_watch_info));
                }
                break;
        }
    }

    private void done(){
        if(mRichEt.hasContent()){
            mHideList = (ArrayList<RichEntity>) mRichEt.buildEditData();
            ArrayList<String> ids = new ArrayList<>();
            for (RichEntity entity : mHideList){
                if (!TextUtils.isEmpty(entity.getInputStr())) {
                    entity.setInputStr(StringUtils.buildDataAtUser(entity.getInputStr()));
                    ids.addAll(StringUtils.getAtUserIds(entity.getInputStr()));
                }
            }
            Intent i = new Intent();
            i.putParcelableArrayListExtra("hide_list",mHideList);
            i.putExtra("hide_type",coinComment);
            i.putStringArrayListExtra("at_user",ids);
            setResult(RESULT_OK,i);
        }else {
            setResult(RESULT_OK);
        }
        finish();
    }

    private void choosePhoto() {
      //  if (mPathMap.size() < ICON_NUM_LIMIT) {
        if (mImageSize < ICON_NUM_LIMIT) {
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
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(final ArrayList<String> photoPaths, boolean override) {
                    Collections.reverse(photoPaths);
                    createDialog("图片插入中...");
                    onGetPhotos(photoPaths);
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
}
