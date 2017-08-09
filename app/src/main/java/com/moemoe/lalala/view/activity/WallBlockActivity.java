package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSimpleComponent;
import com.moemoe.lalala.di.components.DaggerWallComponent;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.di.modules.WallModule;
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
import com.moemoe.lalala.view.fragment.DiscoveryMainFragment;
import com.moemoe.lalala.view.fragment.FollowMainFragment;
import com.moemoe.lalala.view.fragment.WallBlockFragment;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/2.
 */

public class WallBlockActivity extends BaseAppCompatActivity implements WallContract.View{

    @BindView(R.id.iv_back)
    ImageView IvBack;
    @BindView(R.id.pager_person_data)
    ViewPager mDataPager;
    @BindView(R.id.indicator_person_data)
    CommonTabLayout mPageIndicator;
    @BindView(R.id.tv_simple_label)
    TextView mSimpleLabel;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
   // private ClassFragment classFragment;
    private DiscoveryMainFragment discoveryMainFragment;
    private FollowMainFragment followMainFragment;
    private boolean mCurTag;

    @Inject
    WallPresenter mPresenter;

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
        followMainFragment = FollowMainFragment.newInstance();
       // classFragment = new ClassFragment();
        discoveryMainFragment = DiscoveryMainFragment.newInstance();
        WallBlockFragment wallBlockFragment = new WallBlockFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(followMainFragment);
        fragmentList.add(discoveryMainFragment);
        fragmentList.add(wallBlockFragment);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_follow));
        titles.add(getString(R.string.label_square));
        titles.add(getString(R.string.label_club));
        String[] mTitles = {getString(R.string.label_follow),getString(R.string.label_square),getString(R.string.label_club)};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        TabFragmentPagerAdapter mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mDataPager.setOffscreenPageLimit(2);
        mDataPager.setCurrentItem(1);
        mPageIndicator.setTabData(mTabEntities);
        mPageIndicator.setCurrentTab(1);
        mSimpleLabel.setVisibility(View.VISIBLE);
        mSimpleLabel.setSelected(AppSetting.SUB_TAG);
        mCurTag = AppSetting.SUB_TAG;
        if(PreferenceUtils.isAppFirstLaunch(this) || PreferenceUtils.isVersion2FirstLaunch(this)){
            Intent i = new Intent(this,MengXinActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        IvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mSimpleLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSetting.SUB_TAG = !AppSetting.SUB_TAG;
                mCurTag = AppSetting.SUB_TAG;
                mSimpleLabel.setSelected(AppSetting.SUB_TAG);
                PreferenceUtils.setSimpleLabel(WallBlockActivity.this,AppSetting.SUB_TAG);
                if(discoveryMainFragment != null) discoveryMainFragment.changeLabelAdapter();
                if(followMainFragment != null) followMainFragment.changeLabelAdapter();
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

    public void createTagPre(String docId){
        tagDocId = docId;
        mKlCommentBoard.setVisibility(View.VISIBLE);
        mEdtCommentInput.setText("");
        mEdtCommentInput.setHint("输入标签");
        mEdtCommentInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(this, mEdtCommentInput);
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == CreateRichDocActivity.RESPONSE_CODE){
            if(discoveryMainFragment != null){
                discoveryMainFragment.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCurTag != AppSetting.SUB_TAG){
            mSimpleLabel.setSelected(AppSetting.SUB_TAG);
            if(discoveryMainFragment != null) discoveryMainFragment.changeLabelAdapter();
            if(followMainFragment != null) followMainFragment.changeLabelAdapter();
            mCurTag = AppSetting.SUB_TAG;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.release();
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
