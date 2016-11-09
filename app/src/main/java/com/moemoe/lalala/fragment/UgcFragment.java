package com.moemoe.lalala.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.R;
import com.moemoe.lalala.adapter.DocListAdapter;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/23.
 */
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class UgcFragment extends BaseFragment {
    public static final String TAG = "UgcFragment";

    @FindView(R.id.list)
    private PullAndLoadView mListPost;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DocListAdapter mDocAdapter;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private ArrayList<DocItemBean> mDocData = new ArrayList<>();
    private String uuid;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getActivity())
                .cancelTag(TAG);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uuid = getArguments().getString("uuid");
        mSwipeRefreshLayout = mListPost.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListPost.getRecyclerView();
        mDocAdapter = new DocListAdapter(getActivity(),DocListAdapter.TYPE_CLUB_DOC,true,TAG);
        mRecyclerView.setAdapter(mDocAdapter);
        mDocAdapter.setOnItemClickListener(new DocListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mDocAdapter.getItem(position);
                if (object != null && object instanceof DocItemBean) {
                    DocItemBean bean = (DocItemBean) object;
                    if (!TextUtils.isEmpty(bean.doc.schema)) {
                        Uri uri = Uri.parse(bean.doc.schema);
                        IntentUtils.toActivityFromUri(getActivity(), uri, view);
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
                    Picasso.with(getActivity()).resumeTag(TAG);
                } else {
                    Picasso.with(getActivity()).pauseTag(TAG);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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
        if(!NetworkUtils.checkNetworkAndShowError(getActivity())){
            return;
        }
        Otaku.getDocV2().requestFavoriteDocListUgc(PreferenceManager.getInstance(getActivity()).getToken(),uuid, index, Otaku.LENGTH).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> datas = DocItemBean.readFromJsonList(getActivity(), s);
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
                ToastUtil.showToast(getActivity(), R.string.msg_refresh_fail);
            }
        }));
    }
}
