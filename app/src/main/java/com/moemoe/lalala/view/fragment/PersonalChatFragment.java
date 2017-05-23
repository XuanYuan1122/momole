package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.PrivateMessageEvent;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.model.entity.PrivateMessageItemEntity;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.view.activity.ChatActivity;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalChatFragment extends BaseFragment{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;
    private int mCurOffset;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static PersonalChatFragment newInstance(String id){
        PersonalChatFragment fragment = new PersonalChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            subscribeEvent();
            return;
        }
        final String id = getArguments().getString("uuid");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(getContext(),12);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof PrivateMessageItemEntity){
                    Intent i = new Intent(getContext(), ChatActivity.class);
                    i.putExtra("talkId",((PrivateMessageItemEntity) o).getTalkId());
                    i.putExtra("title",((PrivateMessageItemEntity) o).getName());
                    startActivity(i);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                readListFromDb(mCurOffset);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mCurOffset = 0;
        subscribeEvent();
        readListFromDb(mCurOffset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void subscribeEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(PrivateMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<PrivateMessageEvent>() {
                    @Override
                    public void call(PrivateMessageEvent event) {
                        int pos = findPosByTalkId(event.getTalkId());
                        if(event.isDelete()){
                            if(pos >= 0 && pos < mAdapter.getItemCount()){
                                mAdapter.getList().remove(pos);
                                mAdapter.notifyDataSetChanged();
                            }
                        }else {
                            if(pos >= 0 && pos < mAdapter.getItemCount()){
                                mAdapter.notifyItemChanged(pos);
                            }else if(pos == -1 && mAdapter.getItemCount() < 20){
                                mCurOffset = 0;
                                readListFromDb(mCurOffset);
                            }
                        }
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
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }

    private void readListFromDb(int offset){
        PrivateMessageItemEntityDao dao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
        List<PrivateMessageItemEntity> list = dao.queryBuilder()
                .offset(offset * 20)
                .limit(20)
                .list();
        if(list.size() < 20){
            mListDocs.setLoadMoreEnabled(false);
        }else {
            mCurOffset++;
            mListDocs.setLoadMoreEnabled(true);
        }
        if(offset == 0){
            mAdapter.setData(list);
        }else {
            mAdapter.addData(list);
        }
        isLoading = false;
        mListDocs.setComplete();
    }

    private int findPosByTalkId(String talkId){
        ArrayList<Object> list = mAdapter.getList();
        for (Object o : list){
            PrivateMessageItemEntity entity = (PrivateMessageItemEntity) o;
            if(entity.getTalkId().equals(talkId)){
                return list.indexOf(o);
            }
        }
        return -1;
    }
}
