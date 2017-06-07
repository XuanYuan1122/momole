package com.moemoe.lalala.view.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
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
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.adapter.ClassAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/2.
 */

public class ClassFragment extends BaseFragment implements DepartContract.View {
    private final int REQUEST_CODE_CREATE_DOC = 2333;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_send_post)
    View mSendPost;
    @Inject
    DepartPresenter mPresenter;
    private ClassAdapter mListAdapter;
    private String mRoomId;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_pulltorefresh_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mRoomId = "CLASSROOM";
        DaggerDepartComponent.builder()
                .departModule(new DepartModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListAdapter = new ClassAdapter(getActivity());
        mListDocs.getRecyclerView().setAdapter(mListAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mListAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof DocListEntity) {
                        DocListEntity bean = (DocListEntity) object;
                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                            Uri uri = Uri.parse(bean.getDesc().getSchema());
                            IntentUtils.toActivityFromUri(getActivity(), uri,view);
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
                    if(!getActivity().isFinishing())Glide.with(getActivity()).resumeRequests();
                } else {
                    if(!getActivity().isFinishing())Glide.with(getActivity()).pauseRequests();
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
            }
        });
        mSendPost.setVisibility(View.VISIBLE);
        mSendPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc();
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.requestDocList(mListAdapter.getItemCount() - 2,"",1);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.requestBannerData(mRoomId);
                mPresenter.requestFeatured(mRoomId);
                mPresenter.requestDocList(0,"",1);
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
        mPresenter.requestDocList(0,"",1);
    }

    public void changeLabelAdapter(){
        mPresenter.requestDocList(0,"change",1);
    }

    @Override
    public void onChangeSuccess(Object entity) {
        ArrayList<BannerEntity> banner = mListAdapter.getBannerList();
        ArrayList<FeaturedEntity> featured = mListAdapter.getFeaturedList();
        mListAdapter = new ClassAdapter(getActivity());
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
                            IntentUtils.toActivityFromUri(getActivity(), uri,view);
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
    public void onDestroyView() {
        mPresenter.release();
        if(mListAdapter !=null )mListAdapter.onDestroy();
        super.onDestroyView();
    }

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mSendPost,"translationY",mSendPost.getHeight()+ DensityUtil.dip2px(getContext(),10),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mSendPost,"translationY",0,mSendPost.getHeight()+DensityUtil.dip2px(getContext(),10)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut);
        set.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateRichDocActivity.RESPONSE_CODE){
            mListDocs.getRecyclerView().scrollToPosition(0);
            mPresenter.requestDocList(0,"",1);
        }
    }

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(getActivity())){
            Intent intent = new Intent(getActivity(), CreateRichDocActivity.class);
            intent.putExtra("from_name",getString(R.string.label_square));
            intent.putExtra("from_schema","neta://com.moemoe.lalala/room_1.0");
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
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
        }else {
            mListDocs.setLoadMoreEnabled(true);
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
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }
}
