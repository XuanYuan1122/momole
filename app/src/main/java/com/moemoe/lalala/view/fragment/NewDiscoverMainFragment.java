//package com.moemoe.lalala.view.fragment;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.di.components.DaggerFeedComponent;
//import com.moemoe.lalala.di.modules.FeedModule;
//import com.moemoe.lalala.model.entity.BannerEntity;
//import com.moemoe.lalala.model.entity.Comment24Entity;
//import com.moemoe.lalala.model.entity.DepartmentEntity;
//import com.moemoe.lalala.model.entity.DiscoverEntity;
//import com.moemoe.lalala.model.entity.FolderType;
//import com.moemoe.lalala.model.entity.NewDynamicEntity;
//import com.moemoe.lalala.model.entity.ShowFolderEntity;
//import com.moemoe.lalala.model.entity.SimpleUserEntity;
//import com.moemoe.lalala.model.entity.XianChongEntity;
//import com.moemoe.lalala.presenter.FeedContract;
//import com.moemoe.lalala.presenter.FeedPresenter;
//import com.moemoe.lalala.utils.FolderVDecoration;
//import com.moemoe.lalala.utils.IntentUtils;
//import com.moemoe.lalala.utils.NoDoubleClickListener;
//import com.moemoe.lalala.utils.PreferenceUtils;
//import com.moemoe.lalala.utils.StringUtils;
//import com.moemoe.lalala.utils.ViewUtils;
//import com.moemoe.lalala.utils.tag.TagControl;
//import com.moemoe.lalala.view.activity.Comment24ListActivity;
//import com.moemoe.lalala.view.activity.DynamicActivity;
//import com.moemoe.lalala.view.activity.NewFileCommonActivity;
//import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
//import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
//import com.moemoe.lalala.view.adapter.BagCollectionTopAdapter;
//import com.moemoe.lalala.view.adapter.DiscoverAdapter;
//import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
//import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
//import com.moemoe.lalala.view.widget.recycler.PullCallback;
//
//import java.util.ArrayList;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//import jp.wasabeef.glide.transformations.CropSquareTransformation;
//
///**
// *
// * Created by yi on 2017/9/4.
// */
//
//public class NewDiscoverMainFragment extends BaseFragment implements FeedContract.View{
//
//    @BindView(R.id.list)
//    PullAndLoadView mListDocs;
//    @Inject
//    FeedPresenter mPresenter;
//
//    private DiscoverAdapter mAdapter;
//    private boolean isLoading = false;
//    private View folderView;
//    private View commentView;
//    private boolean loadFolder;
//    private long minIdx;
//    private long maxIdx;
//    private String type;
//
//    public static NewDiscoverMainFragment newInstance(String type){
//        NewDiscoverMainFragment fragment = new NewDiscoverMainFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("type",type);
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.frag_onepull;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        DaggerFeedComponent.builder()
//                .feedModule(new FeedModule(this))
//                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
//                .build()
//                .inject(this);
//        type = getArguments().getString("type");
//        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
//        mListDocs.setLoadMoreEnabled(true);
//        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
//        mAdapter = new DiscoverAdapter();
//        mListDocs.getRecyclerView().setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                DiscoverEntity entity = mAdapter.getItem(position);
//                if("dynamic".equals(entity.getType())){
//                    DynamicActivity.startActivity(getContext(),new Gson().fromJson(mAdapter.getItem(position).getObj(),NewDynamicEntity.class));
//                }else if("doc".equals(entity.getType())){
//                    DepartmentEntity.DepartmentDoc departmentDoc = new Gson().fromJson(mAdapter.getItem(position).getObj(),DepartmentEntity.DepartmentDoc.class);
//                    if(!TextUtils.isEmpty(departmentDoc.getSchema())){
//                        Uri uri = Uri.parse(departmentDoc.getSchema());
//                        IntentUtils.toActivityFromUri(getContext(), uri,view);
//                    }
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        });
//        mListDocs.setPullCallback(new PullCallback() {
//            @Override
//            public void onLoadMore() {
//                isLoading = true;
//                mPresenter.loadDiscoverList(type,minIdx,maxIdx,false);
//            }
//
//            @Override
//            public void onRefresh() {
//                isLoading = true;
//                if("random".equals(type)){
//                    mPresenter.loadFolder();
//                    mPresenter.loadComment();
//                    mPresenter.loadDiscoverList(type,minIdx,maxIdx,true);
//                }else {
//                    mPresenter.loadDiscoverList(type,0,0,true);
//                }
//
//            }
//
//            @Override
//            public boolean isLoading() {
//                return isLoading;
//            }
//
//            @Override
//            public boolean hasLoadedAllItems() {
//                return false;
//            }
//        });
//        if("random".equals(type)){
//            mPresenter.loadFolder();
//            mPresenter.loadComment();
//            minIdx = PreferenceUtils.getDiscoverMinIdx(getContext(),type);
//            maxIdx = PreferenceUtils.getDiscoverMaxIdx(getContext(),type);
//            mPresenter.loadDiscoverList(type,minIdx,maxIdx,true);
//        }else {
//            mPresenter.loadDiscoverList(type,0,0,true);
//        }
//
//    }
//
//    public void likeDynamic(String id,boolean isLie,int position){
//        mPresenter.likeDynamic(id, isLie, position);
//    }
//
//    @Override
//    public void onFailure(int code, String msg) {
//        isLoading = false;
//        mListDocs.setComplete();
//    }
//
//    public void release(){
//        if(mPresenter != null) mPresenter.release();
//        super.release();
//    }
//
//    @Override
//    public void onLoadListSuccess(ArrayList<NewDynamicEntity> resList, boolean isPull) {
//
//    }
//
//    @Override
//    public void onBannerLoadSuccess(final ArrayList<BannerEntity> bannerEntities) {
//    }
//
//    @Override
//    public void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities) {
//    }
//
//    @Override
//    public void onLoadFolderSuccess(ArrayList<ShowFolderEntity> entities) {
//        if(entities.size() > 0){
//            if(folderView == null){
//                folderView = LayoutInflater.from(getContext()).inflate(R.layout.item_collection_top, null);
//                mAdapter.addHeaderView(folderView,0);
//            }
//            View root = folderView.findViewById(R.id.ll_root);
//            root.setBackgroundColor(Color.WHITE);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.topMargin = (int) getResources().getDimension(R.dimen.y24);
//            root.setLayoutParams(lp);
//            TextView tv = folderView.findViewById(R.id.tv_text);
//            tv.setText("热门书包");
//            tv.setTextColor(ContextCompat.getColor(getContext(),R.color.main_red));
//            RecyclerView rv =  folderView.findViewById(R.id.rv_list);
//            LinearLayoutManager m = new LinearLayoutManager(getContext());
//            m.setOrientation(LinearLayoutManager.HORIZONTAL);
//            rv.setLayoutManager(m);
//            if(!loadFolder){
//                rv.addItemDecoration(new FolderVDecoration());
//                loadFolder = true;
//            }
//            final BagCollectionTopAdapter mTopAdapter = new BagCollectionTopAdapter();
//            rv.setAdapter(mTopAdapter);
//            mTopAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    ShowFolderEntity entity = mTopAdapter.getItem(position);
//                    if(entity.getType().equals(FolderType.ZH.toString())){
//                        NewFileCommonActivity.startActivity(getContext(),FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
//                    }else if(entity.getType().equals(FolderType.TJ.toString())){
//                        NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
//                    }else if(entity.getType().equals(FolderType.MH.toString())){
//                        NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
//                    }else if(entity.getType().equals(FolderType.XS.toString())){
//                        NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
//                    }
//                }
//
//                @Override
//                public void onItemLongClick(View view, int position) {
//
//                }
//            });
//            mTopAdapter.setList(entities);
//        }else {
//            if(folderView != null){
//                mAdapter.removeHeaderView(folderView);
//                folderView = null;
//            }
//        }
//    }
//
//    @Override
//    public void onLoadCommentSuccess(final Comment24Entity entity) {
//        if(entity != null){
//            if(commentView == null){
//                commentView = LayoutInflater.from(getContext()).inflate(R.layout.item_feed_24_comment, null);
//                mAdapter.addHeaderView(commentView);
//            }
//            LinearLayout root1 = commentView.findViewById(R.id.ll_root);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.topMargin = (int) getResources().getDimension(R.dimen.y24);
//            root1.setLayoutParams(lp);
//
//            TextView more = commentView.findViewById(R.id.tv_more);
//            more.setOnClickListener(new NoDoubleClickListener() {
//                @Override
//                public void onNoDoubleClick(View v) {
//                    Intent i = new Intent(getContext(),Comment24ListActivity.class);
//                    startActivity(i);
//                }
//            });
//            TextView userName = commentView.findViewById(R.id.tv_user_name);
//            userName.setText("@" + entity.getCommentCreateUserName());
//            userName.setOnClickListener(new NoDoubleClickListener() {
//                @Override
//                public void onNoDoubleClick(View v) {
//                    ViewUtils.toPersonal(getContext(),entity.getCommentCreateUser());
//                }
//            });
//            TextView userContent = commentView.findViewById(R.id.tv_user_content);
//            userContent.setText(TagControl.getInstance().paresToSpann(getContext(),": "+entity.getCommentText()));
//            userContent.setMovementMethod(LinkMovementMethod.getInstance());
//            TextView userFavorite = commentView.findViewById(R.id.tv_favorite);
//            userFavorite.setText(entity.getLikes() + "");
//            int size = (int) getResources().getDimension(R.dimen.x100);
//            ImageView cover = commentView.findViewById(R.id.iv_cover);
//            Glide.with(getContext())
//                    .load(StringUtils.getUrl(getContext(),entity.getDynamicIcon(),size,size,false,true))
//                    .error(R.drawable.bg_default_square)
//                    .placeholder(R.drawable.bg_default_square)
//                    .bitmapTransform(new CropSquareTransformation(getContext()))
//                    .into(cover);
//            TextView dynamicContent = commentView.findViewById(R.id.tv_dynamic_content);
//            String res = "<at_user user_id="+ entity.getDynamicCreateUser() + ">" + entity.getDynamicCreateUserName() + ":</at_user>" +  entity.getDynamicText();
//            dynamicContent.setText(TagControl.getInstance().paresToSpann(getContext(),res));
//            dynamicContent.setMovementMethod(LinkMovementMethod.getInstance());
//            View root = commentView.findViewById(R.id.rl_root);
//            root.setOnClickListener(new NoDoubleClickListener() {
//                @Override
//                public void onNoDoubleClick(View v) {
//                   DynamicActivity.startActivity(getContext(),entity.getDynamicId());
//                }
//            });
//        }else {
//            if(commentView != null){
//                mAdapter.removeHeaderView(commentView);
//                commentView = null;
//            }
//        }
//    }
//
//    @Override
//    public void onLikeDynamicSuccess(boolean isLike, int position) {
//        NewDynamicEntity entity = new Gson().fromJson(mAdapter.getList().get(position).getObj(),NewDynamicEntity.class);
//        entity.setThumb(isLike);
//        if(isLike){
//            SimpleUserEntity userEntity = new SimpleUserEntity();
//            userEntity.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
//            userEntity.setUserId(PreferenceUtils.getUUid());
//            userEntity.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
//            entity.getThumbUsers().add(0,userEntity);
//            entity.setThumbs(entity.getThumbs() + 1);
//        }else {
//            for(SimpleUserEntity userEntity : entity.getThumbUsers()){
//                if(userEntity.getUserId().equals(PreferenceUtils.getUUid())){
//                    entity.getThumbUsers().remove(userEntity);
//                    break;
//                }
//            }
//            entity.setThumbs(entity.getThumbs() - 1);
//        }
//        Gson gson = new Gson();
//        JsonObject newObj = gson.toJsonTree(entity).getAsJsonObject();
//        mAdapter.getList().get(position).setObj(newObj);
//        if(mAdapter.getHeaderLayoutCount() != 0){
//            mAdapter.notifyItemChanged(position + 1);
//        }else {
//            mAdapter.notifyItemChanged(position);
//        }
//    }
//
//    @Override
//    public void onLoadDiscoverListSuccess(ArrayList<DiscoverEntity> entities, boolean isPull) {
//        isLoading = false;
//        changeIdx(entities);
//        mListDocs.setComplete();
//        if(isPull){
//            mAdapter.setList(entities);
//        }else {
//            mAdapter.addList(entities);
//        }
//    }
//
//    private void changeIdx(ArrayList<DiscoverEntity> entities){
//        if(entities.size() > 0){
//            long min = entities.get(0).getTimestamp();
//            long max = entities.get(0).getTimestamp();
//            for(DiscoverEntity entity : entities){
//                if(entity.getTimestamp() == 0){
//                    min = 0;
//                    max = 0;
//                    break;
//                }else if(entity.getTimestamp() > max){
//                    max = entity.getTimestamp();
//                }else if(entity.getTimestamp() < min){
//                    min = entity.getTimestamp();
//                }
//            }
//            if(min == 0 && max == 0){
//                minIdx = maxIdx = 0;
//            }else {
//                if(min < minIdx || minIdx == 0){
//                    minIdx = min;
//                }
//                if(max > maxIdx){
//                    maxIdx = max;
//                }
//            }
//            if("random".equals(type)){
//                PreferenceUtils.setDiscoverMinIdx(getContext(),type,minIdx);
//                PreferenceUtils.setDiscoverMaxIdx(getContext(),type,maxIdx);
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//    }
//}
