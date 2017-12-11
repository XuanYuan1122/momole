package com.moemoe.lalala.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPhoneMsgComponent;
import com.moemoe.lalala.di.modules.PhoneMsgModule;
import com.moemoe.lalala.event.EventDoneEvent;
import com.moemoe.lalala.event.GroupMsgChangeEvent;
import com.moemoe.lalala.presenter.PhoneMsgContract;
import com.moemoe.lalala.presenter.PhoneMsgPresenter;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.ConversationListAdapterNew;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

/**
 *
 * Created by yi on 2017/11/20.
 */

public class KiraConversationListFragment extends ConversationListFragment implements IPhoneFragment,PhoneMsgContract.View,RongIM.ConversationListBehaviorListener{

    @Inject
    PhoneMsgPresenter mPresenter;

    private String Title;
    private ConversationListAdapterNew mAdapter;

    public static KiraConversationListFragment newInstance(Uri uri){
        KiraConversationListFragment fragment = new KiraConversationListFragment();
        fragment.setUri(uri);
        return fragment;
    }

    @Override
    public ConversationListAdapter onResolveAdapter(Context context) {
        return mAdapter;
    }

    public KiraConversationListFragment() {
        mAdapter = new ConversationListAdapterNew(RongContext.getInstance());
        Title = "消息";
    }

    public ConversationListAdapterNew getAdapter() {
        return mAdapter;
    }

    @Override
    protected void initFragment(Uri uri) {
        DaggerPhoneMsgComponent.builder()
                .phoneMsgModule(new PhoneMsgModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        RongIM.setConversationListBehaviorListener(this);
        subscribeSearchChangedEvent();
        super.initFragment(uri);
        UIConversation item = new UIConversation();
        item.setConversationType(Conversation.ConversationType.PRIVATE);
        item.setLatestMessageId(-1);
        item.setTop(true);
        item.setConversationTargetId("len");
        item.setUIConversationTitle("len");
        item.setUIConversationTime(Long.MAX_VALUE);
        mAdapter.add(item);
        UIConversation item1 = new UIConversation();
        item1.setConversationType(Conversation.ConversationType.PRIVATE);
        item1.setLatestMessageId(-1);
        item1.setTop(true);
        item1.setConversationTargetId("mei");
        item1.setUIConversationTitle("mei");
        item1.setUIConversationTime(Long.MAX_VALUE - 1);
        mAdapter.add(item1);
        UIConversation item2 = new UIConversation();
        item2.setConversationType(Conversation.ConversationType.PRIVATE);
        item2.setLatestMessageId(-1);
        item2.setTop(true);
        item2.setConversationTargetId("sari");
        item2.setUIConversationTitle("sari");
        item2.setUIConversationTime(Long.MAX_VALUE - 2);
        mAdapter.add(item2);
        UIConversation item3 = new UIConversation();
        item3.setConversationType(Conversation.ConversationType.PRIVATE);
        item3.setLatestMessageId(-1);
        item3.setTop(true);
        item3.setConversationTargetId("kira_system");
        item3.setUIConversationTitle("kira_system");
        item3.setUIConversationTime(Long.MAX_VALUE - 3);
        mAdapter.add(item3);
        mAdapter.notifyDataSetChanged();
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
                            mAdapter.setShowRed("");
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        Disposable subscription1 = RxBus.getInstance()
                .toObservable(GroupMsgChangeEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<GroupMsgChangeEvent>() {
                    @Override
                    public void accept(GroupMsgChangeEvent eventDoneEvent) throws Exception {
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
        RxBus.getInstance().addSubscription(this, subscription1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) mPresenter.release();
        RxBus.getInstance().unSubscribe(this);
    }

    @Override
    public String getTitle() {
        return Title;
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return R.drawable.btn_add_group;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onMenuClick() {
        BottomMenuFragment mBottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(3, "我的群聊");
        items.add(item);
        item = new MenuItem(1, "加入群聊");
        items.add(item);
        item = new MenuItem(2, "vip创建群聊(需审核)");
        items.add(item);
        mBottomMenuFragment.setMenuItems(items);
        mBottomMenuFragment.setShowTop(false);
        mBottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        mBottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(DialogUtils.checkLoginAndShowDlg(getContext())){
                    if (itemId == 1) {
                        ((PhoneMainV2Activity)getContext()).toFragment(PhoneGroupListV2Fragment.newInstance(false,"加入群聊"));
                    }
                    if(itemId == 2){
                        ((PhoneMainV2Activity)getContext()).toFragment(PhoneEditGroupV2Fragment.newInstance("create"));
                    }
                    if(itemId == 3){
                        ((PhoneMainV2Activity)getContext()).toFragment(PhoneGroupListV2Fragment.newInstance(true,"我的群聊"));
                    }
                }
            }
        });
        mBottomMenuFragment.show(getChildFragmentManager(),"kira_msg_list");
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onGetTimeSuccess(Date time, String role) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        if(TextUtils.isEmpty(role)){
            String[] id = JuQingUtil.checkJuQingMobile(calendar);
            if(!TextUtils.isEmpty(id[0])){
                if(id[1].equals("len")){
                    mAdapter.setShowRed("len");
                    mAdapter.notifyDataSetChanged();
                }
                if(id[1].equals("mei")){
                    mAdapter.setShowRed("mei");
                    mAdapter.notifyDataSetChanged();
                }
                if(id[1].equals("sari")){
                    mAdapter.setShowRed("sari");
                    mAdapter.notifyDataSetChanged();
                }
            }
        }else {
            String[] id = JuQingUtil.checkJuQingMobile(calendar,role);
            if(!TextUtils.isEmpty(id[0])){
                BaseFragment juQingChatFragment =  JuQingChatV2Fragment.newInstance(role,id[0]);
                ((PhoneMainV2Activity)getContext()).toFragment(juQingChatFragment);
            }
        }
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
        return uiConversation.getUIConversationTitle().equals("len") || uiConversation.getUIConversationTitle().equals("mei") || uiConversation.getUIConversationTitle().equals("sari");
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        if("len".equals(uiConversation.getUIConversationTitle())){
            mPresenter.getServerTime("len");
        }else if("mei".equals(uiConversation.getUIConversationTitle())){
            mPresenter.getServerTime("mei");
        }else if("sari".equals(uiConversation.getUIConversationTitle())){
            mPresenter.getServerTime("sari");
        }else if("kira_system".equals(uiConversation.getUIConversationTitle())){
            ((PhoneMainV2Activity)getContext()).toFragment(PhoneMsgListV2Fragment.newInstance());
        }else {
            Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(uiConversation.getConversationType().getName())
                    .appendQueryParameter("targetId", uiConversation.getConversationTargetId()).build();
            ((PhoneMainV2Activity)getContext()).toFragment(KiraConversationFragment.newInstance(uiConversation.getConversationTargetId(),uiConversation.getUIConversationTitle(),uiConversation.getConversationType().getName(),uri));
        }
        return true;
    }
}
