package com.moemoe.lalala;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.moemoe.lalala.adapter.DocListAdapter;
import com.moemoe.lalala.data.ClubDbbean;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/28 0028.
 */
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class QiuMingShanActivity extends BaseActivity{
    public static final String TAG = "QiuMingshanActivity";
    public static final String EXTRA_USER_ID = "user_id";
    public static final int REQUEST_CODE_CREATE_DOC = 2333;

    @FindView(R.id.rl_bar)
    private View mRlRoot;
    @FindView(R.id.tv_title)
    private TextView mTitle;
    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.list)
    private PullAndLoadView mListDocs;
    @FindView(R.id.iv_send_post)
    private View mSendPost;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DocListAdapter mDocAdapter;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private DbManager db;
    private ClubDbbean clubBean = new ClubDbbean();
    private String mUserId;

    private ArrayList<DocItemBean> mDocData = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelTag(TAG);
    }

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mRlRoot.setVisibility(View.VISIBLE);
        mRlRoot.getBackground().mutate().setAlpha(0);
        mTitle.setText("秋名山");
        mTitle.setTextColor(Color.argb(0, 255, 255, 255));
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        if(mIntent != null){
            mUserId = mIntent.getStringExtra(EXTRA_USER_ID);
        }
//        if(!mUserId.equals(mPreferMng.getUUid())){
//            mTitle.setText(R.string.label_friend_post);
//        }else {
//            mTitle.setText(R.string.label_my_post);
//        }
        mSwipeRefreshLayout = mListDocs.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListDocs.getRecyclerView();
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
                        IntentUtils.toActivityFromUri(QiuMingShanActivity.this, uri, view);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mSendPost.setVisibility(View.VISIBLE);
        mSendPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_IMG_DOC);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int curY = 0;
            boolean isChange = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(QiuMingShanActivity.this).resumeTag(TAG);
                } else {
                    Picasso.with(QiuMingShanActivity.this).pauseTag(TAG);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                curY += dy;
                if (isChange) {
                    if (dy > 10) {
                        sendBtnOut();
                        isChange = false;
                    }
                } else {
                    if (dy < -10) {
                        sendBtnIn();
                        isChange = true;
                    }
                }
                toolBarAlpha(curY);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mListDocs.setLayoutManager(linearLayoutManager);
        mListDocs.isLoadMoreEnabled(true);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
                mListDocs.isLoadMoreEnabled(true);
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
        loadDataFromDb();
        mListDocs.initLoad();
    }

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(int type){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(QiuMingShanActivity.this)){
            Intent intent = new Intent(QiuMingShanActivity.this, CreateNormalDocActivity.class);
            intent.putExtra(CreateNormalDocActivity.TYPE_CREATE,type);
            intent.putExtra(CreateNormalDocActivity.TYPE_QIU_MING_SHAN,true);
            startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
        }
    }

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mSendPost,"translationY",mSendPost.getHeight()+ DensityUtil.dip2px(10),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
//        ObjectAnimator sendMusicIn = ObjectAnimator.ofFloat(mSendMusicPost,"translationY",mSendMusicPost.getHeight()+DensityUtil.dip2px(10),0).setDuration(300);
//        sendMusicIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mSendPost,"translationY",0,mSendPost.getHeight()+DensityUtil.dip2px(10)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
//        ObjectAnimator sendMusicOut = ObjectAnimator.ofFloat(mSendMusicPost,"translationY",0,mSendMusicPost.getHeight()+DensityUtil.dip2px(10)).setDuration(300);
//        sendMusicOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut);
        set.start();
    }

    public void toolBarAlpha(int curY) {
        int startOffset = 0;
        int endOffset = mRlRoot.getHeight();
        //curY -= CommonUtils.getStatusHeight(this);
        if (Math.abs(curY) <= startOffset) {
            mRlRoot.getBackground().mutate().setAlpha(0);
            mTitle.setTextColor(Color.argb(0, 255, 255, 255));
        } else if (Math.abs(curY) > startOffset && Math.abs(curY) < endOffset) {
            float precent = (float) (Math.abs(curY) - startOffset) / endOffset;
            int alpha = Math.round(precent * 255);
            mRlRoot.getBackground().mutate().setAlpha(alpha);
            mTitle.setTextColor(Color.argb(alpha, 255, 255, 255));
        } else if (Math.abs(curY) >= endOffset) {
            mRlRoot.getBackground().mutate().setAlpha(255);
            mTitle.setTextColor(Color.argb(255, 255, 255, 255));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            mRecyclerView.scrollToPosition(0);
            requestDocList(0);
        }
    }

    private class UpdateTask extends AsyncTask<Void,Void,Void> {

        private boolean mIsPullDown;

        public UpdateTask(boolean IsPullDown){
            this.mIsPullDown = IsPullDown;
        }


        @Override
        protected Void doInBackground(Void... params) {
            mIsLoading = true;
            if (mIsPullDown) {
                requestDocList(0);
                mIsHasLoadedAll = false;
            }else {
                requestDocList(mDocAdapter.getItemCount());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private void requestDocList(final int index){
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        Otaku.getDocV2().requestQiuMingShanDocList(mPreferMng.getToken(), index, Otaku.LENGTH).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(QiuMingShanActivity.this, s);
                clubBean.uuid = mPreferMng.getUUid();
                clubBean.docsJson = s;
                try {
                    db.saveOrUpdate(clubBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (index == 0) {
                    mDocData.clear();
                    mDocAdapter.setData(datas);
                } else {
                    mDocAdapter.addData(datas);
                }
                mDocData.addAll(datas);

                if (index != 0) {
                    if (datas.size() >= Otaku.LENGTH) {
                        mListDocs.isLoadMoreEnabled(true);
                    } else {
                        mListDocs.isLoadMoreEnabled(false);
                        mIsHasLoadedAll = true;
                    }
                }
                mListDocs.setComplete();
                mIsLoading = false;
            }

            @Override
            public void failure(String e) {
                mListDocs.setComplete();
                mIsLoading = false;
                //NetworkUtils.checkNetworkAndShowError(MyPostActivity.this);
                ToastUtil.showToast(QiuMingShanActivity.this, R.string.msg_refresh_fail);
            }
        }));
    }

    private void loadDataFromDb(){
        try {
            ClubDbbean clubBean = db.selector(ClubDbbean.class)
                    .where("uuid", "=", mPreferMng.getUUid())
                    .findFirst();
            if(clubBean != null){
                if(clubBean.docsJson != null){
                    ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(QiuMingShanActivity.this, clubBean.docsJson);
                    mDocAdapter.setData(datas);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
