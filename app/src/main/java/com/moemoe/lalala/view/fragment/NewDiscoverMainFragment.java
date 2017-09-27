package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedComponent;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.FeedContract;
import com.moemoe.lalala.presenter.FeedPresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
import com.moemoe.lalala.view.adapter.ClassRecyclerViewAdapter;
import com.moemoe.lalala.view.adapter.FeedAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.XianChongListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class NewDiscoverMainFragment extends BaseFragment implements FeedContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    FeedPresenter mPresenter;

    private FeedAdapter mAdapter;
    private boolean isLoading = false;
    private Banner banner;
    private View bannerView;
    private View featuredView;
    private View xianChongView;

    public static NewDiscoverMainFragment newInstance(String type){
        NewDiscoverMainFragment fragment = new NewDiscoverMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedComponent.builder()
                .feedModule(new FeedModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String type = getArguments().getString("type");
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new FeedAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DynamicActivity.startActivity(getContext(),mAdapter.getItem(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadList(mAdapter.getItem(mAdapter.getItemCount() - 1).getTimestamp(),type);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadList(0,type);
                mPresenter.requestBannerData("CLASSROOM");
                mPresenter.requestFeatured("");
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
        mPresenter.requestBannerData("CLASSROOM");
        mPresenter.requestFeatured("");
        mPresenter.loadXianChongList();
        mPresenter.loadList(0,type);
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
    public void onLoadListSuccess(ArrayList<NewDynamicEntity> resList, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (resList.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(resList);
        }else {
            mAdapter.addList(resList);
        }
    }

    @Override
    public void onBannerLoadSuccess(final ArrayList<BannerEntity> bannerEntities) {
        if(bannerEntities.size() > 0){
            if(bannerView == null){
                bannerView = LayoutInflater.from(getContext()).inflate(R.layout.item_new_banner, null);
                banner = (Banner) bannerView.findViewById(R.id.banner);
                mAdapter.addHeaderView(bannerView,0);
            }
            banner.setImages(bannerEntities)
                    .setImageLoader(new BannerImageLoader())
                    .setDelayTime(2000)
                    .setIndicatorGravity(BannerConfig.CENTER)
                    .start();
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    BannerEntity bean = bannerEntities.get(position);
                    if(!TextUtils.isEmpty(bean.getSchema())){
                        Uri uri = Uri.parse(bean.getSchema());
                        IntentUtils.toActivityFromUri(getContext(), uri,null);
                    }
                }
            });
        }else {
            if(bannerView != null){
                mAdapter.removeHeaderView(bannerView);
                bannerView = null;
            }
        }
    }

    @Override
    public void onFeaturedLoadSuccess(final ArrayList<FeaturedEntity> featuredEntities) {
        if(featuredEntities.size() > 0){
            if(featuredView == null){
                featuredView  = LayoutInflater.from(getContext()).inflate(R.layout.item_class_featured, null);
                int count = mAdapter.getHeaderViewCount();
                if(count == 0){
                    mAdapter.addHeaderView(featuredView);
                }else if(count == 1){
                    View v = mAdapter.getmHeaderLayout().getChildAt(0);
                    if(v == bannerView){
                        mAdapter.addHeaderView(featuredView);
                    }else {
                        mAdapter.addHeaderView(featuredView,0);
                    }
                }else if(count == 2){
                    mAdapter.addHeaderView(featuredView,1);
                }
            }
            RecyclerView rvList = (RecyclerView) featuredView.findViewById(R.id.rv_class_featured);
            rvList.setBackgroundColor(Color.WHITE);
            ClassRecyclerViewAdapter recyclerViewAdapter = new ClassRecyclerViewAdapter(getContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvList.setLayoutManager(layoutManager);
            rvList.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    FeaturedEntity docBean = featuredEntities.get(position);
                    if(!TextUtils.isEmpty(docBean.getSchema())){
                        Uri uri = Uri.parse(docBean.getSchema());
                        IntentUtils.toActivityFromUri(getContext(),uri,view);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            recyclerViewAdapter.setData(featuredEntities);
        }else {
            if(featuredView != null){
                mAdapter.removeHeaderView(featuredView);
                featuredView = null;
            }
        }
    }

    @Override
    public void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities) {
        if(entities.size() > 0){
            if(xianChongView == null){
                xianChongView  = LayoutInflater.from(getContext()).inflate(R.layout.item_class_featured, null);
                mAdapter.addHeaderView(xianChongView);
            }
            RecyclerView rvList = (RecyclerView) xianChongView.findViewById(R.id.rv_class_featured);
            rvList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 90)));
            rvList.setBackgroundColor(Color.WHITE);
            final XianChongListAdapter recyclerViewAdapter = new XianChongListAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvList.setLayoutManager(layoutManager);
            TextView text  = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_text, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            lp.leftMargin = DensityUtil.dip2px(getContext(),10);
            lp.rightMargin = DensityUtil.dip2px(getContext(),6);
            text.setLayoutParams(lp);
            recyclerViewAdapter.addHeaderView(text,-1,LinearLayout.HORIZONTAL);
            rvList.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    XianChongEntity entity = recyclerViewAdapter.getItem(position);
                    ViewUtils.toPersonal(getContext(),entity.getUserId());
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            recyclerViewAdapter.setList(entities);
        }else {
            if(xianChongView != null){
                mAdapter.removeHeaderView(xianChongView);
                xianChongView = null;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
