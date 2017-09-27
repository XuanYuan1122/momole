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
import com.moemoe.lalala.di.components.DaggerPhoneMsgComponent;
import com.moemoe.lalala.di.modules.PhoneMsgModule;
import com.moemoe.lalala.presenter.PhoneMsgContract;
import com.moemoe.lalala.presenter.PhoneMsgPresenter;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.MapActivity;
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
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
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
    private boolean isFromPush = false;
    private String mCurId;
    private BottomMenuFragment mBottomMenuFragment;

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
                    if(targetId.equals("juqing")){

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
        listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
        Uri uri;
        uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
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
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        conversationFragment = enterCoversationFragment(uiConversation.getConversationType().getName(),uiConversation.getConversationTargetId());
        mFragmentTransaction.hide(mCurFragment).add(R.id.container,conversationFragment,"ConversationFragment");
        mFragmentTransaction.commit();
        mCurFragment = conversationFragment;
        mCurId = uiConversation.getConversationTargetId();
        mIvMenu.setVisibility(View.VISIBLE);
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
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onGetTimeSuccess(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        String id = JuQingUtil.checkJuQing(calendar);
        if(!TextUtils.isEmpty(id)){

        }
    }
}
