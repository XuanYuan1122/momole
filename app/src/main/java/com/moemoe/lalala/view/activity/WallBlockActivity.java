package com.moemoe.lalala.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerSimpleComponent;
import com.moemoe.lalala.di.components.DaggerWallComponent;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.di.modules.WallModule;
import com.moemoe.lalala.event.MateChangeEvent;
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
import com.moemoe.lalala.view.fragment.NewDiscoverMainFragment;
import com.moemoe.lalala.view.fragment.NewFollowMainFragment;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

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
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.iv_role)
    ImageView mIvRole;
    @BindView(R.id.iv_create_dynamic)
    ImageView mIvCreatDynamic;
//    @BindView(R.id.iv_create_wenzhang)
//    ImageView mIvCreateWen;
    @BindView(R.id.tv_show_text)
    TextView mTvText;
    @BindView(R.id.rl_role_root)
    RelativeLayout mRoleRoot;

    private NewFollowMainFragment classMainFragment;
    private TabFragmentPagerAdapter mAdapter;
    private GestureDetector gestureDetector;

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
        classMainFragment = NewFollowMainFragment.newInstance("ground");
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(NewFollowMainFragment.newInstance("follow"));
        fragmentList.add(NewDiscoverMainFragment.newInstance("random"));
        fragmentList.add(classMainFragment);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_follow));
        titles.add(getString(R.string.label_find));
        titles.add(getString(R.string.label_square));
        String[] mTitles = {getString(R.string.label_follow),getString(R.string.label_find),getString(R.string.label_square)};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mDataPager.setCurrentItem(1);
        mPageIndicator.setTabData(mTabEntities);
        mPageIndicator.setCurrentTab(1);
        gestureDetector = new GestureDetector(this,onGestureListener);
        subscribeSearchChangedEvent();
        ViewUtils.setRoleButton(mIvRole,mTvText);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;
    private boolean isHide;

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY){
            if (e1.getY()-e2.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                if(!isHide){
                    hideRole();
                    isHide = true;
                }
            } else if (e2.getY()-e1.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                if(isHide){
                    showRole();
                    isHide = false;
                }

            }
            return false;
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
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
        mIvRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRole();
            }
        });
        mIvCreatDynamic.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                clickRole();
                Intent i4 = new Intent(WallBlockActivity.this,CreateDynamicActivity.class);
                i4.putExtra("default_tag","广场");
                startActivity(i4);
            }
        });
//        mIvCreateWen.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                clickRole();
//                Intent intent = new Intent(WallBlockActivity.this, CreateRichDocActivity.class);
//                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,3);
//                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"书包");
//                intent.putExtra("from_name","书包");
//                intent.putExtra("from_schema","neta://com.moemoe.lalala/bag_2.0");
//                startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
//            }
//        });
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

    private void clickRole(){
        mIvRole.setSelected(!mIvRole.isSelected());
        if(mIvRole.isSelected()){
            mIvCreatDynamic.setVisibility(View.VISIBLE);
            //mIvCreateWen.setVisibility(View.VISIBLE);
            mTvText.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(WallBlockActivity.this,R.color.alph_60));
            mRoleRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickRole();
                }
            });
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_END);
            mIvRole.setLayoutParams(lp1);
        }else {
            mIvCreatDynamic.setVisibility(View.GONE);
            //mIvCreateWen.setVisibility(View.GONE);
            mTvText.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(WallBlockActivity.this,R.color.transparent));
            mRoleRoot.setOnClickListener(null);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mIvRole.setLayoutParams(lp1);
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void subscribeSearchChangedEvent() {
        Disposable subscription1 = RxBus.getInstance()
                .toObservable(MateChangeEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<MateChangeEvent>() {
                    @Override
                    public void accept(MateChangeEvent backSchoolEvent) throws Exception {
                        ViewUtils.setRoleButton(mIvRole,mTvText);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.release();
        if(mAdapter != null) mAdapter.release();
        RxBus.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mIvRole.isSelected()){
            clickRole();
            return;
        }
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
