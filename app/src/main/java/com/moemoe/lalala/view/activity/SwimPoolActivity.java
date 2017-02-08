package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerDepartComponent;
import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.presenter.DepartContract;
import com.moemoe.lalala.presenter.DepartPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.adapter.ClassAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/1/13.
 */

public class SwimPoolActivity extends BaseAppCompatActivity implements DepartContract.View {

    private final int REQUEST_CODE_CREATE_DOC = 2333;
    @BindView(R.id.rl_one_list_root)
    RelativeLayout mMainRoot;
    @BindView(R.id.rl_bar)
    View mRlRoot;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_send_post)
    View mSendPost;
    @BindView(R.id.iv_send_music_post)
    View mSendMusicPost;
    @BindView(R.id.tv_simple_label)
    TextView mSimpleLabel;
    @Inject
    DepartPresenter mPresenter;
    private ClassAdapter mListAdapter;
    private String mRoomId;
    private boolean isLoading = false;
    private ImageView mBtnView;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_pulltorefresh_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mRoomId = "SWIM_POOL";
        DaggerDepartComponent.builder()
                .departModule(new DepartModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mRlRoot.setVisibility(View.VISIBLE);
        mRlRoot.getBackground().mutate().setAlpha(0);
        mSimpleLabel.setAlpha(0);
        mTitle.setText("温泉");
        mTitle.setTextColor(Color.argb(0, 255, 255, 255));
        mSimpleLabel.setVisibility(View.VISIBLE);
        mSimpleLabel.setSelected(AppSetting.SUB_TAG);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListAdapter = new ClassAdapter(this);
        mListDocs.getRecyclerView().setAdapter(mListAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.isLoadMoreEnabled(false);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mBtnView = new ImageView(this);
        mBtnView.setImageResource(R.drawable.btn_show_pool_rank);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        lp.bottomMargin = DensityUtil.dip2px(this,10);
        lp.leftMargin = DensityUtil.dip2px(this,10);
        mMainRoot.addView(mBtnView,lp);
        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtils.checkNetworkAndShowError(SwimPoolActivity.this) && DialogUtils.checkLoginAndShowDlg(SwimPoolActivity.this)){
                    String temp = "neta://com.moemoe.lalala/url_inner_1.0?http://www.moemoe.la/shuirank/?token=" + PreferenceUtils.getToken();
                    Uri uri = Uri.parse(temp);
                    IntentUtils.toActivityFromUri(SwimPoolActivity.this, uri, null);
                }
            }
        });
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mListAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof DocListEntity) {
                        DocListEntity bean = (DocListEntity) object;
                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                            Uri uri = Uri.parse(bean.getDesc().getSchema());
                            IntentUtils.toActivityFromUri(SwimPoolActivity.this, uri,view);
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            int curY = 0;
            boolean isChange = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!SwimPoolActivity.this.isFinishing()) Glide.with(SwimPoolActivity.this).resumeRequests();
                } else {
                    if(!SwimPoolActivity.this.isFinishing())Glide.with(SwimPoolActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                curY += dy;
                if (isChange) {
                    if (dy > 10) {
                        sendBtnOut();
                        isChange = false;
                    }
                } else {
                    if (dy < -10) {
                        sendBtnIn();
                        isChange = true;
                    }
                }
                toolBarAlpha(curY);
            }
        });
        mSendPost.setVisibility(View.VISIBLE);
        mSendPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_IMG_DOC);
            }
        });
        mSendMusicPost.setVisibility(View.VISIBLE);
        mSendMusicPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_MUSIC_DOC);
            }
        });
        mSimpleLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSetting.SUB_TAG = !AppSetting.SUB_TAG;
                mSimpleLabel.setSelected(AppSetting.SUB_TAG);
                PreferenceUtils.setSimpleLabel(SwimPoolActivity.this,AppSetting.SUB_TAG);
                mPresenter.requestDocList(0,"change",2);
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.requestDocList(mListAdapter.getItemCount() - 2,"",2);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.requestBannerData(mRoomId);
                mPresenter.requestFeatured(mRoomId);
                mPresenter.requestDocList(0,"",2);
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

        mPresenter.requestBannerData(mRoomId);
        mPresenter.requestFeatured(mRoomId);
        mPresenter.requestDocList(0,"",2);
    }

    public void toolBarAlpha(int curY) {
        int startOffset = 0;
        int endOffset = mRlRoot.getHeight();
        //curY -= CommonUtils.getStatusHeight(this);
        if (Math.abs(curY) <= startOffset) {
            mRlRoot.getBackground().mutate().setAlpha(0);
            mSimpleLabel.setAlpha(0);
            mTitle.setTextColor(Color.argb(0, 255, 255, 255));
        } else if (Math.abs(curY) > startOffset && Math.abs(curY) < endOffset) {
            float precent = (float) (Math.abs(curY) - startOffset) / endOffset;
            int alpha = Math.round(precent * 255);
            mRlRoot.getBackground().mutate().setAlpha(alpha);
            mSimpleLabel.setAlpha(precent);
            mTitle.setTextColor(Color.argb(alpha, 255, 255, 255));
        } else if (Math.abs(curY) >= endOffset) {
            mRlRoot.getBackground().mutate().setAlpha(255);
            mSimpleLabel.setAlpha(1.0f);
            mTitle.setTextColor(Color.argb(255, 255, 255, 255));
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onChangeSuccess(Object entity) {
        ArrayList<BannerEntity> banner = mListAdapter.getBannerList();
        ArrayList<FeaturedEntity> featured = mListAdapter.getFeaturedList();
        mListAdapter = new ClassAdapter(SwimPoolActivity.this);
        mListAdapter.setBanner(banner);
        mListAdapter.setFeaturedBeans(featured);
        mListAdapter.setDocList((ArrayList<DocListEntity>) entity);
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mListAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof DocListEntity) {
                        DocListEntity bean = (DocListEntity) object;
                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                            Uri uri = Uri.parse(bean.getDesc().getSchema());
                            IntentUtils.toActivityFromUri(SwimPoolActivity.this, uri,view);
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getRecyclerView().setAdapter(mListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListAdapter !=null )mListAdapter.onDestroy();
    }

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mSendPost,"translationY",mSendPost.getHeight()+ DensityUtil.dip2px(this,10),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicIn = ObjectAnimator.ofFloat(mSendMusicPost,"translationY",mSendMusicPost.getHeight()+DensityUtil.dip2px(this,10),0).setDuration(300);
        sendMusicIn.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendrankIn = ObjectAnimator.ofFloat(mBtnView,"translationY",mBtnView.getHeight() + DensityUtil.dip2px(this,10),0).setDuration(300);
        sendMusicIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn).with(sendMusicIn);
        set.play(sendMusicIn).with(sendrankIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mSendPost,"translationY",0,mSendPost.getHeight()+DensityUtil.dip2px(this,10)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicOut = ObjectAnimator.ofFloat(mSendMusicPost,"translationY",0,mSendMusicPost.getHeight()+DensityUtil.dip2px(this,10)).setDuration(300);
        sendMusicOut.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendRankOut = ObjectAnimator.ofFloat(mBtnView,"translationY",0,mBtnView.getHeight()+DensityUtil.dip2px(this,10)).setDuration(300);
        sendMusicOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut).with(sendMusicOut);
        set.play(sendMusicOut).with(sendRankOut);
        set.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            mListDocs.getRecyclerView().scrollToPosition(0);
            mPresenter.requestDocList(0,"",2);
        }
    }

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(int type){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(this)){
            Intent intent = new Intent(this, CreateNormalDocActivity.class);
            intent.putExtra(CreateNormalDocActivity.TYPE_CREATE,type);
            intent.putExtra(CreateNormalDocActivity.TYPE_QIU_MING_SHAN,2);
            startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
        }
    }

    @Override
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {
        mListDocs.setComplete();
        isLoading = false;
        mListAdapter.setBanner(bannerEntities);
    }

    @Override
    public void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities) {
        mListDocs.setComplete();
        isLoading = false;
        mListAdapter.setFeaturedBeans(featuredEntities);
    }

    @Override
    public void onDocLoadSuccess(Object entity, boolean pull) {
        if(((ArrayList<DocListEntity>) entity).size() == 0){
            mListDocs.isLoadMoreEnabled(false);
            ToastUtils.showCenter(this,getString(R.string.msg_all_load_down));
        }else {
            mListDocs.isLoadMoreEnabled(true);
        }
        mListDocs.setComplete();
        isLoading = false;
        if(pull){
            mListAdapter.setDocList((ArrayList<DocListEntity>) entity);
        }else {
            mListAdapter.addDocList((ArrayList<DocListEntity>) entity);
        }
    }

    @Override
    public void onFailure(int code,String msg) {
        mListDocs.setComplete();
        isLoading = false;
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
