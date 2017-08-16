//package com.moemoe.lalala.view.activity;
//
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.animation.OvershootInterpolator;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.AppSetting;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.di.components.DaggerDepartComponent;
//import com.moemoe.lalala.di.modules.DepartModule;
//import com.moemoe.lalala.model.entity.BannerEntity;
//import com.moemoe.lalala.model.entity.DocListEntity;
//import com.moemoe.lalala.model.entity.FeaturedEntity;
//import com.moemoe.lalala.presenter.DepartContract;
//import com.moemoe.lalala.presenter.DepartPresenter;
//import com.moemoe.lalala.utils.DensityUtil;
//import com.moemoe.lalala.utils.DialogUtils;
//import com.moemoe.lalala.utils.ErrorCodeUtils;
//import com.moemoe.lalala.utils.IntentUtils;
//import com.moemoe.lalala.utils.NoDoubleClickListener;
//import com.moemoe.lalala.utils.PreferenceUtils;
//import com.moemoe.lalala.utils.StartActivityConstant;
//import com.moemoe.lalala.utils.ToastUtils;
//import com.moemoe.lalala.utils.ViewUtils;
//import com.moemoe.lalala.view.adapter.ClassAdapter;
//import com.moemoe.lalala.view.adapter.OnItemClickListener;
//import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
//import com.moemoe.lalala.view.widget.recycler.PullCallback;
//
//import java.util.ArrayList;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//
///**
// * Created by yi on 2017/7/30.
// */
//
//public class ClassActivity extends BaseAppCompatActivity implements DepartContract.View {
//
//    private final int REQUEST_CODE_CREATE_DOC = 2333;
//    @BindView(R.id.rl_bar)
//    View mRlRoot;
//    @BindView(R.id.tv_title)
//    TextView mTitle;
//    @BindView(R.id.iv_back)
//    ImageView mIvBack;
//    @BindView(R.id.list)
//    PullAndLoadView mListDocs;
//    @BindView(R.id.iv_send_post)
//    View mSendPost;
//    @BindView(R.id.tv_simple_label)
//    TextView mSimpleLabel;
//
//    @Inject
//    DepartPresenter mPresenter;
//    private ClassAdapter mListAdapter;
//    private String mRoomId;
//    private boolean isLoading = false;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ac_one_pulltorefresh_list;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
//        mRoomId = "CLASSROOM";
//        DaggerDepartComponent.builder()
//                .departModule(new DepartModule(this))
//                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
//                .build()
//                .inject(this);
//        mRlRoot.setVisibility(View.VISIBLE);
//        mRlRoot.getBackground().mutate().setAlpha(0);
//        mSimpleLabel.setAlpha(0);
//        mTitle.setText("广场");
//        mTitle.setAlpha(0);
//        mSimpleLabel.setVisibility(View.VISIBLE);
//        mSimpleLabel.setSelected(AppSetting.SUB_TAG);
//        mIvBack.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                finish();
//            }
//        });
//        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
//        mListAdapter = new ClassAdapter(this);
//        mListDocs.getRecyclerView().setAdapter(mListAdapter);
//        mListDocs.setLayoutManager(new LinearLayoutManager(this));
//        mListDocs.setLoadMoreEnabled(false);
//        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Object object = mListAdapter.getItem(position);
//                if (object != null) {
//                    if (object instanceof DocListEntity) {
//                        DocListEntity bean = (DocListEntity) object;
//                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
//                            String mSchema = bean.getDesc().getSchema();
//                            if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
//                                String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
//                                String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
//                                mSchema = begin + "uuid=" + uuid + "&from_name=广场&position=" + position;
//                            }
//                            Uri uri = Uri.parse(mSchema);
//                            IntentUtils.toActivityForResultFromUri(ClassActivity.this, uri,view, StartActivityConstant.REQ_DOC_DETAIL_ACTIVITY);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        });
//        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
//            int curY = 0;
//            boolean isChange = false;
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if(!ClassActivity.this.isFinishing()) Glide.with(ClassActivity.this).resumeRequests();
//                } else {
//                    if(!ClassActivity.this.isFinishing())Glide.with(ClassActivity.this).pauseRequests();
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                curY += dy;
//                if (isChange) {
//                    if (dy > 10) {
//                        sendBtnOut();
//                        isChange = false;
//                    }
//                } else {
//                    if (dy < -10) {
//                        sendBtnIn();
//                        isChange = true;
//                    }
//                }
//                toolBarAlpha(curY);
//            }
//        });
//        mSendPost.setVisibility(View.VISIBLE);
//        mSendPost.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                go2CreateDoc();
//            }
//        });
//        mSimpleLabel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppSetting.SUB_TAG = !AppSetting.SUB_TAG;
//                mSimpleLabel.setSelected(AppSetting.SUB_TAG);
//                PreferenceUtils.setSimpleLabel(ClassActivity.this,AppSetting.SUB_TAG);
//                mPresenter.requestDocList(0,"change",1);
//            }
//        });
//        mListDocs.setPullCallback(new PullCallback() {
//            @Override
//            public void onLoadMore() {
//                isLoading = true;
//                mPresenter.requestDocList(mListAdapter.getItemCount() - 2,"",1);
//            }
//
//            @Override
//            public void onRefresh() {
//                isLoading = true;
//                mPresenter.requestBannerData(mRoomId);
//                mPresenter.requestFeatured(mRoomId);
//                mPresenter.requestDocList(0,"",1);
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
//        mPresenter.requestBannerData(mRoomId);
//        mPresenter.requestFeatured(mRoomId);
//        mPresenter.requestDocList(0,"",1);
//    }
//
//    private boolean isChanged = false;
//
//    public void toolBarAlpha(int curY) {
//        int startOffset = 5;
//        int endOffset = mRlRoot.getHeight();
//        if (Math.abs(curY) <= startOffset) {
//            mRlRoot.getBackground().mutate().setAlpha(0);
//            mSimpleLabel.setAlpha(0);
//            mTitle.setAlpha(0);
//            if(isChanged){
//                mIvBack.setImageResource(R.drawable.btn_back_cover_normal);
//                isChanged = false;
//            }
//            mIvBack.setImageAlpha(255);
//        } else if (Math.abs(curY) > startOffset && Math.abs(curY) < endOffset) {
//            float percent = (float) (Math.abs(curY) - startOffset) / endOffset;
//            int alpha = Math.round(percent * 255);
//            mRlRoot.getBackground().mutate().setAlpha(alpha);
//            mSimpleLabel.setAlpha(percent);
//            mTitle.setAlpha(percent);
//            if(!isChanged){
//                mIvBack.setImageResource(R.drawable.btn_back_blue_normal);
//                isChanged = true;
//            }
//            mIvBack.setImageAlpha(alpha);
//        } else if (Math.abs(curY) >= endOffset) {
//            mRlRoot.getBackground().mutate().setAlpha(255);
//            mIvBack.setImageAlpha(255);
//            mSimpleLabel.setAlpha(1.0f);
//            mTitle.setAlpha(1.0f);
//        }
//    }
//
//
//    @Override
//    protected void initToolbar(Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    protected void initListeners() {
//
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//    @Override
//    public void onFailure(int code, String msg) {
//        mListDocs.setComplete();
//        isLoading = false;
//        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
//    }
//
//    @Override
//    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {
//        mListDocs.setComplete();
//        isLoading = false;
//        mListAdapter.setBanner(bannerEntities);
//    }
//
//    @Override
//    public void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities) {
//        mListDocs.setComplete();
//        isLoading = false;
//        mListAdapter.setFeaturedBeans(featuredEntities);
//    }
//
//    @Override
//    public void onDocLoadSuccess(Object entity, boolean pull) {
//        if(((ArrayList<DocListEntity>) entity).size() == 0){
//            mListDocs.setLoadMoreEnabled(false);
//            ToastUtils.showShortToast(this,getString(R.string.msg_all_load_down));
//        }else {
//            mListDocs.setLoadMoreEnabled(true);
//        }
//        mListDocs.setComplete();
//        isLoading = false;
//        if(pull){
//            mListAdapter.setDocList((ArrayList<DocListEntity>) entity);
//        }else {
//            mListAdapter.addDocList((ArrayList<DocListEntity>) entity);
//        }
//    }
//
//    @Override
//    public void onChangeSuccess(Object entity) {
//        ArrayList<BannerEntity> banner = mListAdapter.getBannerList();
//        ArrayList<FeaturedEntity> featured = mListAdapter.getFeaturedList();
//        mListAdapter = new ClassAdapter(this);
//        mListAdapter.setBanner(banner);
//        mListAdapter.setFeaturedBeans(featured);
//        mListAdapter.setDocList((ArrayList<DocListEntity>) entity);
//        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Object object = mListAdapter.getItem(position);
//                if (object != null) {
//                    if (object instanceof DocListEntity) {
//                        DocListEntity bean = (DocListEntity) object;
//                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
//                            String mSchema = bean.getDesc().getSchema();
//                            if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
//                                String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
//                                String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
//                                mSchema = begin + "uuid=" + uuid + "&from_name=广场";
//                            }
//                            Uri uri = Uri.parse(mSchema);
//                            IntentUtils.toActivityFromUri(ClassActivity.this, uri,view);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        });
//        mListDocs.getRecyclerView().setAdapter(mListAdapter);
//    }
//
//    @Override
//    public void onFollowDepartmentSuccess(boolean follow) {
//
//    }
//
//    private void sendBtnIn(){
//        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mSendPost,"translationY",mSendPost.getHeight()+ DensityUtil.dip2px(this,10),0).setDuration(300);
//        sendPostIn.setInterpolator(new OvershootInterpolator());
//        AnimatorSet set = new AnimatorSet();
//        set.play(sendPostIn);
//        set.start();
//    }
//
//    private void sendBtnOut(){
//        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mSendPost,"translationY",0,mSendPost.getHeight()+DensityUtil.dip2px(this,10)).setDuration(300);
//        sendPostOut.setInterpolator(new OvershootInterpolator());
//        AnimatorSet set = new AnimatorSet();
//        set.play(sendPostOut);
//        set.start();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == CreateRichDocActivity.RESPONSE_CODE){
//            mListDocs.getRecyclerView().scrollToPosition(0);
//            mPresenter.requestDocList(0,"",1);
//        }else if(requestCode == StartActivityConstant.REQ_DOC_DETAIL_ACTIVITY && resultCode == Activity.RESULT_OK){
//            String type = data.getStringExtra("type");
//            int position = data.getIntExtra("position", -1);
//            if(position < 2){
//                return;
//            }
//            if("delete".equals(type)){
//                mListAdapter.getDocList().remove(position - 2);
//            }else if("egg".equals(type)){
//                DocListEntity entity = (DocListEntity) mListAdapter.getItem(position);
//                int eggs = entity.getEggs();
//                if(eggs < 10){
//                    ((DocListEntity) mListAdapter.getItem(position)).setEggs(eggs + 1);
//                    mListAdapter.notifyItemChanged(position);
//                }
//            }else if("finish".equals(type)){
//
//            }
//        }
//    }
//
//    /**
//     * 前往创建帖子界面
//     */
//    private void go2CreateDoc(){
//        // 检查是否登录，是否关注，然后前面创建帖子界面
//        if (DialogUtils.checkLoginAndShowDlg(this)){
//            Intent intent = new Intent(this, CreateRichDocActivity.class);
//            intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,getString(R.string.label_square));
//            intent.putExtra("from_name",getString(R.string.label_square));
//            intent.putExtra("from_schema","neta://com.moemoe.lalala/room_1.0");
//            startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        mPresenter.release();
//        if(mListAdapter !=null )mListAdapter.onDestroy();
//        super.onDestroy();
//    }
//}