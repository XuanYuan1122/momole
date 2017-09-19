//package com.moemoe.lalala.view.activity;
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.utils.AndroidBug5497Workaround;
//import com.moemoe.lalala.utils.PreferenceUtils;
//import com.moemoe.lalala.utils.ViewUtils;
//
//import java.util.Locale;
//
//import butterknife.BindView;
//import io.rong.imkit.RongIM;
//import io.rong.imkit.fragment.ConversationFragment;
//import io.rong.imlib.RongIMClient;
//import io.rong.imlib.model.Conversation;
//
///**
// * Created by yi on 2017/9/7.
// */
//
//public class IMChatActivity extends BaseAppCompatActivity {
//
//    @BindView(R.id.iv_back)
//    ImageView mIvBack;
//    @BindView(R.id.tv_toolbar_title)
//    TextView mTvTitle;
//
//    private String mTargetId;
//    private Conversation.ConversationType mConversationType;
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
//        AndroidBug5497Workaround.assistActivity(this);
//        mTargetId = getIntent().getData().getQueryParameter("targetId");
//        String title = getIntent().getData().getQueryParameter("title");
//        mConversationType = Conversation.ConversationType.valueOf(getIntent().getData()
//                .getLastPathSegment().toUpperCase(Locale.US));
//        mTvTitle.setText(title);
//        if(!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)){
//            createDialog();
//            RongIM.connect(PreferenceUtils.getAuthorInfo().getRcToken(), new RongIMClient.ConnectCallback() {
//                @Override
//                public void onTokenIncorrect() {
//
//                }
//
//                @Override
//                public void onSuccess(String s) {
//                    finalizeDialog();
//                    enterFragment();
//                }
//
//                @Override
//                public void onError(RongIMClient.ErrorCode errorCode) {
//                    finalizeDialog();
//                    enterFragment();
//                }
//            });
//        }else {
//            enterFragment();
//        }
//    }
//
//    private void enterFragment(){
//        ConversationFragment fragment = new ConversationFragment();
//        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
//                .appendQueryParameter("targetId", mTargetId).build();
//        fragment.setUri(uri);
//        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
//        //xxx 为你要加载的 id
//        mFragmentTransaction.add(R.id.container, fragment);
//        mFragmentTransaction.commitAllowingStateLoss();
//    }
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
//    protected void onDestroy() {
//        RongIM.getInstance().disconnect();
//        super.onDestroy();
//    }
//}
