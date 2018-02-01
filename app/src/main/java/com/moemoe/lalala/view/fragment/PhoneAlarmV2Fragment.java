package com.moemoe.lalala.view.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;

import com.moemoe.lalala.event.AlarmEvent;
import com.moemoe.lalala.greendao.gen.AlarmClockEntityDao;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.PhoneAlarmAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class PhoneAlarmV2Fragment extends BaseFragment implements IPhoneFragment{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    private PhoneAlarmAdapter mAdapter;
    private ArrayList<AlarmClockEntity> mAlarmClockList;

    public static PhoneAlarmV2Fragment newInstance(){
        return new PhoneAlarmV2Fragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAlarmClockList = new ArrayList<>();
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new PhoneAlarmAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setList(mAlarmClockList);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlarmClockEntity entity = mAlarmClockList.get(position);
                ((PhoneMainV2Activity)getContext()).toFragment(PhoneAlarmEditV2Fragment.newInstance(entity));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        EventBus.getDefault().register(this);
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
        mAlarmClockList.addAll(list);
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
            }else {
                Utils.cancelAlarmClock(getContext(), (int) entity.getId());
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void alarmEvent(AlarmEvent event){
        if(event.getType() == 1){
            GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao().insertOrReplace(event.getEntity());
            addList(event.getEntity());
        }else if(event.getType() == 2){
            GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao().delete(event.getEntity());
            Utils.cancelAlarmClock(getContext(), (int) event.getEntity().getId());
            NotificationManager notificationManager = (NotificationManager) getContext()
                    .getSystemService(Activity.NOTIFICATION_SERVICE);
            // 取消下拉列表通知消息
            assert notificationManager != null;
            notificationManager.cancel((int) event.getEntity().getId());
            ((PhoneMainV2Activity)getContext()).onBackPressed();
            deleteList();
        }else {
            updateList();
        }
    }

    @Override
    public void release() {
        super.release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getTitle() {
        return "定时提醒";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return R.drawable.btn_add_alarm;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onMenuClick() {
        ((PhoneMainV2Activity)getContext()).toFragment(PhoneAlarmEditV2Fragment.newInstance());
    }

}
