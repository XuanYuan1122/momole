package com.moemoe.lalala.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.MateBackPressEvent;
import com.moemoe.lalala.event.MateLuyinEvent;
import com.moemoe.lalala.view.activity.PhoneMainActivity;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2017/9/8.
 */

public class PhoneMateFragment extends BaseFragment{

    public static final String TAG = "PhoneMateFragment";

    @BindView(R.id.app_bar)
    RelativeLayout mToolRoot;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;

    private Fragment mCurFragment;
    private PhoneMateSelectFragment phoneMateSelectFragment;
    private PhoneTicketFragment phoneTicketFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_msg;
    }

    public static PhoneMateFragment newInstance(){
        return new PhoneMateFragment();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("同桌");
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvMenu.setVisibility(View.GONE);
        mTvMenu.setTextColor(Color.WHITE);
        mTvMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.x20));
        mTvMenu.setGravity(Gravity.CENTER);
        mTvMenu.setBackgroundResource(R.drawable.shape_rect_border_main_background_y22);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.x144),(int)getResources().getDimension(R.dimen.y44));
        lp.rightMargin = (int) getResources().getDimension(R.dimen.x20);
        mTvMenu.setLayoutParams(lp);
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        mCurFragment = phoneMateSelectFragment = PhoneMateSelectFragment.newInstance();
        mFragmentTransaction.add(R.id.container,phoneMateSelectFragment,"phoneMateSelectFragment");
        mFragmentTransaction.commit();
        subscribeBackOrChangeEvent();
    }

    @Override
    public void onBackPressed() {
        if(mCurFragment != null && mCurFragment instanceof PhoneTicketFragment){
            mTvTitle.setText("同桌");
            mTvMenu.setVisibility(View.GONE);
            FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
            mFragmentTransaction.remove(mCurFragment).show(phoneMateSelectFragment);
            mFragmentTransaction.commit();
            mCurFragment = phoneMateSelectFragment;
            phoneTicketFragment.release();
            phoneTicketFragment = null;
        }else {
            if(!phoneMateSelectFragment.onBack()){
                ((PhoneMainActivity)getContext()).finishCurFragment();
            }else {
                mTvTitle.setText("同桌");
            }

        }
    }

    private void changeShow(String mate){
        mTvTitle.setText("录音收集");
        mTvMenu.setVisibility(View.VISIBLE);
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        phoneTicketFragment = PhoneTicketFragment.newInstance(mate);
        mFragmentTransaction.hide(mCurFragment).add(R.id.container,phoneTicketFragment,"phoneTicketFragment");
        mFragmentTransaction.commit();
        mCurFragment = phoneTicketFragment;
    }

    private void subscribeBackOrChangeEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(MateBackPressEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<MateBackPressEvent>() {
                    @Override
                    public void accept(MateBackPressEvent mateBackPressEvent) throws Exception {
                        if(mateBackPressEvent.getName().equals("服装")){
                            mTvTitle.setText(mateBackPressEvent.getName());
                        }else {
                            mTvMenu.setText(mateBackPressEvent.getName());
                        }
                    }

                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        Disposable subscription1 = RxBus.getInstance()
                .toObservable(MateLuyinEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<MateLuyinEvent>() {
                    @Override
                    public void accept(MateLuyinEvent mateLuyinEvent) throws Exception {
                        changeShow(mateLuyinEvent.getSelectMate());
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
    public void release() {
        phoneMateSelectFragment.release();
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }
}
