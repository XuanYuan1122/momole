package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
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
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
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
 * Created by yi on 2017/9/28.
 */

public class JuQingChatFragment extends BaseFragment implements JuQIngChatContract.View{

    @BindView(R.id.list)
    RecyclerView mListDocs;
//    @BindView(R.id.rl_click)
//    View mClickView;

    @Inject
    JuQingChatPresenter mPresenter;

    private ArrayList<JuQingShowEntity> mList;
    private ChatAdapter mAdapter;
    private int mCurIndex;
    private BottomMenuFragment menuFragment;
    private String id;
    private String role;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_chat_phone;
    }

    public static JuQingChatFragment newInstance(String role,String id){
        JuQingChatFragment fragment = new JuQingChatFragment();
        Bundle b = new Bundle();
        b.putString("role",role);
        b.putString("id",id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerJuQIngChatComponent.builder()
                .juQingChatModule(new JuQingChatModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        role = getArguments().getString("role");
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
//        mClickView.setOnClickListener(new NoDoubleClickListener(500) {
//            @Override
//            public void onNoDoubleClick(View v) {
//                if(mCurIndex != -1){
//                    JuQingShowEntity entity = mList.get(mCurIndex);
//                    if(entity.getChoice().size() > 0){
//                        if(entity.getChoice().size() == 1){
//                            for(int index : entity.getChoice().values()){
//                                mCurIndex = index;
//                                toNext();
//                            }
//                        }else {
//                            showMenu(entity.getChoice());
//                        }
//                    }else {
//                        mCurIndex = -1;
//                        mPresenter.doneJuQing(id);
//                    }
//                }
//            }
//        });
        if(mList.size() > 0){
            JuQingShowEntity entity = mList.get(0);
            mCurIndex = 0;
            mAdapter.addItem(entity);
            mListDocs.scrollToPosition(mAdapter.getItemCount()-1);
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
        menuFragment = new BottomMenuFragment();
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
        RxBus.getInstance().post(new EventDoneEvent("mobile",role));
    }
}
