//package com.moemoe.lalala.view.activity;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.di.components.DaggerDepartComponent;
//import com.moemoe.lalala.di.modules.DepartModule;
//import com.moemoe.lalala.model.api.ApiService;
//import com.moemoe.lalala.model.entity.BannerEntity;
//import com.moemoe.lalala.model.entity.DepartmentEntity;
//import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
//import com.moemoe.lalala.model.entity.FeaturedEntity;
//import com.moemoe.lalala.model.entity.FolderType;
//import com.moemoe.lalala.model.entity.SendSubmissionEntity;
//import com.moemoe.lalala.presenter.DepartContract;
//import com.moemoe.lalala.presenter.DepartPresenter;
//import com.moemoe.lalala.utils.BannerImageLoader;
//import com.moemoe.lalala.utils.DepartmentDecoration;
//import com.moemoe.lalala.utils.DialogUtils;
//import com.moemoe.lalala.utils.ErrorCodeUtils;
//import com.moemoe.lalala.utils.IntentUtils;
//import com.moemoe.lalala.utils.NoDoubleClickListener;
//import com.moemoe.lalala.utils.PreferenceUtils;
//import com.moemoe.lalala.utils.ViewUtils;
//import com.moemoe.lalala.view.adapter.ClassRecyclerViewAdapter;
//import com.moemoe.lalala.view.adapter.NewDepartmentAdapter;
//import com.moemoe.lalala.view.adapter.OnItemClickListener;
//import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
//import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
//import com.moemoe.lalala.view.widget.recycler.PullCallback;
//import com.youth.banner.Banner;
//import com.youth.banner.BannerConfig;
//import com.youth.banner.listener.OnBannerListener;
//
//import java.util.ArrayList;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//
//import static com.moemoe.lalala.utils.StartActivityConstant.REQ_WEN_ZHANG;
//
///**
// *
// * Created by yi on 2016/11/30.
// */
//
//public class DepartmentV2Activity extends BaseAppCompatActivity implements DepartContract.View {
//
//    private final String EXTRA_NAME = "name";
//
//    @BindView(R.id.iv_back)
//    View mIvBack;
//    @BindView(R.id.tv_toolbar_title)
//    TextView mTitle;
//    @BindView(R.id.rv_list)
//    PullAndLoadView mListDocs;
//    @BindView(R.id.tv_right_menu)
//    TextView mTvMenu;
//
//    @Inject
//    DepartPresenter mPresenter;
//    private NewDepartmentAdapter mListAdapter;
//    private String mRoomId;
//    private Banner banner;
//    private View bannerView;
//    private View featuredView;
//    private boolean mIsLoading = false;
//    private int mIsFollow;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ac_bar_list;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
//        Intent i = getIntent();
//        mRoomId = "";
//
//        if(i != null){
//            String roomId = i.getStringExtra(UUID);
//            if(!TextUtils.isEmpty(roomId)){
//                mRoomId = roomId;
//            }
//            String title = i.getStringExtra(EXTRA_NAME);
//            if(!TextUtils.isEmpty(title)){
//                mTitle.setText(title);
//                mTitle.setVisibility(View.VISIBLE);
//            }else {
//                mTitle.setVisibility(View.GONE);
//            }
//        }else {
//            finish();
//        }
//        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
//        DaggerDepartComponent.builder()
//                .departModule(new DepartModule(this))
//                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
//                .build()
//                .inject(this);
//        mIsFollow = -1;
//        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
//        mListDocs.getRecyclerView().addItemDecoration(new DepartmentDecoration());
//        mListDocs.setLayoutManager(new LinearLayoutManager(this));
//        mListAdapter = new NewDepartmentAdapter();
//        mListDocs.getRecyclerView().setAdapter(mListAdapter);
//        mListDocs.setLoadMoreEnabled(false);
//    }
//
//    @Override
//    protected void initToolbar(Bundle savedInstanceState) {
//        mTvMenu.setVisibility(View.VISIBLE);
//        mTvMenu.setText("投稿");
//        mTvMenu.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                if(DialogUtils.checkLoginAndShowDlg(DepartmentV2Activity.this)){
//                    NewFolderWenZhangActivity.startActivityForResult(DepartmentV2Activity.this, PreferenceUtils.getUUid(), FolderType.WZ.toString(),"my",true);
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void initListeners() {
//        mIvBack.setVisibility(View.VISIBLE);
//        mIvBack.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                finish();
//            }
//        });
//        mListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                DepartmentEntity.DepartmentDoc bean = mListAdapter.getItem(position);
//                if (bean != null) {
//                    if (!TextUtils.isEmpty(bean.getSchema())) {
//                        String mSchema = bean.getSchema();
//                        if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
//                            String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
//                            String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
//                            mSchema = begin + "uuid=" + uuid + "&from_name=" + mTitle.getText().toString();
//                        }
//                        Uri uri = Uri.parse(mSchema);
//                        IntentUtils.toActivityFromUri(DepartmentV2Activity.this, uri,view);
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
//                mIsLoading = true;
//                mPresenter.requestDocList(mListAdapter.getList().size(),mRoomId,0);
//            }
//
//            @Override
//            public void onRefresh() {
//                mIsLoading = true;
//                mPresenter.requestBannerData(mRoomId);
//                mPresenter.requestFeatured(mRoomId);
//                mPresenter.requestDocList(0,mRoomId,0);
//            }
//
//            @Override
//            public boolean isLoading() {
//                return mIsLoading;
//            }
//
//            @Override
//            public boolean hasLoadedAllItems() {
//                return false;
//            }
//        });
//        mPresenter.loadIsFollow(mRoomId);
//        mPresenter.requestBannerData(mRoomId);
//        mPresenter.requestFeatured(mRoomId);
//        mPresenter.requestDocList(0,mRoomId,0);
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        if(mPresenter != null)mPresenter.release();
//        super.onDestroy();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onBannerLoadSuccess(final ArrayList<BannerEntity> bannerEntities) {
//        if(bannerEntities.size() > 0){
//            if(bannerView == null){
//                bannerView = LayoutInflater.from(this).inflate(R.layout.item_new_banner, null);
//                banner =  bannerView.findViewById(R.id.banner);
//                mListAdapter.addHeaderView(bannerView,0);
//            }
//            banner.setImages(bannerEntities)
//                    .setImageLoader(new BannerImageLoader())
//                    .setDelayTime(2000)
//                    .setIndicatorGravity(BannerConfig.CENTER)
//                    .start();
//            banner.setOnBannerListener(new OnBannerListener() {
//                @Override
//                public void OnBannerClick(int position) {
//                    BannerEntity bean = bannerEntities.get(position);
//                    if(!TextUtils.isEmpty(bean.getSchema())){
//                        Uri uri = Uri.parse(bean.getSchema());
//                        IntentUtils.toActivityFromUri(DepartmentV2Activity.this, uri,null);
//                    }
//                }
//            });
//        }else {
//            if(bannerView != null){
//                mListAdapter.removeHeaderView(bannerView);
//                bannerView = null;
//            }
//        }
//    }
//
//    @Override
//    public void onFeaturedLoadSuccess(final ArrayList<FeaturedEntity> featuredEntities) {
//        if(featuredEntities.size() > 0){
//            if(featuredView == null){
//                featuredView  = LayoutInflater.from(this).inflate(R.layout.item_class_featured, null);
//                int count = mListAdapter.getHeaderViewCount();
//                if(count == 0){
//                    mListAdapter.addHeaderView(featuredView);
//                }else {
//                    View v = mListAdapter.getmHeaderLayout().getChildAt(0);
//                    if(v == bannerView){
//                        mListAdapter.addHeaderView(featuredView,1);
//                    }else {
//                        mListAdapter.addHeaderView(featuredView,0);
//                    }
//                }
//            }
//            RecyclerView rvList = featuredView.findViewById(R.id.rv_class_featured);
//            rvList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)getResources().getDimension(R.dimen.y220)));
//            rvList.setBackgroundColor(Color.WHITE);
//            final ClassRecyclerViewAdapter recyclerViewAdapter = new ClassRecyclerViewAdapter(this);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            rvList.setLayoutManager(layoutManager);
//            rvList.setAdapter(recyclerViewAdapter);
//            recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    FeaturedEntity docBean = featuredEntities.get(position);
//                    if(!TextUtils.isEmpty(docBean.getSchema())){
//                        Uri uri = Uri.parse(docBean.getSchema());
//                        IntentUtils.toActivityFromUri(DepartmentV2Activity.this, uri,view);
//                    }
//                }
//
//                @Override
//                public void onItemLongClick(View view, int position) {
//
//                }
//            });
//            recyclerViewAdapter.setData(featuredEntities);
//        }else {
//            if(featuredView != null){
//                mListAdapter.removeHeaderView(featuredView);
//                featuredView = null;
//            }
//        }
//    }
//
//    @Override
//    public void onDocLoadSuccess(Object entity,boolean pull) {
//        mIsLoading = false;
//        if(((DepartmentEntity)entity).getList().size() >= ApiService.LENGHT){
//            mListDocs.setLoadMoreEnabled(true);
//        }else {
//            mListDocs.setLoadMoreEnabled(false);
//        }
//        mListDocs.setComplete();
//        if(pull){
//            mListAdapter.setList(((DepartmentEntity)entity).getList());
//        }else {
//            mListAdapter.addList(((DepartmentEntity)entity).getList());
//        }
//    }
//
//    @Override
//    public void onChangeSuccess(Object entity) {
//
//    }
//
//    @Override
//    public void onFollowDepartmentSuccess(boolean follow) {
//    }
//
//    @Override
//    public void onSubmissionSuccess() {
//        finalizeDialog();
//        showToast("投稿成功");
//    }
//
//    @Override
//    public void onLoadGroupSuccess(ArrayList<DepartmentGroupEntity> entity) {
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == REQ_WEN_ZHANG && resultCode == RESULT_OK  && data != null){
//            String docId = data.getStringExtra("docId");
//            SendSubmissionEntity e = new SendSubmissionEntity(mRoomId,docId);
//            createDialog();
//            mPresenter.submission(e);
//        }
//    }
//
//    @Override
//    public void onFailure(int code,String msg) {
//        finalizeDialog();
//        mIsLoading = false;
//        mListDocs.setComplete();
//        ErrorCodeUtils.showErrorMsgByCode(DepartmentV2Activity.this,code,msg);
//    }
//}
