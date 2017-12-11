package com.moemoe.lalala.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 *
 * Created by yi on 2017/11/9.
 */

public class KiraConversationFragment extends ConversationFragment implements IPhoneFragment,RongIM.ConversationBehaviorListener{

    private String title;
    private int menuRes;
    private String mType;
    private String mId;

    public static KiraConversationFragment newInstance(String id,String title,String type,Uri uri){
        KiraConversationFragment fragment = new KiraConversationFragment();
        fragment.setUri(uri);
        fragment.setTitle(title);
        if(Conversation.ConversationType.PRIVATE.getName().equals(type)){
            fragment.setMenu(R.drawable.btn_black_user);
        }else {
            fragment.setMenu(R.drawable.btn_menu_normal);
        }
        fragment.setType(type);
        fragment.setId(id);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initFragment(Uri uri) {
        RongIM.setConversationBehaviorListener(this);
        super.initFragment(uri);
    }

    @Override
    public MessageListAdapter onResolveAdapter(Context context) {
        return super.onResolveAdapter(context);
    }

    public void setType(String type) {
        this.mType = type;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setMenu(@DrawableRes int res){
        this.menuRes = res;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return menuRes;
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
        if(Conversation.ConversationType.PRIVATE.getName().equals(mType)){
            if(!TextUtils.isEmpty(mId)){
                RongIM.getInstance().getBlacklistStatus(mId, new RongIMClient.ResultCallback<RongIMClient.BlacklistStatus>() {
                    @Override
                    public void onSuccess(RongIMClient.BlacklistStatus blacklistStatus) {
                        black(blacklistStatus);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            }
        }else if(Conversation.ConversationType.GROUP.getName().equals(mType)){
            ((PhoneMainV2Activity)getContext()).toFragment(PhoneGroupDetailV2Fragment.newInstance(mId));
        }
    }

    private void black(final RongIMClient.BlacklistStatus blacklistStatus){
        BottomMenuFragment mBottomMenuFragment = new BottomMenuFragment();
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
                    if(mId != null){
                        if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
                            RongIM.getInstance().removeFromBlacklist(mId, new RongIMClient.OperationCallback() {
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
                            RongIM.getInstance().addToBlacklist(mId, new RongIMClient.OperationCallback() {
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
            }
        });
        mBottomMenuFragment.show(getFragmentManager(),"MSG");
    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        ViewUtils.toPersonal(context,userInfo.getUserId());
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
}
