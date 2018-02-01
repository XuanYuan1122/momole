package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerWallComponent;
import com.moemoe.lalala.di.modules.WallModule;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.presenter.WallContract;
import com.moemoe.lalala.presenter.WallPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.FeedBagFragment;
import com.moemoe.lalala.view.fragment.FeedNoticeFragment;
import com.moemoe.lalala.view.fragment.LuntanAllFragment;
import com.moemoe.lalala.view.fragment.NewFollowMainFragment;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/12/2.
 */

public class WallBlockActivity extends BaseAppCompatActivity implements WallContract.View{

    @BindView(R.id.iv_back)
    ImageView IvBack;
    @BindView(R.id.pager_person_data)
    ViewPager mDataPager;
    @BindView(R.id.indicator_person_data)
    CommonTabLayout mPageIndicator;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.rl_role_root)
    RelativeLayout mRoleRoot;

    private NewFollowMainFragment classMainFragment;
    private TabFragmentPagerAdapter mAdapter;

    @Inject
    WallPresenter mPresenter;
    private LuntanAllFragment luntanAllFragment;
    private FeedNoticeFragment noticeFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_wall;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        DaggerWallComponent.builder()
                .wallModule(new WallModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        clickEvent("dongtai");
        mRoleRoot.setVisibility(View.GONE);
        classMainFragment = NewFollowMainFragment.newInstance("ground");
        List<BaseFragment> fragmentList = new ArrayList<>();

        noticeFragment =  FeedNoticeFragment.newInstance();

        fragmentList.add(noticeFragment);

        fragmentList.add(classMainFragment);

        fragmentList.add(FeedBagFragment.newInstance());

        luntanAllFragment = LuntanAllFragment.newInstance();
        fragmentList.add(luntanAllFragment);


        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_follow));
        titles.add(getString(R.string.label_dynamic));
        titles.add(getString(R.string.label_bag));
        titles.add(getString(R.string.label_luntan));
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (String mTitle : titles) {
            mTabEntities.add(new TabEntity(mTitle, R.drawable.ic_personal_bag, R.drawable.ic_personal_bag));
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mDataPager.setCurrentItem(2);
        mPageIndicator.setTabData(mTabEntities);
        mPageIndicator.setCurrentTab(2);
        if(PreferenceUtils.getMessageDot(this,"neta")
                || PreferenceUtils.getMessageDot(this,"system")
                || PreferenceUtils.getMessageDot(this,"at_user")
                || PreferenceUtils.getMessageDot(this,"normal")){
            mPageIndicator.showDot(0);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void likeDynamic(String id,boolean isLike,int position){
        int page = mDataPager.getCurrentItem();
        if(page == 0){
            noticeFragment.likeDynamic(id, isLike, position);
        }
        if(page == 1){
            classMainFragment.likeDynamic(id, isLike, position);
        }
        if(page == 2){
           // randomFragment.likeDynamic(id, isLike, position);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void systemMsgEvent(SystemMessageEvent event){
        if(PreferenceUtils.getMessageDot(WallBlockActivity.this,"neta")
                || PreferenceUtils.getMessageDot(WallBlockActivity.this,"system")
                || PreferenceUtils.getMessageDot(WallBlockActivity.this,"at_user")
                || PreferenceUtils.getMessageDot(WallBlockActivity.this,"normal")){
            mPageIndicator.showDot(0);
        }else {
            mPageIndicator.hideMsg(0);
        }
    }

    @Override
    protected void initListeners() {
        IvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mPageIndicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mDataPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mDataPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicator.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mTvSendComment.setEnabled(false);
                } else {
                    mTvSendComment.setEnabled(true);
                }

            }
        });
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    mKlCommentBoard.setVisibility(View.GONE);
                }
            }
        });
        mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEdtCommentInput.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    createTag(content);
                    SoftKeyboardUtils.dismissSoftKeyboard(WallBlockActivity.this);
                }else {
                    showToast(R.string.msg_doc_comment_not_empty);
                }
            }
        });
    }


    private String tagDocId = "";

    private void createTag(String content){
        if(!TextUtils.isEmpty(tagDocId)){
            TagSendEntity bean = new TagSendEntity(tagDocId,content);
            mPresenter.createLabel(bean);
        }
        tagDocId = "";
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.release();
        if(mAdapter != null) mAdapter.release();
        EventBus.getDefault().unregister(this);
        stayEvent("dongtai");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,R.anim.main_list_out);
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onCreateLabel(String s, String name) {
        showToast("添加标签成功");
    }
}
