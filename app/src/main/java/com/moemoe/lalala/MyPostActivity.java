package com.moemoe.lalala;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.moemoe.lalala.adapter.DocListAdapter;
import com.moemoe.lalala.data.ClubDbbean;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
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
@ContentView(R.layout.ac_my_post)
public class MyPostActivity extends BaseActivity{
    public static final String TAG = "MyPostActivity";
    public static final String EXTRA_USER_ID = "user_id";

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTitle;
    @FindView(R.id.list_club_docs)
    private PullAndLoadView mListPost;
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
        Picasso.with(this)
                .cancelTag(TAG);
    }

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        if(mIntent != null){
            mUserId = mIntent.getStringExtra(EXTRA_USER_ID);
        }
        if(!mUserId.equals(mPreferMng.getUUid())){
            mTitle.setText(R.string.label_friend_post);
        }else {
            mTitle.setText(R.string.label_my_post);
        }
        mSwipeRefreshLayout = mListPost.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListPost.getRecyclerView();
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
                        IntentUtils.toActivityFromUri(MyPostActivity.this, uri, view);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(MyPostActivity.this).resumeTag(TAG);
                } else {
                    Picasso.with(MyPostActivity.this).pauseTag(TAG);
                }
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
        loadDataFromDb();
        mListPost.initLoad();
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
        Otaku.getDocV2().requestMyTagDocList(mPreferMng.getToken(), index, Otaku.LENGTH, mUserId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(MyPostActivity.this, s);
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
                        mListPost.isLoadMoreEnabled(true);
                    } else {
                        mListPost.isLoadMoreEnabled(false);
                        mIsHasLoadedAll = true;
                    }
                }
                mListPost.setComplete();
                mIsLoading = false;
            }

            @Override
            public void failure(String e) {
                mListPost.setComplete();
                mIsLoading = false;
                //NetworkUtils.checkNetworkAndShowError(MyPostActivity.this);
                ToastUtil.showToast(MyPostActivity.this, R.string.msg_refresh_fail);
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
                    ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(MyPostActivity.this, clubBean.docsJson);
                    mDocAdapter.setData(datas);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
