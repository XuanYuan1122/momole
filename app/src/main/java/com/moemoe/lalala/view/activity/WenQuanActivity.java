package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerOldDocComponent;
import com.moemoe.lalala.di.modules.OldDocModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.presenter.OldDocContract;
import com.moemoe.lalala.presenter.OldDocPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.OldDocAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/30.
 */

public class WenQuanActivity extends BaseAppCompatActivity implements OldDocContract.View {

    private final String EXTRA_NAME = "name";

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_send_post)
    ImageView mIvSend;

    @Inject
    OldDocPresenter mPresenter;
    private OldDocAdapter mListAdapter;
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
        MoeMoeApplication.getInstance().getNetComponent().getApiService().clickDepartment("wenquan")
                .subscribeOn(Schedulers.io())
                .subscribe();
        if(i != null){
            String roomId = i.getStringExtra(UUID);
            if(!TextUtils.isEmpty(roomId)){
                mRoomId = roomId;
            }
            String title = i.getStringExtra(EXTRA_NAME);
            if(!TextUtils.isEmpty(title)){
                mTitle.setText(title);
                mTitle.setVisibility(View.VISIBLE);
            }else {
                mTitle.setVisibility(View.GONE);
            }
        }
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
        ImageView mBtnView = new ImageView(this);
        mBtnView.setImageResource(R.drawable.btn_show_pool_rank);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = (int)getResources().getDimension(R.dimen.y20);
        lp.leftMargin = (int)getResources().getDimension(R.dimen.x20);
        lp.gravity = Gravity.BOTTOM;
        mListDocs.addView(mBtnView,lp);
        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtils.checkNetworkAndShowError(WenQuanActivity.this) && DialogUtils.checkLoginAndShowDlg(WenQuanActivity.this)){
                    String temp = "neta://com.moemoe.lalala/url_inner_1.0?http://www.moemoe.la/shuirank/?token=" + PreferenceUtils.getToken();
                    Uri uri = Uri.parse(temp);
                    IntentUtils.toActivityFromUri(WenQuanActivity.this, uri, null);
                }
            }
        });
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
                Intent i = new Intent(WenQuanActivity.this,NewDocDetailActivity.class);
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
                Intent intent = new Intent(WenQuanActivity.this, CreateRichDocActivity.class);
                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,2);
                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"温泉");
                intent.putExtra("from_name","温泉");
                intent.putExtra("from_schema","neta://com.moemoe.lalala/swim_2.0");
                startActivity(intent);
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.loadOldDocList("swim",mListAdapter.getItem(mListAdapter.getItemCount() - 1).getTimestamp());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.loadOldDocList("swim",0);
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
        mPresenter.loadOldDocList("swim",0);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        mHandler.removeCallbacks(timeRunnabel);
        MoeMoeApplication.getInstance().getNetComponent().getApiService().stayDepartment("wenquan",mStayTime)
                .subscribeOn(Schedulers.io())
                .subscribe();
        super.onDestroy();

    }

    private int mStayTime;

    private Handler mHandler = new Handler();
    private Runnable timeRunnabel = new Runnable() {
        @Override
        public void run() {
            mStayTime++;
            mHandler.postDelayed(this,1000);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timeRunnabel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(timeRunnabel);
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(WenQuanActivity.this,code,msg);
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
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {

    }
}
