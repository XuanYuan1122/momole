package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedBagComponent;
import com.moemoe.lalala.di.modules.FeedBagModule;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.FeedBagContract;
import com.moemoe.lalala.presenter.FeedBagPresenter;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FolderFeedDecoration;
import com.moemoe.lalala.utils.FolderFeedTopDecoration;
import com.moemoe.lalala.utils.FolderVDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.FilesUploadActivity;
import com.moemoe.lalala.view.activity.NewBagActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.NewFolderActivity;
import com.moemoe.lalala.view.activity.NewFolderEditActivity;
import com.moemoe.lalala.view.adapter.BagHotAdapter;
import com.moemoe.lalala.view.adapter.FeedBagAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class FeedBagFragment extends BaseFragment implements FeedBagContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_to_wen)
    ImageView mIvCreateFolder;

    @Inject
    FeedBagPresenter mPresenter;

    private BottomMenuFragment fragment;
    private FeedBagAdapter mAdapter;
    private boolean isLoading = false;
    private View folderView;
    private boolean loadFolder;
    private int mHotIndex;

    public static FeedBagFragment newInstance(){
        return new FeedBagFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedBagComponent.builder()
                .feedBagModule(new FeedBagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mIvCreateFolder.setVisibility(View.VISIBLE);
        mIvCreateFolder.setImageResource(R.drawable.btn_feed_create_bag);
        fragment = new BottomMenuFragment();
        initMenu();
        mIvCreateFolder.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                fragment.show(getChildFragmentManager(),"FeedBag");
            }
        });
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setLayoutManager(new GridLayoutManager(getContext(),2));
        mListDocs.getRecyclerView().addItemDecoration(new FolderFeedDecoration());
        mAdapter = new FeedBagAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setBackgroundColor(Color.WHITE);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShowFolderEntity entity = mAdapter.getItem(position);
                if(entity.getType().equals(FolderType.ZH.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.TJ.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.MH.toString())){
                    NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.XS.toString())){
                    NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadFeedBagList(mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadHotBag(mHotIndex);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.loadHotBag(mHotIndex);
    }

    private void initMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem item = new MenuItem(1,"综合");
        items.add(item);
        item = new MenuItem(2, "漫画");
        items.add(item);
        item = new MenuItem(3,"图集");
        items.add(item);
        item = new MenuItem(4,"小说");
        items.add(item);

        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                String mFolderType = FolderType.ZH.toString();
                if (itemId == 1) {
                    mFolderType = FolderType.ZH.toString();
                } else if(itemId == 2){
                    mFolderType = FolderType.MH.toString();
                } else if(itemId == 3){
                    mFolderType = FolderType.TJ.toString();
                }else if(itemId == 4) {
                    mFolderType = FolderType.XS.toString();
                }
                NewFolderEditActivity.startActivity(getContext(),"create",mFolderType,null);
            }
        });

    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onLoadHotBagSuccess(ArrayList<ShowFolderEntity> entities) {
        if(entities.size() > 0){
            mHotIndex += 3;
            if(folderView == null){
                folderView = LayoutInflater.from(getContext()).inflate(R.layout.item_hot_bag, null);
                mAdapter.addHeaderView(folderView,0);
            }
            TextView refresh = folderView.findViewById(R.id.tv_refresh);
            refresh.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPresenter.loadHotBag(mHotIndex);
                }
            });
            folderView.findViewById(R.id.tv_my_bag).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(DialogUtils.checkLoginAndShowDlg(getContext())){
                        Intent i2 = new Intent(getContext(),NewBagActivity.class);
                        i2.putExtra("uuid", PreferenceUtils.getUUid());
                        startActivity(i2);
                    }
                }
            });

            RecyclerView rv =  folderView.findViewById(R.id.rv_list);
            LinearLayoutManager m = new LinearLayoutManager(getContext());
            m.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv.setLayoutManager(m);
            if(!loadFolder){
                rv.addItemDecoration(new FolderFeedTopDecoration());
                loadFolder = true;
            }
            final BagHotAdapter mTopAdapter = new BagHotAdapter();
            rv.setAdapter(mTopAdapter);
            mTopAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ShowFolderEntity entity = mTopAdapter.getItem(position);
                    if(entity.getType().equals(FolderType.ZH.toString())){
                        NewFileCommonActivity.startActivity(getContext(),FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                    }else if(entity.getType().equals(FolderType.TJ.toString())){
                        NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                    }else if(entity.getType().equals(FolderType.MH.toString())){
                        NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                    }else if(entity.getType().equals(FolderType.XS.toString())){
                        NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            mTopAdapter.setList(entities);
        }else {
            if(folderView != null){
                mAdapter.removeHeaderView(folderView);
                folderView = null;
            }
        }
        mPresenter.loadFeedBagList(0);
    }

    @Override
    public void onLoadFeedBagListSuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }
}
