package com.moemoe.lalala.view.fragment;

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
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerDiscoveryComponent;
import com.moemoe.lalala.di.modules.DiscoveryModule;
import com.moemoe.lalala.di.modules.FollowMainModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.DiscovertMainContract;
import com.moemoe.lalala.presenter.DiscoveryMainPresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.ClassRecyclerViewAdapter;
import com.moemoe.lalala.view.adapter.DiscoveryMainAdapter;
import com.moemoe.lalala.view.adapter.FollowMainAdapter;
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
 * Created by yi on 2016/12/15.
 */

public class DiscoveryMainFragment extends BaseFragment  implements DiscovertMainContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.ll_not_show)
    View mLlShow;
    @Inject
    DiscoveryMainPresenter mPresenter;
    private DiscoveryMainAdapter mAdapter;
    private boolean isLoading = false;
    private Banner banner;
    private View bannerView;
    private View featuredView;
    private View xianChongView;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static DiscoveryMainFragment newInstance(){
        DiscoveryMainFragment fragment = new DiscoveryMainFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        DaggerDiscoveryComponent.builder()
                .discoveryModule(new DiscoveryModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.setVisibility(View.VISIBLE);
        mLlShow.setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new DiscoveryMainAdapter(null);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewDocListEntity entity = mAdapter.getItem(position);
                String schema = "";
                if("DOC".equals(entity.getDetail().getType())){
                    NewDocListEntity.Doc doc = (NewDocListEntity.Doc) entity.getDetail().getTrueData();
                    schema = doc.getSchema();
                }else if("FOLLOW_DEPARTMENT".equals(entity.getDetail().getType()) || "FOLLOW_BROADCAST".equals(entity.getDetail().getType())){
                    NewDocListEntity.FollowDepartment doc = (NewDocListEntity.FollowDepartment) entity.getDetail().getTrueData();
                    schema = doc.getSchema();
                }
                if (!TextUtils.isEmpty(schema)) {
                    if(schema.contains(getString(R.string.label_doc_path)) && !schema.contains("uuid")){
                        String begin = schema.substring(0,schema.indexOf("?") + 1);
                        String uuid = schema.substring(schema.indexOf("?") + 1);
                        schema = begin + "uuid=" + uuid + "&from_name=发现";
                    }
                    Uri uri = Uri.parse(schema);
                    IntentUtils.toActivityFromUri(getActivity(), uri,view);
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
                mPresenter.loadDocList(mAdapter.getList().get(mAdapter.getList().size() - 1).getTime(),false,false);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.requestBannerData("");
                mPresenter.requestFeatured("");
                mPresenter.loadDocList(0,false,true);
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
        mPresenter.requestBannerData("");
        mPresenter.requestFeatured("");
        mPresenter.loadXianChongList();
        mPresenter.loadDocList(0,false,true);
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    @Override
    public void onDestroyView() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }

    public void changeLabelAdapter(){
        mPresenter.loadDocList(0,true,true);
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
    public void onLoadXianChongSuccess(final ArrayList<XianChongEntity> entities) {
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
    public void onChangeSuccess(ArrayList<NewDocListEntity> entities) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
        }
        Gson gson = new Gson();
        for (NewDocListEntity detail : entities){
            if(detail.getDetail().getType().equals("DOC")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.Doc.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLDER")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowFolder.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_COMMENT")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowComment.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLLOW")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowUser.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_BROADCAST")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowDepartment.class));
            }
        }
        mAdapter.setList(entities);
    }

    @Override
    public void onLoadDocListSuccess(ArrayList<NewDocListEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
        }
        Gson gson = new Gson();
        for (NewDocListEntity detail : entities){
            if(detail.getDetail().getType().equals("DOC")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.Doc.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLDER")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowFolder.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_COMMENT")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowComment.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_USER_FOLLOW")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowUser.class));
            }else if(detail.getDetail().getType().equals("FOLLOW_BROADCAST")){
                detail.getDetail().setTrueData(gson.fromJson(detail.getDetail().getData(),NewDocListEntity.FollowDepartment.class));
            }
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }
}
