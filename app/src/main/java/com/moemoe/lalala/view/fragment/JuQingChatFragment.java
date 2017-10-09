package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerJuQIngChatComponent;
import com.moemoe.lalala.di.modules.JuQingChatModule;
import com.moemoe.lalala.model.entity.JuQingShowEntity;
import com.moemoe.lalala.presenter.JuQIngChatContract;
import com.moemoe.lalala.presenter.JuQingChatPresenter;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.JuQingDoneEntity;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.adapter.ChatAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/28.
 */

public class JuQingChatFragment extends BaseFragment implements JuQIngChatContract.View{

    @BindView(R.id.list)
    RecyclerView mListDocs;

    @Inject
    JuQingChatPresenter mPresenter;

    private ArrayList<JuQingShowEntity> mList;
    private ChatAdapter mAdapter;
    private int mCurIndex;
    private BottomMenuFragment menuFragment;
    private String id;

    @Override
    protected int getLayoutId() {
        return R.layout.view_one_recycler;
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
        String role = getArguments().getString("role");
        id = getArguments().getString("id");
        mAdapter = new ChatAdapter(getContext());
        LinearLayoutManager m = new LinearLayoutManager(getContext());
        // m.setStackFromEnd(true);
        mListDocs.setLayoutManager(m);
        mListDocs.setAdapter(mAdapter);
        mListDocs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mCurIndex != -1){
                    toNext();
                }
                return false;
            }
        });
        ArrayList<JuQingShowEntity> list = JuQingUtil.getJuQingShow(id);
        mList = new ArrayList<>();
        for(JuQingShowEntity entity : list){
            if(entity.getName().equals("me")){
                entity.setPath(PreferenceUtils.getAuthorInfo().getHeadPath());
            }else {
                if(role.equals("len")){
                    entity.setOthenPath(R.drawable.ic_phone_message_len);
                }
                if(role.equals("mei")){
                    entity.setOthenPath(R.drawable.ic_phone_message_mei);
                }
                if(role.equals("sari")){
                    entity.setOthenPath(R.drawable.ic_phone_message_sari);
                }
            }
            mList.add(entity);
        }
        if(mList.size() > 0){
            JuQingShowEntity entity = mList.get(0);
            mAdapter.addItem(entity);
            if(entity.getChoice().size() > 0){
                if (entity.getChoice().size() == 1){
                    for(int index : entity.getChoice().values()){
                        mCurIndex = index;
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
        menuFragment.setMenuItems(items);
        menuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        menuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                mCurIndex = itemId;
            }
        });
        menuFragment.show(getChildFragmentManager(), "MsgMenu");
    }

    private void toNext(){
        if(mList.size() > 0){
            JuQingShowEntity entity = mList.get(mCurIndex);
            if(entity.getChoice().size() > 0){
                if(entity.getChoice().size() == 1){
                    for(int index : entity.getChoice().values()){
                        mCurIndex = index;
                    }
                }else {
                    showMenu(entity.getChoice());
                }
            }else {
                mCurIndex = -1;
                mPresenter.doneJuQing(id);
            }
            mAdapter.addItem(entity);
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onDoneSuccess(long time) {
        JuQingUtil.saveJuQingDone(id,time);
    }
}