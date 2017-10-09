package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

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
        mToolRoot.setVisibility(View.GONE);
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        mCurFragment = phoneMateSelectFragment = PhoneMateSelectFragment.newInstance();
        mFragmentTransaction.add(R.id.container,phoneMateSelectFragment,"phoneMateSelectFragment");
        mFragmentTransaction.commit();
        subscribeBackOrChangeEvent();
    }

    @Override
    public void onBackPressed() {
        if(mCurFragment != null && mCurFragment instanceof PhoneTicketFragment){
            FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
            mFragmentTransaction.remove(mCurFragment).show(phoneMateSelectFragment);
            mFragmentTransaction.commit();
            mCurFragment = phoneMateSelectFragment;
            phoneTicketFragment = null;
        }else {
            ((PhoneMainActivity)getContext()).finishCurFragment();
        }
    }

    private void changeShow(String mate){
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
                            onBackPressed();
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
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }
}
