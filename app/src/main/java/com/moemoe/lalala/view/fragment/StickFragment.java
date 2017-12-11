package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.PhonePlayMusicEvent;
import com.moemoe.lalala.event.StickChangeEvent;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.model.entity.StickEntity;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.CameraPreview2Activity;
import com.moemoe.lalala.view.adapter.StickAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class StickFragment extends BaseFragment{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    private StickAdapter mAdapter;
    private String roleId;

    public static StickFragment newInstance(ArrayList<StickEntity.Stick> list,String roleId){
        StickFragment fragment = new StickFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list",list);
        bundle.putString("roleId",roleId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ArrayList<StickEntity.Stick> list = getArguments().getParcelableArrayList("list");
        roleId = getArguments().getString("roleId");
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new StickAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setList(list);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final StickEntity.Stick stick = mAdapter.getItem(position);
                if(stick.isBelong()){
                    int pos = mAdapter.getSelectPosition();
                    if(pos != position){
                        mAdapter.setSelectPosition(position);
                        mAdapter.notifyItemChanged(position);
                        if(pos >= 0){
                            mAdapter.notifyItemChanged(pos);
                        }
                        ((CameraPreview2Activity)getContext()).changeStick(stick.getPath());
                        RxBus.getInstance().post(new StickChangeEvent(roleId,position));
                    }
                }else {
                    if("JC".equals(stick.getType())){
                        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                        alertDialogUtil.createNormalDialog(getContext(),"是否购买？");
                        alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                            @Override
                            public void CancelOnClick() {
                                alertDialogUtil.dismissDialog();
                            }

                            @Override
                            public void ConfirmOnClick() {
                                ((CameraPreview2Activity)getContext()).buyStick(stick.getStickId(),roleId,position);
                                alertDialogUtil.dismissDialog();
                            }
                        });
                        alertDialogUtil.showDialog();
                    }else {
                        ToastUtils.showShortToast(getContext(),"未拥有");
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        subscribeBackOrChangeEvent();
    }

    private void subscribeBackOrChangeEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(StickChangeEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<StickChangeEvent>() {
                    @Override
                    public void accept(StickChangeEvent event) throws Exception {
                        if(!roleId.equals(event.getRoleId())){
                            int position = mAdapter.getSelectPosition();
                            if(position >= 0){
                                mAdapter.setSelectPosition(-1);
                                mAdapter.notifyItemChanged(position);
                            }
                        }else {
                            int position = mAdapter.getSelectPosition();
                            if(position != event.getPosition()){
                                mAdapter.setSelectPosition(event.getPosition());
                                mAdapter.notifyItemChanged(event.getPosition());
                                if(position >= 0){
                                    mAdapter.notifyItemChanged(position);
                                }
                                ((CameraPreview2Activity)getContext()).changeStick(mAdapter.getItem(event.getPosition()).getPath());
                                RxBus.getInstance().post(new StickChangeEvent(roleId,position));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    public void release(){
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }
}
