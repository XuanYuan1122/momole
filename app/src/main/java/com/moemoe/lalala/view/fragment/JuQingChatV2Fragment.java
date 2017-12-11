package com.moemoe.lalala.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerJuQIngChatComponent;
import com.moemoe.lalala.di.modules.JuQingChatModule;
import com.moemoe.lalala.event.EventDoneEvent;
import com.moemoe.lalala.model.entity.JuQingShowEntity;
import com.moemoe.lalala.presenter.JuQIngChatContract;
import com.moemoe.lalala.presenter.JuQingChatPresenter;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.ChatAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 *
 * Created by yi on 2017/9/28.
 */

public class JuQingChatV2Fragment extends BaseFragment implements IPhoneFragment,JuQIngChatContract.View{

    @BindView(R.id.list)
    RecyclerView mListDocs;

    @Inject
    JuQingChatPresenter mPresenter;

    private ArrayList<JuQingShowEntity> mList;
    private ChatAdapter mAdapter;
    private int mCurIndex;
    private String id;
    private String role;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_chat_phone;
    }

    public static JuQingChatV2Fragment newInstance(String role, String id){
        JuQingChatV2Fragment fragment = new JuQingChatV2Fragment();
        Bundle b = new Bundle();
        b.putString("id",id);
        fragment.setRole(role);
        fragment.setArguments(b);
        return fragment;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerJuQIngChatComponent.builder()
                .juQingChatModule(new JuQingChatModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        id = getArguments().getString("id");
        mAdapter = new ChatAdapter(getContext());
        LinearLayoutManager m = new LinearLayoutManager(getContext());
        mListDocs.setLayoutManager(m);
        mListDocs.setAdapter(mAdapter);
        ArrayList<JuQingShowEntity> list = JuQingUtil.getJuQingShow(id);
        mList = new ArrayList<>();
        for(JuQingShowEntity entity : list){
            if(entity.getName().equals("me")){
                entity.setPath(PreferenceUtils.getAuthorInfo().getHeadPath());
            }else {
                if(role.equals("len")){
                    entity.setOtherPath(R.drawable.ic_phone_message_len);
                }
                if(role.equals("mei")){
                    entity.setOtherPath(R.drawable.ic_phone_message_mei);
                }
                if(role.equals("sari")){
                    entity.setOtherPath(R.drawable.ic_phone_message_sari);
                }
            }
            mList.add(entity);
        }
        if(mList.size() > 0){
            JuQingShowEntity entity = mList.get(0);
            mCurIndex = 0;
            mAdapter.addItem(entity);
            mListDocs.scrollToPosition(mAdapter.getItemCount()-1);
            mListDocs.setOnTouchListener(new View.OnTouchListener() {
                private int MIN_CLICK_DELAY_TIME = 500;
                private long lastClickTime = 0;

                private float x;
                private float y;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        x = event.getX();
                        y = event.getY();
                    }

                    if(event.getAction() == MotionEvent.ACTION_UP){
                        float tempX = event.getX();
                        float tempY = event.getY();

                        if(Math.abs(tempX - x) < 20 && Math.abs(tempY - y) < 20){
                            long currentTime = System.currentTimeMillis();
                            long timeD = currentTime - lastClickTime;
                            lastClickTime = currentTime;
                            if(timeD >= MIN_CLICK_DELAY_TIME){
                                if(mCurIndex != -1){
                                    JuQingShowEntity entity = mList.get(mCurIndex);
                                    if(entity.getChoice().size() > 0){
                                        if(entity.getChoice().size() == 1){
                                            for(int index : entity.getChoice().values()){
                                                mCurIndex = index;
                                                toNext();
                                            }
                                        }else {
                                            showMenu(entity.getChoice());
                                        }
                                    }else {
                                        mCurIndex = -1;
                                        mPresenter.doneJuQing(id);
                                    }
                                }
                            }
                        }
                        x = 0;
                        y = 0;
                    }
                    return false;
                }
            });
        }else {
            ((PhoneMainV2Activity)getContext()).onBackPressed();
        }
    }

    @Override
    public void release() {
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    public void showMenu(LinkedHashMap<String,Integer> map){
        final ArrayList<MenuItem> items = new ArrayList<>();
        for(String name : map.keySet()){
            MenuItem item = new MenuItem(map.get(name), name);
            items.add(item);
        }
        BottomMenuFragment menuFragment = new BottomMenuFragment();
        menuFragment.setShowTop(false);
        menuFragment.setShowCancel(false);
        menuFragment.setCancelable(false);
        menuFragment.setMenuItems(items);
        menuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        menuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                mCurIndex = itemId;
                if(mCurIndex != -1){
                    toNext();
                }
            }
        });
        menuFragment.show(getChildFragmentManager(), "MsgMenu");
    }

    private void toNext(){
        if(mList.size() > 0){
            Observable.timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull Long aLong) {

                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            JuQingShowEntity entity = mList.get(mCurIndex);
                            mAdapter.addItem(entity);
                            mListDocs.scrollToPosition(mAdapter.getItemCount()-1);
                        }
                    });
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onDoneSuccess(long time) {
        if(3 != JuQingUtil.getLevel(id)){
            JuQingUtil.saveJuQingDone(id,time);
        }
        int dotNum = PreferenceUtils.getJuQIngDotNum(getContext());
        if(dotNum > 0){
            dotNum -= 1;
        }
        PreferenceUtils.setJuQingDotNum(getContext(),dotNum);
        RxBus.getInstance().post(new EventDoneEvent("mobile",role));
    }

    @Override
    public String getTitle() {
        if("len".equals(role)){
            return "小莲";
        }else if("mei".equals(role)){
            return "美藤双树";
        }else if("sari".equals(role)){
            return "沙利尔";
        }
        return "";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return 0;
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

    }
}
