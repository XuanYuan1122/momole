package com.moemoe.lalala.view.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.AlarmEvent;
import com.moemoe.lalala.greendao.gen.AlarmClockEntityDao;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.PhoneAlarmAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneAlarmFragment extends BaseFragment{

    public static final String TAG = "PhoneAlarmFragment";

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvAdd;
    @BindView(R.id.container)
    View mFragRoot;

    private PhoneAlarmAdapter mAdapter;
    private PhoneAlarmEditFragment mPhoneAlarmEditFragment;
    private ArrayList<AlarmClockEntity> mAlarmClockList;
    private boolean isUpdate;

    public static PhoneAlarmFragment newInstance(){
        return new PhoneAlarmFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_alarm;
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
        mAlarmClockList = new ArrayList<>();
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("定时提醒");
        mIvAdd.setVisibility(View.VISIBLE);
        mIvAdd.setImageResource(R.drawable.btn_add_alarm);
        mIvAdd.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mPhoneAlarmEditFragment == null){
                    mIvAdd.setImageResource(R.drawable.btn_alarm_save);
                    mFragRoot.setVisibility(View.VISIBLE);
                    mListDocs.setVisibility(View.GONE);
                    FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
                    mPhoneAlarmEditFragment = PhoneAlarmEditFragment.newInstance();
                    mFragmentTransaction.add(R.id.container,mPhoneAlarmEditFragment,PhoneAlarmEditFragment.TAG);
                    mFragmentTransaction.commit();
                    isUpdate = false;
                }else {
                    mPhoneAlarmEditFragment.sendAlarmEvent(isUpdate);
                    onBackPressed();
                }
            }
        });

        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new PhoneAlarmAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setList(mAlarmClockList);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mPhoneAlarmEditFragment == null){
                    mIvAdd.setImageResource(R.drawable.btn_alarm_save);
                    AlarmClockEntity entity = mAlarmClockList.get(position);
                    mFragRoot.setVisibility(View.VISIBLE);
                    mListDocs.setVisibility(View.GONE);
                    FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
                    mPhoneAlarmEditFragment = PhoneAlarmEditFragment.newInstance(entity);
                    mFragmentTransaction.add(R.id.container,mPhoneAlarmEditFragment,PhoneAlarmEditFragment.TAG);
                    mFragmentTransaction.commit();
                    isUpdate = true;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        subscribeAlarmEvent();
        updateList();
    }

    private void addList(AlarmClockEntity entity){
        mAlarmClockList.clear();
        int id = (int) entity.getId();
        int count = 0;
        int position = 0;
        AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
        List<AlarmClockEntity> list = dao.loadAll();
        for (AlarmClockEntity entity1 : list){
            mAlarmClockList.add(entity1);
            if(id == (int)entity1.getId()){
                position = count;
                if(entity1.isOnOff()){
                    Utils.startAlarmClock(getContext(),entity1);
                }
            }
            count++;
        }
        mAdapter.notifyItemInserted(position);
        mListDocs.getRecyclerView().scrollToPosition(position);
    }

    private void deleteList(){
        mAlarmClockList.clear();
        AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
        List<AlarmClockEntity> list = dao.loadAll();
        for (AlarmClockEntity entity1 : list){
            mAlarmClockList.add(entity1);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateList(){
        mAlarmClockList.clear();
        AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
        List<AlarmClockEntity> list = dao.loadAll();
        for (AlarmClockEntity entity : list){
            mAlarmClockList.add(entity);
            if(entity.isOnOff()){
                Utils.startAlarmClock(getContext(),entity);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void subscribeAlarmEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(AlarmEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<AlarmEvent>() {
                    @Override
                    public void accept(AlarmEvent alarmEvent) throws Exception {
                        if(alarmEvent.getType() == 1){
                            GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao().insertOrReplace(alarmEvent.getEntity());
                            addList(alarmEvent.getEntity());
                        }else if(alarmEvent.getType() == 2){
                            GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao().delete(alarmEvent.getEntity());
                            Utils.cancelAlarmClock(getContext(), (int) alarmEvent.getEntity().getId());
                            NotificationManager notificationManager = (NotificationManager) getContext()
                                    .getSystemService(Activity.NOTIFICATION_SERVICE);
                            // 取消下拉列表通知消息
                            notificationManager.cancel((int) alarmEvent.getEntity().getId());
                            onBackPressed();
                            deleteList();
                        }else {
                            updateList();
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    public void release() {
        super.release();
        RxBus.getInstance().unSubscribe(this);
    }

    @Override
    public void onBackPressed() {
        if(mPhoneAlarmEditFragment != null){
            mFragRoot.setVisibility(View.GONE);
            mListDocs.setVisibility(View.VISIBLE);
            FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
            mFragmentTransaction.remove(mPhoneAlarmEditFragment);
            mFragmentTransaction.commit();
            mPhoneAlarmEditFragment.release();
            mPhoneAlarmEditFragment = null;
            mIvAdd.setImageResource(R.drawable.btn_add_alarm);
        }else {
            ((PhoneMainActivity)getContext()).finishCurFragment();
        }
    }

}
