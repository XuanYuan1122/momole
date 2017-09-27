package com.moemoe.lalala.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneMainComponent;
import com.moemoe.lalala.di.modules.PhoneMainModule;
import com.moemoe.lalala.presenter.PhoneMainContract;
import com.moemoe.lalala.presenter.PhoneMainPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.PhoneAlarmFragment;
import com.moemoe.lalala.view.fragment.PhoneJuQingFragment;
import com.moemoe.lalala.view.fragment.PhoneMateFragment;
import com.moemoe.lalala.view.fragment.PhoneMateSelectFragment;
import com.moemoe.lalala.view.fragment.PhoneMenuFragment;
import com.moemoe.lalala.view.fragment.PhoneMsgFragment;
import com.moemoe.lalala.view.fragment.PhoneTicketFragment;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 * Created by yi on 2017/9/4.
 */

@SuppressWarnings("deprecation")
public class PhoneMainActivity extends BaseAppCompatActivity implements PhoneMainContract.View,IUnReadMessageObserver {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.layout_phone_main)
    View mMainRoot;
    @BindView(R.id.tv_msg)
    TextView mTvMsg;
    @BindView(R.id.iv_role)
    ImageView mIvRole;
    @BindView(R.id.iv_create_dynamic)
    ImageView mIvCreatDynamic;
    @BindView(R.id.iv_create_wenzhang)
    ImageView mIvCreateWen;
    @BindView(R.id.tv_show_text)
    TextView mTvText;
    @BindView(R.id.rl_role_root)
    RelativeLayout mRoleRoot;
    @Inject
    PhoneMainPresenter mPresenter;

    private BaseFragment mCurFragment;

    private FragmentTransaction mFragmentTransaction;
    private GestureDetector gestureDetector;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_phone;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneMainComponent.builder()
                .phoneMainModule(new PhoneMainModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        AndroidBug5497Workaround.assistActivity(this);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
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
        Uri uri = getIntent().getData();
        if(uri != null && uri.getScheme().equals("rong")){
            mMainRoot.setVisibility(View.GONE);
            mIvBack.setVisibility(View.GONE);
            mCurFragment = PhoneMsgFragment.newInstance(uri);
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneMsgFragment.TAG);
            mFragmentTransaction.commit();
        }
        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        gestureDetector = new GestureDetector(this,onGestureListener);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void hideRole(){
        ObjectAnimator roleAnimator = ObjectAnimator.ofFloat(mRoleRoot,"translationY",0,mRoleRoot.getHeight()).setDuration(300);
        roleAnimator.setInterpolator(new OvershootInterpolator());
        roleAnimator.start();
    }

    public void showRole(){
        ObjectAnimator roleAnimator = ObjectAnimator.ofFloat(mRoleRoot,"translationY",mRoleRoot.getHeight(),0).setDuration(300);
        roleAnimator.setInterpolator(new OvershootInterpolator());
        roleAnimator.start();
    }

    private static final int FLING_MIN_DISTANCE = 10;
    private static final int FLING_MIN_VELOCITY = 0;

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY){
            if (e1.getY()-e2.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                hideRole();
            } else if (e2.getY()-e1.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                showRole();
            }
            return false;
        }
    };

    @Override
    protected void initListeners() {
        mIvRole.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                clickRole();
            }
        });
        mIvCreatDynamic.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i4 = new Intent(PhoneMainActivity.this,CreateDynamicActivity.class);
                i4.putExtra("default_tag","广场");
                startActivity(i4);
            }
        });
        mIvCreateWen.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(PhoneMainActivity.this, CreateRichDocActivity.class);
                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,3);
                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"书包");
                intent.putExtra("from_name","书包");
                intent.putExtra("from_schema","neta://com.moemoe.lalala/bag_2.0");
                startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
            }
        });
    }

    private void clickRole(){
        mIvRole.setSelected(!mIvRole.isSelected());
        if(mIvRole.isSelected()){
            mIvCreatDynamic.setVisibility(View.VISIBLE);
            mIvCreateWen.setVisibility(View.VISIBLE);
            mTvText.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(PhoneMainActivity.this,R.color.alph_60));
            mRoleRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {

                }
            });
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_END);
            mIvRole.setLayoutParams(lp1);
        }else {
            mIvCreatDynamic.setVisibility(View.GONE);
            mIvCreateWen.setVisibility(View.GONE);
            mTvText.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(PhoneMainActivity.this,R.color.transparent));
            mRoleRoot.setOnClickListener(null);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mIvRole.setLayoutParams(lp1);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.setRoleButton(mIvRole);
    }

    @Override
    public void onBackPressed() {
        if(mIvRole.isSelected()){
            clickRole();
            return;
        }
        if(mIvBack.getVisibility() == View.VISIBLE){
            super.onBackPressed();
        }else {
            if(mCurFragment != null ){
                mCurFragment.onBackPressed();
            }
        }
    }

    public void finishCurFragment(){
        mMainRoot.setVisibility(View.VISIBLE);
        mIvBack.setVisibility(View.VISIBLE);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.remove(mCurFragment);
        mFragmentTransaction.commit();
        mCurFragment.release();
        mCurFragment = null;
    }

    public void changeRoleState(boolean select){
        if(mCurFragment instanceof PhoneMateFragment){
            ((PhoneMateFragment) mCurFragment).changeRoleState(select);
        }
    }

    @OnClick({R.id.ll_menu_root,R.id.ll_msg_root,R.id.ll_mate_root,R.id.ll_album_root,R.id.ll_alarm_root,R.id.ll_shop_root,R.id.ll_search_root})
    public void onClick(View v){
        mMainRoot.setVisibility(View.GONE);
        mIvBack.setVisibility(View.GONE);
        switch (v.getId()){
            case R.id.ll_menu_root:
                mCurFragment = PhoneMenuFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneMateSelectFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_msg_root:
                mCurFragment = PhoneMsgFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneMsgFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_mate_root:
                mCurFragment = PhoneMateFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneMateFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_album_root:
                mCurFragment = PhoneJuQingFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneJuQingFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_alarm_root:
                mCurFragment = PhoneAlarmFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneAlarmFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_shop_root:
                mMainRoot.setVisibility(View.VISIBLE);
                mIvBack.setVisibility(View.VISIBLE);
                Intent i7 = new Intent(PhoneMainActivity.this,CoinShopActivity.class);
                startActivity(i7);
                break;
            case R.id.ll_search_root:
                mMainRoot.setVisibility(View.VISIBLE);
                mIvBack.setVisibility(View.VISIBLE);
                Intent i6 = new Intent(PhoneMainActivity.this,SearchActivity.class);
                startActivity(i6);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        RongIM.getInstance().disconnect();
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(this);
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
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
    public void onCountChanged(int i) {
        if(i > 0){
            mTvMsg.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(this,R.drawable.ic_inform_reddot),null);
            mTvMsg.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.x4));
        }else {
            mTvMsg.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            mTvMsg.setCompoundDrawablePadding(0);
        }
    }
}
