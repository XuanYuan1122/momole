package com.moemoe.lalala.view.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerClassMainComponent;
import com.moemoe.lalala.di.modules.ClassMainModule;
import com.moemoe.lalala.di.modules.DiscoveryModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.presenter.ClassMainContract;
import com.moemoe.lalala.presenter.ClassMainPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StartActivityConstant;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.activity.WallActivity;
import com.moemoe.lalala.view.adapter.ClassMainAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

import static com.moemoe.lalala.view.activity.CreateRichDocActivity.REQUEST_CODE_CREATE_DOC;

/**
 * Created by yi on 2016/12/15.
 */

public class ClassMainFragment extends BaseFragment  implements ClassMainContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_send_post)
    View mSendPost;
    @Inject
    ClassMainPresenter mPresenter;

    private boolean isLoading = false;
    private ClassMainAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_pulltorefresh_list;
    }

    public static ClassMainFragment newInstance(){
        ClassMainFragment fragment = new ClassMainFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        DaggerClassMainComponent.builder()
                .classMainModule(new ClassMainModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.setVisibility(View.VISIBLE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new ClassMainAdapter(null);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mSendPost.setVisibility(View.VISIBLE);
        mSendPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DocListEntity bean = mAdapter.getItem(position);
                if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                    String mSchema = bean.getDesc().getSchema();
                    if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                        String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                        String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                        mSchema = begin + "uuid=" + uuid + "&from_name=广场&position=" + position;
                    }
                    Uri uri = Uri.parse(mSchema);
                    IntentUtils.toActivityForResultFromUri(getContext(), uri,view, StartActivityConstant.REQ_DOC_DETAIL_ACTIVITY);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isChange = false;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadDocList(mAdapter.getList().size(),false,false);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
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
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
        ImageView iv = new ImageView(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(getContext(),70));
        lp.topMargin = DensityUtil.dip2px(getContext(),10);
        lp.bottomMargin = DensityUtil.dip2px(getContext(),10);
        lp.leftMargin = DensityUtil.dip2px(getContext(),10);
        lp.rightMargin = DensityUtil.dip2px(getContext(),10);

        iv.setLayoutParams(lp);
        iv.setImageResource(R.drawable.btn_feed_club);
        iv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(getContext(), WallActivity.class);
                startActivity(i);
            }
        });
        frameLayout.addView(iv);
        mAdapter.addHeaderView(frameLayout);
        mPresenter.loadDocList(0,false,true);
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        RxBus.getInstance().unSubscribe(this);
        super.release();
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

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(getContext())){
            Intent intent = new Intent(getContext(), CreateRichDocActivity.class);
            intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,getString(R.string.label_square));
            intent.putExtra("from_name",getString(R.string.label_square));
            intent.putExtra("from_schema","neta://com.moemoe.lalala/room_1.0");
            startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
        }
    }

    public void changeLabelAdapter(){
        mPresenter.loadDocList(0,true,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateRichDocActivity.RESPONSE_CODE){
            mListDocs.getRecyclerView().scrollToPosition(0);
            mPresenter.loadDocList(0,false,true);
        }else if(requestCode == StartActivityConstant.REQ_DOC_DETAIL_ACTIVITY && resultCode == Activity.RESULT_OK){
            String type = data.getStringExtra("type");
            int position = data.getIntExtra("position", -1);
            if(position < 2){
                return;
            }
            if("delete".equals(type)){
                mAdapter.getList().remove(position);
                mAdapter.notifyDataSetChanged();
            }else if("egg".equals(type)){
                DocListEntity entity = mAdapter.getItem(position);
                int eggs = entity.getEggs();
                if(eggs < 10){
                    mAdapter.getItem(position).setEggs(eggs + 1);
                    mAdapter.notifyItemChanged(position);
                }
            }else if("finish".equals(type)){

            }
        }
    }

    @Override
    public void onChangeSuccess(ArrayList<DocListEntity> entities) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
        }
        mAdapter.setList(entities);
    }

    @Override
    public void onLoadDocListSuccess(ArrayList<DocListEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }
}
