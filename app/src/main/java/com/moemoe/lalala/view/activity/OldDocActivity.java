package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.moemoe.lalala.di.components.DaggerOldDocComponent;
import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.modules.OldDocModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.presenter.OldDocContract;
import com.moemoe.lalala.presenter.OldDocPresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.OldDocAdapter;
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
 * Created by yi on 2016/11/30.
 */

public class OldDocActivity extends BaseAppCompatActivity implements OldDocContract.View {

    private final String EXTRA_NAME = "name";

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_send_post)
    ImageView mIvSend;
    private View bannerView;

    @Inject
    OldDocPresenter mPresenter;
    private OldDocAdapter mListAdapter;
    private Banner banner;
    private String mRoomId;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        Intent i = getIntent();
        mRoomId = "";

        if(i != null){
            String roomId = i.getStringExtra(UUID);
            if(!TextUtils.isEmpty(roomId)){
                mRoomId = roomId;
            }
//            String title = i.getStringExtra(EXTRA_NAME);
//            if(!TextUtils.isEmpty(title)){
//                mTitle.setText(title);
//                mTitle.setVisibility(View.VISIBLE);
//            }else {
//                mTitle.setVisibility(View.GONE);
//            }
        }
        mTitle.setText("论坛");
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        DaggerOldDocComponent.builder()
                .oldDocModule(new OldDocModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListAdapter = new OldDocAdapter();
        mListDocs.getRecyclerView().addItemDecoration(new MenuVItemDecoration((int) getResources().getDimension(R.dimen.y24)));
        mListDocs.getRecyclerView().setAdapter(mListAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DocResponse docResponse = mListAdapter.getItem(position);
                Intent i = new Intent(OldDocActivity.this,NewDocDetailActivity.class);
                i.putExtra(UUID,docResponse.getId());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mIvSend.setVisibility(View.VISIBLE);
        mIvSend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(OldDocActivity.this, CreateRichDocActivity.class);
                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,1);
                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"论坛");
                intent.putExtra("from_name","论坛");
                intent.putExtra("from_schema","neta://com.moemoe.lalala/luntan_2.0");
                startActivity(intent);
            }
        });
        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing()) Glide.with(OldDocActivity.this).resumeRequests();
                } else {
                    if(!isFinishing()) Glide.with(OldDocActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                if(mListAdapter.getList().size() == 0){
                    mPresenter.loadOldDocList("room",0);
                }else {
                    mPresenter.loadOldDocList("room",mListAdapter.getItem(mListAdapter.getList().size() - 1).getTimestamp());
                }
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.loadOldDocList("room",0);
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.requestBannerData("CLASSROOM");
        mPresenter.loadOldDocList("room",0);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(OldDocActivity.this,code,msg);
    }

    @Override
    public void loadOldDocListSuccess(ArrayList<DocResponse> list, boolean isPull) {
        mIsLoading = false;
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setComplete();
        if(isPull){
            mListAdapter.setList(list);
        }else {
            mListAdapter.addList(list);
        }
    }

    @Override
    public void onBannerLoadSuccess(final ArrayList<BannerEntity> bannerEntities) {
        if(bannerEntities.size() > 0){
            if(bannerView == null){
                bannerView = LayoutInflater.from(this).inflate(R.layout.item_new_banner, null);
                banner = bannerView.findViewById(R.id.banner);
                mListAdapter.addHeaderView(bannerView,0);
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
                        IntentUtils.toActivityFromUri(OldDocActivity.this, uri,null);
                    }
                }
            });
        }else {
            if(bannerView != null){
                mListAdapter.removeHeaderView(bannerView);
                bannerView = null;
            }
        }
    }
}
