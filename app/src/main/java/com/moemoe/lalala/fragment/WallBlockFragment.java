package com.moemoe.lalala.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.moemoe.lalala.MoemoeApplication;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.WallBlock;
import com.moemoe.lalala.data.WallBlockDb;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.view.DraggableLayout;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.moemoe.lalala.view.scroll.PullAndLoadLayout;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/23.
 */
@ContentView(R.layout.frag_wall)
public class WallBlockFragment extends BaseFragment {

    private DraggableLayout mFsdLayout;
    @FindView(R.id.scroll_root)
    private PullAndLoadLayout mFuturePv;
    private ArrayList<WallBlock> mWallBlocks;
    private WallBlockDb wallDbBean = new WallBlockDb();
    private DbManager db;
    private boolean mIsLoading = false;
    private boolean mIsHasLoadedAll = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mWallBlocks = new ArrayList<>();
        SwipeRefreshLayout swipeRefreshLayout = mFuturePv.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mFsdLayout = mFuturePv.getFsdLayout();
        mFsdLayout.setItemClickListener(new DraggableLayout.DragItemClickListener() {
            @Override
            public void itemClick(View v,int position, WallBlock wallBlock) {
                if (!TextUtils.isEmpty(wallBlock.schema)) {
                    Uri uri = Uri.parse(wallBlock.schema);
                    IntentUtils.toActivityFromUri(getActivity(), uri,v);
                }
            }
        });
        mFuturePv.isLoadMoreEnabled(true);
        mFuturePv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
                mIsHasLoadedAll = false;
                mFuturePv.isLoadMoreEnabled(true);
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
        mFuturePv.initLoad();
    }

    private void loadDataFromDb(){
        try {
            WallBlockDb wallblock = db.selector(WallBlockDb.class)
                    .where("uuid", "=", "cache")
                    .findFirst();
            if(wallblock != null){
                if(wallblock.wallJson != null){
                    ArrayList<WallBlock> datas = WallBlock.readFromJsonArray(wallblock.wallJson);
                    mFsdLayout.setDragList(getActivity(), datas);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
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
                requestWallData(0);
            }else {
                requestWallData(mWallBlocks.size());
            }
            return null;
        }
    }

    private void checkData(ArrayList<WallBlock> wallBlocks){
        for(WallBlock wallBlock : wallBlocks){
            for(WallBlock wallBlock1 : mWallBlocks){
                if(wallBlock.id.equals(wallBlock1.id)){
                    wallBlocks.remove(wallBlock);
                    break;
                }
            }
        }
    }

    private void requestWallData(final int index){
        Otaku.getCommonV2().getWallBlocks(index,Otaku.LENGTH).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<WallBlock> wallBlocks = WallBlock.readFromJsonArray(s);
                if(index == 0){
                    mWallBlocks.clear();
                }
                if (wallBlocks.size() < Otaku.LENGTH){
                    mFuturePv.isLoadMoreEnabled(false);
                    mIsHasLoadedAll = true;
                }
                mWallBlocks.addAll(wallBlocks);
                wallDbBean.uuid = "cache";
                wallDbBean.wallJson = s;
                try {
                    db.saveOrUpdate(wallDbBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mFsdLayout.setDragList(getActivity(),mWallBlocks);
                mFuturePv.setComplete();
                mIsLoading = false;
            }

            @Override
            public void failure(String e) {
                mFuturePv.setComplete();
                mIsLoading = false;
            }
        }));
    }
}
