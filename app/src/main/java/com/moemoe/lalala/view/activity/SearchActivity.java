package com.moemoe.lalala.view.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.AtUserEvent;
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.SearchBagFragment;
import com.moemoe.lalala.view.fragment.SearchDocFragment;
import com.moemoe.lalala.view.fragment.SearchUserFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2017/3/1.
 */

public class SearchActivity extends BaseAppCompatActivity {

    public static final int SHOW_ALL = -1;
    public static final int SHOW_DOC = 1;
    public static final int SHOW_BAG = 2;
    public static final int SHOW_USER = 3;

    @BindView(R.id.rl_search_root)
    View mSearchRoot;
    @BindView(R.id.ll_search)
    View mLlSearchRoot;
    @BindView(R.id.ll_search_root_1)
    View mLlSearchRoot1;
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.iv_clear)
    ImageView mIvClear;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.indicator_search_data)
    TabLayout mTab;
    @BindView(R.id.pager_search_data)
    ViewPager mVpSearch;

    private boolean isOpen;
    private String mKeyWord;
    private TabFragmentPagerAdapter mAdapter;
    private int showType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_search;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        showType = getIntent().getIntExtra("show_type",SHOW_ALL);
        mTab.setVisibility(View.GONE);
        List<BaseFragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        if(showType == SHOW_ALL){
            SearchDocFragment docFragment = SearchDocFragment.newInstance();
            SearchBagFragment docFragment1 = SearchBagFragment.newInstance();
            SearchUserFragment docFragment2 = SearchUserFragment.newInstance(false);
            fragmentList.add(docFragment);
            fragmentList.add(docFragment1);
            fragmentList.add(docFragment2);
            titles.add(getString(R.string.label_doc));
            titles.add(getString(R.string.label_bag));
            titles.add(getString(R.string.label_user));
        }
        if(showType == SHOW_DOC){
            SearchDocFragment docFragment = SearchDocFragment.newInstance();
            fragmentList.add(docFragment);
            titles.add(getString(R.string.label_doc));
        }
        if(showType == SHOW_BAG){
            SearchBagFragment docFragment1 = SearchBagFragment.newInstance();
            fragmentList.add(docFragment1);
            titles.add(getString(R.string.label_bag));
        }
        if(showType == SHOW_USER){
            SearchUserFragment docFragment2 = SearchUserFragment.newInstance(true);
            fragmentList.add(docFragment2);
            titles.add(getString(R.string.label_user));
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mVpSearch.setAdapter(mAdapter);
        mTab.setupWithViewPager(mVpSearch);
        mVpSearch.setCurrentItem(1);
        mVpSearch.setOffscreenPageLimit(2);
        subscribeChangedEvent();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }

    @Override
    protected void initListeners() {
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SoftKeyboardUtils.dismissSoftKeyboard(SearchActivity.this);
                    String curKey = mEtSearch.getText().toString();
                    if(TextUtils.isEmpty(mKeyWord) || (!TextUtils.isEmpty(curKey) && !mKeyWord.equals(mEtSearch.getText().toString()))){
                        mKeyWord = mEtSearch.getText().toString();
                        RxBus.getInstance().post(new SearchChangedEvent(mEtSearch.getText().toString()));
                    }
                    if(mTab.getVisibility() == View.GONE && showType == SHOW_ALL){
                        mTab.setVisibility(View.VISIBLE);
                        mVpSearch.setCurrentItem(0);
                    }
                }
                return false;
            }
        });
        mIvClear.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mEtSearch.setText("");
            }
        });
        mSearchRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(!isOpen){
                    isOpen = true;
                    int[] location = new int[2];
                    mLlSearchRoot.getLocationOnScreen(location);
                    ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mLlSearchRoot,"translationX",mLlSearchRoot.getTranslationX(),-(location[0]- DensityUtil.dip2px(SearchActivity.this,22))).setDuration(300);
                    ObjectAnimator searchAnimator = ObjectAnimator.ofFloat(mIvSearch,"alpha",0.5f,1.0f).setDuration(300);
                    AnimatorSet set = new AnimatorSet();
                    set.play(cardAnimator).with(searchAnimator);
                    set.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLlSearchRoot.setVisibility(View.GONE);
                            mLlSearchRoot1.setVisibility(View.VISIBLE);
                            mEtSearch.setFocusable(true);
                            mEtSearch.setFocusableInTouchMode(true);
                            mEtSearch.requestFocus();
                            InputMethodManager imm = (InputMethodManager)SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    set.start();
                }
            }
        });
        mTvCancel.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        if(isOpen){
            isOpen = false;
            mEtSearch.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(),0);
            int[] location = new int[2];
            mLlSearchRoot.getLocationOnScreen(location);
            ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mLlSearchRoot,"translationX",mLlSearchRoot.getTranslationX(),location[0]- DensityUtil.dip2px(SearchActivity.this,22)).setDuration(300);
            ObjectAnimator searchAnimator = ObjectAnimator.ofFloat(mIvSearch,"alpha",1.0f,0.5f).setDuration(300);
            AnimatorSet set = new AnimatorSet();
            set.play(cardAnimator).with(searchAnimator);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mLlSearchRoot.setVisibility(View.VISIBLE);
                    mLlSearchRoot1.setVisibility(View.GONE);
                    mEtSearch.setText("");
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.start();
        }else {
            finish();
        }
    }

    private void subscribeChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(AtUserEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<AtUserEvent>() {
                    @Override
                    public void call(AtUserEvent event) {
                        Intent i = new Intent();
                        i.putExtra("user_id",event.getUserId());
                        i.putExtra("user_name",event.getUserName());
                        setResult(RESULT_OK,i);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    protected void onDestroy() {
        if(mAdapter != null)mAdapter.release();
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }
}
