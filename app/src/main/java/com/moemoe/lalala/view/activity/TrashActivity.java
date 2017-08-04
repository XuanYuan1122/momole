package com.moemoe.lalala.view.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerTrashComponent;
import com.moemoe.lalala.di.modules.TrashModule;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.presenter.TrashContract;
import com.moemoe.lalala.presenter.TrashPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.MoeMoeCallback;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.trashcard.CardSlidePanel;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 文本小纸条
 * Created by yi on 2016/12/2.
 */

public class TrashActivity extends BaseAppCompatActivity implements TrashContract.View{

    @BindView(R.id.swipe_card)
    CardSlidePanel mSwipeView;
    @BindView(R.id.swipe_card_top)
    CardSlidePanel mSwipeViewTop;
    @BindView(R.id.iv_fun)
    ImageView mIvFun;
    @BindView(R.id.iv_create)
    ImageView mIvCreate;
    @BindView(R.id.iv_shit)
    ImageView mIvShit;
    @BindView(R.id.iv_my_trash)
    ImageView mIvMyTrash;
    @BindView(R.id.iv_yesterday_best_trash)
    ImageView mIvYesterday;
    @BindView(R.id.tv_fun)
    TextView mTvFun;
    @BindView(R.id.tv_shit)
    TextView mTvShite;

    @Inject
    TrashPresenter mPresenter;
    private boolean mIsTop3;
    private ArrayList<TrashEntity> entities;
    private ArrayList<TrashEntity> topEntities;
    private boolean mIsFun;
    private boolean mIsFirst;
    private boolean mIsInFinishAnim;
    private MoeMoeCallback mCallback;
    private MoeMoeCallback mTagCallback;
    private String mEtContent;
    private boolean mCanLoad;
    private int mCurTime;
    private int mCurLastTime;
    private String mShit = "";
    private String mFun = "";

    @Override
    protected int getLayoutId() {
        return R.layout.ac_trash_text_text;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerTrashComponent.builder()
                .trashModule(new TrashModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), null);
        entities = new ArrayList<>();
        topEntities = new ArrayList<>();
        mIsFirst = true;
        mCurTime = PreferenceUtils.getLastTrashTime(this,"text");
        loadData(mCurTime);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    private void loadData(int time){
        mPresenter.getTrashList(0,time);
    }

    @Override
    protected void initListeners() {
        mSwipeView.setCardSwitchListener(new CardSlidePanel.CardSwitchListener() {
            int width = DensityUtil.getScreenWidth(TrashActivity.this) / 2;
            @Override
            public void onShow(int index) {
                if(entities != null && entities.size() > 0 && index < entities.size()){
                    TrashEntity entity = entities.get(index);
                    mTvFun.setText(entity.getFun() + "");
                    mTvShite.setText(entity.getShit() + "");
                    mCurTime = entity.getTimestamp();
                }else {
                    mTvFun.setText("");
                    mTvShite.setText("");
                    showToast("这个垃圾桶已经见底了…\n" +
                            "学长们正在努力创作更多\n" +
                            "请稍后再来翻翻\n" +
                            "要么，你也丢几个？");
                }
            }

            @Override
            public void onCardVanish(int index, int type) {
                if(type == 0){
                    mPresenter.operationTrash(entities.get(index).getDustbinId(),true);
                    scaleFunAndRotaToNormal();
                }else if(type == 1){
                    mPresenter.operationTrash(entities.get(index).getDustbinId(),false);
                    scaleShitAndRotaToNormal();
                }
                if(mCanLoad && index >= entities.size() - 8){
                    mIsFirst = false;
                    loadData(mCurLastTime);
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
                if(entities != null && entities.size() > 0 && index < entities.size()){
                    Intent i = new Intent(TrashActivity.this,TrashDetailActivity.class);
                    i.putExtra("type","text");
                    //i.putExtra("id",entities.get(index).getDustbinId());
                    i.putExtra("item",entities.get(index));
                    startActivity(i);
                }
            }

            @Override
            public void onItemScroll(View cardImageView, int position) {
                if(!mIsInFinishAnim){
                    float progress = ((float) Math.min(Math.abs(position),width) )/ width;
                    if(position < 0){
                        scaleFun(1 + progress);
                        mIsFun = true;
                    }else if(position > 0){
                        scaleShit(1 + progress);
                        mIsFun = false;
                    }
                }
            }

            @Override
            public void onRelease(View releasedChild) {
                if(mIsFun){
                    scaleFunToNormal();
                }else {
                    scaleShitToNormal();
                }
            }
        });
        mSwipeViewTop.setCardSwitchListener(new CardSlidePanel.CardSwitchListener() {
            int width = DensityUtil.getScreenWidth(TrashActivity.this) / 2;
            @Override
            public void onShow(int index) {
                if(topEntities != null && topEntities.size() > 0 && index < topEntities.size()){
                    TrashEntity entity = topEntities.get(index);
                    mTvFun.setText(entity.getFun() + "");
                    mTvShite.setText(entity.getShit() + "");
                }else {
                    mTvFun.setText("");
                    mTvShite.setText("");
                }
            }

            @Override
            public void onCardVanish(int index, int type) {
                if(type == 0){
                    mPresenter.operationTrash(topEntities.get(index).getDustbinId(),true);
                    scaleFunAndRotaToNormal();
                }else if(type == 1){
                    mPresenter.operationTrash(topEntities.get(index).getDustbinId(),false);
                    scaleShitAndRotaToNormal();
                }
                if(index >= topEntities.size() - 8){
                    ArrayList<TrashEntity> entities = new ArrayList<>();
                    entities.addAll(topEntities);
                    topEntities.addAll(entities);
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
                if(topEntities != null && topEntities.size() > 0 && index < topEntities.size()) {
                    Intent i = new Intent(TrashActivity.this, TrashDetailActivity.class);
                    i.putExtra("type", "text");
                    i.putExtra("item",topEntities.get(index));
                  //  i.putExtra("id", topEntities.get(index).getDustbinId());
                    startActivity(i);
                }
            }

            @Override
            public void onItemScroll(View cardImageView, int position) {
                if(!mIsInFinishAnim){
                    float progress = ((float) Math.min(Math.abs(position),width) )/ width;
                    if(position < 0){
                        scaleFun(1 + progress);
                        mIsFun = true;
                    }else if(position > 0){
                        scaleShit(1 + progress);
                        mIsFun = false;
                    }
                }
            }

            @Override
            public void onRelease(View releasedChild) {
                if(mIsFun){
                    scaleFunToNormal();
                }else {
                    scaleShitToNormal();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_back,R.id.iv_my_trash,R.id.iv_create,R.id.iv_yesterday_best_trash})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_back:
                if (mIsTop3){
                    mIsTop3 = false;
                    mSwipeView.setVisibility(View.VISIBLE);
                    mSwipeViewTop.setVisibility(View.GONE);
                    mIvMyTrash.setVisibility(View.VISIBLE);
                    mIvYesterday.setVisibility(View.VISIBLE);
                    mIvCreate.setVisibility(View.VISIBLE);
                    mTvFun.setText(mFun);
                    mTvShite.setText(mShit);
                    mIvCreate.setEnabled(true);
                }else {
                    finish();
                }
                break;
            case R.id.iv_my_trash:
                if(DialogUtils.checkLoginAndShowDlg(this)){
                    Intent i = new Intent(TrashActivity.this,TrashFavoriteActivity.class);
                    i.putExtra(TrashFavoriteActivity.EXTRA_TYPE,"text");
                    startActivity(i);
                }
                break;
            case R.id.iv_create:
                Intent intent = new Intent(TrashActivity.this,CreateTrashActivity.class);
                intent.putExtra(CreateTrashActivity.TYPE_CREATE,CreateTrashActivity.TYPE_TEXT_TRASH);
                startActivity(intent);
                break;
            case R.id.iv_yesterday_best_trash:
                mIsTop3 = true;
                mIvMyTrash.setVisibility(View.GONE);
                mIvYesterday.setVisibility(View.GONE);
                mIvCreate.setVisibility(View.INVISIBLE);
                mIvCreate.setEnabled(false);
                mSwipeView.setVisibility(View.GONE);
                mSwipeViewTop.setVisibility(View.VISIBLE);
                mFun = mTvFun.getText().toString();
                mShit = mTvShite.getText().toString();
                if (topEntities.size() == 0){
                    mPresenter.getTop3List(0);
                }
                break;
        }
    }

    @Override
    public void onLoadListSuccess(ArrayList<TrashEntity> entities) {
        mCurLastTime = entities.get(entities.size() - 1).getTimestamp();
        this.entities.addAll(entities);
        if (mIsFirst){
            if(this.entities.size() != 0) {
                mSwipeView.fillData(this.entities);
            }else {
                showToast("这个垃圾桶已经见底了…\n" +
                        "学长们正在努力创作更多\n" +
                        "请稍后再来翻翻\n" +
                        "要么，你也丢几个？");
            }
            if(entities.size() < 4){
                mCanLoad = false;
            }else {
                mCanLoad = true;
            }
        }
    }

    @Override
    public void onFavoriteTrashSuccess(TrashEntity entity) {
        finalizeDialog();
    }

    @Override
    public void onTop3LoadSuccess(ArrayList<TrashEntity> entities) {
        topEntities.addAll(entities);
        if(this.topEntities.size() != 0) {
            topEntities.addAll(entities);
            topEntities.addAll(entities);
            mSwipeViewTop.fillData(topEntities);
        }
    }

    @Override
    public void onTop3LoadFail(int code,String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadDetailSuccess(ArrayList<DocTagEntity> entity) {

    }

    @Override
    public void onLikeTag(boolean like, int position) {
        finalizeDialog();
        if(mTagCallback != null){
            mTagCallback.onSuccess(position);
            mTagCallback = null;
        }
    }

    @Override
    public void onCreateTag(String id) {
        finalizeDialog();
        if(mCallback != null){
            DocTagEntity docTag = new DocTagEntity();
            docTag.setLikes(1);
            docTag.setName(mEtContent);
            docTag.setLiked(true);
            docTag.setId(id);
            mCallback.onSuccess(docTag);
            mEtContent = "";
            mCallback = null;
        }
    }

    public void addLabel(String tadId,String dustId,boolean liked,int position,MoeMoeCallback callback){
        TagLikeEntity bean = new TagLikeEntity(tadId,dustId,"text");
        createDialog();
        mPresenter.likeTrashTag(bean,liked,position);
        mTagCallback = callback;
    }

    public void createLabel(final String id, final MoeMoeCallback callback){
        final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
        dialogUtil.createAddLabelDialog(this);
        dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                dialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                mEtContent = dialogUtil.getEditTextContent();
                if(!TextUtils.isEmpty(mEtContent)){
                    createDialog();
                    TagSendEntity entity = new TagSendEntity(mEtContent,"text",id);
                    mPresenter.createTag(entity);
                    mCallback = callback;
                    dialogUtil.dismissDialog();
                }else {
                    showToast(R.string.msg_can_not_empty);
                }
            }
        });
        dialogUtil.showDialog();
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    public void favoriteTrash(TrashEntity entity){
        createDialog();
        mPresenter.favoriteTrash(entity,"text");
    }

    private void scaleFun(float scale){
        if(scale > 1.2f){
            scale = 1.2f;
        }
        mIvFun.setScaleX(scale);
        mIvFun.setScaleY(scale);
    }

    private void scaleShit(float scale){
        if(scale > 1.2f){
            scale = 1.2f;
        }
        mIvShit.setScaleX(scale);
        mIvShit.setScaleY(scale);
    }

    private void scaleFunToNormal(){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", mIvFun.getScaleX(), 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", mIvFun.getScaleY(), 1f);
        ObjectAnimator.ofPropertyValuesHolder(mIvFun, pvhX, pvhY).setDuration(200).start();
    }

    private void scaleShitToNormal(){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", mIvShit.getScaleX(), 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", mIvShit.getScaleY(), 1f);
        ObjectAnimator.ofPropertyValuesHolder(mIvShit, pvhX, pvhY).setDuration(200).start();
    }

    private void scaleFunAndRotaToNormal(){
        PropertyValuesHolder roteZ = PropertyValuesHolder.ofFloat("rotation", 0f,
                -30f, 30f,0f);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", mIvFun.getScaleX(), 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", mIvFun.getScaleY(), 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mIvFun, pvhX, pvhY,roteZ);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsInFinishAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsInFinishAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setDuration(500).start();

    }

    private void scaleShitAndRotaToNormal(){
        PropertyValuesHolder roteZ = PropertyValuesHolder.ofFloat("rotation", 0f,
                -30f, 30f,0f);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", mIvShit.getScaleX(), 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", mIvShit.getScaleY(), 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mIvShit, pvhX, pvhY,roteZ);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsInFinishAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsInFinishAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setDuration(500).start();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.sendOperationTrash();
        if(mPresenter != null) mPresenter.release();
        if(mCurTime != 0) PreferenceUtils.setLastTrashTime(this,mCurTime,"text");
        super.onDestroy();
    }
}
