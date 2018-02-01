package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerFeedNoticeComponent;
import com.moemoe.lalala.di.modules.FeedNoticeModule;
import com.moemoe.lalala.event.FollowUserEvent;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.presenter.FeedNoticeContract;
import com.moemoe.lalala.presenter.FeedNoticePresenter;
import com.moemoe.lalala.utils.FeedRecommendUserDecoration;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FeedNoticeAdapter;
import com.moemoe.lalala.view.adapter.FeedRecommendUserAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class FeedNoticeFragment extends BaseFragment implements FeedNoticeContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.tv_show_menu)
    TextView mTvMenu;

    @Inject
    FeedNoticePresenter mPresenter;

    private View topView;
    private FeedNoticeAdapter mAdapter;
    private boolean isLoading = false;
    private BottomMenuFragment fragment;
    private long followTime;
    private long notifyTime;
    private String mCurType;
    private FeedRecommendUserAdapter recommendUserAdapter;
    private boolean isLoadTop;

    public static FeedNoticeFragment newInstance(){
        return new FeedNoticeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_guanzhu;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedNoticeComponent.builder()
                .feedNoticeModule(new FeedNoticeModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mCurType = "ALL";
        fragment = new BottomMenuFragment();
        initMenu();
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new FeedNoticeAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if(mAdapter.getItemCount() == 0){
                    followTime = notifyTime = 0;
                    mPresenter.loadFeedNoticeList(mCurType,followTime,notifyTime);
                }else {
                    mPresenter.loadFeedNoticeList(mCurType,followTime,notifyTime);
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                followTime = notifyTime = 0;
                mPresenter.loadRecommendUserList();
                mPresenter.loadFeedNoticeList(mCurType,followTime,notifyTime);
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
        mPresenter.loadRecommendUserList();
        mPresenter.loadFeedNoticeList(mCurType,followTime,notifyTime);
        int num = PreferenceUtils.hasMsg(getContext());
        if(num > 0){
            if(num > 999) num = 999;
            String content = "显示全部(" +  num + "条新通知)";
            String colorText = "(" + num + "条新通知)";
            ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.main_red));
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            style.setSpan(span, content.indexOf(colorText), content.indexOf(colorText) + colorText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvMenu.setText(style);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        EventBus.getDefault().unregister(this);
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onLoadFeedNoticeListSuccess(ArrayList<FeedNoticeEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        getNextTime(entities);
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }

    private void getNextTime(ArrayList<FeedNoticeEntity> entities){
        boolean followSet = false;
        boolean notifySet = false;

        for(int i = entities.size() - 1;i >= 0;i--){
            FeedNoticeEntity entity = entities.get(i);
            if("doc".equals(entity.getNotifyType()) || "dynamic".equals(entity.getNotifyType())){
                if(!followSet){
                    followTime = entity.getTimestamp();
                }
                followSet = true;
            }else {
                if(!notifySet){
                    notifyTime = entity.getTimestamp();
                }
                notifySet = true;
            }
            if(followSet && notifySet){
                break;
            }
        }
    }

    @Override
    public void onLikeDynamicSuccess(boolean isLike, int position) {
        NewDynamicEntity entity = new Gson().fromJson(mAdapter.getList().get(position).getTargetObj(),NewDynamicEntity.class);
        entity.setThumb(isLike);
        if(isLike){
            SimpleUserEntity userEntity = new SimpleUserEntity();
            userEntity.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
            userEntity.setUserId(PreferenceUtils.getUUid());
            userEntity.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
            entity.getThumbUsers().add(0,userEntity);
            entity.setThumbs(entity.getThumbs() + 1);
        }else {
            for(SimpleUserEntity userEntity : entity.getThumbUsers()){
                if(userEntity.getUserId().equals(PreferenceUtils.getUUid())){
                    entity.getThumbUsers().remove(userEntity);
                    break;
                }
            }
            entity.setThumbs(entity.getThumbs() - 1);
        }
        Gson gson = new Gson();
        JsonObject newObj = gson.toJsonTree(entity).getAsJsonObject();
        mAdapter.getList().get(position).setTargetObj(newObj);
        if(mAdapter.getHeaderLayoutCount() != 0){
            mAdapter.notifyItemChanged(position + 1);
        }else {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onLoadRecommendUserListSuccess(ArrayList<FeedRecommendUserEntity> entities) {
        if(entities.size() > 0){
            if(topView == null){
                topView = LayoutInflater.from(getContext()).inflate(R.layout.item_h_top_list, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) getResources().getDimension(R.dimen.y24);
                topView.setLayoutParams(lp);
                mAdapter.addHeaderView(topView);
            }
            TextView fromName = topView.findViewById(R.id.tv_from_name);
            fromName.setText("推荐关注");
            RecyclerView listView = topView.findViewById(R.id.rv_list);
            listView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
            if(!isLoadTop){
                listView.addItemDecoration(new FeedRecommendUserDecoration());
                isLoadTop = true;
            }
            recommendUserAdapter = new FeedRecommendUserAdapter();
            listView.setAdapter(recommendUserAdapter);
            recommendUserAdapter.setList(entities);
            recommendUserAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ViewUtils.toPersonal(getContext(),recommendUserAdapter.getItem(position).getUserId());
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }else {
            if(topView != null){
                mAdapter.removeHeaderView(topView);
                topView = null;
            }
        }
    }

    @Override
    public void onFollowUserSuccess(boolean isFollow, int position) {
        if(recommendUserAdapter != null) {
            recommendUserAdapter.getItem(position).setFollow(isFollow);
            recommendUserAdapter.notifyItemChanged(position);
        }
    }

    @OnClick({R.id.fl_choice})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fl_choice:
                fragment.show(getChildFragmentManager(),"DynamicActivity");
                break;
        }
    }

    public void likeDynamic(String id,boolean isLie,int position){
        mPresenter.likeDynamic(id, isLie, position);
    }

    private void initMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem item = new MenuItem(1,"显示全部");
        items.add(item);
        item = new MenuItem(2, "关注人");
        items.add(item);

        if(PreferenceUtils.getMessageDot(getContext(),"neta")
                || PreferenceUtils.getMessageDot(getContext(),"system")
                || PreferenceUtils.getMessageDot(getContext(),"at_user")
                || PreferenceUtils.getMessageDot(getContext(),"normal")){
            int num = PreferenceUtils.getNetaMsgDotNum(getContext())
                    + PreferenceUtils.getSysMsgDotNum(getContext())
                    + PreferenceUtils.getAtUserMsgDotNum(getContext())
                    + PreferenceUtils.getNormalMsgDotNum(getContext());
            if(num > 999) num = 999;
            item = new MenuItem(3,"通知("+ num +"条新消息)");
            items.add(item);
        }else {
            item = new MenuItem(3,"通知");
            items.add(item);
        }

        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    mCurType = "ALL";
                    if(PreferenceUtils.getMessageDot(getContext(),"neta")
                            || PreferenceUtils.getMessageDot(getContext(),"system")
                            || PreferenceUtils.getMessageDot(getContext(),"at_user")
                            || PreferenceUtils.getMessageDot(getContext(),"normal")){
                        int num = PreferenceUtils.getNetaMsgDotNum(getContext())
                                + PreferenceUtils.getSysMsgDotNum(getContext())
                                + PreferenceUtils.getAtUserMsgDotNum(getContext())
                                + PreferenceUtils.getNormalMsgDotNum(getContext());
                        if(num > 999) num = 999;
                        String content = "显示全部(" +  num + "条新通知)";
                        String colorText = "(" + num + "条新通知)";
                        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.main_red));
                        SpannableStringBuilder style = new SpannableStringBuilder(content);
                        style.setSpan(span, content.indexOf(colorText), content.indexOf(colorText) + colorText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTvMenu.setText(style);
                    }else {
                        mTvMenu.setText("显示全部");
                    }
                } else if(itemId == 2){
                    mCurType = "FOLLOW";
                    mTvMenu.setText("关注人");
                } else if(itemId == 3){
                    mCurType = "NOTIFY";
                    mTvMenu.setText("通知");
                    PreferenceUtils.setNetaMsgDotNum(getContext(),0);
                    PreferenceUtils.setSysMsgDotNum(getContext(),0);
                    PreferenceUtils.setAtUserMsgDotNum(getContext(),0);
                    PreferenceUtils.setNormalMsgDotNum(getContext(),0);
                    PreferenceUtils.setMessageDot(getContext(),"neta",false);
                    PreferenceUtils.setMessageDot(getContext(),"system",false);
                    PreferenceUtils.setMessageDot(getContext(),"at_user",false);
                    PreferenceUtils.setMessageDot(getContext(),"normal",false);
                    EventBus.getDefault().post(new SystemMessageEvent(""));
                }
                followTime = notifyTime = 0;
                mPresenter.loadFeedNoticeList(mCurType,followTime,notifyTime);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void systemMsgEvent(SystemMessageEvent event){
        if("ALL".equals(mCurType)){
            if(PreferenceUtils.getMessageDot(getContext(),"neta")
                    || PreferenceUtils.getMessageDot(getContext(),"system")
                    || PreferenceUtils.getMessageDot(getContext(),"at_user")
                    || PreferenceUtils.getMessageDot(getContext(),"normal")){
                int num = PreferenceUtils.getNetaMsgDotNum(getContext())
                        + PreferenceUtils.getSysMsgDotNum(getContext())
                        + PreferenceUtils.getAtUserMsgDotNum(getContext())
                        + PreferenceUtils.getNormalMsgDotNum(getContext());
                if(num > 999) num = 999;
                String content = "显示全部(" +  num + "条新通知)";
                String colorText = "(" + num + "条新通知)";
                ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.main_red));
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(span, content.indexOf(colorText), content.indexOf(colorText) + colorText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTvMenu.setText(style);
            }else {
                mTvMenu.setText("显示全部");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void followUserEvent(FollowUserEvent event){
        mPresenter.followUser(event.getUserId(),event.isFollow(),event.getPosition());
    }
}
