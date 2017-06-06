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
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.compress.NetaImgCompress;
import com.moemoe.lalala.view.widget.richtext.NetaRichEditor;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;
import java.util.HashMap;

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

public class CreateRichDocHideActivity extends BaseAppCompatActivity {

    /**
     * 9张图片上限
     */
    private final int ICON_NUM_LIMIT = 15;

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

    private ArrayList<String> mIconPaths;
    private HashMap<String,String> mPathMap;
    private ArrayList<RichEntity> mHideList;
    private boolean coinComment;//false coin  true comment

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_rich_doc;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AndroidBug5497Workaround.assistActivity(this);
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        mHideList = i.getParcelableArrayListExtra("hide_list");
        mTypeRoot.setVisibility(View.VISIBLE);
        mIconPaths = new ArrayList<>();
        mPathMap = new HashMap<>();
        mTvMenuLeft.setVisibility(View.VISIBLE);
        mTvMenuLeft.setText(getString(R.string.label_cancel));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.label_hide_area));
        mTvMenuRight.setVisibility(View.VISIBLE);
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
            Observable.from(mHideList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<RichEntity>() {
                        @Override
                        public void call(RichEntity richEntity) {
                            if(!TextUtils.isEmpty(richEntity.getInputStr())){
                                mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),richEntity.getInputStr());
                            }else {
                                mRichEt.addImageViewAtIndex(mRichEt.getLastIndex(),richEntity.getImagePath());
                            }
                        }
                    });
        }
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
                        String value = mPathMap.remove(event.getPath());
                        for(String s : mIconPaths){
                            if(s.equals(value)){
                                mIconPaths.remove(s);
                                break;
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

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
        mHideList = (ArrayList<RichEntity>) mRichEt.buildEditData();
        Intent i = new Intent();
        i.putParcelableArrayListExtra("hide_list",mHideList);
        i.putExtra("hide_type",coinComment);
        setResult(RESULT_OK,i);
        finish();
    }

    private void choosePhoto() {
        if (mIconPaths.size() < ICON_NUM_LIMIT) {
            try {
                DialogUtils.createImgChooseDlg(this, null, this, mIconPaths, ICON_NUM_LIMIT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showToast(R.string.msg_create_doc_9_jpg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    final ArrayList<String> tmp = new ArrayList<>();
                    if (override) {
                        checkPaths(photoPaths,tmp);
                        mIconPaths = photoPaths;
                    } else {
                        mIconPaths.addAll(photoPaths);
                        tmp.addAll(photoPaths);
                    }
                    final ArrayList<String> res = new ArrayList<>();
                    NetaImgCompress.get(CreateRichDocHideActivity.this)
                            .load(tmp)
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
                                    for (int i = 0;i < tmp.size();i++){
                                        mPathMap.put(res.get(i),tmp.get(i));
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

    private void checkPaths(ArrayList<String> paths,ArrayList<String> list){
        for(String s : paths){
            boolean res = true;
            for (String s1 : mIconPaths){
                if(s.equals(s1)){
                    res = false;
                    break;
                }
            }
            if(res) list.add(s);
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
              //  mRichEt.addEditTextAtIndex(mRichEt.getLastIndex(),"");
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
}
