package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerDepartComponent;
import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.SendSubmissionEntity;
import com.moemoe.lalala.presenter.DepartContract;
import com.moemoe.lalala.presenter.DepartPresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.DepartmentDecoration;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.NewFolderWenZhangActivity;
import com.moemoe.lalala.view.adapter.ClassRecyclerViewAdapter;
import com.moemoe.lalala.view.adapter.NewDepartmentAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.rong.imlib.model.Conversation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_WEN_ZHANG;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class DepartmentFragment extends BaseFragment implements DepartContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_to_wen)
    ImageView mIvTouGao;

    @Inject
    DepartPresenter mPresenter;
    private NewDepartmentAdapter mListAdapter;
    private boolean mIsLoading = false;
    private String mRoomId;
    private Banner banner;
    private View bannerView;
    private View featuredView;
    private View groupView;

    public static DepartmentFragment newInstance(String id,String name){
        DepartmentFragment fragment = new DepartmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("name",name);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerDepartComponent.builder()
                .departModule(new DepartModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mRoomId = getArguments().getString("id");
        mIvTouGao.setVisibility(View.VISIBLE);
        mIvTouGao.setImageResource(R.drawable.btn_tougao);
        final String name = getArguments().getString("name");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.getRecyclerView().addItemDecoration(new DepartmentDecoration());
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mListAdapter = new NewDepartmentAdapter();
        mListDocs.getRecyclerView().addItemDecoration(new MenuVItemDecoration((int) getResources().getDimension(R.dimen.y24)));
        mListDocs.getRecyclerView().setAdapter(mListAdapter);

        mListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DepartmentEntity.DepartmentDoc bean = mListAdapter.getItem(position);
                if (bean != null) {
                    if (!TextUtils.isEmpty(bean.getSchema())) {
                        String mSchema = bean.getSchema();
                        if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                            String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                            String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                            mSchema = begin + "uuid=" + uuid + "&from_name=" + name;
                        }
                        Uri uri = Uri.parse(mSchema);
                        IntentUtils.toActivityFromUri(getContext(), uri,view);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mIvTouGao.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(DialogUtils.checkLoginAndShowDlg(getContext())){
                    NewFolderWenZhangActivity.startActivityForResult(DepartmentFragment.this, PreferenceUtils.getUUid(), FolderType.WZ.toString(),"my",true);
                }
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.requestDocList(mListAdapter.getList().size(),mRoomId,0);
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.requestBannerData(mRoomId);
                mPresenter.requestFeatured(mRoomId);
                mPresenter.loadDepartmentGroup(mRoomId);
                mPresenter.requestDocList(0,mRoomId,0);
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
        mPresenter.loadIsFollow(mRoomId);
        mPresenter.loadDepartmentGroup(mRoomId);
        mPresenter.requestBannerData(mRoomId);
        mPresenter.requestFeatured(mRoomId);
        mPresenter.requestDocList(0,mRoomId,0);
    }

    @Override
    public void onFailure(int code, String msg) {
        ((BaseAppCompatActivity)getContext()).finalizeDialog();
        mIsLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onBannerLoadSuccess(final ArrayList<BannerEntity> bannerEntities) {
        if(bannerEntities.size() > 0){
            if(bannerView == null){
                bannerView = LayoutInflater.from(getContext()).inflate(R.layout.item_new_banner, null);
                banner =  bannerView.findViewById(R.id.banner);
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
                        IntentUtils.toActivityFromUri(getContext(), uri,null);
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

    @Override
    public void onFeaturedLoadSuccess(final ArrayList<FeaturedEntity> featuredEntities) {
        if(featuredEntities.size() > 0){
            if(featuredView == null){
                featuredView  = LayoutInflater.from(getContext()).inflate(R.layout.item_class_featured, null);
                int count = mListAdapter.getHeaderViewCount();
                if(count == 0){
                    mListAdapter.addHeaderView(featuredView);
                }else {
                    View v = mListAdapter.getmHeaderLayout().getChildAt(0);
                    if(v == bannerView){
                        mListAdapter.addHeaderView(featuredView,1);
                    }else {
                        mListAdapter.addHeaderView(featuredView,0);
                    }
                }
            }
            RecyclerView rvList = featuredView.findViewById(R.id.rv_class_featured);
            rvList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)getResources().getDimension(R.dimen.y220)));
            rvList.setBackgroundColor(Color.WHITE);
            final ClassRecyclerViewAdapter recyclerViewAdapter = new ClassRecyclerViewAdapter(getContext());
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
                        IntentUtils.toActivityFromUri(getContext(), uri,view);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            recyclerViewAdapter.setData(featuredEntities);
        }else {
            if(featuredView != null){
                mListAdapter.removeHeaderView(featuredView);
                featuredView = null;
            }
        }
    }

    @Override
    public void onLoadGroupSuccess(ArrayList<DepartmentGroupEntity> entities) {
        if(entities.size() > 0){
            if(groupView == null){
                groupView = LayoutInflater.from(getContext()).inflate(R.layout.item_department_group, null);
                mListAdapter.addHeaderView(groupView);
            }
            final DepartmentGroupEntity entity = entities.get(0);
            ImageView cover = groupView.findViewById(R.id.iv_group_img);
            TextView title = groupView.findViewById(R.id.tv_group_name);
            TextView num = groupView.findViewById(R.id.tv_group_num);
            ImageView addGroup = groupView.findViewById(R.id.iv_add_group);

            int size = (int) getResources().getDimension(R.dimen.y80);
            Glide.with(getContext())
                    .load(StringUtils.getUrl(getContext(),entity.getCover(),size,size,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), (int) getResources().getDimension(R.dimen.y8),0))
                    .into(cover);
            title.setText(entity.getGroupName());
            num.setText(entity.getUsers() + " 人");

            addGroup.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(entity.isJoin()){
                        Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                                .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName())
                                .appendQueryParameter("targetId",entity.getId())
                                .appendQueryParameter("title", entity.getGroupName()).build();
                        IntentUtils.toActivityFromUri(getContext(),uri,null);
                    }else {
                        if(entity.isAuthority()){
                            mPresenter.joinAuthor(entity.getId(),entity.getGroupName());
                        }else {
                            Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                                    .appendPath("conversation").appendPath("detail")
                                    .appendQueryParameter("targetId",entity.getId())
                                    .appendQueryParameter("title", entity.getGroupName()).build();
                            IntentUtils.toActivityFromUri(getContext(),uri,null);
                        }
                    }
                }
            });
        }else {
            if(groupView != null){
                mListAdapter.removeHeaderView(groupView);
                groupView = null;
            }
        }
    }

    @Override
    public void onJoinSuccess(String id, String name) {
        Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName())
                .appendQueryParameter("targetId",id)
                .appendQueryParameter("title", name).build();
        IntentUtils.toActivityFromUri(getContext(),uri,null);
    }

    @Override
    public void onDocLoadSuccess(Object entity, boolean pull) {
        mIsLoading = false;
        if(((DepartmentEntity)entity).getList().size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        mListDocs.setComplete();
        if(pull){
            mListAdapter.setList(((DepartmentEntity)entity).getList());
        }else {
            mListAdapter.addList(((DepartmentEntity)entity).getList());
        }
    }

    @Override
    public void onChangeSuccess(Object entity) {

    }

    @Override
    public void onFollowDepartmentSuccess(boolean follow) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_WEN_ZHANG && resultCode == RESULT_OK  && data != null){
            String docId = data.getStringExtra("docId");
            SendSubmissionEntity e = new SendSubmissionEntity(mRoomId,docId);
            ((BaseAppCompatActivity)getContext()).createDialog();
            mPresenter.submission(e);
        }
    }

    @Override
    public void onSubmissionSuccess() {
        ((BaseAppCompatActivity)getContext()).finalizeDialog();
        ToastUtils.showShortToast(getContext(),"投稿成功");
    }


}
