package com.moemoe.lalala;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
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
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.adapter.DocListAdapter;
import com.moemoe.lalala.data.ClubBean;
import com.moemoe.lalala.data.ClubDbbean;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.data.TagNode;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.TagTextView;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Haru on 2016/5/1 0001.
 */
@ContentView(R.layout.ac_group_with_post)
public class ClubPostListActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener{

    public static final String TAG = "ClubPostListActivity";
    private static final int REQUEST_CODE_CREATE_DOC = 9000;

    @FindView(R.id.rl_group_head)
    public View mRlHeadPack;
    @FindView(R.id.ll_club_nums_pack)
    public View mLlClubNumsPack;
    @FindView(R.id.appbar)
    private AppBarLayout mAppBarLayout;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.iv_group_image)
    public MyRoundedImageView mIvIcon;
    @FindView(R.id.iv_group_office_flag)
    public View mIvOfficeVFlag;
    @FindView(R.id.tv_group_name)
    public TextView mTvGroupTitle;
    @FindView(R.id.tv_group_brief)
    public TextView mTvBrief;
    @FindView(R.id.iv_group_more)
    private View mBtnGroupMore;
    @FindView(R.id.tv_follow)
    public TextView mBtnFollow;
    @FindView(R.id.tv_gift_num)
    public TextView mTvGiftNum;
    @FindView(R.id.tv_follower_num)
    public TextView mTvFollowerNum;
    @FindView(R.id.tv_post_num)
    public TextView mTvPostNum;
    @FindView(R.id.iv_club_background)
    private ImageView mIvClubBackground;
    @FindView(R.id.iv_send_post)
    private View mIvSendDoc;
    @FindView(R.id.iv_send_music_post)
    private View mIvSendMusicDoc;
    @FindView(R.id.rl_doc_loading)
    private View mRlEmptyDocLoading;
    @FindView(R.id.list_club_docs)
    private PullAndLoadView mListPost;
    @FindView(R.id.rl_group_head)
    private View mHeadRoot;
    @FindView(R.id.dv_label_root)
    private DocLabelView mDocLabel;
    @FindView(R.id.tv_like_num)
    private TextView mLikesNum;
    @FindView(R.id.tv_doc_num)
    private TextView mDocNum;
    private String mTagName;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DocListAdapter mDocAdapter;
    public TagTextView mTags[] = new TagTextView[5];
    private TagNode mClubData;
    private ArrayList<DocItemBean> mDocData = new ArrayList<>();
    private ArrayList<DocItemBean> mDocTop = new ArrayList<>();
    private ArrayList<DocItemBean> mDocHot = new ArrayList<>();
    private int mSplitNum;
    private String mClubUuid;
    private boolean mIsLoadedBackground = false;
    private boolean mHasLoadClub = false;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private DbManager db;
    private ClubDbbean clubBean = new ClubDbbean();
    private MoeMoeCallback allFinishCallback;
    private boolean mIsPullDown;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelTag(TAG);
    }

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mClubUuid = getIntent().getStringExtra(EXTRA_KEY_UUID);
        if(!TextUtils.isEmpty(mClubUuid)){
            //TODO 从数据库读取club
        }else{
            finish();
        }
        mHeadRoot.setVisibility(View.INVISIBLE);
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
        mDocNum.setText(getString(R.string.label_doc_num, 0));
        mLikesNum.setText(getString(R.string.label_like_num, 0));
        mSwipeRefreshLayout = mListPost.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListPost.getRecyclerView();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isChange = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(ClubPostListActivity.this).resumeTag(TAG);
                } else {
                    Picasso.with(ClubPostListActivity.this).pauseTag(TAG);
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
        mDocAdapter = new DocListAdapter(this,DocListAdapter.TYPE_CLUB_DOC,true,TAG);
        mRecyclerView.setAdapter(mDocAdapter);
        mDocAdapter.setOnItemClickListener(new DocListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mDocAdapter.getItem(position);
                if (object != null && object instanceof DocItemBean) {
                    DocItemBean bean = (DocItemBean) object;
                    if (!TextUtils.isEmpty(bean.doc.schema)) {
                        Uri uri = Uri.parse(bean.doc.schema);
                        IntentUtils.toActivityFromUri(ClubPostListActivity.this, uri,view);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mListPost.setLayoutManager(linearLayoutManager);
        mListPost.isLoadMoreEnabled(true);
        mListPost.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
                mListPost.isLoadMoreEnabled(true);
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

        mTags[0] = (TagTextView)findViewById(R.id.iv_group_tag1);
        mTags[1] = (TagTextView)findViewById(R.id.iv_group_tag2);
        mTags[2] = (TagTextView)findViewById(R.id.iv_group_tag3);
        mTags[3] = (TagTextView)findViewById(R.id.iv_group_tag4);
        mTags[4] = (TagTextView)findViewById(R.id.iv_group_tag5);
        if(mClubData != null){
            bindClubViewData();
        }
        loadDataFromDb();
        mListPost.initLoad();
        allFinishCallback = new MoeMoeCallback() {
            AtomicInteger count = new AtomicInteger(0);
            @Override
            public void onSuccess() {
                int curSize = count.incrementAndGet();
                if(curSize == 3 && mIsPullDown){
                    mListPost.setComplete();
                    mIsLoading = false;
                    count.set(0);
                }
                if(!mIsPullDown){
                    mListPost.setComplete();
                    mIsLoading = false;
                    count.set(0);
                }
            }

            @Override
            public void onFailure() {

            }
        };
    }

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mIvSendDoc,"translationY",mIvSendDoc.getHeight()+DensityUtil.dip2px(10),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicIn = ObjectAnimator.ofFloat(mIvSendMusicDoc,"translationY",mIvSendMusicDoc.getHeight()+DensityUtil.dip2px(10),0).setDuration(300);
        sendMusicIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn).with(sendMusicIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mIvSendDoc,"translationY",0,mIvSendDoc.getHeight()+DensityUtil.dip2px(10)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicOut = ObjectAnimator.ofFloat(mIvSendMusicDoc,"translationY",0,mIvSendMusicDoc.getHeight()+DensityUtil.dip2px(10)).setDuration(300);
        sendMusicOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut).with(sendMusicOut);
        set.start();
    }

    private void requestClubData(){
        Otaku.getDocV2().requestTagNode(mPreferMng.getToken(),mClubUuid).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                if (mClubData == null) {
                    mClubData = new TagNode();
                }
                try {
                    mClubData.readFromJson(ClubPostListActivity.this,s);
                    clubBean.uuid = mClubUuid;
                    clubBean.tagJson = s;
                    db.saveOrUpdate(clubBean);
                    mTagName = mClubData.name;
                    mDocNum.setText(getString(R.string.label_doc_num, mClubData.docNum));
                    mLikesNum.setText(getString(R.string.label_like_num, mClubData.commentNum));
                    bindClubViewData();
                    requestTopList();
                    requestDocList(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String e) {
                NetworkUtils.checkNetworkAndShowError(ClubPostListActivity.this);
                //ToastUtil.showToast(ClubPostListActivity.this, R.string.msg_refresh_fail);
            }
        }));
    }

    private class UpdateTask extends AsyncTask<Void,Void,Void> {

        public UpdateTask(boolean IsPullDown){
            mIsPullDown = IsPullDown;
        }


        @Override
        protected Void doInBackground(Void... params) {
            mIsLoading = true;
            if (mIsPullDown) {
                mDocAdapter.clearTopAndHot();
                requestClubData();
                mIsHasLoadedAll = false;
            }else {
                requestDocList(mDocAdapter.getItemCount());
            }
            return null;
        }
    }

    private void loadDataFromDb(){
        try {
            ClubDbbean clubBean = db.selector(ClubDbbean.class)
                    .where("uuid", "=", mClubUuid)
                    .findFirst();
            if(clubBean != null && clubBean.tagJson != null){
                mClubData = new TagNode();
                mClubData.readFromJson(ClubPostListActivity.this, clubBean.tagJson);
                bindClubViewData();
                if(clubBean.docsJson != null){
                    ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(ClubPostListActivity.this, clubBean.docsJson);
                    mDocAdapter.setData(datas);
                }
                if(clubBean.topJson != null){
                    ArrayList<DocItemBean> top = DocItemBean.readFromJsonList(ClubPostListActivity.this,clubBean.topJson);
                    mDocAdapter.setTopData(top, top.size());
                }
                if(clubBean.hotJson != null){
                    ArrayList<DocItemBean> hot = DocItemBean.readFromJsonList(ClubPostListActivity.this,clubBean.hotJson);
                    mDocAdapter.setHotData(hot);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void requestTopList(){
        mDocTop.clear();
        mDocHot.clear();
        Otaku.getDocV2().requestTopTagDocList(mPreferMng.getToken(),mTagName).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(ClubPostListActivity.this, s);
                clubBean.uuid = mClubUuid;
                clubBean.topJson = s;
                try {
                    db.saveOrUpdate(clubBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mDocTop.addAll(datas);
                mSplitNum = mDocTop.size();
                mDocAdapter.setTopData(mDocTop, mSplitNum);
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
            }
        }));
        Otaku.getDocV2().requestHotTagDocList(mPreferMng.getToken(),mTagName).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(ClubPostListActivity.this, s);
                clubBean.uuid = mClubUuid;
                clubBean.hotJson = s;
                try {
                    db.saveOrUpdate(clubBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mDocHot.addAll(datas);
                mDocAdapter.setHotData(mDocHot);
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
            }
        }));
    }

    private void requestDocList(final int index){
        Otaku.getDocV2().requestTagDocList(mPreferMng.getToken(),index, Otaku.LENGTH, mTagName).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(ClubPostListActivity.this, s);
                clubBean.uuid = mClubUuid;
                clubBean.docsJson = s;
                try {
                    db.saveOrUpdate(clubBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (index == 0) {
                    mDocData.clear();
                    mDocAdapter.setData(datas);
                }else {
                    mDocAdapter.addData(datas);
                }
                mDocData.addAll(datas);

                if(index != 0){
                    if (datas.size() >= Otaku.LENGTH) {
                        mListPost.isLoadMoreEnabled(true);
                    } else {
                        mListPost.isLoadMoreEnabled(false);
                        mIsHasLoadedAll = true;
                    }
                }
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
                NetworkUtils.checkNetworkAndShowError(ClubPostListActivity.this);
               // ToastUtil.showToast(ClubPostListActivity.this, R.string.msg_refresh_fail);
            }
        }));
    }

    /**
     * 数据更新时，更新view中数据内容
     */
    private void bindClubViewData(){
        if(mClubData != null){
            mHeadRoot.setVisibility(View.VISIBLE);
            mRlEmptyDocLoading.setVisibility(View.GONE);
            mTvGroupTitle.setText(mClubData.name);
            mTvBrief.setVisibility(View.GONE);
            if(mClubData.icon != null && !mHasLoadClub){
                // 这个页面的社团头像只会加载一次
                Picasso.with(this)
                        .load(StringUtils.getUrl(this,mClubData.icon.path,DensityUtil.dip2px(80),DensityUtil.dip2px(80),false,false))
                        .resize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
                        .placeholder(R.drawable.ic_default_club_l)
                        .error(R.drawable.ic_default_club_l)
                        .into(mIvIcon);
                mHasLoadClub = true;
            }
            ArrayList<String> tags = new ArrayList<>();
            Collections.addAll(tags,mClubData.texts);
            mDocLabel.setLabels(tags,ClubPostListActivity.this);

            if (mClubData.bg != null) {
                if (!mIsLoadedBackground) {
                    Picasso.with(this)
                            .load(StringUtils.getUrl(this, mClubData.bg.path, DensityUtil.getScreenWidth(), DensityUtil.dip2px(175), false, false))
                            .fit()
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
            mTvTitle.setText(mClubData.name);
        }else {
            mTvTitle.setText("");
            mRlEmptyDocLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(int type){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(this)){
                Intent intent = new Intent(ClubPostListActivity.this, CreateNormalDocActivity.class);
                intent.putExtra(CreateNormalDocActivity.EXTRA_KEY_UUID, mClubUuid);
                intent.putExtra(CreateNormalDocActivity.TYPE_CREATE,type);
                intent.putExtra(CreateNormalDocActivity.TYPE_TAG_NAME_DEFAULT,mTagName);
                startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            mRecyclerView.scrollToPosition(0);
            requestTopList();
            requestDocList(0);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if(verticalOffset == 0 ){
            mSwipeRefreshLayout.setEnabled(true);
        }else if(verticalOffset < - DensityUtil.dip2px(127)){
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
        int newAlpha;
        if (verticalOffset != 0){
             newAlpha = Math.min(255,Math.abs(verticalOffset));
        }else {
            newAlpha = 0;
        }
        mTvTitle.setTextColor(Color.argb(newAlpha, 255, 255, 255));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }
}
