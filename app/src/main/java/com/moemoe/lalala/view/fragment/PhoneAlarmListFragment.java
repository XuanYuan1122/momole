//package com.moemoe.lalala.view.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.view.View;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.di.components.DaggerPhoneMenuListComponent;
//import com.moemoe.lalala.di.modules.PhoneMenuListModule;
//import com.moemoe.lalala.model.api.ApiService;
//import com.moemoe.lalala.model.entity.PhoneMenuEntity;
//import com.moemoe.lalala.presenter.PhoneMenuListContract;
//import com.moemoe.lalala.presenter.PhoneMenuListPresenter;
//import com.moemoe.lalala.utils.PreferenceUtils;
//import com.moemoe.lalala.view.activity.NewPersonalActivity;
//import com.moemoe.lalala.view.adapter.PhoneAlarmAdapter;
//import com.moemoe.lalala.view.adapter.PhoneMenuListAdapter;
//import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
//import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
//import com.moemoe.lalala.view.widget.recycler.PullCallback;
//
//import java.util.ArrayList;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//
///**
// * Created by yi on 2017/9/4.
// */
//
//public class PhoneAlarmListFragment extends BaseFragment{
//
//    public static final String TAG = "PhoneAlarmListFragment";
//
//    @BindView(R.id.list)
//    PullAndLoadView mListDocs;
//
//    private PhoneAlarmAdapter mAdapter;
//
//    public static PhoneAlarmListFragment newInstance(){
//        return new PhoneAlarmListFragment();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.frag_onepull;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        mListDocs.getSwipeRefreshLayout().setEnabled(false);
//        mListDocs.setLoadMoreEnabled(false);
//        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
//        mAdapter= new PhoneAlarmAdapter();
//        mListDocs.getRecyclerView().setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                //TODO 编辑
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        });
//        updateList();
//    }
//
//    public void updateList(){
//
//    }
//}
