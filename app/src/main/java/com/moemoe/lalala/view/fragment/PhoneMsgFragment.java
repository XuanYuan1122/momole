package com.moemoe.lalala.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPhoneMsgComponent;
import com.moemoe.lalala.di.modules.PhoneMsgModule;
import com.moemoe.lalala.event.BackSchoolEvent;
import com.moemoe.lalala.event.EventDoneEvent;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.presenter.PhoneMsgContract;
import com.moemoe.lalala.presenter.PhoneMsgPresenter;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.activity.SplashActivity;
import com.moemoe.lalala.view.adapter.ConversationListAdapterEx;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 聊天和手机剧情root界面
 * Created by yi on 2017/9/8.
 */

@SuppressWarnings("deprecation")
public class PhoneMsgFragment extends BaseFragment implements PhoneMsgContract.View,RongIM.ConversationListBehaviorListener{

    public static final String TAG = "PhoneMsgFragment";

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;

    @Inject
    PhoneMsgPresenter mPresenter;

    private Fragment mCurFragment;
    private ConversationListFragment conversationListFragment;
    private ConversationFragment conversationFragment;
    private JuQingChatFragment juQingChatFragment;
    private boolean isFromPush = false;
    private String mCurId;
    private BottomMenuFragment mBottomMenuFragment;
    private ConversationListAdapterEx mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_msg;
    }

    public static PhoneMsgFragment newInstance(){
        return new PhoneMsgFragment();
    }

    public static PhoneMsgFragment newInstance(Uri uri){
        PhoneMsgFragment fragment = new PhoneMsgFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("uri",uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getServerTime("");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneMsgComponent.builder()
                .phoneMsgModule(new PhoneMsgModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        if(!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)){
            if(!TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getRcToken())){
                RongIM.connect(PreferenceUtils.getAuthorInfo().getRcToken(), new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        mPresenter.loadRcToken();
                    }

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }else {
                mPresenter.loadRcToken();
            }
        }
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("消息");
        mIvMenu.setImageResource(R.drawable.btn_black_user);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(!TextUtils.isEmpty(mCurId)){
                    RongIM.getInstance().getBlacklistStatus(mCurId, new RongIMClient.ResultCallback<RongIMClient.BlacklistStatus>() {
                        @Override
                        public void onSuccess(RongIMClient.BlacklistStatus blacklistStatus) {
                            black(blacklistStatus);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                }
            }
        });
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        mCurFragment = conversationListFragment = initConversationList();
        mFragmentTransaction.add(R.id.container,conversationListFragment,"ConversationListFragment");
        mFragmentTransaction.commit();
        RongIM.setConversationListBehaviorListener(this);
        if(getArguments() != null){
            Uri uri = getArguments().getParcelable("uri");
            if(uri != null){
                if(uri.getPath().equals("/conversation/private")){
                    String targetId = uri.getQueryParameter("targetId");
                    if(targetId.contains("juqing")){
                        String[] juQing = targetId.split(":");
                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                        juQingChatFragment =  JuQingChatFragment.newInstance(juQing[2],juQing[1]);
                        fragmentTransaction.hide(mCurFragment).add(R.id.container,juQingChatFragment,"juQingFragment");
                        fragmentTransaction.commit();
                        mCurFragment = juQingChatFragment;
                    }else {
                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                        conversationFragment = enterCoversationFragment("private",targetId);
                        fragmentTransaction.hide(mCurFragment).add(R.id.container,conversationFragment,"ConversationFragment");
                        fragmentTransaction.commit();
                        mCurFragment = conversationFragment;
                        mCurId = targetId;
                        if(uri.getQueryParameter("isFromPush") != null && uri.getQueryParameter("isFromPush").equals("true")){
                            isFromPush = true;
                        }
                        mIvMenu.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        subscribeSearchChangedEvent();
    }

    private void black(final RongIMClient.BlacklistStatus blacklistStatus){
        mBottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1, blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST?"移除黑名单":"加入黑名单");
        items.add(item);
        mBottomMenuFragment.setMenuItems(items);
        mBottomMenuFragment.setShowTop(false);
        mBottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        mBottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 1){
                    if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
                        RongIM.getInstance().removeFromBlacklist(mCurId, new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showShortToast(getContext(),"移除成功");
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                ToastUtils.showShortToast(getContext(),"移除失败");
                            }
                        });
                    }else {
                        RongIM.getInstance().addToBlacklist(mCurId, new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showShortToast(getContext(),"加入成功");
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                ToastUtils.showShortToast(getContext(),"加入失败");
                            }
                        });
                    }
                }
            }
        });
        mBottomMenuFragment.show(getFragmentManager(),"MSG");
    }

    private ConversationListFragment initConversationList() {
        ConversationListFragment listFragment = new ConversationListFragment();
        mAdapter = new ConversationListAdapterEx(RongContext.getInstance());
        listFragment.setAdapter(mAdapter);
        Uri uri;
        uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                .build();
        listFragment.setUri(uri);
        return listFragment;
    }

    private ConversationFragment enterCoversationFragment(String type, String targetId){
        ConversationFragment fragment = new ConversationFragment();
        Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(type)
                .appendQueryParameter("targetId", targetId).build();
        fragment.setUri(uri);
        return fragment;
    }

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return true;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return true;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        if(uiConversation.getUIConversationTitle().equals("len") || uiConversation.getUIConversationTitle().equals("mei") || uiConversation.getUIConversationTitle().equals("sari")){
            return true;
        }
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        if(uiConversation.getUIConversationTitle().equals("len")){
            mPresenter.getServerTime("len");
        }else if(uiConversation.getUIConversationTitle().equals("mei")){
            mPresenter.getServerTime("mei");
        }else if(uiConversation.getUIConversationTitle().equals("sari")){
            mPresenter.getServerTime("sari");
        }else {
            FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
            conversationFragment = enterCoversationFragment(uiConversation.getConversationType().getName(),uiConversation.getConversationTargetId());
            mFragmentTransaction.hide(mCurFragment).add(R.id.container,conversationFragment,"ConversationFragment");
            mFragmentTransaction.commit();
            mCurFragment = conversationFragment;
            mCurId = uiConversation.getConversationTargetId();
            mIvMenu.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mCurFragment != null && mCurFragment instanceof ConversationFragment){
            FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
            mFragmentTransaction.remove(mCurFragment).show(conversationListFragment);
            mFragmentTransaction.commit();
            mCurFragment = conversationListFragment;
            conversationFragment = null;
            mCurId = null;
            mIvMenu.setVisibility(View.GONE);
        }else if(mCurFragment != null && mCurFragment instanceof JuQingChatFragment){
            FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
            mFragmentTransaction.remove(mCurFragment).show(conversationListFragment);
            mFragmentTransaction.commit();
            mCurFragment = conversationListFragment;
            juQingChatFragment.release();
            juQingChatFragment = null;
            mCurId = null;
            mIvMenu.setVisibility(View.GONE);
        }else {
            if(isFromPush){
                isFromPush = false;
                Intent resultIntent = new Intent(getContext(),
                        SplashActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(resultIntent);
                ((PhoneMainActivity)getContext()).finish();
            }else {
                ((PhoneMainActivity)getContext()).finishCurFragment();
            }
        }
    }

    @Override
    public void release() {
        if(mPresenter != null) mPresenter.release();
        RongIM.getInstance().disconnect();
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }

    private void subscribeSearchChangedEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(EventDoneEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<EventDoneEvent>() {
                    @Override
                    public void accept(EventDoneEvent eventDoneEvent) throws Exception {
                        if(eventDoneEvent.getType().equals("mobile")){
                            mAdapter.setShowRed(-1);
                            mAdapter.notifyDataSetChanged();
                            PreferenceUtils.setDot(getContext(),false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadRcTokenSuccess(String token) {
        PreferenceUtils.getAuthorInfo().setRcToken(token);
        RongIM.connect(PreferenceUtils.getAuthorInfo().getRcToken(), new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    @Override
    public void onLoadRcTokenFail(int code, String msg) {

    }

    @Override
    public void onGetTimeSuccess(Date time,String role) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        if(TextUtils.isEmpty(role)){
            String[] id = JuQingUtil.checkJuQingMobile(calendar);
            if(!TextUtils.isEmpty(id[0])){
                if(id[1].equals("len")){
                    mAdapter.setShowRed(0);
                    mAdapter.notifyDataSetChanged();
                }
                if(id[1].equals("mei")){
                    mAdapter.setShowRed(1);
                    mAdapter.notifyDataSetChanged();
                }
                if(id[1].equals("sari")){
                    mAdapter.setShowRed(2);
                    mAdapter.notifyDataSetChanged();
                }
                PreferenceUtils.setDot(getContext(),true);
            }
        }else {
            String[] id = JuQingUtil.checkJuQingMobile(calendar,role);
            if(!TextUtils.isEmpty(id[0])){
                FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
                juQingChatFragment =  JuQingChatFragment.newInstance(role,id[0]);
                mFragmentTransaction.hide(mCurFragment).add(R.id.container,juQingChatFragment,"juQingFragment");
                mFragmentTransaction.commit();
                mCurFragment = juQingChatFragment;
            }
        }
    }
}
