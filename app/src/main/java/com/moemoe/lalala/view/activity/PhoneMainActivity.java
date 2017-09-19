package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneMainComponent;
import com.moemoe.lalala.di.modules.PhoneMainModule;
import com.moemoe.lalala.presenter.PhoneMainContract;
import com.moemoe.lalala.presenter.PhoneMainPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.adapter.ConversationListAdapterEx;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.PhoneAlarmEditFragment;
import com.moemoe.lalala.view.fragment.PhoneAlarmFragment;
import com.moemoe.lalala.view.fragment.PhoneAlbumFragment;
import com.moemoe.lalala.view.fragment.PhoneMateSelectFragment;
import com.moemoe.lalala.view.fragment.PhoneMenuFragment;
import com.moemoe.lalala.view.fragment.PhoneMsgFragment;
import com.moemoe.lalala.view.fragment.PhoneTicketFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by yi on 2017/9/4.
 */

@SuppressWarnings("deprecation")
public class PhoneMainActivity extends BaseAppCompatActivity implements PhoneMainContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.layout_phone_main)
    View mMainRoot;
    @Inject
    PhoneMainPresenter mPresenter;

    private BaseFragment mCurFragment;

    private FragmentTransaction mFragmentTransaction;
    @Override
    protected int getLayoutId() {
        return R.layout.ac_phone;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
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
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
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

    @OnClick({R.id.ll_menu_root,R.id.ll_msg_root,R.id.ll_mate_root,R.id.ll_album_root,R.id.ll_ticket_root,R.id.ll_alarm_root,R.id.ll_shop_root,R.id.ll_search_root})
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
                mCurFragment = PhoneMateSelectFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneMateSelectFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_album_root:
                mCurFragment = PhoneAlbumFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneAlbumFragment.TAG);
                mFragmentTransaction.commit();
                break;
            case R.id.ll_ticket_root:
                mCurFragment = PhoneTicketFragment.newInstance();
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.add(R.id.phone_container,mCurFragment,PhoneTicketFragment.TAG);
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
}
