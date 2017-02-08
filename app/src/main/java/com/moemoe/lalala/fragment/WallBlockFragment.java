package com.moemoe.lalala.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moemoe.lalala.MoemoeApplication;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.WallBlock;
import com.moemoe.lalala.data.WallBlockDb;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DraggableLayout;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.moemoe.lalala.view.scroll.PullAndLoadLayout;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/23.
 */
@ContentView(R.layout.frag_wall)
public class WallBlockFragment extends BaseFragment {

    private DraggableLayout mFsdLayout;
    @ViewInject(R.id.scroll_root)
    private PullAndLoadLayout mFuturePv;
    private ArrayList<WallBlock> mWallBlocks;
    private WallBlockDb wallDbBean = new WallBlockDb();
    private DbManager db;
    private boolean mIsLoading = false;
    private boolean mIsHasLoadedAll = false;
    private int mCurPage = 1;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = x.getDb(MoemoeApplication.sDaoConfig);
        mWallBlocks = new ArrayList<>();
        SwipeRefreshLayout swipeRefreshLayout = mFuturePv.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mFsdLayout = mFuturePv.getFsdLayout();
        mFsdLayout.setItemClickListener(new DraggableLayout.DragItemClickListener() {
            @Override
            public void itemClick(View v,int position, WallBlock wallBlock) {
                if (!TextUtils.isEmpty(wallBlock.getSchema())) {
                    Uri uri = Uri.parse(wallBlock.getSchema());
                    IntentUtils.toActivityFromUri(getActivity(), uri,v);
                }
            }
        });
        mFuturePv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                mCurPage = 1;
                new UpdateTask(true).execute();
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
        mFuturePv.initLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void loadDataFromDb(){
        try {
            WallBlockDb wallblock = db.selector(WallBlockDb.class)
                    .where("uuid", "=", "cache")
                    .findFirst();
            if(wallblock != null){
                if(wallblock.wallJson != null){
                    Gson gson = new Gson();
                    ArrayList<WallBlock> datas = gson.fromJson(wallblock.wallJson,new TypeToken<ArrayList<WallBlock>>(){}.getType());
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
                requestWallData(1);
            }else {
                requestWallData(mCurPage);
            }
            return null;
        }
    }

    private void requestWallData(final int page){
        if(!NetworkUtils.checkNetworkAndShowError(getActivity())){
            return;
        }
        Otaku.getCommonV2().getWallBlocksV2(page, new OneParameterCallback<ArrayList<WallBlock>>() {
            @Override
            public void action(ArrayList<WallBlock> wallBlocks) {
                if(page == 1){
                    mWallBlocks.clear();
                }
                if(wallBlocks.size() == 0){
                    ToastUtil.showCenterToast(getActivity(),R.string.msg_all_load_down);
                }else {
                    mFuturePv.isLoadMoreEnabled(true);
                }
                mWallBlocks.addAll(wallBlocks);
                wallDbBean.uuid = "cache";
                Gson gson = new Gson();
                wallDbBean.wallJson = gson.toJson(wallBlocks);
                try {
                    db.saveOrUpdate(wallDbBean);

                } catch (DbException e) {
                    e.printStackTrace();
                }
                mFsdLayout.setDragList(getActivity(),mWallBlocks);
                mCurPage++;
                mFuturePv.setComplete();
                mIsLoading = false;
            }
        }, new OneParameterCallback<Integer>() {
            @Override
            public void action(Integer integer) {
                mFuturePv.setComplete();
                mIsLoading = false;
            }
        });
    }
}
