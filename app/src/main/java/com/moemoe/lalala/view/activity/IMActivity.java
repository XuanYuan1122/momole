//package com.moemoe.lalala.view.activity;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.utils.NoDoubleClickListener;
//import com.moemoe.lalala.utils.ViewUtils;
//import com.moemoe.lalala.view.adapter.ConversationListAdapterEx;
//
//import butterknife.BindView;
//import io.rong.imkit.RongContext;
//import io.rong.imkit.RongIM;
//import io.rong.imkit.fragment.ConversationListFragment;
//import io.rong.imkit.model.UIConversation;
//import io.rong.imlib.model.Conversation;
//
///**
// *
// * Created by yi on 2017/9/7.
// */
//
//@SuppressWarnings("deprecation")
//public class IMActivity extends BaseAppCompatActivity implements RongIM.ConversationListBehaviorListener{
//
//    @BindView(R.id.iv_back)
//    ImageView mIvBack;
//
//    private FragmentTransaction mFragmentTransaction;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ac_im;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
//        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
//        ConversationListFragment fragment = initConversationList();
//        mFragmentTransaction.add(R.id.container,fragment,"IM");
//        mFragmentTransaction.commit();
//        RongIM.setConversationListBehaviorListener(this);
//    }
//
//    private ConversationListFragment initConversationList() {
//            ConversationListFragment listFragment = new ConversationListFragment();
//            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
//            Uri uri;
//                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                        .appendPath("conversationlist")
//                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
//                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
//                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
//                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
//                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
//                        .build();
//            listFragment.setUri(uri);
//            return listFragment;
//    }
//
//    @Override
//    protected void initToolbar(Bundle savedInstanceState) {
//        mIvBack.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                finish();
//            }
//        });
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
//    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
//        return true;
//    }
//
//    @Override
//    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
//        return true;
//    }
//
//    @Override
//    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
//        return true;
//    }
//
//    @Override
//    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
//        return false;
//    }
//}
