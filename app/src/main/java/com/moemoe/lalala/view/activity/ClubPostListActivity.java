package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerClubComponent;
import com.moemoe.lalala.di.modules.ClubModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.ClubZipEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.TagNodeEntity;
import com.moemoe.lalala.presenter.ClubPostContract;
import com.moemoe.lalala.presenter.ClubPostPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.adapter.DocListAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.view.DocLabelView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/29.
 */

public class ClubPostListActivity extends BaseAppCompatActivity implements ClubPostContract.View,AppBarLayout.OnOffsetChangedListener{

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_group_image)
    ImageView mIvIcon;
    @BindView(R.id.tv_group_name)
    TextView mTvGroupTitle;
    @BindView(R.id.tv_group_brief)
    TextView mTvBrief;
    @BindView(R.id.iv_club_background)
    ImageView mIvClubBackground;
    @BindView(R.id.iv_send_post)
    View mIvSendDoc;
    @BindView(R.id.iv_send_music_post)
    View mIvSendMusicDoc;
    @BindView(R.id.list_club_docs)
    PullAndLoadView mListPost;
    @BindView(R.id.rl_group_head)
    View mHeadRoot;
    @BindView(R.id.dv_label_root)
    DocLabelView mDocLabel;
    @BindView(R.id.tv_like_num)
    TextView mLikesNum;
    @BindView(R.id.tv_doc_num)
    TextView mDocNum;
    @BindView(R.id.tv_simple_label)
    TextView mSimpleLabel;

    @Inject
    ClubPostPresenter mPresenter;
    private String mClubUuid;
    private DocListAdapter mDocAdapter;
    private boolean mIsLoadedBackground = false;
    private boolean mHasLoadClub = false;
    private boolean mIsLoading = false;
    private String mTagName;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_group_with_post;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        if (i == null){
            finish();
            return;
        }
        mClubUuid = i.getStringExtra("uuid");
        if(TextUtils.isEmpty(mClubUuid)){
            finish();
            return;
        }
        DaggerClubComponent.builder()
                .clubModule(new ClubModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mHeadRoot.setVisibility(View.INVISIBLE);
        mDocNum.setText(getString(R.string.label_doc_num, 0));
        mLikesNum.setText(getString(R.string.label_like_num, 0));
        mListPost.isLoadMoreEnabled(false);
        mListPost.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListPost.setLayoutManager(new LinearLayoutManager(this));
        mDocAdapter = new DocListAdapter(this,true);
        mListPost.getRecyclerView().setAdapter(mDocAdapter);
        mSimpleLabel.setVisibility(View.VISIBLE);
        mSimpleLabel.setSelected(AppSetting.SUB_TAG);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvSendDoc.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_IMG_DOC);
            }
        });
        mIvSendMusicDoc.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_MUSIC_DOC);
            }
        });
        mSimpleLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSetting.SUB_TAG = !AppSetting.SUB_TAG;
                mSimpleLabel.setSelected(AppSetting.SUB_TAG);
                PreferenceUtils.setSimpleLabel(ClubPostListActivity.this,AppSetting.SUB_TAG);
                changeLabelAdapter();
            }
        });
        mListPost.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isChange = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing())Glide.with(ClubPostListActivity.this).resumeRequests();
                }else {
                    if(!isFinishing())Glide.with(ClubPostListActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isChange){
                    if(dy > 10){
                        sendBtnOut();
                        isChange = false;
                    }
                }else {
                    if(dy < -10){
                        sendBtnIn();
                        isChange = true;
                    }
                }
            }
        });
        mDocAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DocListEntity bean = mDocAdapter.getItem(position);
                if (bean != null ) {
                    if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                        Uri uri = Uri.parse(bean.getDesc().getSchema());
                        IntentUtils.toActivityFromUri(ClubPostListActivity.this, uri,view);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListPost.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.requestDocList(mDocAdapter.getData().size());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.requestClubData(mClubUuid);
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
        mPresenter.requestClubData(mClubUuid);
    }

    private void changeLabelAdapter(){
        mPresenter.requestDocList(0);
    }

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mIvSendDoc,"translationY",mIvSendDoc.getHeight()+ DensityUtil.dip2px(this,10),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicIn = ObjectAnimator.ofFloat(mIvSendMusicDoc,"translationY",mIvSendMusicDoc.getHeight()+DensityUtil.dip2px(this,10),0).setDuration(300);
        sendMusicIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn).with(sendMusicIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mIvSendDoc,"translationY",0,mIvSendDoc.getHeight()+DensityUtil.dip2px(this,10)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicOut = ObjectAnimator.ofFloat(mIvSendMusicDoc,"translationY",0,mIvSendMusicDoc.getHeight()+DensityUtil.dip2px(this,10)).setDuration(300);
        sendMusicOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut).with(sendMusicOut);
        set.start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void bindClubViewData(TagNodeEntity entity) {
        mTagName = entity.getName();
        mDocNum.setText(getString(R.string.label_doc_num, entity.getDocNum()));
        mLikesNum.setText(getString(R.string.label_like_num, entity.getCommentNum()));
        mHeadRoot.setVisibility(View.VISIBLE);
        mTvGroupTitle.setText(entity.getName());
        mTvBrief.setVisibility(View.GONE);
        if(entity.getIcon() != null && !mHasLoadClub){
            // 这个页面的社团头像只会加载一次
            Glide.with(this)
                    .load(StringUtils.getUrl(this,ApiService.URL_QINIU + entity.getIcon().getPath(),DensityUtil.dip2px(this,80),DensityUtil.dip2px(this,80),false,true))
                    .override(DensityUtil.dip2px(this,80), DensityUtil.dip2px(this,80))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .transform(new GlideRoundTransform(this,5))
                    .into(mIvIcon);
            mHasLoadClub = true;
        }
        ArrayList<String> tags = entity.getTexts();
        mDocLabel.setLabels(tags,ClubPostListActivity.this);
        if (entity.getBg() != null) {
            if (!mIsLoadedBackground) {
                Glide.with(this)
                        .load(StringUtils.getUrl(this,ApiService.URL_QINIU +  entity.getBg().getPath(), DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,175), false, true))
                        .override(DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,175))
                        .placeholder(R.drawable.bg_netaschool)
                        .error(R.drawable.bg_netaschool)
                        .into(mIvClubBackground);
                mIsLoadedBackground = true;
            }
        }else {
            mIvClubBackground.setBackgroundResource(R.drawable.bg_netaschool);
        }
        mIvSendDoc.setVisibility(View.VISIBLE);
        mIvSendMusicDoc.setVisibility(View.VISIBLE);
        mTvTitle.setText(entity.getName());
    }

    @Override
    public void bindListViewData(ClubZipEntity entity) {
        mIsLoading = false;
        mDocAdapter.clearTopAndHot();
        mListPost.setComplete();
        if(entity.docList.getState() == 200 && entity.docList.getData() != null){
            mListPost.isLoadMoreEnabled(true);
        }
        if(entity.topList.getState() == 200 && entity.topList.getData() != null){
            mDocAdapter.setTopData(entity.topList.getData(),entity.topList.getData().size());
        }
        if(entity.hotList.getState() == 200 && entity.hotList.getData() != null){
            mDocAdapter.setHotData(entity.hotList.getData());
        }
        if(entity.docList.getState() == 200 && entity.docList.getData() != null){
            mDocAdapter.setData(entity.docList.getData());
        }
    }

    @Override
    public void onLoadDocList(ArrayList<DocListEntity> docListEntities,boolean isPull) {
        mIsLoading = false;
        mListPost.setComplete();
        if(isPull){
            ArrayList<DocListEntity> top = mDocAdapter.getTopAndHot();
            int split = mDocAdapter.getSplit();
            mDocAdapter = new DocListAdapter(this,true);
            mDocAdapter.setTopData(top,split);
            mDocAdapter.setData(docListEntities);
            mDocAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    DocListEntity bean = mDocAdapter.getItem(position);
                    if (bean != null ) {
                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                            Uri uri = Uri.parse(bean.getDesc().getSchema());
                            IntentUtils.toActivityFromUri(ClubPostListActivity.this, uri,view);
                        }
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            mListPost.getRecyclerView().setAdapter(mDocAdapter);
        }else {
            if(docListEntities.size() == 0){
                showToast(R.string.msg_all_load_down);
            }else {
                mDocAdapter.addData(docListEntities);
            }
        }
    }

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(int type){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(this)){
            Intent intent = new Intent(ClubPostListActivity.this, CreateNormalDocActivity.class);
            intent.putExtra(UUID, mClubUuid);
            intent.putExtra(CreateNormalDocActivity.TYPE_CREATE,type);
            intent.putExtra(CreateNormalDocActivity.TYPE_TAG_NAME_DEFAULT,mTagName);
            startActivityForResult(intent, CreateNormalDocActivity.REQUEST_CODE_CREATE_DOC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            mListPost.getRecyclerView().scrollToPosition(0);
            mPresenter.requestClubData(mClubUuid);
        }
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mListPost.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(ClubPostListActivity.this,code,msg);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if(verticalOffset == 0 ){
            mListPost.setEnabled(true);
        }else if(verticalOffset < - DensityUtil.dip2px(this,127)){

        } else {
            mListPost.setEnabled(false);
        }
        int newAlpha;
        if (verticalOffset != 0){
            newAlpha = Math.min(255,Math.abs(verticalOffset));
        }else {
            newAlpha = 0;
        }
        mSimpleLabel.setAlpha((float) newAlpha/255);
        mTvTitle.setTextColor(Color.argb(newAlpha, 255, 255, 255));
    }
}