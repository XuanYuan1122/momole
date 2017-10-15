package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedComponent;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.FeedContract;
import com.moemoe.lalala.presenter.FeedPresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FolderVDecoration;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.Comment24ListActivity;
import com.moemoe.lalala.view.activity.CommentListActivity;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.WallBlockActivity;
import com.moemoe.lalala.view.adapter.BagCollectionTopAdapter;
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
import jp.wasabeef.glide.transformations.CropSquareTransformation;

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
    private View xianChongView;
    private View folderView;
    private View commentView;
    private boolean loadFolder;

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
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FeedAdapter();
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
                mPresenter.loadList(mAdapter.getList().size(),type,"");
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadList(0,type,"");
                mPresenter.requestBannerData("CLASSROOM");
                mPresenter.loadFolder();
                mPresenter.loadComment();
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
        mPresenter.loadXianChongList();
        mPresenter.loadFolder();
        mPresenter.loadComment();
        mPresenter.loadList(0,type,"");
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
    public void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities) {
        if(entities.size() > 0){
            if(xianChongView == null){
                xianChongView  = LayoutInflater.from(getContext()).inflate(R.layout.item_class_featured, null);
                int count = mAdapter.getHeaderViewCount();
                if(count == 0){
                    mAdapter.addHeaderView(xianChongView);
                }else {
                    View v = mAdapter.getmHeaderLayout().getChildAt(0);
                    if(v == bannerView){
                        mAdapter.addHeaderView(xianChongView,1);
                    }else {
                        mAdapter.addHeaderView(xianChongView,0);
                    }
                }
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
    public void onLoadFolderSuccess(ArrayList<ShowFolderEntity> entities) {
        if(entities.size() > 0){
            if(folderView == null){
                folderView = LayoutInflater.from(getContext()).inflate(R.layout.item_collection_top, null);
                int count = mAdapter.getHeaderViewCount();
                if(count == 0){
                    mAdapter.addHeaderView(folderView);
                }else {
                    View v = mAdapter.getmHeaderLayout().getChildAt(mAdapter.getmHeaderLayout().getChildCount() - 1);
                    if(v == commentView){
                        mAdapter.addHeaderView(folderView,mAdapter.getmHeaderLayout().getChildCount() - 1);
                    }else {
                        mAdapter.addHeaderView(folderView);
                    }
                }
            }
            View root = folderView.findViewById(R.id.ll_root);
            root.setBackgroundColor(Color.WHITE);
            TextView tv = (TextView) folderView.findViewById(R.id.tv_text);
            tv.getPaint().setFakeBoldText(false);
            tv.setText("24h 热门书包");
            tv.setTextColor(ContextCompat.getColor(getContext(),R.color.main_red));
            RecyclerView rv = (RecyclerView) folderView.findViewById(R.id.rv_list);
            LinearLayoutManager m = new LinearLayoutManager(getContext());
            m.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv.setLayoutManager(m);
            if(!loadFolder){
                rv.addItemDecoration(new FolderVDecoration());
                loadFolder = true;
            }
            final BagCollectionTopAdapter mTopAdapter = new BagCollectionTopAdapter();
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
    }

    @Override
    public void onLoadCommentSuccess(final Comment24Entity entity) {
        if(entity != null){
            if(commentView == null){
                commentView = LayoutInflater.from(getContext()).inflate(R.layout.item_feed_24_comment, null);
                mAdapter.addHeaderView(commentView);
            }
            TextView more = (TextView) commentView.findViewById(R.id.tv_more);
            more.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(getContext(),Comment24ListActivity.class);
                    startActivity(i);
                }
            });
            TextView userName = (TextView) commentView.findViewById(R.id.tv_user_name);
            userName.setText("@" + entity.getCommentCreateUserName());
            userName.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(getContext(),entity.getCommentCreateUser());
                }
            });
            TextView userContent = (TextView) commentView.findViewById(R.id.tv_user_content);
            userContent.setText(TagControl.getInstance().paresToSpann(getContext(),": "+entity.getCommentText()));
            userContent.setMovementMethod(LinkMovementMethod.getInstance());
            TextView userFavorite = (TextView) commentView.findViewById(R.id.tv_favorite);
            userFavorite.setText(entity.getLikes() + "");
            int size = (int) getResources().getDimension(R.dimen.x100);
            ImageView cover = (ImageView) commentView.findViewById(R.id.iv_cover);
            Glide.with(getContext())
                    .load(StringUtils.getUrl(getContext(),entity.getDynamicIcon(),size,size,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropSquareTransformation(getContext()))
                    .into(cover);
            TextView dynamicContent = (TextView) commentView.findViewById(R.id.tv_dynamic_content);
            String res = "<at_user user_id="+ entity.getDynamicCreateUser() + ">" + entity.getDynamicCreateUserName() + ":</at_user>" +  entity.getDynamicText();
            dynamicContent.setText(TagControl.getInstance().paresToSpann(getContext(),res));
            dynamicContent.setMovementMethod(LinkMovementMethod.getInstance());
            View root = commentView.findViewById(R.id.rl_root);
            root.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                   DynamicActivity.startActivity(getContext(),entity.getDynamicId());
                }
            });
        }else {
            if(commentView != null){
                mAdapter.removeHeaderView(commentView);
                commentView = null;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
